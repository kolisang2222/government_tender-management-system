package controller;

import dao.DAOException;
import dao.impl.UserDAOImpl;
import dao.interfaces.UserDAO;
import model.User;
import model.enums.UserRole;
import service.FileUploadHandler;
import util.PasswordHasher;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.CSRFTokenUtil;

@WebServlet("/supplier/profile")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class SupplierProfileServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SupplierProfileServlet.class.getName());
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
            User supplier = userDAO.findById(currentUser.getUserId());
            req.setAttribute("supplier", supplier);

            req.getRequestDispatcher("/supplier/profile.jsp").forward(req, resp);

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading profile", e);
            req.setAttribute("errorMessage", "Error loading profile. Please try again.");
            req.getRequestDispatcher("/supplier/profile.jsp").forward(req, resp);
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

        User currentUser = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        try {
            if ("updateCompany".equals(action)) {
                updateCompanyInfo(req, resp, currentUser);
            } else if ("updateContact".equals(action)) {
                updateContactInfo(req, resp, currentUser);
            } else if ("changePassword".equals(action)) {
                changePassword(req, resp, currentUser);
            } else if ("uploadDocument".equals(action)) {
                uploadDocument(req, resp, currentUser);
            } else {
                resp.sendRedirect(req.getContextPath() + "/supplier/profile");
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error updating profile", e);
            session.setAttribute("errorMessage", "Error updating profile. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/supplier/profile");
        }
    }

    private void updateCompanyInfo(HttpServletRequest req, HttpServletResponse resp, User user)
            throws DAOException, IOException {

        List<String> errors = new ArrayList<>();

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String contactNumber = req.getParameter("contactNumber");
        String address = req.getParameter("address");

        if (fullName == null || fullName.trim().length() < 3) {
            errors.add("Company name must be at least 3 characters.");
        }
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            errors.add("Valid email address is required.");
        }
        if (address == null || address.trim().length() < 10) {
            errors.add("Physical address must be at least 10 characters.");
        }

        if (!errors.isEmpty()) {
            req.getSession().setAttribute("validationErrors", errors);
            resp.sendRedirect(req.getContextPath() + "/supplier/profile#company");
            return;
        }

        user.setFullName(fullName);
        user.setEmail(email);
        user.setContactNumber(contactNumber);
        user.setAddress(address);

        boolean updated = userDAO.update(user);

        if (updated) {
            req.getSession().setAttribute("user", user);
            req.getSession().setAttribute("userName", user.getFullName());
            req.getSession().setAttribute("successMessage", "Company information updated successfully.");
        }

        resp.sendRedirect(req.getContextPath() + "/supplier/profile#company");
    }

    private void updateContactInfo(HttpServletRequest req, HttpServletResponse resp, User user)
            throws DAOException, IOException {

        String contactName = req.getParameter("contactName");
        String contactEmail = req.getParameter("contactEmail");
        String contactPhone = req.getParameter("contactPhone");

        user.setContactName(contactName);
        user.setContactEmail(contactEmail);
        user.setContactPhone(contactPhone);

        boolean updated = userDAO.update(user);

        if (updated) {
            req.getSession().setAttribute("successMessage", "Contact information updated successfully.");
        }

        resp.sendRedirect(req.getContextPath() + "/supplier/profile#contact");
    }

    private void changePassword(HttpServletRequest req, HttpServletResponse resp, User user)
            throws DAOException, IOException {

        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            req.getSession().setAttribute("errorMessage", "New passwords do not match.");
            resp.sendRedirect(req.getContextPath() + "/supplier/profile#security");
            return;
        }

        String hashedCurrent = PasswordHasher.hash(currentPassword);
        if (!hashedCurrent.equals(user.getPasswordHash())) {
            req.getSession().setAttribute("errorMessage", "Current password is incorrect.");
            resp.sendRedirect(req.getContextPath() + "/supplier/profile#security");
            return;
        }

        user.setPasswordHash(PasswordHasher.hash(newPassword));
        boolean updated = userDAO.updatePassword(user);

        if (updated) {
            req.getSession().setAttribute("successMessage", "Password changed successfully.");
        }

        resp.sendRedirect(req.getContextPath() + "/supplier/profile#security");
    }

    private void uploadDocument(HttpServletRequest req, HttpServletResponse resp, User user)
            throws DAOException, IOException, ServletException {

        String documentType = req.getParameter("documentType");
        Part filePart = req.getPart("documentFile");

        if (filePart == null || filePart.getSize() == 0) {
            req.getSession().setAttribute("errorMessage", "Please select a file to upload.");
            resp.sendRedirect(req.getContextPath() + "/supplier/profile#documents");
            return;
        }

        String uploadDir = getServletContext().getInitParameter("uploadDirectory");
        FileUploadHandler uploadHandler = new FileUploadHandler(uploadDir);

        String filePath = uploadHandler.uploadDocument(filePart, "supplier_" + user.getUserId() + "_" + documentType);

        if ("tax".equals(documentType)) {
            user.setTaxCertificatePath(filePath);
        } else if ("license".equals(documentType)) {
            user.setLicensePath(filePath);
        } else if ("profile".equals(documentType)) {
            user.setProfilePath(filePath);
        }

        boolean updated = userDAO.update(user);

        if (updated) {
            req.getSession().setAttribute("successMessage", "Document uploaded successfully.");
        }

        resp.sendRedirect(req.getContextPath() + "/supplier/profile#documents");
    }
}
