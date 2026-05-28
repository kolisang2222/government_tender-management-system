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
import model.EvaluationScore;
import model.Tender;
import model.User;
import model.enums.TenderStatus;
import model.enums.UserRole;
import service.EvaluationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/evaluator/score-bids")
public class ScoreBidsServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ScoreBidsServlet.class.getName());
    
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private UserDAO userDAO;
    private EvaluationDAO evaluationDAO;
    private EvaluationService evaluationService;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            userDAO = new UserDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
            evaluationService = new EvaluationService();
            logger.info("ScoreBidsServlet initialized");
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
        
        if (currentUser.getRole() != UserRole.EVALUATION_COMMITTEE) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String tenderIdParam = req.getParameter("tenderId");
        String action = req.getParameter("action");
        String bidIdParam = req.getParameter("bidId");

        try {
            // Case 1: Scoring a specific bid
            if ("score".equals(action) && tenderIdParam != null && bidIdParam != null) {
                int tenderId = Integer.parseInt(tenderIdParam);
                int bidId = Integer.parseInt(bidIdParam);
                Tender tender = tenderDAO.findById(tenderId);
                Bid bid = bidDAO.findById(bidId);
                
                if (tender == null || bid == null) {
                    session.setAttribute("errorMessage", "Tender or Bid not found.");
                    resp.sendRedirect(req.getContextPath() + "/evaluator/score-bids");
                    return;
                }
                
                showScoreForm(req, resp, currentUser, tender, bid);
            }
            // Case 2: Viewing all bids for a tender
            else if (tenderIdParam != null && !tenderIdParam.isEmpty()) {
                int tenderId = Integer.parseInt(tenderIdParam);
                Tender tender = tenderDAO.findById(tenderId);
                
                if (tender == null) {
                    session.setAttribute("errorMessage", "Tender not found.");
                    resp.sendRedirect(req.getContextPath() + "/evaluator/score-bids");
                    return;
                }
                
                if (tender.getStatus() != TenderStatus.UNDER_EVALUATION) {
                    session.setAttribute("errorMessage", 
                        "This tender is not available for scoring. Status: " + tender.getStatus().getDisplayName());
                    resp.sendRedirect(req.getContextPath() + "/evaluator/score-bids");
                    return;
                }
                
                showTenderBids(req, resp, currentUser, tender);
            }
            // Case 3: Showing all available tenders
            else {
                showAvailableTenders(req, resp, currentUser);
            }
            
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid ID format", e);
            session.setAttribute("errorMessage", "Invalid request parameters.");
            resp.sendRedirect(req.getContextPath() + "/evaluator/score-bids");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Database error", e);
            session.setAttribute("errorMessage", "Error loading data. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/evaluator/dashboard.jsp");
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

        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser.getRole() != UserRole.EVALUATION_COMMITTEE) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String action = req.getParameter("action");
        
        if ("submitScore".equals(action)) {
            handleSubmitScore(req, resp, currentUser);
        } else {
            resp.sendRedirect(req.getContextPath() + "/evaluator/score-bids");
        }
    }

    private void showAvailableTenders(HttpServletRequest req, HttpServletResponse resp,
            User currentUser) throws ServletException, IOException, DAOException {
        
        List<Tender> tenders = tenderDAO.findByStatus(TenderStatus.UNDER_EVALUATION);
        
        for (Tender tender : tenders) {
            List<Bid> bids = bidDAO.findByTenderId(tender.getTenderId());
            int totalBids = bids.size();
            int scoredBids = 0;
            
            for (Bid bid : bids) {
                if (evaluationDAO.hasEvaluatorScored(currentUser.getUserId(), bid.getBidId())) {
                    scoredBids++;
                }
            }
            
            tender.setBidCount(totalBids);
            tender.setScoredBids(scoredBids);
            tender.setEvaluatorProgressPercent(totalBids > 0 ? (scoredBids * 100) / totalBids : 0);
        }
        
        req.setAttribute("tenders", tenders);
        req.getRequestDispatcher("/evaluator/score-bids.jsp").forward(req, resp);
    }

    private void showTenderBids(HttpServletRequest req, HttpServletResponse resp,
            User currentUser, Tender tender) throws ServletException, IOException, DAOException {
        
        List<Bid> bids = bidDAO.findByTenderId(tender.getTenderId());
        BigDecimal lowestBid = bidDAO.findLowestBidAmount(tender.getTenderId());
        int shortestTimeline = bidDAO.findShortestTimeline(tender.getTenderId());
        
        for (Bid bid : bids) {
            boolean hasScored = evaluationDAO.hasEvaluatorScored(currentUser.getUserId(), bid.getBidId());
            bid.setHasCurrentUserScored(hasScored);
            
            User supplier = userDAO.findById(bid.getSupplierId());
            if (supplier != null) {
                bid.setSupplierName(supplier.getFullName());
            }
            
            // Calculate price score for display
            if (lowestBid != null && lowestBid.doubleValue() > 0 && bid.getBidAmount() != null) {
                double priceScore = (lowestBid.doubleValue() / bid.getBidAmount().doubleValue()) * 100;
                bid.setFinalScore(Math.round(priceScore * 100.0) / 100.0);
            }
            
            // Calculate timeline score for display
            if (shortestTimeline > 0 && bid.getProposedTimelineDays() > 0) {
                double timelineScore = ((double) shortestTimeline / bid.getProposedTimelineDays()) * 100;
                bid.setRank((int) Math.round(timelineScore));
            }
        }
        
        int scoredBids = 0;
        for (Bid bid : bids) {
            if (bid.isHasCurrentUserScored()) scoredBids++;
        }
        
        req.setAttribute("tender", tender);
        req.setAttribute("bids", bids);
        req.setAttribute("totalBids", bids.size());
        req.setAttribute("scoredBids", scoredBids);
        req.setAttribute("progressPercent", bids.size() > 0 ? (scoredBids * 100) / bids.size() : 0);
        req.setAttribute("lowestBid", lowestBid);
        req.setAttribute("shortestTimeline", shortestTimeline);
        
        req.getRequestDispatcher("/evaluator/tender-bids.jsp").forward(req, resp);
    }

    private void showScoreForm(HttpServletRequest req, HttpServletResponse resp,
            User currentUser, Tender tender, Bid bid) throws ServletException, IOException, DAOException {
        
        boolean hasScored = evaluationDAO.hasEvaluatorScored(currentUser.getUserId(), bid.getBidId());
        BigDecimal lowestBid = bidDAO.findLowestBidAmount(tender.getTenderId());
        int shortestTimeline = bidDAO.findShortestTimeline(tender.getTenderId());
        
        double priceScore = 0;
        double timelineScore = 0;
        
        if (lowestBid != null && lowestBid.doubleValue() > 0 && bid.getBidAmount() != null) {
            priceScore = (lowestBid.doubleValue() / bid.getBidAmount().doubleValue()) * 100;
        }
        if (shortestTimeline > 0 && bid.getProposedTimelineDays() > 0) {
            timelineScore = ((double) shortestTimeline / bid.getProposedTimelineDays()) * 100;
        }
        
        User supplier = userDAO.findById(bid.getSupplierId());
        if (supplier != null) {
            bid.setSupplierName(supplier.getFullName());
        }
        
        EvaluationScore existingScore = null;
        if (hasScored) {
            existingScore = evaluationDAO.findByEvaluatorAndBid(currentUser.getUserId(), bid.getBidId());
        }
        
        req.setAttribute("tender", tender);
        req.setAttribute("bid", bid);
        req.setAttribute("priceScore", Math.round(priceScore * 100.0) / 100.0);
        req.setAttribute("timelineScore", Math.round(timelineScore * 100.0) / 100.0);
        req.setAttribute("lowestBid", lowestBid);
        req.setAttribute("shortestTimeline", shortestTimeline);
        req.setAttribute("hasScored", hasScored);
        req.setAttribute("existingScore", existingScore);
        
        req.getRequestDispatcher("/evaluator/score-form.jsp").forward(req, resp);
    }

    private void handleSubmitScore(HttpServletRequest req, HttpServletResponse resp,
            User currentUser) throws IOException, ServletException {
        
        HttpSession session = req.getSession();
        
        try {
            String tenderIdParam = req.getParameter("tenderId");
            String bidIdParam = req.getParameter("bidId");
            String technicalScoreParam = req.getParameter("technicalScore");
            
            int tenderId = Integer.parseInt(tenderIdParam);
            int bidId = Integer.parseInt(bidIdParam);
            int technicalScore = Integer.parseInt(technicalScoreParam);
            
            if (technicalScore < 0 || technicalScore > 100) {
                session.setAttribute("errorMessage", "Technical score must be between 0 and 100.");
                resp.sendRedirect(req.getContextPath() + "/evaluator/score-bids?tenderId=" + tenderId);
                return;
            }
            
            if (evaluationDAO.hasEvaluatorScored(currentUser.getUserId(), bidId)) {
                session.setAttribute("errorMessage", "You have already scored this bid.");
                resp.sendRedirect(req.getContextPath() + "/evaluator/score-bids?tenderId=" + tenderId);
                return;
            }
            
            evaluationService.saveEvaluationScores(tenderId, bidId, currentUser.getUserId(), technicalScore);
            session.setAttribute("successMessage", "Scores submitted successfully!");
            
            if (evaluationService.isAllEvaluationsComplete(tenderId)) {
                evaluationService.completeEvaluation(tenderId);
                session.setAttribute("successMessage", "All evaluations complete! Tender moved to EVALUATED status.");
            }
            
            resp.sendRedirect(req.getContextPath() + "/evaluator/score-bids?tenderId=" + tenderId);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error submitting score", e);
            session.setAttribute("errorMessage", "Error submitting score. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/evaluator/score-bids");
        }
    }
}