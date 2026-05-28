package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.impl.UserDAOImpl;
import dao.interfaces.BidDAO;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servlet handling the Procurement Officer dashboard.
 * Displays summary statistics, recent tenders, upcoming deadlines, and pending tasks.
 * 
 * @author kolisang
 * @version 1.0
 */
@WebServlet("/officer/dashboard")
public class OfficerDashboardServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(OfficerDashboardServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            userDAO = new UserDAOImpl();
            logger.info("DAOs initialized successfully in OfficerDashboardServlet");
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
        if (currentUser.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        
        try {
            int officerId = currentUser.getUserId();
            
            // Get all tenders
            List<Tender> allTenders = tenderDAO.findAll();
            
            // Get tenders created by this officer
            List<Tender> myTenders = tenderDAO.findByCreatedBy(officerId);
            
            // Calculate statistics
            int totalTenders = allTenders.size();
            int draftTenders = (int) allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.DRAFT)
                .count();
            int openTenders = (int) allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.OPEN)
                .count();
            int closedTenders = (int) allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.CLOSED)
                .count();
            int underEvaluationTenders = (int) allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.UNDER_EVALUATION)
                .count();
            int evaluatedTenders = (int) allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.EVALUATED)
                .count();
            int awardedTenders = (int) allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.AWARDED)
                .count();
            
            // Get pending evaluations (tenders that are closed but have bids)
            List<Tender> pendingEvaluations = new ArrayList<>();
            for (Tender tender : allTenders) {
                if (tender.getStatus() == TenderStatus.CLOSED) {
                    int bidCount = bidDAO.countBidsByTenderId(tender.getTenderId());
                    if (bidCount > 0) {
                        tender.setBidCount(bidCount);
                        pendingEvaluations.add(tender);
                    }
                }
            }
            
            // Get tenders ready for award (evaluated tenders)
            List<Tender> readyForAward = allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.EVALUATED)
                .collect(Collectors.toList());
            
            // Get recent tenders (last 5 created)
            List<Tender> recentTenders = allTenders.stream()
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());
            
            // Get upcoming deadlines (open tenders closing in next 7 days)
            List<Tender> upcomingDeadlines = allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.OPEN)
                .filter(t -> t.getClosingDateTime() != null)
                .filter(t -> t.getClosingDateTime().isAfter(LocalDateTime.now()))
                .filter(t -> t.getClosingDateTime().isBefore(LocalDateTime.now().plusDays(7)))
                .sorted((t1, t2) -> t1.getClosingDateTime().compareTo(t2.getClosingDateTime()))
                .limit(5)
                .collect(Collectors.toList());
            
            // Get recent activity (combine recent tenders and awards)
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            
            for (Tender tender : recentTenders) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("type", "tender_created");
                activity.put("title", "Tender Created");
                activity.put("description", "Tender " + tender.getReferenceNumber() + " was created");
                activity.put("time", tender.getCreatedAt());
                activity.put("link", "/officer/tender-detail?id=" + tender.getTenderId());
                recentActivity.add(activity);
            }
            
            // Add awarded tenders to recent activity
            List<Tender> recentAwards = allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.AWARDED)
                .filter(t -> t.getAwardDate() != null)
                .sorted((t1, t2) -> t2.getAwardDate().compareTo(t1.getAwardDate()))
                .limit(5)
                .collect(Collectors.toList());
            
            for (Tender tender : recentAwards) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("type", "tender_awarded");
                activity.put("title", "Contract Awarded");
                activity.put("description", "Tender " + tender.getReferenceNumber() + " was awarded");
                activity.put("time", tender.getAwardDate());
                activity.put("link", "/officer/award-notice?id=" + tender.getTenderId());
                recentActivity.add(activity);
            }
            
            // Sort recent activity by time descending
            recentActivity.sort((a1, a2) -> {
                LocalDateTime t1 = (LocalDateTime) a1.get("time");
                LocalDateTime t2 = (LocalDateTime) a2.get("time");
                if (t1 == null) return 1;
                if (t2 == null) return -1;
                return t2.compareTo(t1);
            });
            
            // Limit to 10 most recent activities
            if (recentActivity.size() > 10) {
                recentActivity = recentActivity.subList(0, 10);
            }
            
            // Get supplier statistics
            int totalSuppliers = userDAO.countByRole(UserRole.SUPPLIER);
            int activeSuppliers = totalSuppliers; // Could filter by recent activity
            
            // Get evaluator statistics
            int totalEvaluators = userDAO.countByRole(UserRole.EVALUATION_COMMITTEE);
            
            // Calculate total award value
            double totalAwardValue = allTenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.AWARDED)
                .filter(t -> t.getAwardedValue() != null)
                .mapToDouble(t -> t.getAwardedValue().doubleValue())
                .sum();
            
            // Calculate average bids per tender
            long totalBids = 0;
            for (Tender tender : allTenders) {
                int bidCount = bidDAO.countBidsByTenderId(tender.getTenderId());
                totalBids += bidCount;
            }
            double avgBidsPerTender = totalTenders > 0 ? (double) totalBids / totalTenders : 0;
            
            // Set attributes for JSP
            req.setAttribute("totalTenders", totalTenders);
            req.setAttribute("draftTenders", draftTenders);
            req.setAttribute("openTenders", openTenders);
            req.setAttribute("closedTenders", closedTenders);
            req.setAttribute("underEvaluationTenders", underEvaluationTenders);
            req.setAttribute("evaluatedTenders", evaluatedTenders);
            req.setAttribute("awardedTenders", awardedTenders);
            
            req.setAttribute("pendingEvaluations", pendingEvaluations);
            req.setAttribute("readyForAward", readyForAward);
            req.setAttribute("recentTenders", recentTenders);
            req.setAttribute("upcomingDeadlines", upcomingDeadlines);
            req.setAttribute("recentActivity", recentActivity);
            
            req.setAttribute("totalSuppliers", totalSuppliers);
            req.setAttribute("activeSuppliers", activeSuppliers);
            req.setAttribute("totalEvaluators", totalEvaluators);
            req.setAttribute("totalAwardValue", String.format("M %,.2f", totalAwardValue));
            req.setAttribute("avgBidsPerTender", String.format("%.1f", avgBidsPerTender));
            req.setAttribute("myTendersCount", myTenders.size());
            
            logger.info("Dashboard loaded for officer: " + currentUser.getEmail());
            
            req.getRequestDispatcher("/officer/dashboard.jsp").forward(req, resp);
            
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading officer dashboard", e);
            
            // Set default values on error
            req.setAttribute("totalTenders", 0);
            req.setAttribute("draftTenders", 0);
            req.setAttribute("openTenders", 0);
            req.setAttribute("closedTenders", 0);
            req.setAttribute("underEvaluationTenders", 0);
            req.setAttribute("evaluatedTenders", 0);
            req.setAttribute("awardedTenders", 0);
            req.setAttribute("pendingEvaluations", new ArrayList<>());
            req.setAttribute("readyForAward", new ArrayList<>());
            req.setAttribute("recentTenders", new ArrayList<>());
            req.setAttribute("upcomingDeadlines", new ArrayList<>());
            req.setAttribute("recentActivity", new ArrayList<>());
            req.setAttribute("totalSuppliers", 0);
            req.setAttribute("activeSuppliers", 0);
            req.setAttribute("totalEvaluators", 0);
            req.setAttribute("totalAwardValue", "M 0.00");
            req.setAttribute("avgBidsPerTender", "0.0");
            req.setAttribute("myTendersCount", 0);
            req.setAttribute("errorMessage", "Error loading dashboard data. Please try again.");
            
            req.getRequestDispatcher("/officer/dashboard.jsp").forward(req, resp);
        }
    }
}