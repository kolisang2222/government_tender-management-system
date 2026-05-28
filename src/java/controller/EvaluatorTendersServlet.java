package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.EvaluationDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.EvaluationDAO;
import dao.interfaces.TenderDAO;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/evaluator/tenders")
public class EvaluatorTendersServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(EvaluatorTendersServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private EvaluationDAO evaluationDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
            logger.info("DAOs initialized successfully in EvaluatorTendersServlet");
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
            
            // Get all tenders that are in evaluation stages
            List<Tender> allTenders = tenderDAO.findAll();
            
            List<Tender> pendingTenders = new ArrayList<>();
            List<Tender> inProgressTenders = new ArrayList<>();
            List<Tender> completedTenders = new ArrayList<>();
            
            for (Tender tender : allTenders) {
                // Only include tenders in evaluation-related statuses
                if (tender.getStatus() == TenderStatus.DRAFT || 
                    tender.getStatus() == TenderStatus.OPEN) {
                    continue;
                }
                
                // Get bid count
                int bidCount = bidDAO.countBidsByTenderId(tender.getTenderId());
                tender.setBidCount(bidCount);
                
                // Check if evaluator has scored any bids for this tender
                List scores = evaluationDAO.findByEvaluatorAndTender(evaluatorId, tender.getTenderId());
                int scoredBids = scores.size();
                tender.setScoredBids(scoredBids);
                
                // Calculate progress percentage
                if (bidCount > 0) {
                    int progressPercent = (scoredBids * 100) / bidCount;
                    tender.setEvaluatorProgressPercent(progressPercent);
                }
                
                // Categorize tenders
                if (tender.getStatus() == TenderStatus.CLOSED && bidCount > 0) {
                    // Check if evaluator has already started
                    if (scoredBids > 0) {
                        inProgressTenders.add(tender);
                    } else {
                        pendingTenders.add(tender);
                    }
                } else if (tender.getStatus() == TenderStatus.UNDER_EVALUATION) {
                    if (scoredBids >= bidCount) {
                        completedTenders.add(tender);
                    } else {
                        inProgressTenders.add(tender);
                    }
                } else if (tender.getStatus() == TenderStatus.EVALUATED || 
                           tender.getStatus() == TenderStatus.AWARDED) {
                    completedTenders.add(tender);
                }
            }
            
            req.setAttribute("pendingTenders", pendingTenders);
            req.setAttribute("inProgressTenders", inProgressTenders);
            req.setAttribute("completedTenders", completedTenders);
            
            logger.info("Loaded tenders for evaluator " + currentUser.getEmail() + 
                       ": Pending=" + pendingTenders.size() + 
                       ", InProgress=" + inProgressTenders.size() + 
                       ", Completed=" + completedTenders.size());
            
            req.getRequestDispatcher("/evaluator/tenders.jsp").forward(req, resp);
            
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading evaluator tenders", e);
            req.setAttribute("errorMessage", "Error loading tenders. Please try again.");
            req.getRequestDispatcher("/evaluator/tenders.jsp").forward(req, resp);
        }
    }
}