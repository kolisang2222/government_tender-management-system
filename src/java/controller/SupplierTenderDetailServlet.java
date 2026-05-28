package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.TenderDAO;
import model.Tender;
import model.User;
import model.enums.TenderStatus;
import model.enums.UserRole;
import service.TenderLifecycleService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/supplier/tender-detail")
public class SupplierTenderDetailServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SupplierTenderDetailServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private TenderLifecycleService lifecycleService;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            lifecycleService = new TenderLifecycleService();
            logger.info("DAOs initialized successfully in SupplierTenderDetailServlet");
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
        if (currentUser.getRole() != UserRole.SUPPLIER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String tenderIdParam = req.getParameter("id");
        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/supplier/tenders");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                req.setAttribute("errorMessage", "Tender not found.");
                req.getRequestDispatcher("/supplier/tender-detail.jsp").forward(req, resp);
                return;
            }

            // ============================================================
            // AUTOMATIC CLOSING CHECK (Exam Requirement)
            // ============================================================
            if (tender.getStatus() == TenderStatus.OPEN) {
                if (lifecycleService.shouldCloseTender(tenderId)) {
                    lifecycleService.closeTender(tenderId);
                    // Refresh tender after auto-close
                    tender = tenderDAO.findById(tenderId);
                    logger.info("Tender auto-closed on view: " + tender.getReferenceNumber());
                }
            }

            // Check if supplier has already bid on this tender
            boolean hasBid = bidDAO.hasSupplierBid(tenderId, currentUser.getUserId());

            // Check if tender is urgent (closing within 3 days)
            if (tender.getClosingDateTime() != null && tender.getStatus() == TenderStatus.OPEN) {
                boolean urgent = tender.getClosingDateTime()
                        .isBefore(java.time.LocalDateTime.now().plusDays(3));
                tender.setUrgent(urgent);
            }

            req.setAttribute("tender", tender);
            req.setAttribute("hasBid", hasBid);

            req.getRequestDispatcher("/supplier/tender-detail.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/supplier/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading tender detail", e);
            req.setAttribute("errorMessage", "Error loading tender details. Please try again.");
            req.getRequestDispatcher("/supplier/tender-detail.jsp").forward(req, resp);
        }
    }
}
