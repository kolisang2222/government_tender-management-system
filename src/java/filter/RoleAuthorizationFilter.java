package filter;

import model.User;
import model.enums.UserRole;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Role-specific authorization filter for ProcureGov. Ensures users only access
 * resources appropriate for their role. Filters all requests to /officer/*
 * paths, allowing only users with PROCUREMENT_OFFICER role to access these
 * resources.
 *
 * @author kolisang
 * @version 1.0
 */
@WebFilter("/officer/*")
public class RoleAuthorizationFilter implements Filter {

    /**
     * Logger for recording authorization events and violations
     */
    private static final Logger logger = Logger.getLogger(RoleAuthorizationFilter.class.getName());

    /**
     * Called when the filter is first initialized by the servlet container.
     * Logs the initialization event for auditing purposes.
     *
     * @param filterConfig the filter configuration object
     * @throws ServletException if initialization fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("RoleAuthorizationFilter initialized for /officer/* paths");
    }

    /**
     * Filters incoming requests to ensure only authenticated Procurement
     * Officers can access resources under the /officer/ path.
     * <p>
     * Redirects unauthenticated users to the login page and returns HTTP 403
     * Forbidden for authenticated users who do not have the PROCUREMENT_OFFICER
     * role.
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param chain the filter chain for passing the request along
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        // Redirect to login if no session or user attribute exists
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Only Procurement Officers can access /officer/* paths
        if (user.getRole() != UserRole.PROCUREMENT_OFFICER) {
            logger.warning("Role violation: " + user.getEmail()
                    + " (" + user.getRole() + ") attempted to access officer resource");

            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access Denied: Procurement Officer role required.");
            return;
        }

        // User is authorized, continue with the request
        chain.doFilter(request, response);
    }

    /**
     * Called when the filter is being taken out of service by the servlet
     * container. Logs the destruction event for auditing purposes.
     */
    @Override
    public void destroy() {
        logger.info("RoleAuthorizationFilter destroyed");
    }
}
