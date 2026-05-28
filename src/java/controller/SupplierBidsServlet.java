package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.interfaces.BidDAO;
import model.Bid;
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
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/supplier/bids")
public class SupplierBidsServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(SupplierBidsServlet.class.getName());
    private BidDAO bidDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            bidDAO = new BidDAOImpl();
            logger.info("BidDAO initialized successfully in SupplierBidsServlet");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize BidDAO", e);
            throw new ServletException("Cannot initialize BidDAO", e);
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
            
            String statusParam = req.getParameter("status");
            String dateFromParam = req.getParameter("dateFrom");
            String dateToParam = req.getParameter("dateTo");
            
            List<Bid> bids = bidDAO.findBySupplierId(supplierId);
            
            // Apply status filter
            if (statusParam != null && !statusParam.trim().isEmpty()) {
                bids = bids.stream()
                    .filter(b -> b.getStatusFilter().equals(statusParam))
                    .collect(Collectors.toList());
            }
            
            // Apply date filters
            if (dateFromParam != null && !dateFromParam.trim().isEmpty()) {
                LocalDate dateFrom = LocalDate.parse(dateFromParam);
                bids = bids.stream()
                    .filter(b -> b.getSubmittedAt() != null && 
                            b.getSubmittedAt().toLocalDate().isAfter(dateFrom.minusDays(1)))
                    .collect(Collectors.toList());
            }
            
            if (dateToParam != null && !dateToParam.trim().isEmpty()) {
                LocalDate dateTo = LocalDate.parse(dateToParam);
                bids = bids.stream()
                    .filter(b -> b.getSubmittedAt() != null && 
                            b.getSubmittedAt().toLocalDate().isBefore(dateTo.plusDays(1)))
                    .collect(Collectors.toList());
            }
            
            // Calculate stats
            int totalBids = bids.size();
            int pendingBids = (int) bids.stream()
                .filter(b -> b.getTenderStatus() == TenderStatus.OPEN)
                .count();
            int wonBids = (int) bids.stream()
                .filter(Bid::isWinner)
                .count();
            
            double totalBidValue = bids.stream()
                .mapToDouble(b -> b.getBidAmount().doubleValue())
                .sum();
            
            req.setAttribute("bids", bids);
            req.setAttribute("totalBids", totalBids);
            req.setAttribute("pendingBids", pendingBids);
            req.setAttribute("wonBids", wonBids);
            req.setAttribute("totalBidValue", String.format("M %,.2f", totalBidValue));
            
            logger.info("Loaded " + bids.size() + " bids for supplier: " + currentUser.getEmail());
            
            req.getRequestDispatcher("/supplier/bids.jsp").forward(req, resp);
            
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading bids", e);
            req.setAttribute("errorMessage", "Error loading bids. Please try again.");
            req.getRequestDispatcher("/supplier/bids.jsp").forward(req, resp);
        }
    }
}