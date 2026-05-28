package controller;

import dao.DAOException;
import model.User;
import service.SupplierRegistrationService;
import service.ValidationService;
import util.LoggingUtil;
import util.SessionValidator;
import util.CSRFTokenUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet handling supplier registration requests. Acts as the Controller in
 * MVC pattern - delegates business logic to service layer.
 *
 * Validates form data, creates user account with SUPPLIER role, generates
 * unique registration number, and redirects to login.
 *
 * @author kolisang
 * @version 1.0
 * @see service.SupplierRegistrationService
 */
@WebServlet("/supplier/register")
public class SupplierRegistrationServlet extends HttpServlet {

    private static final Logger logger = LoggingUtil.getLogger(SupplierRegistrationServlet.class);

    private static final String REGISTRATION_JSP = "/supplier/register.jsp";
    private static final String LOGIN_PAGE = "/login.jsp";

    private SupplierRegistrationService registrationService;
    private ValidationService validationService;

    @Override
    public void init() throws ServletException {
        registrationService = new SupplierRegistrationService();
        validationService = new ValidationService();
        logger.info("SupplierRegistrationServlet initialized successfully");
    }

    /**
     * Handles GET request - displays the registration form.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Redirect if already logged in
        if (SessionValidator.isAuthenticated(req)) {
            logger.fine("Already authenticated user attempted to access registration page");
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // ============================================================
        // Generate CSRF token for the form
        // ============================================================
        HttpSession session = req.getSession(true);
        CSRFTokenUtil.generateToken(session);

        logger.fine("Displaying supplier registration form");
        req.getRequestDispatcher(REGISTRATION_JSP).forward(req, resp);
    }

    /**
     * Handles POST request - processes the registration form submission.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        logger.info("Processing supplier registration submission from IP: " + req.getRemoteAddr());

        // ============================================================
        // FIXED: Get session properly for CSRF validation
        // ============================================================
        HttpSession session = req.getSession(false);

        // CSRF Protection
        if (!CSRFTokenUtil.validateToken(req)) {
            if (session != null) {
                session.setAttribute("errorMessage", "Invalid security token. Please try again.");
            } else {
                session = req.getSession(true);
                session.setAttribute("errorMessage", "Invalid security token. Please try again.");
            }
            resp.sendRedirect(req.getContextPath() + REGISTRATION_JSP);
            return;
        }

        // Invalidate token after validation to prevent reuse
        if (session != null) {
            CSRFTokenUtil.invalidateToken(session);
        }

        Map<String, String> formData = extractFormData(req);
        Map<String, String> validationErrors = validationService.validateSupplierRegistration(formData);

        if (!validationErrors.isEmpty()) {
            handleValidationFailure(req, resp, formData, validationErrors);
            return;
        }

        try {
            User registeredSupplier = registrationService.registerNewSupplier(formData);
            handleRegistrationSuccess(req, resp, registeredSupplier);

        } catch (DAOException e) {
            handleRegistrationFailure(req, resp, formData, e);
        }
    }

    /**
     * Extracts and sanitizes form data from the request.
     */
    private Map<String, String> extractFormData(HttpServletRequest req) {
        Map<String, String> formData = new HashMap<>();

        formData.put("fullName", validationService.sanitizeInput(req.getParameter("fullName")));
        formData.put("email", validationService.sanitizeInput(req.getParameter("email")));
        formData.put("address", validationService.sanitizeInput(req.getParameter("address")));
        formData.put("contactNumber", validationService.sanitizeInput(req.getParameter("contactNumber")));
        formData.put("password", req.getParameter("password"));
        formData.put("confirmPassword", req.getParameter("confirmPassword"));
        formData.put("agreeTerms", req.getParameter("agreeTerms"));
        formData.put("confirmAccuracy", req.getParameter("confirmAccuracy"));

        return formData;
    }

    /**
     * Handles validation failure by returning to form with error messages.
     */
    private void handleValidationFailure(HttpServletRequest req, HttpServletResponse resp,
            Map<String, String> formData,
            Map<String, String> validationErrors)
            throws ServletException, IOException {

        String errorSummary = String.join("<br>", validationErrors.values());
        logger.warning("Supplier registration validation failed: " + errorSummary.replace("<br>", "; "));

        req.setAttribute("errorMessage", errorSummary);
        req.setAttribute("validationErrors", validationErrors);
        req.setAttribute("fullName", formData.get("fullName"));
        req.setAttribute("email", formData.get("email"));
        req.setAttribute("address", formData.get("address"));
        req.setAttribute("contactNumber", formData.get("contactNumber"));

        req.getRequestDispatcher(REGISTRATION_JSP).forward(req, resp);
    }

    /**
     * Handles successful registration using PRG pattern.
     */
    private void handleRegistrationSuccess(HttpServletRequest req, HttpServletResponse resp,
            User registeredSupplier) throws IOException {

        logger.info("New supplier registered successfully: " + registeredSupplier.getEmail()
                + " (ID: " + registeredSupplier.getUserId()
                + ", Reg#: " + registeredSupplier.getRegistrationNumber() + ")");

        // ============================================================
        // FIXED: Get or create session before setting attribute
        // ============================================================
        HttpSession session = req.getSession(true);
        session.setAttribute("registrationSuccess",
                "Registration successful! Your registration number is "
                + registeredSupplier.getRegistrationNumber() + ". You may now login.");

        resp.sendRedirect(req.getContextPath() + LOGIN_PAGE);
    }

    /**
     * Handles registration failure due to system error.
     */
    private void handleRegistrationFailure(HttpServletRequest req, HttpServletResponse resp,
            Map<String, String> formData, DAOException e)
            throws ServletException, IOException {

        logger.log(Level.SEVERE, "Failed to create supplier account: " + formData.get("email"), e);

        req.setAttribute("errorMessage",
                "Registration failed due to a system error. Please try again later or contact ICT Support.");
        req.setAttribute("fullName", formData.get("fullName"));
        req.setAttribute("email", formData.get("email"));
        req.setAttribute("address", formData.get("address"));
        req.setAttribute("contactNumber", formData.get("contactNumber"));

        req.getRequestDispatcher(REGISTRATION_JSP).forward(req, resp);
    }
}
