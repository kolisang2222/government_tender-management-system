package service;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class responsible for bid evaluation business logic. Handles score
 * calculations, evaluation completion checks, and status transitions.
 *
 * @author Kolisang Phatela
 * @version 1.0
 */
public class EvaluationService {

    /**
     * Logger for recording evaluation operations and events
     */
    private static final Logger logger = Logger.getLogger(EvaluationService.class.getName());

    // Weightage constants as per requirements
    /**
     * Weight for price score in weighted total calculation (40%)
     */
    private static final double PRICE_WEIGHT = 0.40;
    /**
     * Weight for technical compliance score in weighted total calculation (35%)
     */
    private static final double TECHNICAL_WEIGHT = 0.35;
    /**
     * Weight for timeline score in weighted total calculation (25%)
     */
    private static final double TIMELINE_WEIGHT = 0.25;

    /**
     * Data access object for bid operations
     */
    private BidDAO bidDAO;
    /**
     * Data access object for evaluation score operations
     */
    private EvaluationDAO evaluationDAO;
    /**
     * Data access object for tender operations
     */
    private TenderDAO tenderDAO;
    /**
     * Data access object for user operations
     */
    private UserDAO userDAO;

    /**
     * Constructor - initializes DAOs with default implementations. Creates new
     * instances of {@link BidDAOImpl}, {@link EvaluationDAOImpl},
     * {@link TenderDAOImpl}, and {@link UserDAOImpl}.
     *
     * @throws DAOException if any DAO initialization fails
     */
    public EvaluationService() throws DAOException {
        this.bidDAO = new BidDAOImpl();
        this.evaluationDAO = new EvaluationDAOImpl();
        this.tenderDAO = new TenderDAOImpl();
        this.userDAO = new UserDAOImpl();
        logger.info("EvaluationService initialized");
    }

    /**
     * Constructor with dependency injection for testing and flexibility. Allows
     * injection of mock or custom DAO implementations.
     *
     * @param bidDAO the bid data access object to use
     * @param evaluationDAO the evaluation data access object to use
     * @param tenderDAO the tender data access object to use
     * @param userDAO the user data access object to use
     */
    public EvaluationService(BidDAO bidDAO, EvaluationDAO evaluationDAO,
            TenderDAO tenderDAO, UserDAO userDAO) {
        this.bidDAO = bidDAO;
        this.evaluationDAO = evaluationDAO;
        this.tenderDAO = tenderDAO;
        this.userDAO = userDAO;
    }

    /**
     * Calculates the Price Score for a bid. Formula: (Lowest Bid Amount / This
     * Bid Amount) × 100
     *
     * @param bidAmount The bid amount to calculate score for
     * @param lowestBidAmount The lowest bid amount among all bids
     * @return Price score (0-100), rounded to 2 decimal places; returns 0 if
     * either amount is invalid
     */
    public double calculatePriceScore(double bidAmount, double lowestBidAmount) {
        if (lowestBidAmount <= 0 || bidAmount <= 0) {
            return 0;
        }
        double score = (lowestBidAmount / bidAmount) * 100;
        return Math.round(score * 100.0) / 100.0;
    }

    /**
     * Calculates the Delivery Timeline Score for a bid. Formula: (Shortest
     * Proposed Timeline / This Bid's Timeline) × 100
     *
     * @param proposedTimeline The proposed timeline in days
     * @param shortestTimeline The shortest timeline among all bids
     * @return Timeline score (0-100), rounded to 2 decimal places; returns 0 if
     * either timeline is invalid
     */
    public double calculateTimelineScore(int proposedTimeline, int shortestTimeline) {
        if (shortestTimeline <= 0 || proposedTimeline <= 0) {
            return 0;
        }
        double score = ((double) shortestTimeline / proposedTimeline) * 100;
        return Math.round(score * 100.0) / 100.0;
    }

    /**
     * Calculates the Weighted Total Score for a bid. Formula: (Price Score ×
     * 0.40) + (Technical Score × 0.35) + (Timeline Score × 0.25)
     *
     * @param priceScore The calculated price score
     * @param technicalScore The evaluator-entered technical score
     * @param timelineScore The calculated timeline score
     * @return Weighted total score (0-100), rounded to 2 decimal places
     */
    public double calculateWeightedTotal(double priceScore, double technicalScore, double timelineScore) {
        double weightedTotal = (priceScore * PRICE_WEIGHT)
                + (technicalScore * TECHNICAL_WEIGHT)
                + (timelineScore * TIMELINE_WEIGHT);
        return Math.round(weightedTotal * 100.0) / 100.0;
    }

    /**
     * Gets the lowest bid amount for a tender from all submitted bids.
     *
     * @param tenderId The tender ID
     * @return The lowest bid amount, or 0 if no bids exist
     * @throws DAOException if database error occurs
     */
    public double getLowestBidAmount(int tenderId) throws DAOException {
        List<Bid> bids = bidDAO.findByTenderId(tenderId);

        if (bids.isEmpty()) {
            return 0;
        }

        return bids.stream()
                .mapToDouble(bid -> bid.getBidAmount().doubleValue())
                .min()
                .orElse(0);
    }

    /**
     * Gets the shortest proposed timeline for a tender from all submitted bids.
     *
     * @param tenderId The tender ID
     * @return The shortest timeline in days, or 0 if no bids exist
     * @throws DAOException if database error occurs
     */
    public int getShortestTimeline(int tenderId) throws DAOException {
        List<Bid> bids = bidDAO.findByTenderId(tenderId);

        if (bids.isEmpty()) {
            return 0;
        }

        return bids.stream()
                .mapToInt(Bid::getProposedTimelineDays)
                .min()
                .orElse(0);
    }

    /**
     * Saves evaluation scores for a bid. Calculates automatic scores (Price and
     * Timeline) and weighted total, then persists the evaluation.
     *
     * @param tenderId The tender ID
     * @param bidId The bid ID
     * @param evaluatorId The evaluator's user ID
     * @param technicalScore The technical compliance score (0-100)
     * @return The saved {@link EvaluationScore} object with generated ID
     * @throws DAOException if database error occurs
     * @throws IllegalArgumentException if technical score is outside 0-100
     * range
     */
    public EvaluationScore saveEvaluationScores(int tenderId, int bidId,
            int evaluatorId, int technicalScore)
            throws DAOException {

        // Validate technical score range
        if (technicalScore < 0 || technicalScore > 100) {
            throw new IllegalArgumentException("Technical score must be between 0 and 100");
        }

        // Get bid details
        Bid bid = bidDAO.findById(bidId);
        if (bid == null) {
            throw new DAOException("Bid not found with ID: " + bidId);
        }

        // Calculate automatic scores
        double lowestBidAmount = getLowestBidAmount(tenderId);
        int shortestTimeline = getShortestTimeline(tenderId);

        double priceScore = calculatePriceScore(bid.getBidAmount().doubleValue(), lowestBidAmount);
        double timelineScore = calculateTimelineScore(bid.getProposedTimelineDays(), shortestTimeline);
        double weightedTotal = calculateWeightedTotal(priceScore, technicalScore, timelineScore);

        // Create evaluation score object
        EvaluationScore score = new EvaluationScore();
        score.setBidId(bidId);
        score.setEvaluatorId(evaluatorId);
        score.setTechnicalComplianceScore(technicalScore);
        score.setPriceScore(priceScore);
        score.setTimelineScore(timelineScore);
        score.setWeightedTotal(weightedTotal);
        score.setSubmittedAt(LocalDateTime.now());

        // Save to database
        int scoreId = evaluationDAO.submitScore(score);
        score.setScoreId(scoreId);

        logger.info("Evaluation score saved - Bid ID: " + bidId
                + ", Evaluator: " + evaluatorId
                + ", Weighted Total: " + weightedTotal);

        // Update bid's average score
        updateBidAverageScore(bidId);

        return score;
    }

    /**
     * Updates the average final score for a bid based on all evaluator scores.
     * Calculates the average across all evaluators and persists it to the bid
     * record.
     *
     * @param bidId The bid ID
     * @throws DAOException if database error occurs
     */
    public void updateBidAverageScore(int bidId) throws DAOException {
        Double averageScore = evaluationDAO.calculateAverageScore(bidId);

        if (averageScore != null) {
            Bid bid = bidDAO.findById(bidId);
            if (bid != null) {
                bid.setFinalScore(averageScore);
                bidDAO.updateBidFinalScore(bidId, averageScore);
                logger.info("Updated average score for bid " + bidId + ": " + averageScore);
            }
        }
    }

    /**
     * Checks if all evaluations for a tender are complete. Evaluation is
     * complete when every evaluator has scored every bid.
     *
     * @param tenderId The tender ID
     * @return true if all evaluations are complete, false otherwise
     * @throws DAOException if database error occurs
     */
    public boolean isAllEvaluationsComplete(int tenderId) throws DAOException {
        // Get total number of bids for this tender
        List<Bid> bids = bidDAO.findByTenderId(tenderId);
        int totalBids = bids.size();

        if (totalBids == 0) {
            return false;
        }

        // Get total number of evaluators
        List<User> evaluators = userDAO.findByRole(UserRole.EVALUATION_COMMITTEE);
        int totalEvaluators = evaluators.size();

        if (totalEvaluators == 0) {
            return false;
        }

        // Count total scores submitted
        int totalScores = evaluationDAO.countSubmittedScores(tenderId);
        int expectedScores = totalBids * totalEvaluators;

        boolean isComplete = totalScores >= expectedScores;

        if (isComplete) {
            logger.info("All evaluations complete for tender ID: " + tenderId);
        }

        return isComplete;
    }

    /**
     * Gets the evaluation progress statistics for a tender.
     *
     * @param tenderId The tender ID
     * @return Map containing progress statistics (total bids, total evaluators,
     * submitted scores, etc.)
     * @throws DAOException if database error occurs
     */
    public java.util.Map<String, Integer> getEvaluationProgress(int tenderId) throws DAOException {
        return evaluationDAO.getEvaluationProgress(tenderId);
    }

    /**
     * Gets the percentage completion of evaluations for a tender. Calculated as
     * (submitted scores / expected scores) × 100.
     *
     * @param tenderId The tender ID
     * @return Completion percentage (0-100); returns 0 if no bids or evaluators
     * exist
     * @throws DAOException if database error occurs
     */
    public int getCompletionPercentage(int tenderId) throws DAOException {
        List<Bid> bids = bidDAO.findByTenderId(tenderId);
        int totalBids = bids.size();

        List<User> evaluators = userDAO.findByRole(UserRole.EVALUATION_COMMITTEE);
        int totalEvaluators = evaluators.size();

        if (totalBids == 0 || totalEvaluators == 0) {
            return 0;
        }

        int expectedScores = totalBids * totalEvaluators;
        int submittedScores = evaluationDAO.countSubmittedScores(tenderId);

        if (expectedScores == 0) {
            return 0;
        }

        return (submittedScores * 100) / expectedScores;
    }

    /**
     * Completes the evaluation process for a tender. Transitions the tender
     * status from UNDER_EVALUATION to EVALUATED. Verifies that all evaluations
     * are complete before transitioning.
     *
     * @param tenderId The tender ID
     * @return true if transition was successful, false otherwise
     * @throws DAOException if database error occurs
     */
    public boolean completeEvaluation(int tenderId) throws DAOException {
        Tender tender = tenderDAO.findById(tenderId);

        if (tender == null) {
            logger.warning("Tender not found for completion: " + tenderId);
            return false;
        }

        if (tender.getStatus() != TenderStatus.UNDER_EVALUATION) {
            logger.warning("Tender " + tenderId + " is not in UNDER_EVALUATION status");
            return false;
        }

        // Verify all evaluations are complete
        if (!isAllEvaluationsComplete(tenderId)) {
            logger.info("Evaluations not yet complete for tender: " + tenderId);
            return false;
        }

        // Update tender status to EVALUATED
        tender.setStatus(TenderStatus.EVALUATED);
        tender.setEvaluationCompletedDate(LocalDateTime.now());
        boolean updated = tenderDAO.update(tender);

        if (updated) {
            logger.info("Tender " + tender.getReferenceNumber()
                    + " completed evaluation and moved to EVALUATED status");
        }

        return updated;
    }

    /**
     * Starts the evaluation process for a tender. Transitions the tender status
     * from CLOSED to UNDER_EVALUATION. Validates that bids exist before
     * starting.
     *
     * @param tenderId The tender ID
     * @param officerId The ID of the procurement officer starting evaluation
     * @return true if transition was successful, false otherwise
     * @throws DAOException if database error occurs
     */
    public boolean startEvaluation(int tenderId, int officerId) throws DAOException {
        Tender tender = tenderDAO.findById(tenderId);

        if (tender == null) {
            logger.warning("Tender not found: " + tenderId);
            return false;
        }

        if (tender.getStatus() != TenderStatus.CLOSED) {
            logger.warning("Tender " + tenderId + " is not in CLOSED status. Current status: "
                    + tender.getStatus());
            return false;
        }

        // Check if there are bids
        int bidCount = bidDAO.countBidsByTenderId(tenderId);
        if (bidCount == 0) {
            logger.warning("Cannot start evaluation: No bids submitted for tender " + tenderId);
            return false;
        }

        // Update tender status to UNDER_EVALUATION
        tender.setStatus(TenderStatus.UNDER_EVALUATION);
        boolean updated = tenderDAO.update(tender);

        if (updated) {
            logger.info("Tender " + tender.getReferenceNumber()
                    + " moved to UNDER_EVALUATION by officer: " + officerId);
        }

        return updated;
    }

    /**
     * Gets the ranked list of bids for a tender sorted by final score (highest
     * first).
     *
     * @param tenderId The tender ID
     * @return List of bids sorted by final score in descending order
     * @throws DAOException if database error occurs
     */
    public List<Bid> getRankedBids(int tenderId) throws DAOException {
        return evaluationDAO.findRankedBidsByTenderId(tenderId);
    }

    /**
     * Gets the winning bid for a tender (the bid with the highest final score).
     *
     * @param tenderId The tender ID
     * @return The bid with the highest final score, or null if no bids exist
     * @throws DAOException if database error occurs
     */
    public Bid getWinningBid(int tenderId) throws DAOException {
        List<Bid> rankedBids = getRankedBids(tenderId);

        if (rankedBids.isEmpty()) {
            return null;
        }

        return rankedBids.get(0);
    }

    /**
     * Checks if an evaluator has already scored a specific bid.
     *
     * @param evaluatorId The evaluator ID
     * @param bidId The bid ID
     * @return true if the evaluator has already submitted a score for this bid,
     * false otherwise
     * @throws DAOException if database error occurs
     */
    public boolean hasEvaluatorScored(int evaluatorId, int bidId) throws DAOException {
        return evaluationDAO.hasEvaluatorScored(evaluatorId, bidId);
    }

    /**
     * Gets the evaluation score submitted by a specific evaluator for a
     * specific bid.
     *
     * @param evaluatorId The evaluator ID
     * @param bidId The bid ID
     * @return {@link EvaluationScore} object containing the score details, or
     * null if not found
     * @throws DAOException if database error occurs
     */
    public EvaluationScore getEvaluationScore(int evaluatorId, int bidId) throws DAOException {
        return evaluationDAO.findByEvaluatorAndBid(evaluatorId, bidId);
    }

    /**
     * Validates that a tender is ready for evaluation to begin. Checks that the
     * tender exists, is in CLOSED status, and has at least one bid.
     *
     * @param tenderId The tender ID
     * @return Error message string if validation fails, or null if the tender
     * is valid and ready
     * @throws DAOException if database error occurs
     */
    public String validateEvaluationReadiness(int tenderId) throws DAOException {
        Tender tender = tenderDAO.findById(tenderId);

        if (tender == null) {
            return "Tender not found.";
        }

        if (tender.getStatus() != TenderStatus.CLOSED) {
            return "Tender must be CLOSED before evaluation can begin. Current status: "
                    + tender.getStatus().getDisplayName();
        }

        int bidCount = bidDAO.countBidsByTenderId(tenderId);
        if (bidCount == 0) {
            return "Cannot start evaluation: No bids have been submitted for this tender.";
        }

        return null; // Valid
    }

    /**
     * Gets the weighting constants used for score calculation, expressed as
     * percentages. Returns the weights multiplied by 100 for display purposes.
     *
     * @return Map of weight names ("price", "technical", "timeline") to their
     * percentage values
     */
    public java.util.Map<String, Double> getWeightageConstants() {
        java.util.Map<String, Double> weights = new java.util.HashMap<>();
        weights.put("price", PRICE_WEIGHT * 100);
        weights.put("technical", TECHNICAL_WEIGHT * 100);
        weights.put("timeline", TIMELINE_WEIGHT * 100);
        return weights;
    }

    // ==================== ADDED METHOD ====================
    /**
     * Checks if the evaluation process is complete and automatically
     * transitions the tender to EVALUATED status if all conditions are met.
     * This method should be called after each score submission to check if
     * evaluation can be completed.
     *
     * @param tenderId The tender ID to check and transition
     * @return true if evaluation was completed and status changed to EVALUATED,
     * false otherwise
     * @throws DAOException if database error occurs
     */
    public boolean checkAndCompleteEvaluation(int tenderId) throws DAOException {
        Tender tender = tenderDAO.findById(tenderId);

        if (tender == null) {
            logger.warning("Tender not found: " + tenderId);
            return false;
        }

        // Only proceed if tender is currently UNDER_EVALUATION
        if (tender.getStatus() != TenderStatus.UNDER_EVALUATION) {
            return false;
        }

        // Check if all evaluations are complete
        if (isAllEvaluationsComplete(tenderId)) {
            // Update tender status to EVALUATED
            tender.setStatus(TenderStatus.EVALUATED);
            tender.setEvaluationCompletedDate(LocalDateTime.now());
            boolean updated = tenderDAO.update(tender);

            if (updated) {
                logger.info("Tender " + tender.getReferenceNumber()
                        + " automatically completed evaluation and moved to EVALUATED status");
                return true;
            }
        }

        return false;
    }

}
