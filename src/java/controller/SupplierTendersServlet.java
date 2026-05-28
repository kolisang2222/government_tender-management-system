package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.interfaces.BidDAO;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/supplier/tenders")
public class SupplierTendersServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(SupplierTendersServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            logger.info("DAOs initialized successfully in SupplierTendersServlet");
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
        if (currentUser.getRole() != UserRole.SUPPLIER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        
        try {
            int supplierId = currentUser.getUserId();
            
            String categoryParam = req.getParameter("category");
            String keyword = req.getParameter("keyword");
            String sortParam = req.getParameter("sort");
            
            // Get open tenders
            List<Tender> tenders = tenderDAO.findByStatus(TenderStatus.OPEN);
            
            // Filter by category
            if (categoryParam != null && !categoryParam.trim().isEmpty()) {
                try {
                    TenderCategory category = TenderCategory.valueOf(categoryParam);
                    tenders = tenders.stream()
                        .filter(t -> t.getCategory() == category)
                        .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    logger.warning("Invalid category: " + categoryParam);
                }
            }
            
            // Filter by keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                String lowerKeyword = keyword.toLowerCase().trim();
                tenders = tenders.stream()
                    .filter(t -> 
                        (t.getReferenceNumber() != null && 
                         t.getReferenceNumber().toLowerCase().contains(lowerKeyword)) ||
                        (t.getTitle() != null && 
                         t.getTitle().toLowerCase().contains(lowerKeyword))
                    )
                    .collect(Collectors.toList());
            }
            
            // Check if supplier has already bid on each tender
            for (Tender tender : tenders) {
                boolean hasBid = bidDAO.hasSupplierBid(tender.getTenderId(), supplierId);
                tender.setHasBid(hasBid);
                
                // Check if urgent (closing within 3 days)
                if (tender.getClosingDateTime() != null) {
                    boolean urgent = tender.getClosingDateTime()
                        .isBefore(LocalDateTime.now().plusDays(3));
                    tender.setUrgent(urgent);
                }
            }
            
            // Sort tenders
            if (sortParam != null) {
                switch (sortParam) {
                    case "closing":
                        tenders.sort(Comparator.comparing(Tender::getClosingDateTime));
                        break;
                    case "value_high":
                        tenders.sort((t1, t2) -> t2.getEstimatedValue().compareTo(t1.getEstimatedValue()));
                        break;
                    case "value_low":
                        tenders.sort(Comparator.comparing(Tender::getEstimatedValue));
                        break;
                    default:
                        tenders.sort((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()));
                }
            } else {
                tenders.sort((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()));
            }
            
            // Stats
            long closingSoonCount = tenders.stream()
                .filter(t -> t.getClosingDateTime() != null)
                .filter(t -> t.getClosingDateTime().isBefore(LocalDateTime.now().plusDays(7)))
                .count();
            
            int myBidsCount = (int) tenders.stream().filter(Tender::hasBid).count();
            
            req.setAttribute("tenders", tenders);
            req.setAttribute("openTendersCount", tenders.size());
            req.setAttribute("closingSoonCount", (int) closingSoonCount);
            req.setAttribute("myBidsCount", myBidsCount);
            
            logger.info("Loaded " + tenders.size() + " tenders for supplier: " + currentUser.getEmail());
            
            req.getRequestDispatcher("/supplier/tenders.jsp").forward(req, resp);
            
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading tenders", e);
            req.setAttribute("errorMessage", "Error loading tenders. Please try again.");
            req.getRequestDispatcher("/supplier/tenders.jsp").forward(req, resp);
        }
    }
}