package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.TenderDAO;
import model.Bid;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/supplier/dashboard")
public class SupplierDashboardServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SupplierDashboardServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            logger.info("DAOs initialized successfully in SupplierDashboardServlet");
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

            // Get open tenders
            List<Tender> openTenders = tenderDAO.findByStatus(TenderStatus.OPEN);
            req.setAttribute("openTenders", openTenders);
            req.setAttribute("openTendersCount", openTenders.size());

            // Get supplier's bids
            List<Bid> myBids = bidDAO.findBySupplierId(supplierId);
            req.setAttribute("myBids", myBids);
            req.setAttribute("myBidsCount", myBids.size());

            // Count won bids - FIXED: Added null safety for getTenderAwardedBidId()
            int wonBidsCount = (int) myBids.stream()
                    .filter(bid -> bid.getTenderStatus() == TenderStatus.AWARDED)
                    .filter(bid -> {
                        Integer awardedBidId = bid.getTenderAwardedBidId();
                        return awardedBidId != null && awardedBidId.intValue() == bid.getBidId();
                    })
                    .count();
            req.setAttribute("wonBidsCount", wonBidsCount);

            // Get upcoming deadlines (tenders closing in next 7 days)
            List<Tender> upcomingDeadlines = openTenders.stream()
                    .filter(t -> t.getClosingDateTime() != null)
                    .filter(t -> t.getClosingDateTime().isAfter(java.time.LocalDateTime.now()))
                    .filter(t -> t.getClosingDateTime().isBefore(java.time.LocalDateTime.now().plusDays(7)))
                    .limit(5)
                    .collect(Collectors.toList());
            req.setAttribute("upcomingDeadlines", upcomingDeadlines);

            // Get award notices for tenders supplier bid on - FIXED: Added null safety
            List<Map<String, Object>> awardNotices = new ArrayList<>();
            for (Bid bid : myBids) {
                if (bid.getTenderStatus() == TenderStatus.AWARDED) {
                    Map<String, Object> notice = new HashMap<>();
                    notice.put("tenderId", bid.getTenderId());
                    notice.put("tenderReference", bid.getTenderReference());
                    notice.put("tenderTitle", bid.getTenderTitle());
                    notice.put("awardDate", bid.getTenderAwardDate());
                    // FIXED: Added null safety for comparing awarded bid ID
                    Integer awardedBidId = bid.getTenderAwardedBidId();
                    notice.put("won", awardedBidId != null && awardedBidId.intValue() == bid.getBidId());
                    awardNotices.add(notice);
                }
            }
            req.setAttribute("awardNotices", awardNotices);

            logger.info("Dashboard loaded for supplier: " + currentUser.getEmail());

            req.getRequestDispatcher("/supplier/dashboard.jsp").forward(req, resp);

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading supplier dashboard", e);
            req.setAttribute("errorMessage", "Error loading dashboard. Please try again.");
            req.getRequestDispatcher("/supplier/dashboard.jsp").forward(req, resp);
        }
    }
}
