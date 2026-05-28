package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.EvaluationDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.EvaluationDAO;
import dao.interfaces.TenderDAO;
import model.EvaluationScore;
import model.Tender;
import model.User;
import model.enums.TenderCategory;
import model.enums.UserRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/evaluator/evaluations")
public class EvaluatorEvaluationsServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(EvaluatorEvaluationsServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private EvaluationDAO evaluationDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
            logger.info("DAOs initialized successfully in EvaluatorEvaluationsServlet");
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
        if (currentUser.getRole() != UserRole.EVALUATION_COMMITTEE) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        
        try {
            int evaluatorId = currentUser.getUserId();
            
            String statusParam = req.getParameter("status");
            String categoryParam = req.getParameter("category");
            
            TenderCategory category = null;
            if (categoryParam != null && !categoryParam.trim().isEmpty()) {
                try {
                    category = TenderCategory.valueOf(categoryParam);
                } catch (IllegalArgumentException e) {
                    logger.warning("Invalid category parameter: " + categoryParam);
                }
            }
            
            // Get all tenders that are in evaluation stages
            List<Tender> allTenders = tenderDAO.findAll();
            List<Tender> evaluations = new ArrayList<>();
            
            int totalAssigned = 0;
            int inProgressCount = 0;
            int completedCount = 0;
            int totalScoresSubmitted = 0;
            
            for (Tender tender : allTenders) {
                if (tender.getStatus() == model.enums.TenderStatus.DRAFT || 
                    tender.getStatus() == model.enums.TenderStatus.OPEN) {
                    continue;
                }
                
                // Apply category filter
                if (category != null && tender.getCategory() != category) {
                    continue;
                }
                
                int bidCount = bidDAO.countBidsByTenderId(tender.getTenderId());
                tender.setBidCount(bidCount);
                
                // Get evaluator's scores for this tender
                List<EvaluationScore> evaluatorScores = evaluationDAO.findByEvaluatorAndTender(evaluatorId, tender.getTenderId());
                int scoredBids = evaluatorScores.size();
                tender.setScoredBids(scoredBids);
                
                if (bidCount > 0) {
                    int progressPercent = (scoredBids * 100) / bidCount;
                    tender.setEvaluatorProgressPercent(progressPercent);
                }
                
                // Set evaluator scores for display
                tender.setEvaluatorScores(evaluatorScores);
                
                // Apply status filter
                boolean evaluationComplete = scoredBids >= bidCount;
                if (statusParam != null && !statusParam.trim().isEmpty()) {
                    if ("COMPLETED".equals(statusParam) && !evaluationComplete) {
                        continue;
                    }
                    if ("IN_PROGRESS".equals(statusParam) && evaluationComplete) {
                        continue;
                    }
                }
                
                evaluations.add(tender);
                totalAssigned++;
                
                if (evaluationComplete) {
                    completedCount++;
                } else {
                    inProgressCount++;
                }
                
                totalScoresSubmitted += scoredBids;
            }
            
            req.setAttribute("evaluations", evaluations);
            req.setAttribute("totalAssigned", totalAssigned);
            req.setAttribute("inProgressCount", inProgressCount);
            req.setAttribute("completedCount", completedCount);
            req.setAttribute("totalScoresSubmitted", totalScoresSubmitted);
            
            logger.info("Loaded " + evaluations.size() + " evaluations for evaluator " + currentUser.getEmail());
            
            req.getRequestDispatcher("/evaluator/evaluations.jsp").forward(req, resp);
            
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading evaluations", e);
            req.setAttribute("errorMessage", "Error loading evaluations. Please try again.");
            req.getRequestDispatcher("/evaluator/evaluations.jsp").forward(req, resp);
        }
    }
}