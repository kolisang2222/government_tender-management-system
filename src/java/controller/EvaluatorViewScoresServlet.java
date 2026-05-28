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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.enums.TenderStatus;

@WebServlet("/evaluator/view-scores")
public class EvaluatorViewScoresServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(EvaluatorViewScoresServlet.class.getName());

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
            resp.sendRedirect(req.getContextPath() + "/evaluator/dashboard");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/evaluator/dashboard");
                return;
            }

            // Get evaluator's scores for this tender
            List<EvaluationScore> scores = evaluationDAO.findByEvaluatorAndTender(
                    currentUser.getUserId(), tenderId);

            // Add supplier names to scores
            for (EvaluationScore score : scores) {
                Bid bid = bidDAO.findById(score.getBidId());
                if (bid != null) {
                    User supplier = userDAO.findById(bid.getSupplierId());
                    if (supplier != null) {
                        score.setSupplierName(supplier.getFullName());
                    }
                    score.setBidAmount(bid.getBidAmount());
                }
            }

            // Calculate average score
            double averageScore = 0.0;
            if (!scores.isEmpty()) {
                double total = 0.0;
                for (EvaluationScore score : scores) {
                    total += score.getWeightedTotal();
                }
                averageScore = Math.round((total / scores.size()) * 100.0) / 100.0;
            }

            // Get consolidated results if evaluation is complete
            List<Bid> consolidatedScores = null;
            if (tender.getStatus() == TenderStatus.EVALUATED || tender.getStatus() == TenderStatus.AWARDED) {
                consolidatedScores = bidDAO.findRankedBidsByTenderId(tenderId);
            }

            req.setAttribute("tender", tender);
            req.setAttribute("scores", scores);
            req.setAttribute("averageScore", String.format("%.2f", averageScore));
            req.setAttribute("consolidatedScores", consolidatedScores);

            req.getRequestDispatcher("/evaluator/view-scores.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid tender ID format", e);
            resp.sendRedirect(req.getContextPath() + "/evaluator/dashboard");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading scores", e);
            session.setAttribute("errorMessage", "Error loading scores. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/evaluator/dashboard");
        }
    }
}
