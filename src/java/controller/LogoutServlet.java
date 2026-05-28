package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Handles user logout by invalidating the session.
 *
 * @author kolisang
 * @version 1.0
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final Logger logger
            = Logger.getLogger(LogoutServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session != null) {
            Object userName = session.getAttribute("userName");
            logger.info("User logged out: " + userName);

            // Store logout message before invalidating
            session.setAttribute("logoutMessage", "You have been logged out successfully.");

            // Invalidate session
            session.invalidate();
        }

        // Redirect to login page
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}
