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
import service.EvaluationService;

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

/**
 * Servlet responsible for managing the bid evaluation process. Handles starting
 * evaluation, displaying evaluation panel, submitting scores, and viewing
 * evaluation results.
 *
 * @author kolisang
 * @version 1.0
 */
@WebServlet("/officer/start-evaluation")
public class OfficerStartEvaluationServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OfficerStartEvaluationServlet.class.getName());

    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private UserDAO userDAO;
    private EvaluationDAO evaluationDAO;
    private EvaluationService evaluationService;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            userDAO = new UserDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
            evaluationService = new EvaluationService();
            logger.info("Evaluation servlet initialized successfully");
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

        // Allow both Procurement Officer and Evaluation Committee Members
        if (currentUser.getRole() != UserRole.PROCUREMENT_OFFICER
                && currentUser.getRole() != UserRole.EVALUATION_COMMITTEE) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String action = req.getParameter("action");
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

            // Determine action based on parameter
            if ("view".equals(action)) {
                // View evaluation panel for scoring
                showEvaluationPanel(req, resp, tender, currentUser);
            } else if ("results".equals(action)) {
                // View evaluation results
                showEvaluationResults(req, resp, tender, currentUser);
            } else {
                // Default - show evaluation dashboard
                showEvaluationDashboard(req, resp, tender, currentUser);
            }

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid tender ID format", e);
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading evaluation", e);
            session.setAttribute("errorMessage", "Error loading evaluation data. Please try again.");
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

        if (currentUser.getRole() != UserRole.PROCUREMENT_OFFICER
                && currentUser.getRole() != UserRole.EVALUATION_COMMITTEE) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String action = req.getParameter("action");
        String tenderIdParam = req.getParameter("tenderId");

        if ("submitScores".equals(action)) {
            try {
                handleSubmitScores(req, resp, currentUser);
            } catch (DAOException ex) {
                logger.log(Level.SEVERE, "Error submitting scores", ex);
                session.setAttribute("errorMessage", "Error submitting scores. Please try again.");
                resp.sendRedirect(req.getContextPath() + "/officer/tenders");
            }
        } else if ("startEvaluation".equals(action)) {
            handleStartEvaluation(req, resp, currentUser, tenderIdParam);
        } else {
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        }
    }

    /**
     * Displays the evaluation dashboard showing all tenders in evaluation.
     */
    private void showEvaluationDashboard(HttpServletRequest req, HttpServletResponse resp,
            Tender tender, User currentUser) throws ServletException, IOException, DAOException {

        // Get all bids for this tender
        List<Bid> bids = bidDAO.findByTenderId(tender.getTenderId());

        // Get all evaluators for progress calculation
        List<User> evaluators = userDAO.findByRole(UserRole.EVALUATION_COMMITTEE);
        int totalEvaluators = evaluators.size();
        int submittedScores = evaluationDAO.countSubmittedScores(tender.getTenderId());
        int expectedScores = bids.size() * totalEvaluators;
        int completionPercentage = expectedScores > 0 ? (submittedScores * 100) / expectedScores : 0;

        // For each bid, check if current user has already scored it
        for (Bid bid : bids) {
            boolean hasScored = evaluationDAO.hasEvaluatorScored(
                    currentUser.getUserId(), bid.getBidId());
            bid.setHasCurrentUserScored(hasScored);

            // Get supplier name
            User supplier = userDAO.findById(bid.getSupplierId());
            if (supplier != null) {
                bid.setSupplierName(supplier.getFullName());
            }
        }

        // Check if evaluation is complete
        boolean evaluationComplete = evaluationDAO.isEvaluationComplete(tender.getTenderId());

        // Create progress map for JSP
        java.util.Map<String, Integer> evaluationProgress = new java.util.HashMap<>();
        evaluationProgress.put("totalBids", bids.size());
        evaluationProgress.put("totalEvaluators", totalEvaluators);
        evaluationProgress.put("submittedScores", submittedScores);
        evaluationProgress.put("expectedScores", expectedScores);
        evaluationProgress.put("remainingScores", expectedScores - submittedScores);

        req.setAttribute("tender", tender);
        req.setAttribute("bids", bids);
        req.setAttribute("bidCount", bids.size());
        req.setAttribute("evaluationComplete", evaluationComplete);
        req.setAttribute("userRole", currentUser.getRole().name());
        req.setAttribute("totalEvaluators", totalEvaluators);
        req.setAttribute("submittedScores", submittedScores);
        req.setAttribute("expectedScores", expectedScores);
        req.setAttribute("completionPercentage", completionPercentage);
        req.setAttribute("evaluationProgress", evaluationProgress);

        req.getRequestDispatcher("/officer/start-evaluation.jsp").forward(req, resp);
    }

    /**
     * Shows the evaluation panel for scoring a specific bid.
     */
    private void showEvaluationPanel(HttpServletRequest req, HttpServletResponse resp,
            Tender tender, User currentUser) throws ServletException, IOException, DAOException {

        String bidIdParam = req.getParameter("bidId");
        if (bidIdParam == null || bidIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/officer/start-evaluation?id=" + tender.getTenderId());
            return;
        }

        int bidId = Integer.parseInt(bidIdParam);
        Bid bid = bidDAO.findById(bidId);

        if (bid == null) {
            req.getSession().setAttribute("errorMessage", "Bid not found.");
            resp.sendRedirect(req.getContextPath() + "/officer/start-evaluation?id=" + tender.getTenderId());
            return;
        }

        // Check if user has already scored this bid
        boolean hasScored = evaluationDAO.hasEvaluatorScored(currentUser.getUserId(), bidId);
        model.EvaluationScore existingScore = null;
        if (hasScored) {
            // Load existing scores
            existingScore = evaluationDAO.findByEvaluatorAndBid(
                    currentUser.getUserId(), bidId);
            req.setAttribute("existingScore", existingScore);
        }

        // Calculate system-generated scores
        List<Bid> allBids = bidDAO.findByTenderId(tender.getTenderId());
        double lowestBid = allBids.stream()
                .mapToDouble(b -> b.getBidAmount().doubleValue())
                .min()
                .orElse(bid.getBidAmount().doubleValue());

        int shortestTimeline = allBids.stream()
                .mapToInt(Bid::getProposedTimelineDays)
                .min()
                .orElse(bid.getProposedTimelineDays());

        // Calculate automatic scores
        double priceScore = (lowestBid / bid.getBidAmount().doubleValue()) * 100;
        double timelineScore = ((double) shortestTimeline / bid.getProposedTimelineDays()) * 100;

        req.setAttribute("tender", tender);
        req.setAttribute("bid", bid);
        req.setAttribute("priceScore", Math.round(priceScore * 100.0) / 100.0);
        req.setAttribute("timelineScore", Math.round(timelineScore * 100.0) / 100.0);
        req.setAttribute("lowestBid", lowestBid);
        req.setAttribute("shortestTimeline", shortestTimeline);
        req.setAttribute("hasScored", hasScored);

        req.getRequestDispatcher("/officer/evaluation-panel.jsp").forward(req, resp);
    }

    /**
     * Shows the evaluation results with ranked bids.
     */
    private void showEvaluationResults(HttpServletRequest req, HttpServletResponse resp,
            Tender tender, User currentUser) throws ServletException, IOException, DAOException {

        // Get ranked bids by final score
        List<Bid> rankedBids = bidDAO.findRankedBidsByTenderId(tender.getTenderId());

        // Calculate overall evaluation status
        int totalEvaluators = evaluationDAO.countEvaluatorsForTender(tender.getTenderId());
        int completedEvaluators = evaluationDAO.countCompletedEvaluators(tender.getTenderId());

        req.setAttribute("tender", tender);
        req.setAttribute("rankedBids", rankedBids);
        req.setAttribute("totalEvaluators", totalEvaluators);
        req.setAttribute("completedEvaluators", completedEvaluators);
        req.setAttribute("allEvaluationsComplete",
                totalEvaluators > 0 && totalEvaluators == completedEvaluators);

        req.getRequestDispatcher("/officer/evaluation-results.jsp").forward(req, resp);
    }

    /**
     * Handles submitting scores for a bid.
     */
    private void handleSubmitScores(HttpServletRequest req, HttpServletResponse resp,
            User currentUser) throws IOException, ServletException, DAOException {

        HttpSession session = req.getSession();

        try {
            String tenderIdParam = req.getParameter("tenderId");
            String bidIdParam = req.getParameter("bidId");
            String technicalScoreParam = req.getParameter("technicalScore");

            if (tenderIdParam == null || bidIdParam == null || technicalScoreParam == null) {
                session.setAttribute("errorMessage", "Missing required parameters.");
                resp.sendRedirect(req.getContextPath() + "/officer/start-evaluation");
                return;
            }

            int tenderId = Integer.parseInt(tenderIdParam);
            int bidId = Integer.parseInt(bidIdParam);
            int technicalScore = Integer.parseInt(technicalScoreParam);

            // Validate technical score
            if (technicalScore < 0 || technicalScore > 100) {
                session.setAttribute("errorMessage", "Technical score must be between 0 and 100.");
                resp.sendRedirect(req.getContextPath() + "/officer/start-evaluation?id=" + tenderId);
                return;
            }

            // Check if user already scored this bid
            if (evaluationDAO.hasEvaluatorScored(currentUser.getUserId(), bidId)) {
                session.setAttribute("errorMessage", "You have already scored this bid.");
                resp.sendRedirect(req.getContextPath() + "/officer/start-evaluation?id=" + tenderId);
                return;
            }

            // Calculate and save evaluation scores
            evaluationService.saveEvaluationScores(
                    tenderId, bidId, currentUser.getUserId(), technicalScore);

            session.setAttribute("successMessage", "Scores submitted successfully!");

            // Check if all evaluations are complete
            if (evaluationService.isAllEvaluationsComplete(tenderId)) {
                // Automatically transition to EVALUATED status
                evaluationService.completeEvaluation(tenderId);
                session.setAttribute("successMessage",
                        "All evaluations complete! Tender has been moved to EVALUATED status.");
            }

            resp.sendRedirect(req.getContextPath() + "/officer/start-evaluation?id=" + tenderId);

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid number format", e);
            session.setAttribute("errorMessage", "Invalid score format.");
            resp.sendRedirect(req.getContextPath() + "/officer/start-evaluation");
        }
    }

    /**
     * Handles starting the evaluation process for a tender.
     */
    private void handleStartEvaluation(HttpServletRequest req, HttpServletResponse resp,
            User currentUser, String tenderIdParam) throws IOException {

        HttpSession session = req.getSession();

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/officer/tenders");
                return;
            }

            if (tender.getStatus() != TenderStatus.CLOSED) {
                session.setAttribute("errorMessage", "Only closed tenders can be evaluated.");
                resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);
                return;
            }

            // Check if there are bids
            int bidCount = bidDAO.countBidsByTenderId(tenderId);
            if (bidCount == 0) {
                session.setAttribute("errorMessage", "Cannot start evaluation: No bids submitted.");
                resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);
                return;
            }

            // Transition to UNDER_EVALUATION status
            boolean success = evaluationService.startEvaluation(tenderId, currentUser.getUserId());

            if (success) {
                session.setAttribute("successMessage", "Evaluation process started successfully!");
                resp.sendRedirect(req.getContextPath() + "/officer/start-evaluation?id=" + tenderId);
            } else {
                session.setAttribute("errorMessage", "Failed to start evaluation.");
                resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);
            }

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid tender ID", e);
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error starting evaluation", e);
            session.setAttribute("errorMessage", "Error starting evaluation. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        }
    }
}
