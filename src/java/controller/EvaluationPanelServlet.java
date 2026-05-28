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
import model.EvaluationScore;
import model.Tender;
import model.User;
import model.enums.TenderStatus;
import model.enums.UserRole;
import service.ScoringService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/evaluator/evaluation-panel")
public class EvaluationPanelServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(EvaluationPanelServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private EvaluationDAO evaluationDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
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
        if (currentUser.getRole() != UserRole.EVALUATION_COMMITTEE
                && currentUser.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String tenderIdParam = req.getParameter("id");
        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/evaluator/tenders");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/evaluator/tenders");
                return;
            }

            // Get all bids for this tender
            List<Bid> bids = bidDAO.findByTenderId(tenderId);

            // Get all evaluators
            List<User> allEvaluators = userDAO.findByRole(UserRole.EVALUATION_COMMITTEE);

            // ============================================================
            // CHECK IF CURRENT EVALUATOR HAS SUBMITTED ALL SCORES
            // ============================================================
            boolean hasSubmittedAllScores = true;
            List<EvaluationScore> myScores = evaluationDAO.findByEvaluatorAndTender(
                    currentUser.getUserId(), tenderId);

            // Check if evaluator has scored every bid
            Set<Integer> scoredBidIds = new HashSet<>();
            for (EvaluationScore score : myScores) {
                scoredBidIds.add(score.getBidId());
            }

            for (Bid bid : bids) {
                if (!scoredBidIds.contains(bid.getBidId())) {
                    hasSubmittedAllScores = false;
                    break;
                }
            }

            // ============================================================
            // BUILD EVALUATION DATA WITH VISIBILITY RULES
            // ============================================================
            List<Map<String, Object>> evaluationData = new ArrayList<>();

            // Get lowest bid and shortest timeline for auto-calculations
            BigDecimal lowestBidAmount = bidDAO.findLowestBidAmount(tenderId);
            int shortestTimeline = bidDAO.findShortestTimeline(tenderId);

            for (Bid bid : bids) {
                Map<String, Object> bidData = new HashMap<>();
                bidData.put("bidId", bid.getBidId());
                bidData.put("supplierName", bid.getSupplierName());
                bidData.put("bidAmount", bid.getFormattedBidAmount());
                bidData.put("bidAmountRaw", bid.getBidAmount());
                bidData.put("timelineDays", bid.getProposedTimelineDays());
                bidData.put("technicalStatement", bid.getTechnicalComplianceStatement());

                // Get current evaluator's score for this bid (if exists)
                EvaluationScore myScore = null;
                for (EvaluationScore score : myScores) {
                    if (score.getBidId() == bid.getBidId()) {
                        myScore = score;
                        break;
                    }
                }

                if (myScore != null) {
                    bidData.put("myTechnicalScore", myScore.getTechnicalComplianceScore());
                    bidData.put("myPriceScore", myScore.getFormattedPriceScore());
                    bidData.put("myTimelineScore", myScore.getFormattedTimelineScore());
                    bidData.put("myWeightedTotal", myScore.getFormattedWeightedTotal());
                    bidData.put("hasScored", true);
                } else {
                    bidData.put("myTechnicalScore", null);
                    bidData.put("hasScored", false);
                }

                // ============================================================
                // VISIBILITY RULE: Only show other scores if evaluator has
                // submitted ALL their scores for this tender
                // ============================================================
                if (hasSubmittedAllScores) {
                    // Get all evaluators' scores for this bid
                    List<EvaluationScore> allScores = evaluationDAO.findByBidId(bid.getBidId());

                    Map<Integer, EvaluationScore> scoresByEvaluator = new HashMap<>();
                    for (EvaluationScore score : allScores) {
                        scoresByEvaluator.put(score.getEvaluatorId(), score);
                    }

                    List<Map<String, Object>> otherScores = new ArrayList<>();
                    double totalWeighted = 0;
                    int scoreCount = 0;

                    for (User evaluator : allEvaluators) {
                        EvaluationScore score = scoresByEvaluator.get(evaluator.getUserId());

                        if (score != null) {
                            Map<String, Object> evaluatorScore = new HashMap<>();
                            evaluatorScore.put("evaluatorName", evaluator.getFullName());
                            evaluatorScore.put("technicalScore", score.getFormattedTechnicalScore());
                            evaluatorScore.put("priceScore", score.getFormattedPriceScore());
                            evaluatorScore.put("timelineScore", score.getFormattedTimelineScore());
                            evaluatorScore.put("weightedTotal", score.getFormattedWeightedTotal());
                            evaluatorScore.put("isCurrentUser", evaluator.getUserId() == currentUser.getUserId());
                            otherScores.add(evaluatorScore);

                            totalWeighted += score.getWeightedTotal();
                            scoreCount++;
                        }
                    }

                    bidData.put("otherScores", otherScores);

                    // Calculate average score
                    if (scoreCount > 0) {
                        double averageScore = totalWeighted / scoreCount;
                        bidData.put("averageScore", String.format("%.2f", averageScore));
                    } else {
                        bidData.put("averageScore", "N/A");
                    }

                    bidData.put("showOtherScores", true);
                } else {
                    bidData.put("otherScores", new ArrayList<>());
                    bidData.put("averageScore", "Submit all scores to view");
                    bidData.put("showOtherScores", false);
                }

                // Auto-calculated scores (visible immediately)
                double priceScore = EvaluationScore.calculatePriceScore(lowestBidAmount, bid.getBidAmount());
                double timelineScore = EvaluationScore.calculateTimelineScore(shortestTimeline, bid.getProposedTimelineDays());

                bidData.put("autoPriceScore", String.format("%.2f", priceScore));
                bidData.put("autoTimelineScore", String.format("%.2f", timelineScore));

                evaluationData.add(bidData);
            }

            // Get procurement officers (can always see all scores)
            boolean isOfficer = currentUser.getRole() == UserRole.PROCUREMENT_OFFICER;

            req.setAttribute("tender", tender);
            req.setAttribute("evaluationData", evaluationData);
            req.setAttribute("hasSubmittedAllScores", hasSubmittedAllScores);
            req.setAttribute("isOfficer", isOfficer);
            req.setAttribute("bidCount", bids.size());
            req.setAttribute("evaluatorCount", allEvaluators.size());

            req.getRequestDispatcher("/evaluator/evaluation-panel.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/evaluator/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading evaluation panel", e);
            session.setAttribute("errorMessage", "Error loading evaluation panel. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/evaluator/tenders");
        }
    }
}
