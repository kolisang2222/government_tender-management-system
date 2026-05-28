package util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating, storing, and validating Cross-Site Request
 * Forgery (CSRF) tokens.
 * <p>
 * This class provides static methods to help protect state-changing operations
 * within the ProcureGov application. It uses a cryptographically strong random
 * number generator to produce URL-safe tokens which are then stored in the
 * user's HTTP session.
 * </p>
 * <p>
 * Typical usage involves generating a token via {@link #getToken(HttpSession)}
 * before displaying a form, embedding the token as a hidden input field named
 * {@code csrfToken}, and then validating it on the server side using
 * {@link #validateToken(HttpServletRequest)} when the form is submitted.
 * </p>
 *
 * @author Kolisang Phatela
 * @version 1.0
 * @see javax.servlet.http.HttpSession
 */
public class CSRFTokenUtil {

    /**
     * The attribute name used to store the CSRF token within the HttpSession.
     */
    private static final String CSRF_TOKEN_ATTR = "csrfToken";

    /**
     * Cryptographically strong random number generator used for producing
     * tokens.
     */
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a new cryptographically random CSRF token and stores it in the
     * provided HTTP session. The generated token is a 32-byte random value
     * encoded in URL-safe Base64 format without padding.
     *
     * @param session The HTTP session in which to store the generated token.
     * Must not be null.
     * @return The newly generated Base64 URL-encoded token string, or
     * {@code null} if the provided session is {@code null}.
     */
    public static String generateToken(HttpSession session) {
        if (session == null) {
            return null;
        }
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        session.setAttribute(CSRF_TOKEN_ATTR, token);
        return token;
    }

    /**
     * Retrieves the current CSRF token from the provided HTTP session. If no
     * token exists in the session, a new token is automatically generated via
     * {@link #generateToken(HttpSession)} and stored in the session before
     * being returned.
     *
     * @param session The HTTP session from which to retrieve the token. Must
     * not be null.
     * @return The existing or newly generated CSRF token, or {@code null} if
     * the provided session is {@code null}.
     */
    public static String getToken(HttpSession session) {
        if (session == null) {
            return null;
        }
        String token = (String) session.getAttribute(CSRF_TOKEN_ATTR);
        if (token == null) {
            token = generateToken(session);
        }
        return token;
    }

    /**
     * Validates a submitted CSRF token against the token currently stored in
     * the user's HTTP session. This method retrieves the session token from the
     * {@code HttpSession} and compares it with the value of the
     * {@code csrfToken} request parameter.
     *
     * @param request The HTTP request containing the {@code csrfToken}
     * parameter. Must not be null.
     * @return {@code true} if a valid session exists, the session contains a
     * token, the request contains a token parameter, and both tokens match;
     * {@code false} otherwise.
     */
    public static boolean validateToken(HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTR);
        if (sessionToken == null) {
            return false;
        }

        String requestToken = request.getParameter("csrfToken");
        if (requestToken == null) {
            return false;
        }

        return sessionToken.equals(requestToken);
    }

    /**
     * Invalidates the CSRF token by removing it from the provided HTTP session.
     * This method is typically called after a form has been successfully
     * processed to prevent token reuse. If the session is {@code null}, no
     * action is taken.
     *
     * @param session The HTTP session from which to remove the CSRF token
     * attribute. May be null.
     */
    public static void invalidateToken(HttpSession session) {
        if (session != null) {
            session.removeAttribute(CSRF_TOKEN_ATTR);
        }
    }
}
