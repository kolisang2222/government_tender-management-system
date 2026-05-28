package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.interfaces.BidDAO;
import model.User;
import model.enums.UserRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/supplier/my-bids")
public class MyBidsServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(MyBidsServlet.class.getName());
    private BidDAO bidDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            bidDAO = new BidDAOImpl();
        } catch (DAOException e) {
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
            // Get filter parameters
            String status = req.getParameter("status");
            String dateFrom = req.getParameter("dateFrom");
            String dateTo = req.getParameter("dateTo");
            
            // Get bids with filters
            var bids = bidDAO.findBySupplierIdWithFilters(
                currentUser.getUserId(), 
                status, 
                dateFrom, 
                dateTo
            );
            
            // Calculate statistics
            int totalBids = bids.size();
            long pendingBids = bids.stream().filter(b -> "PENDING".equals(b.getStatus())).count();
            long wonBids = bids.stream().filter(b -> "WON".equals(b.getStatus())).count();
            
            req.setAttribute("bids", bids);
            req.setAttribute("totalBids", totalBids);
            req.setAttribute("pendingBids", pendingBids);
            req.setAttribute("wonBids", wonBids);
            
        } catch (DAOException e) {
            logger.severe("Error loading bids: " + e.getMessage());
            req.setAttribute("errorMessage", "Error loading your bids");
        }
        
        req.getRequestDispatcher("/supplier/my-bids.jsp").forward(req, resp);
    }
}