package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.TenderDAO;
import model.Bid;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/supplier/award-notice")
public class SupplierAwardNoticeServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(SupplierAwardNoticeServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            logger.info("DAOs initialized successfully");
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
        
        String tenderIdParam = req.getParameter("id");
        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/supplier/bids");
            return;
        }
        
        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);
            
            if (tender == null) {
                req.setAttribute("errorMessage", "Tender not found.");
                req.getRequestDispatcher("/supplier/award-notice.jsp").forward(req, resp);
                return;
            }
            
            // Get supplier's bid for this tender
            Bid supplierBid = null;
            boolean isWinner = false;
            
            java.util.List<Bid> bids = bidDAO.findByTenderId(tenderId);
            for (Bid bid : bids) {
                if (bid.getSupplierId() == currentUser.getUserId()) {
                    supplierBid = bid;
                    isWinner = (tender.getAwardedBidId() != null && 
                               tender.getAwardedBidId() == bid.getBidId());
                    break;
                }
            }
            
            req.setAttribute("tender", tender);
            req.setAttribute("supplierBid", supplierBid);
            req.setAttribute("isWinner", isWinner);
            
            req.getRequestDispatcher("/supplier/award-notice.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/supplier/bids");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading award notice", e);
            req.setAttribute("errorMessage", "Error loading award notice. Please try again.");
            req.getRequestDispatcher("/supplier/award-notice.jsp").forward(req, resp);
        }
    }
}