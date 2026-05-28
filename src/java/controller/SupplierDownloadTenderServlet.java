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

@WebServlet("/supplier/download-tender")
public class SupplierDownloadTenderServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SupplierDownloadTenderServlet.class.getName());
    private String uploadDirectory;
    private TenderDAO tenderDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            uploadDirectory = getServletContext().getInitParameter("upload.directory");
            if (uploadDirectory == null || uploadDirectory.trim().isEmpty()) {
                uploadDirectory = System.getProperty("catalina.base") + File.separator + "uploads";
            }
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize DAOs", e);
            throw new ServletException("Cannot initialize DAOs", e);
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

        // Allow suppliers to download tender notices
        if (currentUser.getRole() != UserRole.SUPPLIER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String tenderIdParam = req.getParameter("id");

        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/supplier/dashboard.jsp");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/supplier/dashboard.jsp");
                return;
            }

            // Only allow download if tender is OPEN
            if (!tender.getStatus().name().equals("OPEN")) {
                session.setAttribute("errorMessage", "Tender is not open for bidding.");
                resp.sendRedirect(req.getContextPath() + "/supplier/dashboard.jsp");
                return;
            }

            String documentPath = tender.getDocumentPath();

            if (documentPath == null || documentPath.trim().isEmpty()) {
                session.setAttribute("errorMessage", "No document attached to this tender.");
                resp.sendRedirect(req.getContextPath() + "/supplier/tender-detail?id=" + tenderId);
                return;
            }

            File documentFile = new File(documentPath);
            if (!documentFile.exists()) {
                documentFile = new File(uploadDirectory, documentPath);
            }

            if (!documentFile.exists()) {
                session.setAttribute("errorMessage", "Document file not found.");
                resp.sendRedirect(req.getContextPath() + "/supplier/tender-detail?id=" + tenderId);
                return;
            }

            // Download the file
            String fileName = tender.getReferenceNumber() + "_Tender_Notice.pdf";
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            resp.setHeader("Content-Length", String.valueOf(documentFile.length()));

            try (FileInputStream fis = new FileInputStream(documentFile); OutputStream os = resp.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }

            logger.info("Supplier " + currentUser.getEmail() + " downloaded: " + tender.getReferenceNumber());

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/supplier/dashboard.jsp");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error downloading tender document", e);
            session.setAttribute("errorMessage", "Error downloading document.");
            resp.sendRedirect(req.getContextPath() + "/supplier/dashboard.jsp");
        }
    }
}
