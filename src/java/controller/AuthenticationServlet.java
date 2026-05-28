package controller;

import dao.DAOException;
import dao.impl.UserDAOImpl;
import dao.interfaces.UserDAO;
import model.User;
import model.enums.UserRole;
import service.EmailNotificationService;
import util.PasswordHasher;
import util.CSRFTokenUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles user authentication and session creation. Implements account lockout
 * after 3 failed attempts PER ACCOUNT.
 *
 * @author kolisang
 * @version 1.0
 */
@WebServlet("/login")
public class AuthenticationServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AuthenticationServlet.class.getName());
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
        logger.info("UserDAO initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // If already logged in, redirect to dashboard
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            redirectToDashboard(resp, user);
            return;
        }

        // Generate CSRF token for the login form
        HttpSession newSession = req.getSession(true);
        CSRFTokenUtil.generateToken(newSession);

        // Not logged in - show login page
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ============================================================
        // CSRF PROTECTION - Fixed null session issue
        // ============================================================
        HttpSession session = req.getSession(false);

        if (!CSRFTokenUtil.validateToken(req)) {
            if (session != null) {
                session.setAttribute("errorMessage", "Invalid security token. Please try again.");
            }
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // Invalidate token after validation to prevent reuse
        if (session != null) {
            CSRFTokenUtil.invalidateToken(session);
        }

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Basic validation
        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Email and password are required.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            // ===== CHECK IF THIS SPECIFIC ACCOUNT IS LOCKED (DATABASE) =====
            boolean isAccountLocked = userDAO.isAccountLocked(email);

            if (isAccountLocked) {
                req.setAttribute("errorMessage",
                        "This account is temporarily locked. Please try again in 30 minutes.");
                req.setAttribute("email", email);
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            // Hash the password
            String hashedPassword = PasswordHasher.hash(password);

            // Authenticate user
            User user = userDAO.findByEmailAndPassword(email, hashedPassword);

            if (user != null) {
                // SUCCESSFUL LOGIN
                handleSuccessfulLogin(req, resp, user);
            } else {
                // FAILED LOGIN - Track per account in database
                handleFailedLogin(req, resp, email);
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Database error during authentication", e);
            req.setAttribute("errorMessage",
                    "System error. Please try again later.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    /**
     * Handles successful login - resets attempts for THIS ACCOUNT ONLY.
     */
    private void handleSuccessfulLogin(HttpServletRequest req,
            HttpServletResponse resp,
            User user) throws IOException, ServletException {

        // ============================================================
        // SESSION FIXATION PREVENTION - Invalidate old session
        // ============================================================
        HttpSession oldSession = req.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        // Create new session
        HttpSession newSession = req.getSession(true);

        // Store user in session
        newSession.setAttribute("user", user);
        newSession.setAttribute("userId", user.getUserId());
        newSession.setAttribute("userRole", user.getRole().name());
        newSession.setAttribute("userName", user.getFullName());

        // Set session timeout (30 minutes)
        newSession.setMaxInactiveInterval(30 * 60);

        // Generate new CSRF token for the session
        CSRFTokenUtil.generateToken(newSession);

        logger.info("User logged in: " + user.getEmail() + " (Role: " + user.getRole() + ")");

        try {
            // Reset failed attempts for THIS ACCOUNT in database
            userDAO.resetFailedAttempts(user.getEmail());

            // Update last login timestamp
            userDAO.updateLastLogin(user.getUserId());
        } catch (DAOException e) {
            logger.warning("Could not reset failed attempts in DB: " + e.getMessage());
        }

        // Redirect to role-specific dashboard
        redirectToDashboard(resp, user);
    }

    /**
     * Handles failed login - increments attempt counter FOR THIS ACCOUNT ONLY.
     */
    private void handleFailedLogin(HttpServletRequest req,
            HttpServletResponse resp,
            String email) throws IOException, ServletException {

        try {
            // Increment failed attempts for THIS ACCOUNT in database
            userDAO.incrementFailedAttempts(email);

            // Get current failed attempts for THIS ACCOUNT from database
            int failedAttempts = userDAO.getFailedAttempts(email);

            logger.warning("Failed login attempt for: " + email
                    + " (Attempt " + failedAttempts + "/" + MAX_LOGIN_ATTEMPTS + ")");

            if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
                // Lock THIS ACCOUNT for 30 minutes
                Timestamp lockUntil = new Timestamp(
                        System.currentTimeMillis() + (LOCKOUT_DURATION_MINUTES * 60 * 1000)
                );
                userDAO.lockAccount(email, lockUntil);

                // Try to send lockout notification email
                try {
                    User lockedUser = userDAO.findByEmail(email);
                    if (lockedUser != null) {
                        EmailNotificationService.sendAccountLockoutNotification(
                                lockedUser.getEmail(),
                                lockedUser.getFullName(),
                                LOCKOUT_DURATION_MINUTES
                        );
                    }
                } catch (Exception e) {
                    logger.warning("Failed to send lockout email: " + e.getMessage());
                }

                req.setAttribute("errorMessage",
                        "Too many failed attempts. This account has been locked for "
                        + LOCKOUT_DURATION_MINUTES + " minutes.");
            } else {
                int remaining = MAX_LOGIN_ATTEMPTS - failedAttempts;
                req.setAttribute("errorMessage",
                        "Invalid email or password. " + remaining + " attempt(s) remaining for this account.");
            }

        } catch (DAOException e) {
            logger.warning("Could not update failed attempts in DB: " + e.getMessage());
            req.setAttribute("errorMessage", "Invalid email or password.");
        }

        // Keep email in form
        req.setAttribute("email", email);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    /**
     * Redirects user to their role-specific dashboard. FIXED: Added context
     * path and ensured URLs point to existing resources.
     */
    private void redirectToDashboard(HttpServletResponse resp, User user)
            throws IOException {

        String contextPath = getServletContext().getContextPath();
        String dashboardUrl;

        switch (user.getRole()) {
            case SUPPLIER:
                dashboardUrl = contextPath + "/supplier/dashboard.jsp";
                break;
            case PROCUREMENT_OFFICER:
                // Redirect to reports page (which exists) instead of dashboard
                dashboardUrl = contextPath + "/officer/reports";
                break;
            case EVALUATION_COMMITTEE:
                dashboardUrl = contextPath + "/evaluator/dashboard.jsp";
                break;
            default:
                dashboardUrl = contextPath + "/login.jsp";
        }

        logger.info("Redirecting to: " + dashboardUrl);
        resp.sendRedirect(dashboardUrl);
    }
}
