package controller;

import dao.DAOException;
import dao.impl.UserDAOImpl;
import dao.interfaces.UserDAO;
import model.User;
import service.EmailNotificationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import util.CSRFTokenUtil;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ForgotPasswordServlet.class.getName());
    private UserDAO userDAO;

    // Store reset tokens
    private static final Map<String, ResetToken> resetTokens = new HashMap<>();

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
        logger.info("UserDAO initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = null;

        // In doPost() method
        if (!CSRFTokenUtil.validateToken(req)) {
            session.setAttribute("errorMessage", "Invalid security token. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
// Invalidate after use to prevent reuse
        CSRFTokenUtil.invalidateToken(session);

        String email = req.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password.jsp?error=Email is required");
            return;
        }

        try {
            User user = userDAO.findByEmail(email);

            if (user == null) {
                // Don't reveal if email exists or not (security)
                resp.sendRedirect(req.getContextPath() + "/forgot-password.jsp?sent=true");
                return;
            }

            // Generate reset token
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(30);

            resetTokens.put(token, new ResetToken(user.getUserId(), email, expiryTime));

            // Build reset link
            String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
            String resetLink = baseUrl + "/reset-password?token=" + token;

            // Try to send email
            boolean emailSent = false;
            try {
                emailSent = EmailNotificationService.sendPasswordResetEmail(
                        user.getEmail(),
                        user.getFullName(),
                        resetLink
                );
            } catch (Exception e) {
                logger.log(Level.WARNING, "Email service error: " + e.getMessage());
            }

            if (emailSent) {
                logger.info("Password reset email sent to: " + email);
            } else {
                logger.warning("Failed to send password reset email to: " + email);
                // Print reset link to console as backup
                System.out.println("");
                System.out.println("=========================================");
                System.out.println("EMAIL FAILED - Use this link for testing:");
                System.out.println(resetLink);
                System.out.println("=========================================");
                System.out.println("");
            }

            resp.sendRedirect(req.getContextPath() + "/forgot-password.jsp?sent=true");

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error processing forgot password", e);
            resp.sendRedirect(req.getContextPath() + "/forgot-password.jsp?error=System error");
        }
    }

    // ============================================================
    // PUBLIC STATIC INNER CLASS
    // ============================================================
    public static class ResetToken {

        public int userId;
        public String email;
        public LocalDateTime expiryTime;

        public ResetToken(int userId, String email, LocalDateTime expiryTime) {
            this.userId = userId;
            this.email = email;
            this.expiryTime = expiryTime;
        }

        public int getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }

        public boolean isExpired() {
            return expiryTime.isBefore(LocalDateTime.now());
        }
    }

    public static ResetToken validateToken(String token) {
        ResetToken resetToken = resetTokens.get(token);

        if (resetToken == null) {
            return null;
        }

        if (resetToken.isExpired()) {
            resetTokens.remove(token);
            return null;
        }

        return resetToken;
    }

    public static void removeToken(String token) {
        resetTokens.remove(token);
    }
}
