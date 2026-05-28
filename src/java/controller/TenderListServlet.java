package controller;

import dao.DAOException;
import dao.impl.TenderDAOImpl;
import dao.interfaces.TenderDAO;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/officer/tenders")
public class TenderListServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(TenderListServlet.class.getName());
    private TenderDAO tenderDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            logger.info("TenderDAO initialized successfully in TenderListServlet");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize TenderDAO", e);
            throw new ServletException("Cannot initialize TenderDAO", e);
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
            // Get filter parameters
            String statusParam = req.getParameter("status");
            String categoryParam = req.getParameter("category");
            String keyword = req.getParameter("keyword");
            
            TenderStatus status = null;
            if (statusParam != null && !statusParam.trim().isEmpty()) {
                try {
                    status = TenderStatus.valueOf(statusParam);
                } catch (IllegalArgumentException e) {
                    logger.warning("Invalid status parameter: " + statusParam);
                }
            }
            
            TenderCategory category = null;
            if (categoryParam != null && !categoryParam.trim().isEmpty()) {
                try {
                    category = TenderCategory.valueOf(categoryParam);
                } catch (IllegalArgumentException e) {
                    logger.warning("Invalid category parameter: " + categoryParam);
                }
            }
            
            // Get pagination parameters
            int page = 1;
            int pageSize = 10;
            String pageParam = req.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            
            // Get tenders with filters
            List<Tender> tenders;
            if (status != null || category != null) {
                tenders = tenderDAO.findWithFilters(status, category);
            } else {
                tenders = tenderDAO.findAll();
            }
            
            // Apply keyword filter if provided
            if (keyword != null && !keyword.trim().isEmpty()) {
                String lowerKeyword = keyword.toLowerCase().trim();
                tenders = tenders.stream()
                    .filter(t -> 
                        (t.getReferenceNumber() != null && 
                         t.getReferenceNumber().toLowerCase().contains(lowerKeyword)) ||
                        (t.getTitle() != null && 
                         t.getTitle().toLowerCase().contains(lowerKeyword))
                    )
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Get bid counts for each tender
            for (Tender tender : tenders) {
                try {
                    int bidCount = tenderDAO.countBids(tender.getTenderId());
                    tender.setBidCount(bidCount);
                } catch (DAOException e) {
                    logger.warning("Could not get bid count for tender: " + tender.getTenderId());
                    tender.setBidCount(0);
                }
            }
            
            // Calculate pagination
            int totalTenders = tenders.size();
            int totalPages = (int) Math.ceil((double) totalTenders / pageSize);
            
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, totalTenders);
            
            List<Tender> paginatedTenders;
            if (start < totalTenders) {
                paginatedTenders = tenders.subList(start, end);
            } else {
                paginatedTenders = new java.util.ArrayList<>();
            }
            
            // Set attributes for JSP
            req.setAttribute("tenderList", paginatedTenders);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalTenders", totalTenders);
            
            logger.info("Loaded " + paginatedTenders.size() + " tenders (Total: " + totalTenders + ")");
            
            // Forward to JSP
            req.getRequestDispatcher("/officer/tenders.jsp").forward(req, resp);
            
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error retrieving tenders", e);
            req.setAttribute("errorMessage", "Error loading tenders. Please try again.");
            req.getRequestDispatcher("/officer/tenders.jsp").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        doGet(req, resp);
    }
}