package controller;

import dao.DAOException;
import dao.impl.UserDAOImpl;
import dao.interfaces.UserDAO;
import util.PasswordHasher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import util.CSRFTokenUtil;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ResetPasswordServlet.class.getName());
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
        logger.info("UserDAO initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = req.getParameter("token");

        if (token == null || token.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password.jsp?error=Invalid request");
            return;
        }

        // Use the fully qualified class name
        ForgotPasswordServlet.ResetToken resetToken = ForgotPasswordServlet.validateToken(token);

        if (resetToken == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password.jsp?expired=true");
            return;
        }

        req.getRequestDispatcher("/reset-password.jsp").forward(req, resp);
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

        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (token == null || token.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password.jsp?error=Invalid request");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            resp.sendRedirect(req.getContextPath() + "/reset-password?token=" + token + "&error=Passwords do not match");
            return;
        }

        if (newPassword.length() < 8) {
            resp.sendRedirect(req.getContextPath() + "/reset-password?token=" + token + "&error=Password must be at least 8 characters");
            return;
        }

        ForgotPasswordServlet.ResetToken resetToken = ForgotPasswordServlet.validateToken(token);

        if (resetToken == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password.jsp?expired=true");
            return;
        }

        try {
            String hashedPassword = PasswordHasher.hash(newPassword);
            boolean updated = userDAO.updatePassword(resetToken.getUserId(), hashedPassword);

            if (updated) {
                ForgotPasswordServlet.removeToken(token);
                logger.info("Password reset successful for user ID: " + resetToken.getUserId());
                resp.sendRedirect(req.getContextPath() + "/login.jsp?reset=success");
            } else {
                resp.sendRedirect(req.getContextPath() + "/reset-password?token=" + token + "&error=Failed to update password");
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error resetting password", e);
            resp.sendRedirect(req.getContextPath() + "/reset-password?token=" + token + "&error=System error");
        }
    }
}
