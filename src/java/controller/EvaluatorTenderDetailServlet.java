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
import model.enums.TenderStatus;
import model.enums.UserRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/evaluator/tender-detail")
public class EvaluatorTenderDetailServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(EvaluatorTenderDetailServlet.class.getName());

    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private UserDAO userDAO;
    private EvaluationDAO evaluationDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            userDAO = new UserDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
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
        if (currentUser.getRole() != UserRole.EVALUATION_COMMITTEE
                && currentUser.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String tenderIdParam = req.getParameter("id");
        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/evaluator/dashboard");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found.");
                resp.sendRedirect(req.getContextPath() + "/evaluator/dashboard");
                return;
            }

            // Get creator name
            try {
                User creator = userDAO.findById(tender.getCreatedBy());
                if (creator != null) {
                    tender.setCreatedByName(creator.getFullName());
                }
            } catch (DAOException e) {
                tender.setCreatedByName("Unknown");
            }

            // Get bids if tender is CLOSED or beyond (evaluators can see bids after closing)
            List<Bid> bids = null;
            int bidCount = 0;

            if (tender.getStatus() == TenderStatus.CLOSED
                    || tender.getStatus() == TenderStatus.UNDER_EVALUATION
                    || tender.getStatus() == TenderStatus.EVALUATED
                    || tender.getStatus() == TenderStatus.AWARDED) {

                bids = bidDAO.findByTenderId(tenderId);
                bidCount = bids.size();

                // Add supplier names and check if current user has scored
                for (Bid bid : bids) {
                    try {
                        User supplier = userDAO.findById(bid.getSupplierId());
                        if (supplier != null) {
                            bid.setSupplierName(supplier.getFullName());
                        }

                        // Check if current user has scored this bid
                        boolean hasScored = evaluationDAO.hasEvaluatorScored(currentUser.getUserId(), bid.getBidId());
                        bid.setHasCurrentUserScored(hasScored);

                    } catch (DAOException e) {
                        bid.setSupplierName("Unknown Supplier");
                    }
                }
            }

            // Get ranked bids if evaluation complete
            List<Bid> rankedBids = null;
            if (tender.getStatus() == TenderStatus.EVALUATED || tender.getStatus() == TenderStatus.AWARDED) {
                rankedBids = bidDAO.findRankedBidsByTenderId(tenderId);
            }

            // Calculate evaluator's completion percentage
            int scoresSubmitted = 0;
            int completionPercentage = 0;
            if (tender.getStatus() == TenderStatus.UNDER_EVALUATION && bidCount > 0) {
                scoresSubmitted = evaluationDAO.findByEvaluatorAndTender(currentUser.getUserId(), tenderId).size();
                completionPercentage = (scoresSubmitted * 100) / bidCount;
            }

            // Check if evaluation is complete
            boolean evaluationComplete = false;
            if (tender.getStatus() == TenderStatus.UNDER_EVALUATION) {
                evaluationComplete = evaluationDAO.isEvaluationComplete(tenderId);
            }

            req.setAttribute("tender", tender);
            req.setAttribute("bids", bids);
            req.setAttribute("bidCount", bidCount);
            req.setAttribute("rankedBids", rankedBids);
            req.setAttribute("scoresSubmitted", scoresSubmitted);
            req.setAttribute("totalBids", bidCount);
            req.setAttribute("completionPercentage", completionPercentage);
            req.setAttribute("evaluationComplete", evaluationComplete);

            req.getRequestDispatcher("/evaluator/tender-detail.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid tender ID format", e);
            resp.sendRedirect(req.getContextPath() + "/evaluator/dashboard");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading tender details", e);
            session.setAttribute("errorMessage", "Error loading tender details. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/evaluator/dashboard");
        }
    }
}
