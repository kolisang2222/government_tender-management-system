package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.impl.UserDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.TenderDAO;
import dao.interfaces.UserDAO;
import model.Bid;
import model.Tender;
import model.User;
import model.enums.TenderStatus;
import model.enums.UserRole;
import service.EmailNotificationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.CSRFTokenUtil;

@WebServlet("/officer/award-tender")
public class AwardTenderServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AwardTenderServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            userDAO = new UserDAOImpl();
            logger.info("DAOs initialized successfully");
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

            if (tender.getStatus() != TenderStatus.EVALUATED) {
                session.setAttribute("errorMessage", "Only evaluated tenders can be awarded.");
                resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);
                return;
            }

            // Get ranked bids
            List<Bid> rankedBids = bidDAO.findRankedBidsByTenderId(tenderId);

            req.setAttribute("tender", tender);
            req.setAttribute("rankedBids", rankedBids);

            req.getRequestDispatcher("/officer/award-tender.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading tender for award", e);
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

        User officer = (User) session.getAttribute("user");
        if (officer.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        try {
            int tenderId = Integer.parseInt(req.getParameter("tenderId"));
            int winningBidId = Integer.parseInt(req.getParameter("winningBidId"));
            String awardValueStr = req.getParameter("awardValue");
            String justification = req.getParameter("justification");

            // Validation
            if (awardValueStr == null || awardValueStr.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Award value is required.");
                resp.sendRedirect(req.getContextPath() + "/officer/award-tender?id=" + tenderId);
                return;
            }

            if (justification == null || justification.trim().length() < 20) {
                session.setAttribute("errorMessage", "Justification must be at least 20 characters.");
                resp.sendRedirect(req.getContextPath() + "/officer/award-tender?id=" + tenderId);
                return;
            }

            BigDecimal awardValue = new BigDecimal(awardValueStr);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/officer/tenders");
                return;
            }

            // Award the tender
            boolean awarded = tenderDAO.awardTender(tenderId, winningBidId, awardValue, justification);

            if (awarded) {
                // Update tender object with award details
                tender.setStatus(TenderStatus.AWARDED);
                tender.setAwardedBidId(winningBidId);
                tender.setAwardedValue(awardValue);
                tender.setAwardJustification(justification);
                tender.setAwardDate(LocalDateTime.now());

                // ============================================================
                // SEND EMAIL NOTIFICATIONS TO ALL BIDDERS
                // ============================================================
                sendAwardNotifications(tender, winningBidId, req);

                logger.info("Tender awarded: " + tender.getReferenceNumber()
                        + " to bid ID: " + winningBidId + " by " + officer.getEmail());

                session.setAttribute("successMessage",
                        "Tender " + tender.getReferenceNumber() + " awarded successfully. "
                        + "Email notifications sent to all bidders.");
            } else {
                session.setAttribute("errorMessage", "Failed to award tender.");
            }

            resp.sendRedirect(req.getContextPath() + "/officer/tenders");

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid input values.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error awarding tender", e);
            session.setAttribute("errorMessage", "Database error. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        }
    }

    /**
     * Sends award notification emails to all suppliers who bid on this tender.
     */
    private void sendAwardNotifications(Tender tender, int winningBidId, HttpServletRequest req) {
        try {
            // Get all bids for this tender
            List<Bid> allBids = bidDAO.findByTenderId(tender.getTenderId());

            if (allBids.isEmpty()) {
                logger.info("No bids to notify for tender: " + tender.getReferenceNumber());
                return;
            }

            // Build award notice URL
            String baseUrl = req.getScheme() + "://" + req.getServerName() + ":"
                    + req.getServerPort() + req.getContextPath();
            String awardNoticeUrl = baseUrl + "/supplier/award-notice?id=" + tender.getTenderId();

            int successCount = 0;
            int failCount = 0;

            // Send email to each supplier who bid
            for (Bid bid : allBids) {
                try {
                    User supplier = userDAO.findById(bid.getSupplierId());

                    if (supplier != null && supplier.getEmail() != null) {
                        boolean won = (bid.getBidId() == winningBidId);

                        boolean sent = EmailNotificationService.sendAwardNotification(
                                supplier.getEmail(),
                                supplier.getFullName(),
                                tender.getReferenceNumber(),
                                tender.getTitle(),
                                won,
                                won ? tender.getFormattedAwardedValue() : null,
                                won ? tender.getFormattedAwardDate() : null,
                                won ? tender.getAwardJustification() : null,
                                awardNoticeUrl
                        );

                        if (sent) {
                            successCount++;
                            logger.info("Award email sent to: " + supplier.getEmail()
                                    + " (Winner: " + won + ")");
                        } else {
                            failCount++;
                            logger.warning("Failed to send award email to: " + supplier.getEmail());
                        }
                    }
                } catch (Exception e) {
                    failCount++;
                    logger.warning("Error sending award email for bid ID " + bid.getBidId() + ": " + e.getMessage());
                }
            }

            logger.info("Award notifications completed: " + successCount + " sent, " + failCount + " failed");

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error fetching bids for award notifications", e);
        }
    }
}
