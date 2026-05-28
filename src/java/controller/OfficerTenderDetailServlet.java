package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.EvaluationDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.impl.UserDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.EvaluationDAO;
import dao.interfaces.TenderDAO;
import dao.interfaces.UserDAO;
import model.Bid;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/officer/tender-detail")
public class OfficerTenderDetailServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OfficerTenderDetailServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private UserDAO userDAO;
    private EvaluationDAO evaluationDAO;
    private TenderLifecycleService lifecycleService;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            userDAO = new UserDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
            lifecycleService = new TenderLifecycleService();
            logger.info("DAOs initialized successfully in OfficerTenderDetailServlet");
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

            // ============================================================
            // AUTOMATIC CLOSING CHECK (Exam Requirement)
            // ============================================================
            if (tender.getStatus() == TenderStatus.OPEN) {
                if (lifecycleService.shouldCloseTender(tenderId)) {
                    lifecycleService.closeTender(tenderId);
                    tender = tenderDAO.findById(tenderId);
                    logger.info("Tender auto-closed on officer view: " + tender.getReferenceNumber());
                }
            }

            // Get creator name
            try {
                User creator = userDAO.findById(tender.getCreatedBy());
                if (creator != null) {
                    tender.setCreatedByName(creator.getFullName());
                }
            } catch (DAOException e) {
                tender.setCreatedByName("Unknown");
            }

            // Get bids for this tender
            List<Bid> bids = bidDAO.findByTenderId(tenderId);
            for (Bid bid : bids) {
                try {
                    User supplier = userDAO.findById(bid.getSupplierId());
                    if (supplier != null) {
                        bid.setSupplierName(supplier.getFullName());
                    }
                } catch (DAOException e) {
                    bid.setSupplierName("Unknown Supplier");
                }
            }

            // Get evaluation status
            boolean evaluationComplete = false;
            if (tender.getStatus() == TenderStatus.UNDER_EVALUATION) {
                evaluationComplete = evaluationDAO.isEvaluationComplete(tenderId);
            }

            // Get ranked bids if evaluated
            if (tender.getStatus() == TenderStatus.EVALUATED
                    || tender.getStatus() == TenderStatus.AWARDED) {
                List<Bid> rankedBids = bidDAO.findRankedBidsByTenderId(tenderId);
                req.setAttribute("rankedBids", rankedBids);
            }

            req.setAttribute("tender", tender);
            req.setAttribute("bids", bids);
            req.setAttribute("bidCount", bids.size());
            req.setAttribute("evaluationComplete", evaluationComplete);

            req.getRequestDispatcher("/officer/tender-detail.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading tender detail", e);
            session.setAttribute("errorMessage", "Error loading tender details. Please try again.");
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

        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String action = req.getParameter("action");
        String tenderIdParam = req.getParameter("id");

        if (action == null || tenderIdParam == null) {
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

            switch (action) {
                case "publish":
                    handlePublishTender(req, resp, tender, currentUser);
                    break;

                case "startEvaluation":
                    handleStartEvaluation(req, resp, tender, currentUser);
                    break;

                default:
                    resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);
            }

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Database error", e);
            session.setAttribute("errorMessage", "System error. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        }
    }

    /**
     * Handles publishing a tender from DRAFT to OPEN status.
     */
    private void handlePublishTender(HttpServletRequest req, HttpServletResponse resp,
            Tender tender, User officer)
            throws IOException, DAOException {

        HttpSession session = req.getSession();

        if (tender.getStatus() != TenderStatus.DRAFT) {
            session.setAttribute("errorMessage", "Only draft tenders can be published.");
            resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tender.getTenderId());
            return;
        }

        TenderLifecycleService.TransitionResult result
                = lifecycleService.publishTender(tender.getTenderId(), officer.getUserId());

        if (result.isSuccess()) {
            session.setAttribute("successMessage", result.getMessage());
        } else {
            session.setAttribute("errorMessage", result.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tender.getTenderId());
    }

    /**
     * Handles starting the evaluation process.
     */
    private void handleStartEvaluation(HttpServletRequest req, HttpServletResponse resp,
            Tender tender, User officer)
            throws IOException, DAOException {

        HttpSession session = req.getSession();

        if (tender.getStatus() != TenderStatus.CLOSED) {
            session.setAttribute("errorMessage", "Only closed tenders can move to evaluation.");
            resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tender.getTenderId());
            return;
        }

        // Check if there are bids
        int bidCount = bidDAO.countBidsByTenderId(tender.getTenderId());
        if (bidCount == 0) {
            session.setAttribute("errorMessage", "Cannot start evaluation: No bids submitted.");
            resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tender.getTenderId());
            return;
        }

        TenderLifecycleService.TransitionResult result
                = lifecycleService.startEvaluation(tender.getTenderId(), officer.getUserId());

        if (result.isSuccess()) {
            session.setAttribute("successMessage", result.getMessage());
        } else {
            session.setAttribute("errorMessage", result.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tender.getTenderId());
    }
}
