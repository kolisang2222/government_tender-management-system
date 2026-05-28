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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/evaluator/dashboard")
public class EvaluatorDashboardServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(EvaluatorDashboardServlet.class.getName());
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
            logger.info("DAOs initialized successfully in EvaluatorDashboardServlet");
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
            
            // Get all tenders in evaluation stages
            List<Tender> allTenders = tenderDAO.findAll();
            
            List<Tender> pendingEvaluationsList = new ArrayList<>();
            List<Tender> completedEvaluationsList = new ArrayList<>();
            List<Tender> recentAwardedTendersList = new ArrayList<>();
            
            int totalAssignedTenders = 0;
            int completedEvaluationsCount = 0;
            int pendingEvaluationsCount = 0;
            
            for (Tender tender : allTenders) {
                if (tender.getStatus() == TenderStatus.DRAFT || 
                    tender.getStatus() == TenderStatus.OPEN) {
                    continue;
                }
                
                int bidCount = bidDAO.countBidsByTenderId(tender.getTenderId());
                tender.setBidCount(bidCount);
                
                if (bidCount == 0) {
                    continue;
                }
                
                // Get evaluator's scores for this tender
                List<EvaluationScore> evaluatorScores = evaluationDAO.findByEvaluatorAndTender(evaluatorId, tender.getTenderId());
                int scoredBids = evaluatorScores.size();
                tender.setScoredBids(scoredBids);
                
                if (bidCount > 0) {
                    int progressPercent = (scoredBids * 100) / bidCount;
                    tender.setEvaluatorProgressPercent(progressPercent);
                }
                
                totalAssignedTenders++;
                
                // Check if evaluation is complete for this evaluator
                boolean evaluationComplete = scoredBids >= bidCount;
                
                if (evaluationComplete) {
                    completedEvaluationsList.add(tender);
                    completedEvaluationsCount++;
                } else {
                    pendingEvaluationsList.add(tender);
                    pendingEvaluationsCount++;
                }
            }
            
            // Sort pending evaluations by closing date
            pendingEvaluationsList.sort((t1, t2) -> {
                if (t1.getClosingDateTime() == null) return 1;
                if (t2.getClosingDateTime() == null) return -1;
                return t1.getClosingDateTime().compareTo(t2.getClosingDateTime());
            });
            
            // Sort completed evaluations by completion date
            completedEvaluationsList.sort((t1, t2) -> {
                if (t1.getEvaluationCompletedDate() == null) return 1;
                if (t2.getEvaluationCompletedDate() == null) return -1;
                return t2.getEvaluationCompletedDate().compareTo(t1.getEvaluationCompletedDate());
            });
            
            // Get recent awarded tenders
            recentAwardedTendersList = allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.AWARDED)
                .filter(t -> t.getAwardDate() != null)
                .sorted((t1, t2) -> t2.getAwardDate().compareTo(t1.getAwardDate()))
                .limit(5)
                .collect(Collectors.toList());
            
            // Set attributes for JSP - USE EXACT NAMES MATCHING JSP
            req.setAttribute("pendingEvaluations", pendingEvaluationsList);
            req.setAttribute("completedEvaluations", completedEvaluationsList);
            req.setAttribute("recentAwardedTenders", recentAwardedTendersList);
            
            // Stats
            req.setAttribute("totalAssignedTenders", totalAssignedTenders);
            req.setAttribute("completedEvaluationsCount", completedEvaluationsCount);
            req.setAttribute("pendingEvaluationsCount", pendingEvaluationsCount);
            
            logger.info("Dashboard loaded for evaluator: " + currentUser.getEmail() + 
                       " - Pending: " + pendingEvaluationsCount + ", Completed: " + completedEvaluationsCount);
            
            req.getRequestDispatcher("/evaluator/dashboard.jsp").forward(req, resp);
            
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading evaluator dashboard", e);
            
            // Set empty lists to avoid JSP errors
            req.setAttribute("pendingEvaluations", new ArrayList<>());
            req.setAttribute("completedEvaluations", new ArrayList<>());
            req.setAttribute("recentAwardedTenders", new ArrayList<>());
            req.setAttribute("totalAssignedTenders", 0);
            req.setAttribute("completedEvaluationsCount", 0);
            req.setAttribute("pendingEvaluationsCount", 0);
            req.setAttribute("errorMessage", "Error loading dashboard. Please try again.");
            
            req.getRequestDispatcher("/evaluator/dashboard.jsp").forward(req, resp);
        }
    }
}