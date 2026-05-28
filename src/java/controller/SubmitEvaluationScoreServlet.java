package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.EvaluationDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.EvaluationDAO;
import dao.interfaces.TenderDAO;
import model.Bid;
import model.EvaluationScore;
import model.Tender;
import model.User;
import model.enums.TenderStatus;
import model.enums.UserRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/evaluator/submit-score")
public class SubmitEvaluationScoreServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SubmitEvaluationScoreServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private EvaluationDAO evaluationDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
            logger.info("DAOs initialized successfully");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize DAOs", e);
            throw new ServletException("Cannot initialize DAOs", e);
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
        if (currentUser.getRole() != UserRole.EVALUATION_COMMITTEE) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        try {
            int tenderId = Integer.parseInt(req.getParameter("tenderId"));
            int bidId = Integer.parseInt(req.getParameter("bidId"));
            double technicalScore = Double.parseDouble(req.getParameter("technicalScore"));

            // Validate technical score
            if (technicalScore < 0 || technicalScore > 100) {
                session.setAttribute("errorMessage", "Technical score must be between 0 and 100.");
                resp.sendRedirect(req.getContextPath() + "/evaluator/evaluation-panel?id=" + tenderId);
                return;
            }

            Tender tender = tenderDAO.findById(tenderId);
            if (tender == null || tender.getStatus() != TenderStatus.UNDER_EVALUATION) {
                session.setAttribute("errorMessage", "Tender is not under evaluation.");
                resp.sendRedirect(req.getContextPath() + "/evaluator/tenders");
                return;
            }

            // Check if evaluator already scored this bid
            boolean alreadyScored = evaluationDAO.hasEvaluatorScoredBid(currentUser.getUserId(), bidId);

            // Get bid details for auto-calculation
            Bid bid = bidDAO.findById(bidId);
            BigDecimal lowestBidAmount = bidDAO.findLowestBidAmount(tenderId);
            int shortestTimeline = bidDAO.findShortestTimeline(tenderId);

            // Calculate scores
            double priceScore = EvaluationScore.calculatePriceScore(lowestBidAmount, bid.getBidAmount());
            double timelineScore = EvaluationScore.calculateTimelineScore(shortestTimeline, bid.getProposedTimelineDays());
            double weightedTotal = EvaluationScore.calculateWeightedTotal(priceScore, technicalScore, timelineScore);

            // Create or update evaluation score
            EvaluationScore score = new EvaluationScore();
            score.setBidId(bidId);
            score.setEvaluatorId(currentUser.getUserId());
            score.setTechnicalComplianceScore(technicalScore);
            score.setPriceScore(priceScore);
            score.setTimelineScore(timelineScore);
            score.setWeightedTotal(weightedTotal);

            if (alreadyScored) {
                // Update existing score
                List<EvaluationScore> existingScores = evaluationDAO.findByEvaluatorAndTender(
                        currentUser.getUserId(), tenderId);
                for (EvaluationScore es : existingScores) {
                    if (es.getBidId() == bidId) {
                        score.setScoreId(es.getScoreId());
                        evaluationDAO.updateScore(score);
                        break;
                    }
                }
                logger.info("Score updated for bid " + bidId + " by evaluator " + currentUser.getUserId());
            } else {
                // Submit new score
                evaluationDAO.submitScore(score);
                logger.info("Score submitted for bid " + bidId + " by evaluator " + currentUser.getUserId());
            }

            // ============================================================
            // CHECK IF EVALUATION IS COMPLETE - AUTO TRANSITION
            // ============================================================
            boolean evaluationComplete = evaluationDAO.isEvaluationComplete(tenderId);

            if (evaluationComplete) {
                tenderDAO.updateStatus(tenderId, TenderStatus.EVALUATED);
                logger.info("Tender " + tender.getReferenceNumber() + " auto-transitioned to EVALUATED");
                session.setAttribute("successMessage",
                        "Score submitted. All evaluators have completed scoring. Tender is now EVALUATED.");
            } else {
                session.setAttribute("successMessage", "Score submitted successfully.");
            }

            // PRG Pattern
            resp.sendRedirect(req.getContextPath() + "/evaluator/evaluation-panel?id=" + tenderId);

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid input values.");
            resp.sendRedirect(req.getContextPath() + "/evaluator/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error submitting score", e);
            session.setAttribute("errorMessage", "Database error. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/evaluator/tenders");
        }
    }
}
