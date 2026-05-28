package controller;

import dao.DAOException;
import dao.impl.TenderDAOImpl;
import dao.impl.UserDAOImpl;
import dao.interfaces.TenderDAO;
import dao.interfaces.UserDAO;
import model.Tender;
import model.User;
import model.enums.TenderCategory;
import model.enums.TenderStatus;
import model.enums.UserRole;
import service.EmailNotificationService;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.CSRFTokenUtil;

@WebServlet("/officer/create-tender")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 5 * 1024 * 1024, // 5 MB
        maxRequestSize = 10 * 1024 * 1024 // 10 MB
)
public class TenderCreationServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TenderCreationServlet.class.getName());

    private TenderDAO tenderDAO;
    private UserDAO userDAO;
    private String uploadDirectory;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String[] ALLOWED_FILE_EXTENSIONS = {".pdf"};
    private static final String TENDER_NOTICES_SUBDIR = "tender-notices";

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            logger.info("TenderDAO initialized successfully");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize TenderDAO", e);
            throw new ServletException("Cannot initialize TenderDAO", e);
        }

        userDAO = new UserDAOImpl();
        logger.info("UserDAO initialized successfully");

        uploadDirectory = getServletContext().getInitParameter("uploadDirectory");

        if (uploadDirectory == null || uploadDirectory.trim().isEmpty()) {
            uploadDirectory = getServletContext().getRealPath("/") + "uploads";
            logger.warning("Upload directory not configured. Using default: " + uploadDirectory);
        }

        try {
            Path uploadPath = Paths.get(uploadDirectory, TENDER_NOTICES_SUBDIR);
            Files.createDirectories(uploadPath);
            logger.info("Upload directory created: " + uploadPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create upload directory", e);
            throw new ServletException("Cannot create upload directory", e);
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

        // ADD THIS LINE — generate and store CSRF token in session
        CSRFTokenUtil.generateToken(req.getSession());

        try {
            if (tenderDAO != null) {
                String nextReference = tenderDAO.generateNextReferenceNumber();
                req.setAttribute("generatedReference", nextReference);
            }
        } catch (DAOException e) {
            logger.warning("Could not generate reference number preview: " + e.getMessage());
        }

        LocalDateTime minDate = LocalDateTime.now().plusDays(7);
        req.setAttribute("minDate", minDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

        req.getRequestDispatcher("/officer/create-tender.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // FIXED: was redirecting to /dashboard (404), now correctly goes to /officer/dashboard
        if (!CSRFTokenUtil.validateToken(req)) {
            session.setAttribute("errorMessage", "Invalid security token. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/officer/dashboard");
            return;
        }

        // Invalidate token after use to prevent reuse
        CSRFTokenUtil.invalidateToken(session);

        User officer = (User) session.getAttribute("user");
        if (officer.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        if (tenderDAO == null) {
            logger.severe("TenderDAO is null - attempting to reinitialize");
            try {
                tenderDAO = new TenderDAOImpl();
            } catch (DAOException e) {
                logger.log(Level.SEVERE, "Failed to reinitialize TenderDAO", e);
                req.setAttribute("errorMessage", "System error: Database connection failed.");
                req.getRequestDispatcher("/officer/create-tender.jsp").forward(req, resp);
                return;
            }
        }

        List<String> errors = new ArrayList<>();
        Tender tender = new Tender();

        try {
            validateAndParseForm(req, tender, errors);

            String filePath = handleFileUpload(req, errors);
            if (filePath != null) {
                tender.setTenderNoticePath(filePath);
            }

            if (!errors.isEmpty()) {
                req.setAttribute("validationErrors", errors);
                req.setAttribute("tender", tender);
                req.getRequestDispatcher("/officer/create-tender.jsp").forward(req, resp);
                return;
            }

            String referenceNumber = tenderDAO.generateNextReferenceNumber();
            tender.setReferenceNumber(referenceNumber);
            tender.setCreatedBy(officer.getUserId());

            boolean publishImmediately = "true".equals(req.getParameter("publishImmediately"));
            boolean notifySuppliers = "true".equals(req.getParameter("notifySuppliers"));
            tender.setStatus(publishImmediately ? TenderStatus.OPEN : TenderStatus.DRAFT);

            int tenderId = tenderDAO.create(tender);

            logger.info("Tender created successfully: " + referenceNumber
                    + " by " + officer.getEmail()
                    + " (Status: " + tender.getStatus() + ")");

            if (publishImmediately && notifySuppliers) {
                sendNewTenderNotifications(tender, req);
            }

            String message = publishImmediately
                    ? "Tender " + referenceNumber + " created and published successfully."
                    : "Tender " + referenceNumber + " saved as draft.";

            session.setAttribute("successMessage", message);
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Database error creating tender", e);
            errors.add("Database error: Unable to create tender. Please try again.");
            req.setAttribute("validationErrors", errors);
            req.setAttribute("tender", tender);
            req.getRequestDispatcher("/officer/create-tender.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error creating tender", e);
            errors.add("System error: " + e.getMessage());
            req.setAttribute("validationErrors", errors);
            req.getRequestDispatcher("/officer/create-tender.jsp").forward(req, resp);
        }
    }

    private void sendNewTenderNotifications(Tender tender, HttpServletRequest req) {
        try {
            List<User> suppliers = userDAO.findByRole(UserRole.SUPPLIER);

            if (suppliers.isEmpty()) {
                logger.info("No registered suppliers to notify");
                return;
            }

            String baseUrl = req.getScheme() + "://" + req.getServerName()
                    + ":" + req.getServerPort() + req.getContextPath();
            String tenderUrl = baseUrl + "/supplier/tender-detail?id=" + tender.getTenderId();

            int successCount = 0;
            int failCount = 0;

            for (User supplier : suppliers) {
                try {
                    boolean sent = EmailNotificationService.sendNewTenderNotification(
                            supplier.getEmail(),
                            supplier.getFullName(),
                            tender.getReferenceNumber(),
                            tender.getTitle(),
                            tender.getCategory().getDisplayName(),
                            tender.getFormattedEstimatedValue(),
                            tender.getFormattedClosingDateTime(),
                            tenderUrl
                    );
                    if (sent) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                    logger.warning("Failed to send tender notification to: " + supplier.getEmail());
                }
            }

            logger.info("Tender notifications sent: " + successCount + " successful, " + failCount + " failed");

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error fetching suppliers for notifications", e);
        }
    }

    private void validateAndParseForm(HttpServletRequest req, Tender tender, List<String> errors) {

        String title = req.getParameter("title");
        if (title == null || title.trim().length() < 10) {
            errors.add("Tender title must be at least 10 characters.");
        } else if (title.length() > 200) {
            errors.add("Tender title cannot exceed 200 characters.");
        } else {
            tender.setTitle(title.trim());
        }

        String categoryParam = req.getParameter("category");
        if (categoryParam == null || categoryParam.trim().isEmpty()) {
            errors.add("Please select a tender category.");
        } else {
            try {
                TenderCategory category = TenderCategory.valueOf(categoryParam);
                tender.setCategory(category);
            } catch (IllegalArgumentException e) {
                errors.add("Invalid tender category selected.");
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
                String dateTimeString = closingDate + "T" + closingTime + ":00";
                LocalDateTime closingDateTime = LocalDateTime.parse(
                        dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                LocalDateTime minAllowed = LocalDateTime.now().plusMinutes(1);
                if (closingDateTime.isBefore(minAllowed)) {
                    errors.add("Closing date must be at least 1 minute from now.");
                } else {
                    tender.setClosingDateTime(closingDateTime);
                }
            } catch (DateTimeParseException e) {
                errors.add("Invalid date or time format.");
            }
        }
    }

    private String handleFileUpload(HttpServletRequest req, List<String> errors)
            throws IOException, ServletException {

        Part filePart = req.getPart("tenderDocument");

        if (filePart == null || filePart.getSize() == 0) {
            errors.add("Tender notice document is required.");
            return null;
        }

        String submittedFileName = getSubmittedFileName(filePart);
        if (submittedFileName == null || submittedFileName.trim().isEmpty()) {
            errors.add("Invalid file name.");
            return null;
        }

        if (!isValidFileExtension(submittedFileName)) {
            errors.add("Only PDF files are allowed.");
            return null;
        }

        String contentType = filePart.getContentType();
        if (contentType != null && !contentType.equals("application/pdf")) {
            errors.add("File must be a valid PDF document.");
            return null;
        }

        if (!isValidFileSize(filePart.getSize())) {
            errors.add("File size exceeds " + formatFileSize(MAX_FILE_SIZE) + " limit.");
            return null;
        }

        String safeFileName = generateSafeFileName(submittedFileName);
        String filePath = saveFile(filePart, safeFileName);
        logger.info("File uploaded successfully: " + filePath);
        return filePath;
    }

    private String getSubmittedFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }

        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                return Paths.get(fileName).getFileName().toString();
            }
        }
        return null;
    }

    private boolean isValidFileExtension(String fileName) {
        if (fileName == null) {
            return false;
        }
        String lowerFileName = fileName.toLowerCase();
        for (String extension : ALLOWED_FILE_EXTENSIONS) {
            if (lowerFileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidFileSize(long fileSize) {
        return fileSize > 0 && fileSize <= MAX_FILE_SIZE;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot).toLowerCase() : "";
    }

    private String generateSafeFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        String baseName = extension.length() > 0
                ? originalFileName.substring(0, originalFileName.length() - extension.length())
                : originalFileName;

        String safeBaseName = baseName.replaceAll("[^a-zA-Z0-9_-]", "_");
        if (safeBaseName.length() > 50) {
            safeBaseName = safeBaseName.substring(0, 50);
        }

        return safeBaseName + "_" + timestamp + "_" + uuid + extension;
    }

    private String saveFile(Part filePart, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDirectory, TENDER_NOTICES_SUBDIR);
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(filePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int unit = 1024;
        String[] units = {"KB", "MB", "GB"};
        int exp = Math.min((int) (Math.log(bytes) / Math.log(unit)), units.length - 1);
        return String.format("%.1f %s", bytes / Math.pow(unit, exp), units[exp]);
    }
}
