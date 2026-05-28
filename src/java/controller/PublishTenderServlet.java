package controller;

import dao.DAOException;
import dao.impl.TenderDAOImpl;
import dao.impl.UserDAOImpl;
import dao.interfaces.TenderDAO;
import dao.interfaces.UserDAO;
import model.Tender;
import model.User;
import model.enums.TenderStatus;
import model.enums.UserRole;
import service.EmailNotificationService;
import service.TenderLifecycleService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.CSRFTokenUtil;

/**
 * Servlet handling tender publication from DRAFT to OPEN status. Sends email
 * notifications to all registered suppliers.
 *
 * @author kolisang
 * @version 1.0
 */
@WebServlet("/officer/publish-tender")
public class PublishTenderServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PublishTenderServlet.class.getName());
    private TenderDAO tenderDAO;
    private UserDAO userDAO;
    private TenderLifecycleService lifecycleService;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            userDAO = new UserDAOImpl();
            lifecycleService = new TenderLifecycleService();
            logger.info("DAOs initialized successfully in PublishTenderServlet");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize DAOs", e);
            throw new ServletException("Cannot initialize DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String tenderIdParam = req.getParameter("id");
        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tender ID is required.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/officer/tenders");
                return;
            }

            if (tender.getStatus() != TenderStatus.DRAFT) {
                session.setAttribute("errorMessage", "Only draft tenders can be published.");
                resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);
                return;
            }

            // Show confirmation page
            req.setAttribute("tender", tender);
            req.getRequestDispatcher("/officer/publish-tender.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid tender ID.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading tender for publish", e);
            session.setAttribute("errorMessage", "Error loading tender. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // In doPost() method
        if (!CSRFTokenUtil.validateToken(req)) {
            session.setAttribute("errorMessage", "Invalid security token. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
// Invalidate after use to prevent reuse
        CSRFTokenUtil.invalidateToken(session);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String tenderIdParam = req.getParameter("tenderId");
        boolean notifySuppliers = "true".equals(req.getParameter("notifySuppliers"));

        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tender ID is required.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/officer/tenders");
                return;
            }

            if (tender.getStatus() != TenderStatus.DRAFT) {
                session.setAttribute("errorMessage", "Only draft tenders can be published.");
                resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);
                return;
            }

            // Publish the tender using lifecycle service
            TenderLifecycleService.TransitionResult result
                    = lifecycleService.publishTender(tenderId, currentUser.getUserId());

            if (!result.isSuccess()) {
                session.setAttribute("errorMessage", result.getMessage());
                resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);
                return;
            }

            // Send email notifications if requested
            int emailsSent = 0;
            if (notifySuppliers) {
                emailsSent = sendNewTenderNotifications(tender, req);
            }

            logger.info("Tender published: " + tender.getReferenceNumber()
                    + " by " + currentUser.getEmail()
                    + " (Notifications sent: " + emailsSent + ")");

            String successMessage = "Tender " + tender.getReferenceNumber() + " published successfully.";
            if (notifySuppliers) {
                successMessage += " Notifications sent to " + emailsSent + " registered suppliers.";
            }

            session.setAttribute("successMessage", successMessage);
            resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid tender ID.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error publishing tender", e);
            session.setAttribute("errorMessage", "Database error. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        }
    }

    /**
     * Sends new tender notifications to all registered suppliers.
     *
     * @param tender The published tender
     * @param req The HTTP request for building URLs
     * @return Number of emails successfully sent
     */
    private int sendNewTenderNotifications(Tender tender, HttpServletRequest req) {
        int successCount = 0;

        try {
            List<User> suppliers = userDAO.findByRole(UserRole.SUPPLIER);

            if (suppliers.isEmpty()) {
                logger.info("No registered suppliers to notify");
                return 0;
            }

            String baseUrl = req.getScheme() + "://" + req.getServerName() + ":"
                    + req.getServerPort() + req.getContextPath();
            String tenderUrl = baseUrl + "/supplier/tender-detail?id=" + tender.getTenderId();

            for (User supplier : suppliers) {
                try {
                    boolean sent = EmailNotificationService.sendNewTenderNotification(
                            supplier.getEmail(),
                            supplier.getFullName(),
                            tender.getReferenceNumber(),
                            tender.getTitle(),
                            tender.getCategory().getDisplayName(),
                            tender.getFormattedEstimatedValue(),
                            tender.getFormattedClosingDateTime(),
                            tenderUrl
                    );

                    if (sent) {
                        successCount++;
                    }
                } catch (Exception e) {
                    logger.warning("Failed to send notification to: " + supplier.getEmail());
                }
            }

            logger.info("Tender notifications sent: " + successCount + "/" + suppliers.size());

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error fetching suppliers for notifications", e);
        }

        return successCount;
    }
}
