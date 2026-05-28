package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.TenderDAO;
import model.Bid;
import model.Tender;
import model.User;
import model.enums.TenderStatus;
import model.enums.UserRole;
import service.EmailNotificationService;
import service.FileUploadHandler;
import service.TenderLifecycleService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.CSRFTokenUtil;

@WebServlet("/supplier/submit-bid")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 20 * 1024 * 1024
)
public class SubmitBidServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SubmitBidServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private TenderLifecycleService lifecycleService;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            lifecycleService = new TenderLifecycleService();
            logger.info("DAOs initialized successfully in SubmitBidServlet");
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
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/supplier/tenders");
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
                    logger.info("Tender auto-closed during bid form view: " + tender.getReferenceNumber());
                }
            }

            // ============================================================
            // SERVER-SIDE CLOSING DATE CHECK (Exam Requirement)
            // ============================================================
            if (!tender.canSubmitBid()) {
                session.setAttribute("errorMessage", "This tender is not open for bidding.");
                resp.sendRedirect(req.getContextPath() + "/supplier/tender-detail?id=" + tenderId);
                return;
            }

            // ============================================================
            // ONE-BID-PER-TENDER CHECK (Exam Requirement)
            // ============================================================
            boolean hasBid = bidDAO.hasSupplierBid(tenderId, currentUser.getUserId());
            if (hasBid) {
                session.setAttribute("errorMessage", "You have already submitted a bid for this tender.");
                resp.sendRedirect(req.getContextPath() + "/supplier/bids");
                return;
            }

            req.setAttribute("tender", tender);
            req.getRequestDispatcher("/supplier/submit-bid.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/supplier/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading tender", e);
            session.setAttribute("errorMessage", "Error loading tender. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/supplier/tenders");
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
        if (currentUser.getRole() != UserRole.SUPPLIER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        List<String> errors = new ArrayList<>();
        Tender tender = null;

        try {
            int tenderId = Integer.parseInt(req.getParameter("tenderId"));
            int supplierId = currentUser.getUserId();

            tender = tenderDAO.findById(tenderId);
            if (tender == null) {
                errors.add("Tender not found.");
            } else {
                // ============================================================
                // AUTOMATIC CLOSING CHECK BEFORE PROCESSING BID
                // ============================================================
                if (tender.getStatus() == TenderStatus.OPEN) {
                    if (lifecycleService.shouldCloseTender(tenderId)) {
                        lifecycleService.closeTender(tenderId);
                        // Refresh tender after auto-close
                        tender = tenderDAO.findById(tenderId);
                        logger.info("Tender auto-closed during bid submission: " + tender.getReferenceNumber());
                    }
                }

                // ============================================================
                // SERVER-SIDE CLOSING DATE ENFORCEMENT (Exam Requirement)
                // ============================================================
                LocalDateTime now = LocalDateTime.now();
                if (tender.getClosingDateTime() != null && now.isAfter(tender.getClosingDateTime())) {
                    errors.add("This tender is closed. Bids are no longer accepted.");
                } else if (!tender.canSubmitBid()) {
                    errors.add("This tender is not open for bidding.");
                }
            }

            // ============================================================
            // ONE-BID-PER-TENDER ENFORCEMENT (Exam Requirement)
            // ============================================================
            boolean hasBid = bidDAO.hasSupplierBid(tenderId, supplierId);
            if (hasBid) {
                errors.add("You have already submitted a bid for this tender.");
            }

            // Validate bid amount
            String bidAmountStr = req.getParameter("bidAmount");
            BigDecimal bidAmount = null;
            if (bidAmountStr == null || bidAmountStr.trim().isEmpty()) {
                errors.add("Bid amount is required.");
            } else {
                try {
                    bidAmount = new BigDecimal(bidAmountStr);
                    if (bidAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.add("Bid amount must be greater than zero.");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Invalid bid amount format.");
                }
            }

            // Validate timeline
            String timelineStr = req.getParameter("timelineDays");
            int timelineDays = 0;
            if (timelineStr == null || timelineStr.trim().isEmpty()) {
                errors.add("Proposed timeline is required.");
            } else {
                try {
                    timelineDays = Integer.parseInt(timelineStr);
                    if (timelineDays < 1) {
                        errors.add("Timeline must be at least 1 day.");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Invalid timeline format.");
                }
            }

            // Validate technical statement
            String technicalStatement = req.getParameter("technicalStatement");
            if (technicalStatement == null || technicalStatement.trim().length() < 50) {
                errors.add("Technical compliance statement must be at least 50 characters.");
            } else if (technicalStatement.length() > 600) {
                errors.add("Technical compliance statement cannot exceed 600 characters.");
            }

            // Validate declarations
            String declaration = req.getParameter("declaration");
            String termsAgreement = req.getParameter("termsAgreement");

            if (!"on".equals(declaration) && !"true".equals(declaration)) {
                errors.add("You must confirm the declaration.");
            }

            if (!"on".equals(termsAgreement) && !"true".equals(termsAgreement)) {
                errors.add("You must agree to the terms and conditions.");
            }

            // Handle file upload
            String filePath = null;
            Part filePart = req.getPart("supportingDocument");
            if (filePart != null && filePart.getSize() > 0) {
                String uploadDir = getServletContext().getInitParameter("uploadDirectory");
                FileUploadHandler uploadHandler = new FileUploadHandler(uploadDir);
                filePath = uploadHandler.uploadBidDocument(filePart, tenderId, supplierId);
            }

            // If validation fails, return to form
            if (!errors.isEmpty()) {
                req.setAttribute("validationErrors", errors);
                req.setAttribute("tender", tender);
                req.setAttribute("bidAmount", bidAmountStr);
                req.setAttribute("timelineDays", timelineStr);
                req.setAttribute("technicalStatement", technicalStatement);
                req.getRequestDispatcher("/supplier/submit-bid.jsp").forward(req, resp);
                return;
            }

            // Create and save bid
            Bid bid = new Bid();
            bid.setTenderId(tenderId);
            bid.setSupplierId(supplierId);
            bid.setBidAmount(bidAmount);
            bid.setTechnicalComplianceStatement(technicalStatement.trim());
            bid.setProposedTimelineDays(timelineDays);
            bid.setSupportingDocumentPath(filePath);
            bid.setSubmittedAt(LocalDateTime.now());

            int bidId = bidDAO.create(bid);

            logger.info("Bid submitted: ID=" + bidId + ", Tender=" + tenderId + ", Supplier=" + supplierId);

            // ============================================================
            // SEND BID CONFIRMATION EMAIL
            // ============================================================
            try {
                String baseUrl = req.getScheme() + "://" + req.getServerName() + ":"
                        + req.getServerPort() + req.getContextPath();
                String bidsUrl = baseUrl + "/supplier/bids";

                EmailNotificationService.sendBidConfirmation(
                        currentUser.getEmail(),
                        currentUser.getFullName(),
                        tender.getReferenceNumber(),
                        tender.getTitle(),
                        bid.getFormattedBidAmount(),
                        bid.getFormattedSubmittedDateTime(),
                        bidsUrl
                );
                logger.info("Bid confirmation email sent to: " + currentUser.getEmail());
            } catch (Exception e) {
                logger.warning("Failed to send bid confirmation email: " + e.getMessage());
            }

            // ============================================================
            // PRG PATTERN - Redirect to GET (Exam Requirement)
            // ============================================================
            session.setAttribute("successMessage", "Your bid has been submitted successfully!");
            resp.sendRedirect(req.getContextPath() + "/supplier/my-bids");

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error submitting bid", e);
            errors.add("Database error. Please try again.");
            req.setAttribute("validationErrors", errors);
            req.setAttribute("tender", tender);
            req.getRequestDispatcher("/supplier/submit-bid.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error submitting bid", e);
            errors.add("System error: " + e.getMessage());
            req.setAttribute("validationErrors", errors);
            req.setAttribute("tender", tender);
            req.getRequestDispatcher("/supplier/submit-bid.jsp").forward(req, resp);
        }
    }
}
