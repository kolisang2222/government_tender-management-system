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
import model.enums.TenderStatus;
import model.enums.UserRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/evaluator/results")
public class EvaluatorResultsServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(EvaluatorResultsServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private EvaluationDAO evaluationDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
            logger.info("DAOs initialized successfully in EvaluatorResultsServlet");
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
            
            String dateFromParam = req.getParameter("dateFrom");
            String dateToParam = req.getParameter("dateTo");
            String categoryParam = req.getParameter("category");
            
            LocalDate dateFrom = null;
            LocalDate dateTo = null;
            
            if (dateFromParam != null && !dateFromParam.trim().isEmpty()) {
                dateFrom = LocalDate.parse(dateFromParam);
            }
            if (dateToParam != null && !dateToParam.trim().isEmpty()) {
                dateTo = LocalDate.parse(dateToParam);
            }
            
            TenderCategory category = null;
            if (categoryParam != null && !categoryParam.trim().isEmpty()) {
                try {
                    category = TenderCategory.valueOf(categoryParam);
                } catch (IllegalArgumentException e) {
                    logger.warning("Invalid category parameter: " + categoryParam);
                }
            }
            
            // Get all awarded tenders
            List<Tender> allTenders = tenderDAO.findAll();
            List<Tender> awardedTenders = new ArrayList<>();
            
            double totalAwardValue = 0;
            int thisMonthAwards = 0;
            LocalDate now = LocalDate.now();
            LocalDate firstOfMonth = now.withDayOfMonth(1);
            
            for (Tender tender : allTenders) {
                if (tender.getStatus() != TenderStatus.AWARDED) {
                    continue;
                }
                
                // Apply date filters
                if (dateFrom != null && tender.getAwardDate() != null && 
                    tender.getAwardDate().toLocalDate().isBefore(dateFrom)) {
                    continue;
                }
                if (dateTo != null && tender.getAwardDate() != null && 
                    tender.getAwardDate().toLocalDate().isAfter(dateTo)) {
                    continue;
                }
                
                // Apply category filter
                if (category != null && tender.getCategory() != category) {
                    continue;
                }
                
                int bidCount = bidDAO.countBidsByTenderId(tender.getTenderId());
                tender.setBidCount(bidCount);
                
                // Get ranked bids
                List rankedBids = bidDAO.findRankedBidsByTenderId(tender.getTenderId());
                tender.setRankedBids(rankedBids);
                
                // Get evaluator's scores for this tender
                List<EvaluationScore> evaluatorScores = evaluationDAO.findByEvaluatorAndTender(evaluatorId, tender.getTenderId());
                tender.setEvaluatorScores(evaluatorScores);
                tender.setScoredBids(evaluatorScores.size());
                
                // Calculate evaluator's average score
                if (!evaluatorScores.isEmpty()) {
                    double avgScore = evaluatorScores.stream()
                        .mapToDouble(EvaluationScore::getWeightedTotal)
                        .average()
                        .orElse(0.0);
                    tender.setEvaluatorAvgScore(String.format("%.2f", avgScore));
                }
                
                awardedTenders.add(tender);
                
                if (tender.getAwardedValue() != null) {
                    totalAwardValue += tender.getAwardedValue().doubleValue();
                }
                
                if (tender.getAwardDate() != null && 
                    !tender.getAwardDate().toLocalDate().isBefore(firstOfMonth)) {
                    thisMonthAwards++;
                }
            }
            
            req.setAttribute("awardedTenders", awardedTenders);
            req.setAttribute("totalAwarded", awardedTenders.size());
            req.setAttribute("totalAwardValue", String.format("M %,.2f", totalAwardValue));
            req.setAttribute("thisMonthAwards", thisMonthAwards);
            
            logger.info("Loaded " + awardedTenders.size() + " awarded tenders for evaluator " + currentUser.getEmail());
            
            req.getRequestDispatcher("/evaluator/results.jsp").forward(req, resp);
            
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading results", e);
            req.setAttribute("errorMessage", "Error loading results. Please try again.");
            req.getRequestDispatcher("/evaluator/results.jsp").forward(req, resp);
        }
    }
}