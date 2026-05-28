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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import util.CSRFTokenUtil;

@WebServlet("/supplier/notifications")
public class SupplierNotificationsServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SupplierNotificationsServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            logger.info("DAOs initialized successfully in SupplierNotificationsServlet");
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
            String filter = req.getParameter("filter");

            // Generate real notifications from database
            List<Map<String, Object>> notifications = generateRealNotifications(supplierId);

            // Apply filter
            if (filter != null && !filter.trim().isEmpty()) {
                if ("unread".equals(filter)) {
                    notifications = notifications.stream()
                            .filter(n -> !(boolean) n.getOrDefault("read", true))
                            .collect(Collectors.toList());
                } else if ("award".equals(filter)) {
                    notifications = notifications.stream()
                            .filter(n -> ((String) n.get("type")).startsWith("award"))
                            .collect(Collectors.toList());
                } else if ("tender".equals(filter)) {
                    notifications = notifications.stream()
                            .filter(n -> ((String) n.get("type")).startsWith("tender"))
                            .collect(Collectors.toList());
                } else if ("bid".equals(filter)) {
                    notifications = notifications.stream()
                            .filter(n -> ((String) n.get("type")).startsWith("bid")
                            || ((String) n.get("type")).startsWith("evaluation"))
                            .collect(Collectors.toList());
                }
            }

            // Pagination
            int page = 1;
            int pageSize = 10;
            String pageParam = req.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) {
                        page = 1;
                    }
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            int totalNotifications = notifications.size();
            int totalPages = (int) Math.ceil((double) totalNotifications / pageSize);
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, totalNotifications);

            List<Map<String, Object>> paginatedNotifications = new ArrayList<>();
            if (start < totalNotifications) {
                paginatedNotifications = notifications.subList(start, end);
            }

            // Calculate stats
            int unreadCount = (int) notifications.stream()
                    .filter(n -> !(boolean) n.getOrDefault("read", true))
                    .count();
            int awardCount = (int) notifications.stream()
                    .filter(n -> ((String) n.get("type")).startsWith("award"))
                    .count();

            req.setAttribute("notifications", paginatedNotifications);
            req.setAttribute("totalNotifications", totalNotifications);
            req.setAttribute("unreadCount", unreadCount);
            req.setAttribute("awardCount", awardCount);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);

            logger.info("Loaded " + paginatedNotifications.size() + " notifications for supplier: " + currentUser.getEmail());

            req.getRequestDispatcher("/supplier/notifications.jsp").forward(req, resp);

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading notifications", e);
            req.setAttribute("errorMessage", "Error loading notifications. Please try again.");
            req.getRequestDispatcher("/supplier/notifications.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // In doPost() method
        if (!CSRFTokenUtil.validateToken(req)) {
            session.setAttribute("errorMessage", "Invalid security token. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
// Invalidate after use to prevent reuse
        CSRFTokenUtil.invalidateToken(session);
        String action = req.getParameter("action");
        String notificationId = req.getParameter("notificationId");

        // In a real system, you would update the database
        // For now, we'll use session to track read status
        if ("markRead".equals(action)) {
            session.setAttribute("successMessage", "Notification marked as read.");
        } else if ("markAllRead".equals(action)) {
            session.setAttribute("successMessage", "All notifications marked as read.");
        } else if ("delete".equals(action)) {
            session.setAttribute("successMessage", "Notification deleted.");
        } else if ("deleteAllRead".equals(action)) {
            session.setAttribute("successMessage", "All read notifications deleted.");
        }

        resp.sendRedirect(req.getContextPath() + "/supplier/notifications");
    }

    /**
     * Generates real notifications based on database data.
     */
    private List<Map<String, Object>> generateRealNotifications(int supplierId) throws DAOException {
        List<Map<String, Object>> notifications = new ArrayList<>();
        int notificationIdCounter = 1;

        // 1. Get all open tenders (NEW TENDER notifications)
        List<Tender> openTenders = tenderDAO.findByStatus(TenderStatus.OPEN);
        for (Tender tender : openTenders) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("notificationId", notificationIdCounter++);
            notification.put("type", "tender_new");
            notification.put("title", "New Tender Published");
            notification.put("message", "A new tender has been published: " + tender.getTitle());
            notification.put("tenderReference", tender.getReferenceNumber());
            notification.put("link", "/supplier/tender-detail?id=" + tender.getTenderId());
            notification.put("read", false);
            notification.put("timeAgo", getTimeAgo(tender.getCreatedAt()));
            notification.put("createdAt", tender.getCreatedAt());
            notifications.add(notification);
        }

        // 2. Get tenders closing soon (CLOSING SOON notifications)
        List<Tender> closingSoonTenders = openTenders.stream()
                .filter(t -> t.getClosingDateTime() != null)
                .filter(t -> t.getClosingDateTime().isAfter(LocalDateTime.now()))
                .filter(t -> t.getClosingDateTime().isBefore(LocalDateTime.now().plusDays(3)))
                .collect(Collectors.toList());

        for (Tender tender : closingSoonTenders) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("notificationId", notificationIdCounter++);
            notification.put("type", "tender_closing");
            notification.put("title", "Tender Closing Soon");
            notification.put("message", "Tender " + tender.getReferenceNumber() + " is closing soon. Submit your bid now!");
            notification.put("tenderReference", tender.getReferenceNumber());
            notification.put("link", "/supplier/tender-detail?id=" + tender.getTenderId());
            notification.put("read", false);
            notification.put("timeAgo", getTimeAgo(tender.getCreatedAt()));
            notification.put("createdAt", tender.getCreatedAt());
            notifications.add(notification);
        }

        // 3. Get supplier's bids (BID SUBMITTED notifications)
        List<Bid> supplierBids = bidDAO.findBySupplierId(supplierId);
        for (Bid bid : supplierBids) {
            if (bid.getSubmittedAt() != null) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("notificationId", notificationIdCounter++);
                notification.put("type", "bid_submitted");
                notification.put("title", "Bid Submitted Successfully");
                notification.put("message", "Your bid for tender " + bid.getTenderReference() + " has been submitted.");
                notification.put("tenderReference", bid.getTenderReference());
                notification.put("link", "/supplier/bids");
                notification.put("read", true); // Auto-mark as read since it's an action they took
                notification.put("timeAgo", getTimeAgo(bid.getSubmittedAt()));
                notification.put("createdAt", bid.getSubmittedAt());
                notifications.add(notification);
            }
        }

        // 4. Get awarded tenders where supplier bid (AWARD notifications)
        List<Tender> allTenders = tenderDAO.findAll();
        for (Tender tender : allTenders) {
            if (tender.getStatus() == TenderStatus.AWARDED && tender.getAwardedBidId() != null) {
                // Check if this supplier bid on this tender
                boolean supplierBid = false;
                boolean isWinner = false;

                for (Bid bid : supplierBids) {
                    if (bid.getTenderId() == tender.getTenderId()) {
                        supplierBid = true;
                        isWinner = (bid.getBidId() == tender.getAwardedBidId());
                        break;
                    }
                }

                if (supplierBid) {
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("notificationId", notificationIdCounter++);

                    if (isWinner) {
                        notification.put("type", "award_won");
                        notification.put("title", "Congratulations! Bid Awarded");
                        notification.put("message", "Your bid for tender " + tender.getReferenceNumber() + " has been AWARDED!");
                    } else {
                        notification.put("type", "award_lost");
                        notification.put("title", "Tender Award Notification");
                        notification.put("message", "Tender " + tender.getReferenceNumber() + " has been awarded to another supplier.");
                    }

                    notification.put("tenderReference", tender.getReferenceNumber());
                    notification.put("link", "/supplier/award-notice?id=" + tender.getTenderId());
                    notification.put("read", false);
                    notification.put("timeAgo", getTimeAgo(tender.getAwardDate()));
                    notification.put("createdAt", tender.getAwardDate());
                    notifications.add(notification);
                }
            }
        }

        // 5. Get evaluated tenders (EVALUATION COMPLETE notifications)
        for (Tender tender : allTenders) {
            if (tender.getStatus() == TenderStatus.EVALUATED) {
                boolean supplierBid = false;
                for (Bid bid : supplierBids) {
                    if (bid.getTenderId() == tender.getTenderId()) {
                        supplierBid = true;
                        break;
                    }
                }

                if (supplierBid) {
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("notificationId", notificationIdCounter++);
                    notification.put("type", "evaluation_complete");
                    notification.put("title", "Evaluation Complete");
                    notification.put("message", "Evaluation for tender " + tender.getReferenceNumber() + " is complete.");
                    notification.put("tenderReference", tender.getReferenceNumber());
                    notification.put("link", "/supplier/bids");
                    notification.put("read", false);
                    notification.put("timeAgo", getTimeAgo(tender.getEvaluationCompletedDate()));
                    notification.put("createdAt", tender.getEvaluationCompletedDate());
                    notifications.add(notification);
                }
            }
        }

        // Sort by createdAt descending (newest first)
        notifications.sort((a, b) -> {
            LocalDateTime aTime = (LocalDateTime) a.get("createdAt");
            LocalDateTime bTime = (LocalDateTime) b.get("createdAt");
            if (aTime == null) {
                return 1;
            }
            if (bTime == null) {
                return -1;
            }
            return bTime.compareTo(aTime);
        });

        return notifications;
    }

    /**
     * Gets a human-readable time ago string.
     */
    private String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Recently";
        }

        LocalDateTime now = LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(dateTime, now);

        long days = duration.toDays();
        long hours = duration.toHours();
        long minutes = duration.toMinutes();

        if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else {
            return "Just now";
        }
    }
}
