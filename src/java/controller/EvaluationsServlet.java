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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/officer/evaluations")
public class EvaluationsServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(EvaluationsServlet.class.getName());
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
        } catch (DAOException e) {
            throw new ServletException("Failed to initialize DAOs", e);
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
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            // Get all tenders that need evaluation
            List<Tender> allTenders = tenderDAO.findAll();

            List<Tender> pendingTenders = new ArrayList<>();
            List<Tender> inProgressTenders = new ArrayList<>();
            List<Tender> completedTenders = new ArrayList<>();

            List<User> evaluators = userDAO.findByRole(UserRole.EVALUATION_COMMITTEE);
            int totalEvaluators = evaluators.size();

            for (Tender tender : allTenders) {
                // Skip tenders not in evaluation-related statuses
                if (tender.getStatus() == TenderStatus.DRAFT
                        || tender.getStatus() == TenderStatus.OPEN) {
                    continue;
                }

                int bidCount = bidDAO.countBidsByTenderId(tender.getTenderId());
                tender.setBidCount(bidCount);

                if (tender.getStatus() == TenderStatus.CLOSED && bidCount > 0) {
                    pendingTenders.add(tender);
                } else if (tender.getStatus() == TenderStatus.UNDER_EVALUATION) {
                    // ============================================================
                    // FIX: Check if evaluation is complete and auto-transition
                    // ============================================================
                    int scoresSubmitted = evaluationDAO.countSubmittedScores(tender.getTenderId());
                    int expectedScores = bidCount * totalEvaluators;

                    // Check if all evaluations are complete
                    boolean allEvaluationsComplete = (expectedScores > 0 && scoresSubmitted >= expectedScores);

                    if (allEvaluationsComplete) {
                        // Auto-transition to EVALUATED status
                        tender.setStatus(TenderStatus.EVALUATED);
                        tender.setEvaluationCompletedDate(java.time.LocalDateTime.now());
                        tenderDAO.update(tender);
                        logger.info("Tender " + tender.getReferenceNumber() + " auto-transitioned to EVALUATED");

                        // Add to completed tenders instead
                        tender.setRankedBids(bidDAO.findRankedBidsByTenderId(tender.getTenderId()));
                        completedTenders.add(tender);
                        continue; // Skip adding to inProgressTenders
                    }

                    // Add progress information for in-progress tenders
                    tender.setScoresSubmitted(scoresSubmitted);
                    tender.setExpectedScores(expectedScores);
                    tender.setProgressPercent(expectedScores > 0
                            ? (scoresSubmitted * 100) / expectedScores : 0);

                    // Get evaluator progress
                    List<Map<String, Object>> evaluatorProgress = new ArrayList<>();
                    for (User evaluator : evaluators) {
                        Map<String, Object> progress = new HashMap<>();
                        progress.put("name", evaluator.getFullName());

                        List scores = evaluationDAO.findByEvaluatorAndTender(
                                evaluator.getUserId(), tender.getTenderId());
                        int evaluatorScores = scores.size();

                        progress.put("scoresSubmitted", evaluatorScores);
                        progress.put("totalBids", bidCount);
                        progress.put("completed", evaluatorScores >= bidCount);
                        evaluatorProgress.add(progress);
                    }
                    tender.setEvaluatorProgress(evaluatorProgress);

                    inProgressTenders.add(tender);
                } else if (tender.getStatus() == TenderStatus.EVALUATED
                        || tender.getStatus() == TenderStatus.AWARDED) {
                    // Get ranked bids
                    tender.setRankedBids(bidDAO.findRankedBidsByTenderId(tender.getTenderId()));
                    completedTenders.add(tender);
                }
            }

            req.setAttribute("pendingTenders", pendingTenders);
            req.setAttribute("inProgressTenders", inProgressTenders);
            req.setAttribute("completedTenders", completedTenders);

            req.getRequestDispatcher("/officer/evaluations.jsp").forward(req, resp);

        } catch (DAOException e) {
            logger.severe("Error loading evaluations: " + e.getMessage());
            req.setAttribute("errorMessage", "Error loading evaluations. Please try again.");
            req.getRequestDispatcher("/officer/evaluations.jsp").forward(req, resp);
        }
    }
}
