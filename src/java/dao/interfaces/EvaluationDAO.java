package dao.interfaces;

import dao.DAOException;
import model.EvaluationScore;
import model.Bid;
import model.User;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for bid evaluation scoring. Handles all
 * evaluation-related database operations.
 *
 * @author Kolisang Phatela
 * @version 1.0
 */
public interface EvaluationDAO {

    /**
     * Submits evaluation scores for a bid.
     *
     * @param score EvaluationScore object with all calculated scores
     * @return Generated score ID
     * @throws DAOException if database error occurs
     */
    int submitScore(EvaluationScore score) throws DAOException;

    /**
     * Finds an evaluation score by its ID.
     *
     * @param scoreId The score ID
     * @return EvaluationScore object or null if not found
     * @throws DAOException if database error occurs
     */
    EvaluationScore findById(int scoreId) throws DAOException;

    /**
     * Retrieves scores submitted by a specific evaluator for a specific tender.
     *
     * @param evaluatorId The evaluator's user ID
     * @param tenderId The tender ID
     * @return List of evaluation scores
     * @throws DAOException if database error occurs
     */
    List<EvaluationScore> findByEvaluatorAndTender(int evaluatorId, int tenderId)
            throws DAOException;

    /**
     * Retrieves all scores for a specific bid.
     *
     * @param bidId The bid ID
     * @return List of evaluation scores
     * @throws DAOException if database error occurs
     */
    List<EvaluationScore> findByBidId(int bidId) throws DAOException;

    /**
     * Retrieves all scores for a specific tender.
     *
     * @param tenderId The tender ID
     * @return List of evaluation scores
     * @throws DAOException if database error occurs
     */
    List<EvaluationScore> findByTenderId(int tenderId) throws DAOException;

    /**
     * Retrieves all scores submitted by a specific evaluator.
     *
     * @param evaluatorId The evaluator's user ID
     * @return List of evaluation scores
     * @throws DAOException if database error occurs
     */
    List<EvaluationScore> findByEvaluatorId(int evaluatorId) throws DAOException;

    /**
     * Checks if an evaluator has already scored a specific bid.
     *
     * @param evaluatorId The evaluator's user ID
     * @param bidId The bid ID
     * @return true if score exists, false otherwise
     * @throws DAOException if database error occurs
     */
    boolean hasEvaluatorScoredBid(int evaluatorId, int bidId) throws DAOException;

    /**
     * Checks if an evaluator has already scored a specific bid.
     * Alias method for better readability.
     *
     * @param evaluatorId The evaluator's user ID
     * @param bidId The bid ID
     * @return true if score exists, false otherwise
     * @throws DAOException if database error occurs
     */
    boolean hasEvaluatorScored(int evaluatorId, int bidId) throws DAOException;

    /**
     * Counts the number of scores submitted for a tender.
     *
     * @param tenderId The tender ID
     * @return Number of scores submitted
     * @throws DAOException if database error occurs
     */
    int countSubmittedScores(int tenderId) throws DAOException;

    /**
     * Counts the number of evaluators who have submitted scores for a tender.
     *
     * @param tenderId The tender ID
     * @return Number of evaluators who have submitted at least one score
     * @throws DAOException if database error occurs
     */
    int countEvaluatorsWhoScored(int tenderId) throws DAOException;

    /**
     * Counts the total number of evaluators assigned to a tender.
     *
     * @param tenderId The tender ID
     * @return Number of evaluators assigned
     * @throws DAOException if database error occurs
     */
    int countEvaluatorsForTender(int tenderId) throws DAOException;

    /**
     * Counts the number of evaluators who have completed all their evaluations.
     *
     * @param tenderId The tender ID
     * @return Number of evaluators who have completed all scores
     * @throws DAOException if database error occurs
     */
    int countCompletedEvaluators(int tenderId) throws DAOException;

    /**
     * Calculates the average weighted total score for a bid.
     *
     * @param bidId The bid ID
     * @return Average weighted total score, or null if no scores exist
     * @throws DAOException if database error occurs
     */
    Double calculateAverageScore(int bidId) throws DAOException;

    /**
     * Retrieves evaluators assigned to a tender.
     *
     * @param tenderId The tender ID
     * @return List of evaluator users
     * @throws DAOException if database error occurs
     */
    List<User> findEvaluatorsForTender(int tenderId) throws DAOException;

    /**
     * Checks if all evaluators have scored all bids for a tender.
     *
     * @param tenderId The tender ID
     * @return true if evaluation is complete, false otherwise
     * @throws DAOException if database error occurs
     */
    boolean isEvaluationComplete(int tenderId) throws DAOException;

    /**
     * Updates an existing evaluation score.
     *
     * @param score EvaluationScore object with updated values
     * @return true if update successful, false otherwise
     * @throws DAOException if database error occurs
     */
    boolean updateScore(EvaluationScore score) throws DAOException;

    /**
     * Deletes an evaluation score.
     *
     * @param scoreId The score ID to delete
     * @return true if deletion successful, false otherwise
     * @throws DAOException if database error occurs
     */
    boolean deleteScore(int scoreId) throws DAOException;

    /**
     * Gets the evaluation progress for a tender. Returns a summary of how many
     * evaluators have scored how many bids.
     *
     * @param tenderId The tender ID
     * @return Map containing progress statistics
     * @throws DAOException if database error occurs
     */
    Map<String, Integer> getEvaluationProgress(int tenderId) throws DAOException;

    /**
     * Retrieves ranked bids for a tender based on final scores.
     *
     * @param tenderId The tender ID
     * @return List of bids with their final scores and ranks
     * @throws DAOException if database error occurs
     */
    List<Bid> findRankedBidsByTenderId(int tenderId) throws DAOException;

    /**
     * Finds an evaluation score by evaluator and bid ID.
     *
     * @param evaluatorId The evaluator's user ID
     * @param bidId The bid ID
     * @return EvaluationScore object or null if not found
     * @throws DAOException if database error occurs
     */
    EvaluationScore findByEvaluatorAndBid(int evaluatorId, int bidId) throws DAOException;

    /**
     * Gets all scores for a specific tender with evaluator details.
     *
     * @param tenderId The tender ID
     * @return List of evaluation scores with evaluator information
     * @throws DAOException if database error occurs
     */
    List<EvaluationScore> findDetailedScoresByTenderId(int tenderId) throws DAOException;

    /**
     * Checks if a tender has any evaluation scores.
     *
     * @param tenderId The tender ID
     * @return true if scores exist, false otherwise
     * @throws DAOException if database error occurs
     */
    boolean hasAnyScores(int tenderId) throws DAOException;

    /**
     * Gets the number of bids that have been fully evaluated for a tender.
     *
     * @param tenderId The tender ID
     * @return Number of fully evaluated bids
     * @throws DAOException if database error occurs
     */
    int countFullyEvaluatedBids(int tenderId) throws DAOException;
}