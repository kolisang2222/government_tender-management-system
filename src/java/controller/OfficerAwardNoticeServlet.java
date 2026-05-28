package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.EvaluationDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.impl.UserDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.EvaluationDAO;
import dao.interfaces.TenderDAO;
import dao.interfaces.UserDAO;
import model.Bid;
import model.Tender;
import model.User;
import model.enums.UserRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/officer/award-notice")
public class OfficerAwardNoticeServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OfficerAwardNoticeServlet.class.getName());

    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private EvaluationDAO evaluationDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
            userDAO = new UserDAOImpl();
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
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/officer/tenders");
                return;
            }

            // Get ranked bids
            List<Bid> rankedBids = evaluationDAO.findRankedBidsByTenderId(tenderId);

            // Get total number of evaluators
            List<User> evaluators = userDAO.findByRole(UserRole.EVALUATION_COMMITTEE);
            int totalEvaluators = evaluators.size();

            // Set current date
            String currentDate = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

            req.setAttribute("tender", tender);
            req.setAttribute("rankedBids", rankedBids);
            req.setAttribute("totalEvaluators", totalEvaluators);
            req.setAttribute("currentDate", currentDate);

            req.getRequestDispatcher("/officer/award-notice.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid tender ID format", e);
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading award notice", e);
            session.setAttribute("errorMessage", "Error loading award notice. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/officer/tenders");
        }
    }
}
