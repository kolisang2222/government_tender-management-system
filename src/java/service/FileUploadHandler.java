package service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for handling file uploads in the ProcureGov system. Manages
 * file validation, storage, and retrieval operations.
 *
 * @author kolisang
 * @version 1.0
 */
public class FileUploadHandler {

    /**
     * Logger for recording file upload operations and errors
     */
    private static final Logger logger = Logger.getLogger(FileUploadHandler.class.getName());

    // File size limits (in bytes)
    /**
     * Maximum file size for tender notice documents (5 MB)
     */
    public static final long TENDER_NOTICE_MAX_SIZE = 5 * 1024 * 1024;      // 5 MB
    /**
     * Maximum file size for bid documents (10 MB)
     */
    public static final long BID_DOCUMENT_MAX_SIZE = 10 * 1024 * 1024;      // 10 MB
    /**
     * Maximum file size for supplier documents (5 MB)
     */
    public static final long SUPPLIER_DOCUMENT_MAX_SIZE = 5 * 1024 * 1024;  // 5 MB

    // Allowed file types
    /**
     * Allowed file extensions for tender documents
     */
    public static final String[] ALLOWED_TENDER_DOC_TYPES = {".pdf"};
    /**
     * Allowed file extensions for bid documents
     */
    public static final String[] ALLOWED_BID_DOC_TYPES = {".pdf", ".docx", ".doc"};
    /**
     * Allowed file extensions for supplier documents
     */
    public static final String[] ALLOWED_SUPPLIER_DOC_TYPES = {".pdf"};

    // Subdirectories
    /**
     * Subdirectory name for tender notice files
     */
    public static final String TENDER_NOTICES_DIR = "tender-notices";
    /**
     * Subdirectory name for bid document files
     */
    public static final String BID_DOCUMENTS_DIR = "bid-documents";
    /**
     * Subdirectory name for supplier document files
     */
    public static final String SUPPLIER_DOCUMENTS_DIR = "supplier-documents";

    /**
     * The base directory where all uploaded files are stored
     */
    private String baseUploadDirectory;

    /**
     * Constructor - initializes the upload directory and creates all required
     * subdirectories.
     *
     * @param baseUploadDirectory The base directory for all uploads
     */
    public FileUploadHandler(String baseUploadDirectory) {
        this.baseUploadDirectory = baseUploadDirectory;
        initializeDirectories();
    }

    /**
     * Default constructor - uses system temp directory with
     * application-specific subdirectory. The default directory will be: {system
     * temp dir}/kolisangphatela2334120
     */
    public FileUploadHandler() {
        this(System.getProperty("java.io.tmpdir") + File.separator + "kolisangphatela2334120");
    }

    /**
     * Initializes all required directories for storing different document
     * types. Creates tender notices, bid documents, and supplier documents
     * subdirectories.
     */
    private void initializeDirectories() {
        try {
            createDirectory(TENDER_NOTICES_DIR);
            createDirectory(BID_DOCUMENTS_DIR);
            createDirectory(SUPPLIER_DOCUMENTS_DIR);
            logger.info("Upload directories initialized at: " + baseUploadDirectory);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize upload directories", e);
        }
    }

    /**
     * Creates a subdirectory under the base upload directory if it doesn't
     * already exist.
     *
     * @param subDirectory the name of the subdirectory to create
     * @throws IOException if directory creation fails
     */
    private void createDirectory(String subDirectory) throws IOException {
        Path dirPath = Paths.get(baseUploadDirectory, subDirectory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            logger.info("Created directory: " + dirPath);
        }
    }

    // ============================================================
    // SUPPLIER DOCUMENT UPLOAD METHODS
    // ============================================================
    /**
     * Handles supplier document upload (tax certificate, license, profile).
     * Uses default allowed extensions and size limits for supplier documents.
     *
     * @param filePart The Part object containing the uploaded file
     * @param filePrefix Prefix for the generated filename
     * @return The server file path where the document was saved
     * @throws IOException if file operations fail
     * @throws ServletException if upload validation fails
     */
    public String uploadSupplierDocument(Part filePart, String filePrefix)
            throws IOException, ServletException {

        return uploadFile(
                filePart,
                SUPPLIER_DOCUMENTS_DIR,
                filePrefix,
                ALLOWED_SUPPLIER_DOC_TYPES,
                SUPPLIER_DOCUMENT_MAX_SIZE
        );
    }

    /**
     * Handles supplier document upload with custom validation rules. Allows
     * specifying custom allowed extensions and maximum file size.
     *
     * @param filePart The Part object containing the uploaded file
     * @param filePrefix Prefix for the generated filename
     * @param allowedExtensions Array of allowed file extensions (e.g., {".pdf",
     * ".doc"})
     * @param maxSize Maximum file size in bytes
     * @return The server file path where the document was saved
     * @throws IOException if file operations fail
     * @throws ServletException if upload validation fails
     */
    public String uploadSupplierDocument(Part filePart, String filePrefix,
            String[] allowedExtensions, long maxSize)
            throws IOException, ServletException {

        return uploadFile(
                filePart,
                SUPPLIER_DOCUMENTS_DIR,
                filePrefix,
                allowedExtensions,
                maxSize
        );
    }

    /**
     * Generic document upload method for any document type. Delegates to
     * {@link #uploadSupplierDocument(Part, String)} with default settings.
     *
     * @param filePart The Part object containing the uploaded file
     * @param filePrefix Prefix for the generated filename
     * @return The server file path where the document was saved
     * @throws IOException if file operations fail
     * @throws ServletException if upload validation fails
     */
    public String uploadDocument(Part filePart, String filePrefix)
            throws IOException, ServletException {

        return uploadSupplierDocument(filePart, filePrefix);
    }

    // ============================================================
    // TENDER NOTICE UPLOAD METHODS
    // ============================================================
    /**
     * Handles tender notice document upload. Validates against allowed tender
     * document types and size limits.
     *
     * @param filePart The Part object containing the uploaded file
     * @param tenderReference The tender reference number for file naming
     * @return The server file path where the document was saved
     * @throws IOException if file operations fail
     * @throws ServletException if upload validation fails
     */
    public String uploadTenderNotice(Part filePart, String tenderReference)
            throws IOException, ServletException {

        return uploadFile(
                filePart,
                TENDER_NOTICES_DIR,
                tenderReference,
                ALLOWED_TENDER_DOC_TYPES,
                TENDER_NOTICE_MAX_SIZE
        );
    }

    /**
     * Handles bid supporting document upload. Generates a file prefix based on
     * tender and supplier IDs for unique identification.
     *
     * @param filePart The Part object containing the uploaded file
     * @param tenderId The tender ID
     * @param supplierId The supplier ID
     * @return The server file path where the document was saved
     * @throws IOException if file operations fail
     * @throws ServletException if upload validation fails
     */
    public String uploadBidDocument(Part filePart, int tenderId, int supplierId)
            throws IOException, ServletException {

        String filePrefix = "bid_" + tenderId + "_" + supplierId;

        return uploadFile(
                filePart,
                BID_DOCUMENTS_DIR,
                filePrefix,
                ALLOWED_BID_DOC_TYPES,
                BID_DOCUMENT_MAX_SIZE
        );
    }

    // ============================================================
    // CORE UPLOAD METHOD
    // ============================================================
    /**
     * Core file upload method with comprehensive validation. Validates file
     * existence, extension, and size before saving to the server. Generates a
     * safe filename with timestamp and UUID to prevent collisions.
     *
     * @param filePart The Part object containing the uploaded file
     * @param subDirectory The subdirectory to save the file in
     * @param filePrefix Prefix for the generated filename
     * @param allowedExtensions Array of allowed file extensions
     * @param maxSize Maximum file size in bytes
     * @return The server file path where the file was saved
     * @throws IOException if file operations fail
     * @throws ServletException if upload validation fails (no file, invalid
     * type, or size exceeded)
     */
    private String uploadFile(Part filePart, String subDirectory, String filePrefix,
            String[] allowedExtensions, long maxSize)
            throws IOException, ServletException {

        // Validate file exists
        if (filePart == null || filePart.getSize() == 0) {
            throw new ServletException("No file was uploaded.");
        }

        // Get submitted filename
        String submittedFileName = getSubmittedFileName(filePart);
        if (submittedFileName == null || submittedFileName.trim().isEmpty()) {
            throw new ServletException("Invalid file name.");
        }

        // Validate file extension
        String fileExtension = getFileExtension(submittedFileName);
        if (!isAllowedExtension(fileExtension, allowedExtensions)) {
            throw new ServletException(
                    "Invalid file type. Allowed types: " + String.join(", ", allowedExtensions)
            );
        }

        // Validate file size
        if (filePart.getSize() > maxSize) {
            throw new ServletException(
                    "File size exceeds limit. Maximum size: " + formatFileSize(maxSize)
            );
        }

        // Generate safe filename
        String safeFileName = generateSafeFileName(filePrefix, fileExtension);

        // Create full path
        Path uploadPath = Paths.get(baseUploadDirectory, subDirectory);
        Path filePath = uploadPath.resolve(safeFileName);

        // Save the file
        Files.copy(filePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        logger.info("File uploaded successfully: " + filePath
                + " (Size: " + formatFileSize(filePart.getSize()) + ")");

        return filePath.toString();
    }

    // ============================================================
    // FILE VALIDATION METHODS
    // ============================================================
    /**
     * Extracts the submitted filename from a Part object's content-disposition
     * header. Parses the header to find the filename parameter and returns just
     * the filename without any path information.
     *
     * @param part the Part object containing the file upload
     * @return the submitted filename, or null if no filename could be extracted
     */
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

    /**
     * Extracts the file extension from a filename. Returns the extension in
     * lowercase, including the dot (e.g., ".pdf").
     *
     * @param fileName the filename to extract extension from
     * @return the file extension in lowercase (e.g., ".pdf"), or empty string
     * if no extension found
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot).toLowerCase();
        }
        return "";
    }

    /**
     * Checks if a file extension is in the list of allowed extensions.
     * Comparison is case-insensitive.
     *
     * @param extension the file extension to check (e.g., ".pdf")
     * @param allowedExtensions array of allowed file extensions
     * @return true if the extension is allowed, false otherwise
     */
    private boolean isAllowedExtension(String extension, String[] allowedExtensions) {
        for (String allowed : allowedExtensions) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a safe filename using the provided prefix, current timestamp,
     * and a UUID fragment. Replaces any unsafe characters in the prefix with
     * underscores to prevent path traversal.
     *
     * @param prefix the prefix for the generated filename
     * @param extension the file extension to append (e.g., ".pdf")
     * @return a safe, unique filename in the format:
     * prefix_timestamp_uuid.extension
     */
    private String generateSafeFileName(String prefix, String extension) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String safePrefix = prefix.replaceAll("[^a-zA-Z0-9_-]", "_");

        return safePrefix + "_" + timestamp + "_" + uuid + extension;
    }

    // ============================================================
    // FILE RETRIEVAL METHODS
    // ============================================================
    /**
     * Gets a file as a Path object if it exists and is not a directory.
     *
     * @param filePath The full server file path
     * @return Path object representing the file, or null if the file doesn't
     * exist or is a directory
     */
    public Path getFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return null;
        }

        Path path = Paths.get(filePath);
        if (Files.exists(path) && !Files.isDirectory(path)) {
            return path;
        }

        return null;
    }

    /**
     * Checks if a file exists at the specified path.
     *
     * @param filePath The file path to check
     * @return true if the file exists and is not a directory, false otherwise
     */
    public boolean fileExists(String filePath) {
        return getFilePath(filePath) != null;
    }

    /**
     * Gets the file size as a human-readable formatted string.
     *
     * @param filePath The file path
     * @return Formatted file size (e.g., "2.5 MB"), or "N/A" if the file
     * doesn't exist
     */
    public String getFileSize(String filePath) {
        Path path = getFilePath(filePath);
        if (path == null) {
            return "N/A";
        }

        try {
            return formatFileSize(Files.size(path));
        } catch (IOException e) {
            return "N/A";
        }
    }

    /**
     * Determines the MIME content type for a file. First attempts to probe the
     * file type, then falls back to extension-based detection.
     *
     * @param filePath The file path
     * @return MIME type string (e.g., "application/pdf"), or
     * "application/octet-stream" if unknown
     */
    public String getContentType(String filePath) {
        Path path = getFilePath(filePath);
        if (path == null) {
            return "application/octet-stream";
        }

        try {
            String contentType = Files.probeContentType(path);
            return contentType != null ? contentType : "application/octet-stream";
        } catch (IOException e) {
            String extension = getFileExtension(filePath);

            if (".pdf".equalsIgnoreCase(extension)) {
                return "application/pdf";
            } else if (".docx".equalsIgnoreCase(extension) || ".doc".equalsIgnoreCase(extension)) {
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }

            return "application/octet-stream";
        }
    }

    /**
     * Extracts just the filename from a full file path.
     *
     * @param filePath The full file path
     * @return Just the filename portion, or null if the file path is null
     */
    public String getFileName(String filePath) {
        if (filePath == null) {
            return null;
        }
        return Paths.get(filePath).getFileName().toString();
    }

    // ============================================================
    // FILE DELETION METHODS
    // ============================================================
    /**
     * Deletes a file from the server if it exists.
     *
     * @param filePath The file path to delete
     * @return true if deletion was successful, false if the file doesn't exist
     * or deletion failed
     */
    public boolean deleteFile(String filePath) {
        Path path = getFilePath(filePath);
        if (path == null) {
            return false;
        }

        try {
            Files.delete(path);
            logger.info("File deleted: " + filePath);
            return true;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to delete file: " + filePath, e);
            return false;
        }
    }

    /**
     * Safely attempts to delete a file, suppressing any errors that occur.
     * Useful for cleanup operations where failures should not interrupt the
     * main flow.
     *
     * @param filePath The file path to delete
     */
    public void deleteFileQuietly(String filePath) {
        try {
            if (filePath != null) {
                Files.deleteIfExists(Paths.get(filePath));
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    // ============================================================
    // DIRECTORY METHODS
    // ============================================================
    /**
     * Gets the base upload directory path where all uploaded files are stored.
     *
     * @return the base upload directory path as a string
     */
    public String getBaseUploadDirectory() {
        return baseUploadDirectory;
    }

    /**
     * Gets the full path for the tender notices directory.
     *
     * @return the full path to the tender notices subdirectory
     */
    public String getTenderNoticesDirectory() {
        return Paths.get(baseUploadDirectory, TENDER_NOTICES_DIR).toString();
    }

    /**
     * Gets the full path for the bid documents directory.
     *
     * @return the full path to the bid documents subdirectory
     */
    public String getBidDocumentsDirectory() {
        return Paths.get(baseUploadDirectory, BID_DOCUMENTS_DIR).toString();
    }

    /**
     * Gets the full path for the supplier documents directory.
     *
     * @return the full path to the supplier documents subdirectory
     */
    public String getSupplierDocumentsDirectory() {
        return Paths.get(baseUploadDirectory, SUPPLIER_DOCUMENTS_DIR).toString();
    }

    /**
     * Cleans up old files in a specified subdirectory. Deletes files that are
     * older than the specified number of days.
     *
     * @param subDirectory The subdirectory to clean (e.g., TENDER_NOTICES_DIR,
     * BID_DOCUMENTS_DIR)
     * @param olderThanDays Delete files older than this many days
     * @return Number of files successfully deleted
     */
    public int cleanupOldFiles(String subDirectory, int olderThanDays) {
        Path dirPath = Paths.get(baseUploadDirectory, subDirectory);
        if (!Files.exists(dirPath)) {
            return 0;
        }

        long cutoffTime = System.currentTimeMillis() - (olderThanDays * 24L * 60 * 60 * 1000);
        int deletedCount = 0;

        try {
            File[] files = dirPath.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.lastModified() < cutoffTime) {
                        if (file.delete()) {
                            deletedCount++;
                        }
                    }
                }
            }
            logger.info("Cleaned up " + deletedCount + " old files from " + subDirectory);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error during file cleanup", e);
        }

        return deletedCount;
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================
    /**
     * Formats a file size in bytes into a human-readable string. Converts to
     * appropriate units (B, KB, MB, GB) for display.
     *
     * @param bytes File size in bytes
     * @return Human-readable file size string (e.g., "2.5 MB", "500 KB", "100
     * B")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }

        int unit = 1024;
        String[] units = {"KB", "MB", "GB"};
        int exp = (int) (Math.log(bytes) / Math.log(unit));

        if (exp >= units.length) {
            exp = units.length - 1;
        }

        double size = bytes / Math.pow(unit, exp);
        return String.format("%.1f %s", size, units[exp]);
    }

    /**
     * Validates a file part for upload without actually saving it. Checks file
     * existence, name validity, extension, and size.
     *
     * @param filePart The file part to validate
     * @param maxSize Maximum allowed size in bytes
     * @param allowedExtensions Allowed file extensions
     * @return Error message if validation fails, or null if the file is valid
     */
    public String validateFileUpload(Part filePart, long maxSize, String[] allowedExtensions) {
        if (filePart == null || filePart.getSize() == 0) {
            return "No file was uploaded.";
        }

        String fileName = getSubmittedFileName(filePart);
        if (fileName == null || fileName.trim().isEmpty()) {
            return "Invalid file name.";
        }

        String extension = getFileExtension(fileName);
        if (!isAllowedExtension(extension, allowedExtensions)) {
            return "Invalid file type. Allowed: " + String.join(", ", allowedExtensions);
        }

        if (filePart.getSize() > maxSize) {
            return "File too large. Maximum: " + formatFileSize(maxSize);
        }

        return null;
    }

    /**
     * Calculates the total size of all files in a subdirectory.
     *
     * @param subDirectory The subdirectory to check
     * @return Total size in bytes of all files in the directory; returns 0 if
     * directory doesn't exist
     */
    public long getDirectorySize(String subDirectory) {
        Path dirPath = Paths.get(baseUploadDirectory, subDirectory);
        if (!Files.exists(dirPath)) {
            return 0;
        }

        long totalSize = 0;
        try {
            File[] files = dirPath.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        totalSize += file.length();
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error calculating directory size", e);
        }

        return totalSize;
    }

    /**
     * Gets formatted statistics about storage usage across all upload
     * directories. Shows individual directory sizes and total storage used.
     *
     * @return Formatted multi-line statistics string showing storage usage per
     * directory
     */
    public String getStorageStatistics() {
        long tenderNoticesSize = getDirectorySize(TENDER_NOTICES_DIR);
        long bidDocumentsSize = getDirectorySize(BID_DOCUMENTS_DIR);
        long supplierDocumentsSize = getDirectorySize(SUPPLIER_DOCUMENTS_DIR);
        long totalSize = tenderNoticesSize + bidDocumentsSize + supplierDocumentsSize;

        return String.format(
                "Storage Usage:\n"
                + "  Tender Notices: %s\n"
                + "  Bid Documents: %s\n"
                + "  Supplier Documents: %s\n"
                + "  Total: %s",
                formatFileSize(tenderNoticesSize),
                formatFileSize(bidDocumentsSize),
                formatFileSize(supplierDocumentsSize),
                formatFileSize(totalSize)
        );
    }
}
