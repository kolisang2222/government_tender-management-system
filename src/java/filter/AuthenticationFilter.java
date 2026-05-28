package filter;

import model.User;
import model.enums.UserRole;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Authentication and Authorization Filter for ProcureGov. Intercepts all
 * requests to protected resources and validates:
 * <ul>
 * <li>User is authenticated (logged in)</li>
 * <li>User has the appropriate role for the requested resource</li>
 * <li>Session is valid and not expired</li>
 * </ul>
 * Public resources such as login, registration, CSS, and JavaScript files are
 * accessible without authentication.
 *
 * @author kolisang
 * @version 1.0
 */
@WebFilter("/*")  // Intercept ALL requests
public class AuthenticationFilter implements Filter {

    /**
     * Logger for recording authentication and authorization events
     */
    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());

    /**
     * Public resources that don't require authentication
     */
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/",
            "/index.jsp",
            "/login.jsp",
            "/login",
            "/logout",
            "/forgot-password",
            "/forgot-password.jsp",
            "/reset-password",
            "/reset-password.jsp",
            "/supplier/register",
            "/supplier/register.jsp",
            "/css/",
            "/js/",
            "/images/",
            "/errors/",
            "/landing.css",
            "/register.css",
            "/ministry-style.css"
    );
    /**
     * Path prefix for Procurement Officer resources
     */
    private static final String OFFICER_PATH = "/officer/";
    /**
     * Path prefix for Supplier resources
     */
    private static final String SUPPLIER_PATH = "/supplier/";
    /**
     * Path prefix for Evaluation Committee resources
     */
    private static final String EVALUATOR_PATH = "/evaluator/";

    /**
     * Called when the filter is first initialized by the servlet container.
     * Logs the initialization event for auditing purposes.
     *
     * @param filterConfig the filter configuration object
     * @throws ServletException if initialization fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized - Protecting ProcureGov resources");
    }

    /**
     * Filters incoming requests to enforce authentication and role-based
     * authorization.
     * <p>
     * Processing flow:
     * <ol>
     * <li>Checks if the requested path is public (allows access if so)</li>
     * <li>Redirects unauthenticated users to login page</li>
     * <li>Validates user role against path requirements</li>
     * <li>Returns HTTP 403 for unauthorized role access</li>
     * <li>Checks session expiry and warns if near timeout</li>
     * <li>Allows access if all checks pass</li>
     * </ol>
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

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Log the request
        logger.fine("Filtering request: " + path);

        // Check if this is a public resource
        if (isPublicResource(path)) {
            logger.fine("Public resource accessed: " + path);
            chain.doFilter(request, response);
            return;
        }

        // Check authentication
        HttpSession session = req.getSession(false);

        // ============================================================
        // FIX: Handle stale sessions properly
        // ============================================================
        if (session == null || session.getAttribute("user") == null) {

            // If session exists but has no user (stale session), invalidate it
            if (session != null) {
                logger.warning("Stale session detected - invalidating");
                try {
                    session.invalidate();
                } catch (IllegalStateException e) {
                    // Session was already invalidated
                }
            }

            logger.warning("Unauthenticated access attempt to: " + path);

            // Create a NEW session for the redirect URL
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("redirectAfterLogin", path);

            resp.sendRedirect(contextPath + "/login.jsp?error=Please login to access this page");
            return;
        }

        // User is authenticated - check authorization
        User user = (User) session.getAttribute("user");

        if (!isAuthorized(user, path)) {
            // User doesn't have permission
            logger.warning("Unauthorized access attempt by " + user.getEmail()
                    + " (Role: " + user.getRole() + ") to: " + path);

            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access Denied: You do not have permission to access this resource.");
            return;
        }

        // Check if session is about to expire (warn user)
        checkSessionExpiry(session, req);

        // All checks passed - allow access
        logger.fine("Authorized access by " + user.getEmail() + " to: " + path);
        chain.doFilter(request, response);
    }

    /**
     * Checks if the requested path is publicly accessible without
     * authentication. Verifies against the list of public paths and static
     * resource file extensions.
     *
     * @param path the request path to check
     * @return true if the path is publicly accessible, false if authentication
     * is required
     */
    private boolean isPublicResource(String path) {
        // Check exact matches
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) || path.startsWith(publicPath)) {
                return true;
            }
        }

        // Check file extensions for static resources
        if (path.endsWith(".css") || path.endsWith(".js")
                || path.endsWith(".png") || path.endsWith(".jpg")
                || path.endsWith(".ico") || path.endsWith(".svg")
                || path.endsWith(".woff") || path.endsWith(".woff2")) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the authenticated user has permission to access the requested
     * path. Enforces role-based access control:
     * <ul>
     * <li>PROCUREMENT_OFFICER can access /officer/, /shared/, and
     * /dashboard.jsp</li>
     * <li>SUPPLIER can access /supplier/, /shared/, and /dashboard.jsp</li>
     * <li>EVALUATION_COMMITTEE can access /evaluator/, /shared/, and
     * /dashboard.jsp</li>
     * </ul>
     *
     * @param user the authenticated user
     * @param path the request path to check
     * @return true if the user is authorized to access the path, false
     * otherwise
     */
    private boolean isAuthorized(User user, String path) {
        UserRole role = user.getRole();

        // Procurement Officer can access officer paths and shared resources
        if (role == UserRole.PROCUREMENT_OFFICER) {
            return path.startsWith(OFFICER_PATH)
                    || path.startsWith("/shared/")
                    || path.equals("/dashboard.jsp");
        }

        // Supplier can access supplier paths
        if (role == UserRole.SUPPLIER) {
            return path.startsWith(SUPPLIER_PATH)
                    || path.startsWith("/shared/")
                    || path.equals("/dashboard.jsp");
        }

        // Evaluation Committee can access evaluator paths
        if (role == UserRole.EVALUATION_COMMITTEE) {
            return path.startsWith(EVALUATOR_PATH)
                    || path.startsWith("/shared/")
                    || path.equals("/dashboard.jsp");
        }

        return false;
    }

    /**
     * Checks session expiry and warns the user if the session is about to
     * expire. Sets a warning attribute on the request when less than 5 minutes
     * remain before session timeout.
     *
     * @param session the current HTTP session
     * @param req the HTTP servlet request to set the warning attribute on
     */
    private void checkSessionExpiry(HttpSession session, HttpServletRequest req) {
        // Session timeout in seconds (30 minutes = 1800 seconds)
        int maxInactiveInterval = session.getMaxInactiveInterval();
        long lastAccessedTime = session.getLastAccessedTime();
        long currentTime = System.currentTimeMillis();

        long timeLeftSeconds = maxInactiveInterval - ((currentTime - lastAccessedTime) / 1000);

        // Warn if less than 5 minutes remaining
        if (timeLeftSeconds < 300 && timeLeftSeconds > 0) {
            long minutesLeft = timeLeftSeconds / 60;
            req.setAttribute("sessionWarning",
                    "Your session will expire in " + minutesLeft + " minute(s). Please save your work.");
        }
    }

    /**
     * Called when the filter is being taken out of service by the servlet
     * container. Logs the destruction event for auditing purposes.
     */
    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed");
    }
}
