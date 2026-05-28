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
import model.enums.UserRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/evaluator/award-notice")
public class EvaluatorAwardNoticeServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(EvaluatorAwardNoticeServlet.class.getName());

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
            resp.sendRedirect(req.getContextPath() + "/evaluator/results");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/evaluator/results");
                return;
            }

            // Get ranked bids
            List<Bid> rankedBids = evaluationDAO.findRankedBidsByTenderId(tenderId);

            // Get evaluator's scores for this tender
            List<EvaluationScore> evaluatorScores = evaluationDAO.findByEvaluatorAndTender(
                    currentUser.getUserId(), tenderId);

            // Count scored bids
            int scoredBidsCount = evaluatorScores.size();
            int totalBids = bidDAO.countBidsByTenderId(tenderId);

            // Calculate evaluator's average score
            String evaluatorAvgScore = "N/A";
            if (!evaluatorScores.isEmpty()) {
                double avg = evaluatorScores.stream()
                        .mapToDouble(EvaluationScore::getWeightedTotal)
                        .average()
                        .orElse(0);
                evaluatorAvgScore = String.format("%.2f", avg);
            }

            // Get total number of evaluators
            List<User> evaluators = userDAO.findByRole(UserRole.EVALUATION_COMMITTEE);
            int totalEvaluators = evaluators.size();

            // Get evaluation completed date
            String evaluationCompletedDate = "N/A";
            if (tender.getEvaluationCompletedDate() != null) {
                evaluationCompletedDate = tender.getEvaluationCompletedDate()
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            }

            // Set attributes
            req.setAttribute("tender", tender);
            req.setAttribute("rankedBids", rankedBids);
            req.setAttribute("evaluatorScores", evaluatorScores);
            req.setAttribute("scoredBidsCount", scoredBidsCount);
            req.setAttribute("totalBids", totalBids);
            req.setAttribute("evaluatorAvgScore", evaluatorAvgScore);
            req.setAttribute("totalEvaluators", totalEvaluators);
            req.setAttribute("evaluationCompletedDate", evaluationCompletedDate);
            req.setAttribute("currentDate", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

            req.getRequestDispatcher("/evaluator/award-notice.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid tender ID format", e);
            resp.sendRedirect(req.getContextPath() + "/evaluator/results");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading award notice", e);
            session.setAttribute("errorMessage", "Error loading award notice. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/evaluator/results");
        }
    }
}
