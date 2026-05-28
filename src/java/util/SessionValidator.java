/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import model.User;

/**
 * Utility class for validating user authentication state and role-based
 * authorization.
 * <p>
 * This class provides static convenience methods to check whether a user is
 * currently authenticated, verify their assigned role against required
 * permissions, and retrieve the currently logged-in user from the HTTP session.
 * These methods are designed to be called from Servlets and Filters to enforce
 * access control throughout the ProcureGov application.
 * </p>
 * <p>
 * All methods safely handle cases where no valid session exists, returning
 * {@code false} or {@code null} as appropriate rather than throwing exceptions.
 * </p>
 *
 * @author Kolisang Phatela
 * @version 1.0
 * @see model.User
 * @see javax.servlet.http.HttpSession
 */
public class SessionValidator {

    /**
     * Checks whether the current request originates from an authenticated user.
     * <p>
     * A user is considered authenticated if a valid HTTP session exists and
     * that session contains a non-null {@code user} attribute.
     * </p>
     *
     * @param req The HTTP request from which to retrieve the session. Must not
     * be null.
     * @return {@code true} if a session exists and contains a valid user
     * object; {@code false} otherwise.
     */
    public static boolean isAuthenticated(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    /**
     * Verifies whether the currently authenticated user possesses the specified
     * role.
     * <p>
     * This method retrieves the User object from the session and compares its
     * assigned role (case-insensitive) against the required role parameter.
     * </p>
     *
     * @param req The HTTP request containing the user's session. Must not be
     * null.
     * @param requiredRole The role name required for access (e.g., "SUPPLIER",
     * "PROCUREMENT_OFFICER", "EVALUATION_COMMITTEE").
     * @return {@code true} if the user is authenticated and their role matches
     * the required role; {@code false} otherwise.
     */
    public static boolean hasRole(HttpServletRequest req, String requiredRole) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }

        return user.getRole() != null && user.getRole().name().equalsIgnoreCase(requiredRole);
    }

    /**
     * Retrieves the currently authenticated User object from the HTTP session.
     *
     * @param req The HTTP request from which to retrieve the session and user.
     * @return The User object stored in the session, or {@code null} if no
     * valid session exists or no user is authenticated.
     */
    public static User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }

        return (User) session.getAttribute("user");
    }

    /**
     * Retrieves the database ID of the currently authenticated user.
     * <p>
     * This is a convenience method that delegates to
     * {@link #getCurrentUser(HttpServletRequest)} and extracts the user's
     * primary key identifier.
     * </p>
     *
     * @param req The HTTP request containing the user's session.
     * @return The user ID of the authenticated user, or {@code -1} if no user
     * is currently authenticated.
     */
    public static int getCurrentId(HttpServletRequest req) {
        User user = getCurrentUser(req);
        return user != null ? user.getUserId() : -1;
    }
}
