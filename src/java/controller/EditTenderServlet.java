package controller;

import dao.DAOException;
import dao.impl.TenderDAOImpl;
import dao.interfaces.TenderDAO;
import model.Tender;
import model.User;
import model.enums.TenderCategory;
import model.enums.TenderStatus;
import model.enums.UserRole;
import service.FileUploadHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.CSRFTokenUtil;

@WebServlet("/officer/edit-tender")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class EditTenderServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(EditTenderServlet.class.getName());
    private TenderDAO tenderDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            logger.info("TenderDAO initialized successfully");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize TenderDAO", e);
            throw new ServletException("Cannot initialize TenderDAO", e);
        }
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
        if (currentUser.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String tenderIdParam = req.getParameter("id");
        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                req.setAttribute("errorMessage", "Tender not found.");
                req.getRequestDispatcher("/officer/edit-tender.jsp").forward(req, resp);
                return;
            }

            // Set minimum date (today + 7 days)
            LocalDateTime minDate = LocalDateTime.now().plusDays(7);
            req.setAttribute("minDate", minDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

            req.setAttribute("tender", tender);
            req.getRequestDispatcher("/officer/edit-tender.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading tender for edit", e);
            req.setAttribute("errorMessage", "Error loading tender. Please try again.");
            req.getRequestDispatcher("/officer/edit-tender.jsp").forward(req, resp);
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
        if (currentUser.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        List<String> errors = new ArrayList<>();

        try {
            int tenderId = Integer.parseInt(req.getParameter("tenderId"));
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                errors.add("Tender not found.");
            } else if (tender.getStatus() != TenderStatus.DRAFT) {
                errors.add("Only draft tenders can be edited.");
            }

            // Validate and update fields
            String title = req.getParameter("title");
            if (title == null || title.trim().length() < 10) {
                errors.add("Tender title must be at least 10 characters.");
            } else {
                tender.setTitle(title.trim());
            }

            String categoryParam = req.getParameter("category");
            if (categoryParam == null || categoryParam.trim().isEmpty()) {
                errors.add("Please select a tender category.");
            } else {
                try {
                    tender.setCategory(TenderCategory.valueOf(categoryParam));
                } catch (IllegalArgumentException e) {
                    errors.add("Invalid tender category.");
                }
            }

            String description = req.getParameter("description");
            if (description == null || description.trim().length() < 50) {
                errors.add("Description must be at least 50 characters.");
            } else {
                tender.setDescription(description.trim());
            }

            String estimatedValueParam = req.getParameter("estimatedValue");
            if (estimatedValueParam == null || estimatedValueParam.trim().isEmpty()) {
                errors.add("Estimated value is required.");
            } else {
                try {
                    BigDecimal estimatedValue = new BigDecimal(estimatedValueParam);
                    if (estimatedValue.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.add("Estimated value must be greater than zero.");
                    } else {
                        tender.setEstimatedValue(estimatedValue);
                    }
                } catch (NumberFormatException e) {
                    errors.add("Invalid estimated value format.");
                }
            }

            String closingDate = req.getParameter("closingDate");
            String closingTime = req.getParameter("closingTime");

            if (closingDate == null || closingDate.trim().isEmpty()) {
                errors.add("Closing date is required.");
            } else if (closingTime == null || closingTime.trim().isEmpty()) {
                errors.add("Closing time is required.");
            } else {
                try {
                    LocalDate date = LocalDate.parse(closingDate);
                    LocalTime time = LocalTime.parse(closingTime);
                    LocalDateTime closingDateTime = LocalDateTime.of(date, time);

                    LocalDateTime minAllowed = LocalDateTime.now().plusDays(7);
                    if (closingDateTime.isBefore(minAllowed)) {
                        errors.add("Closing date must be at least 7 days from today.");
                    } else {
                        tender.setClosingDateTime(closingDateTime);
                    }
                } catch (DateTimeParseException e) {
                    errors.add("Invalid date or time format.");
                }
            }

            // Handle file upload (optional for edit)
            Part filePart = req.getPart("tenderDocument");
            if (filePart != null && filePart.getSize() > 0) {
                String uploadDir = getServletContext().getInitParameter("uploadDirectory");
                FileUploadHandler uploadHandler = new FileUploadHandler(uploadDir);
                String filePath = uploadHandler.uploadTenderNotice(filePart, tender.getReferenceNumber());
                tender.setTenderNoticePath(filePath);
            }

            if (!errors.isEmpty()) {
                req.setAttribute("validationErrors", errors);
                req.setAttribute("tender", tender);
                req.getRequestDispatcher("/officer/edit-tender.jsp").forward(req, resp);
                return;
            }

            boolean updated = tenderDAO.update(tender);

            if (updated) {
                logger.info("Tender updated: " + tender.getReferenceNumber() + " by " + currentUser.getEmail());
                session.setAttribute("successMessage", "Tender " + tender.getReferenceNumber() + " updated successfully.");
                resp.sendRedirect(req.getContextPath() + "/officer/tenders");
            } else {
                errors.add("Failed to update tender.");
                req.setAttribute("validationErrors", errors);
                req.setAttribute("tender", tender);
                req.getRequestDispatcher("/officer/edit-tender.jsp").forward(req, resp);
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error updating tender", e);
            errors.add("Database error. Please try again.");
            req.setAttribute("validationErrors", errors);
            req.getRequestDispatcher("/officer/edit-tender.jsp").forward(req, resp);
        }
    }
}
