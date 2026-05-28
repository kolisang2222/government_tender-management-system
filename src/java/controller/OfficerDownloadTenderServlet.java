package controller;

import dao.DAOException;
import dao.impl.TenderDAOImpl;
import dao.interfaces.TenderDAO;
import model.Tender;
import model.User;
import model.enums.UserRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet responsible for downloading tender notice documents.
 * Only authenticated users can download tender documents.
 * 
 * @author Kolisang Phatela
 * @version 1.0
 */
@WebServlet("/officer/download-tender")
public class OfficerDownloadTenderServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OfficerDownloadTenderServlet.class.getName());
    
    // Configure this path in web.xml or set as context parameter
    private String uploadDirectory;
    
    private TenderDAO tenderDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            
            // Get upload directory from context or use default
            uploadDirectory = getServletContext().getInitParameter("upload.directory");
            if (uploadDirectory == null || uploadDirectory.trim().isEmpty()) {
                // Default path - change as needed
                uploadDirectory = System.getProperty("catalina.base") + File.separator + "uploads";
            }
            
            // Create directory if it doesn't exist
            File uploadDir = new File(uploadDirectory);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            logger.info("Download servlet initialized. Upload directory: " + uploadDirectory);
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize DAOs", e);
            throw new ServletException("Cannot initialize DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        
        // Check authentication
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        
        // Allow Procurement Officers only (or also suppliers? Adjust as needed)
        if (currentUser.getRole() != UserRole.PROCUREMENT_OFFICER 
                && currentUser.getRole() != UserRole.EVALUATION_COMMITTEE) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String tenderIdParam = req.getParameter("id");
        
        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tender ID is required.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);
            
            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/officer/tenders");
                return;
            }
            
            String documentPath = tender.getDocumentPath();
            
            if (documentPath == null || documentPath.trim().isEmpty()) {
                session.setAttribute("errorMessage", "No document attached to this tender.");
                resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);
                return;
            }
            
            // Handle both absolute and relative paths
            File documentFile = getDocumentFile(documentPath);
            
            if (!documentFile.exists() || !documentFile.isFile()) {
                logger.warning("Document not found: " + documentFile.getAbsolutePath());
                session.setAttribute("errorMessage", "Document file not found on server.");
                resp.sendRedirect(req.getContextPath() + "/officer/tender-detail?id=" + tenderId);
                return;
            }
            
            // Download the file
            downloadFile(resp, documentFile, tender.getReferenceNumber());
            
            logger.info("User " + currentUser.getEmail() + " downloaded tender document: " + 
                       tender.getReferenceNumber());
            
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid tender ID format.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error downloading tender document", e);
            session.setAttribute("errorMessage", "Error downloading document. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        }
    }
    
    /**
     * Gets the actual File object from the stored path.
     * Handles both absolute paths and relative paths.
     * 
     * @param documentPath The stored document path
     * @return File object
     */
    private File getDocumentFile(String documentPath) {
        File documentFile = new File(documentPath);
        
        // If absolute path doesn't exist, try relative to upload directory
        if (!documentFile.exists()) {
            documentFile = new File(uploadDirectory, documentPath);
        }
        
        // If still doesn't exist, try removing any prefix
        if (!documentFile.exists()) {
            String fileName = documentPath;
            int lastSlash = Math.max(documentPath.lastIndexOf('/'), documentPath.lastIndexOf('\\'));
            if (lastSlash != -1) {
                fileName = documentPath.substring(lastSlash + 1);
            }
            documentFile = new File(uploadDirectory, fileName);
        }
        
        return documentFile;
    }
    
    /**
     * Downloads the file to the client.
     * 
     * @param resp HttpServletResponse
     * @param file The file to download
     * @param tenderReference The tender reference number for the filename
     * @throws IOException if IO error occurs
     */
    private void downloadFile(HttpServletResponse resp, File file, String tenderReference) 
            throws IOException {
        
        // Get file extension
        String fileName = file.getName();
        String extension = "";
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot != -1) {
            extension = fileName.substring(lastDot);
        }
        
        // Create download filename
        String downloadFileName = tenderReference + "_Tender_Notice" + extension;
        String encodedFileName = URLEncoder.encode(downloadFileName, "UTF-8")
                .replaceAll("\\+", "%20");
        
        // Set response headers
        resp.setContentType(getContentType(extension));
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        resp.setHeader("Content-Length", String.valueOf(file.length()));
        resp.setHeader("Cache-Control", "private, no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "0");
        
        // Stream the file to response
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = resp.getOutputStream()) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            
            logger.info("File downloaded successfully: " + downloadFileName);
        }
    }
    
    /**
     * Determines the content type based on file extension.
     * 
     * @param extension File extension
     * @return MIME type
     */
    private String getContentType(String extension) {
        switch (extension.toLowerCase()) {
            case ".pdf":
                return "application/pdf";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".txt":
                return "text/plain";
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }
}