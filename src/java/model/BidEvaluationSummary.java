package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Summary class for bid evaluation results. Used for displaying consolidated
 * evaluation data for a single bid, including individual evaluator scores,
 * averages, and final weighted score. Implements {@link Serializable} for
 * session storage and caching.
 *
 * @author YourName
 * @version 1.0
 */
public class BidEvaluationSummary implements Serializable {

    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID of the bid being summarized
     */
    private int bidId;
    /**
     * Name of the supplier who submitted the bid
     */
    private String supplierName;
    /**
     * The bid amount submitted by the supplier
     */
    private java.math.BigDecimal bidAmount;
    /**
     * The proposed timeline in days from the bid
     */
    private int proposedTimeline;
    /**
     * List of individual evaluation scores from each evaluator
     */
    private List<EvaluationScore> individualScores;
    /**
     * Average price score across all evaluators
     */
    private Double averagePriceScore;
    /**
     * Average technical compliance score across all evaluators
     */
    private Double averageTechnicalScore;
    /**
     * Average timeline score across all evaluators
     */
    private Double averageTimelineScore;
    /**
     * Final weighted score calculated from the averages
     */
    private Double finalWeightedScore;
    /**
     * Rank of this bid based on final score (1 is best)
     */
    private Integer rank;
    /**
     * Total number of evaluators assigned to evaluate this bid
     */
    private int evaluatorCount;
    /**
     * Number of evaluators who have submitted their scores
     */
    private int scoresSubmitted;

    /**
     * Default constructor. Initializes an empty list for individual scores.
     */
    public BidEvaluationSummary() {
        this.individualScores = new ArrayList<>();
    }

    /**
     * Constructor with bid ID and supplier name.
     *
     * @param bidId the ID of the bid being summarized
     * @param supplierName the name of the supplier who submitted the bid
     */
    public BidEvaluationSummary(int bidId, String supplierName) {
        this();
        this.bidId = bidId;
        this.supplierName = supplierName;
    }

    // Getters and Setters
    /**
     * Gets the bid ID.
     *
     * @return the bid ID
     */
    public int getBidId() {
        return bidId;
    }

    /**
     * Sets the bid ID.
     *
     * @param bidId the bid ID to set
     */
    public void setBidId(int bidId) {
        this.bidId = bidId;
    }

    /**
     * Gets the supplier name.
     *
     * @return the supplier's name
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * Sets the supplier name.
     *
     * @param supplierName the supplier's name to set
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * Gets the bid amount.
     *
     * @return the bid amount
     */
    public java.math.BigDecimal getBidAmount() {
        return bidAmount;
    }

    /**
     * Sets the bid amount.
     *
     * @param bidAmount the bid amount to set
     */
    public void setBidAmount(java.math.BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    /**
     * Gets the proposed timeline in days.
     *
     * @return the proposed timeline days
     */
    public int getProposedTimeline() {
        return proposedTimeline;
    }

    /**
     * Sets the proposed timeline in days.
     *
     * @param proposedTimeline the proposed timeline to set
     */
    public void setProposedTimeline(int proposedTimeline) {
        this.proposedTimeline = proposedTimeline;
    }

    /**
     * Gets the list of individual evaluation scores.
     *
     * @return the list of evaluation scores
     */
    public List<EvaluationScore> getIndividualScores() {
        return individualScores;
    }

    /**
     * Sets the list of individual evaluation scores.
     *
     * @param individualScores the list of scores to set
     */
    public void setIndividualScores(List<EvaluationScore> individualScores) {
        this.individualScores = individualScores;
    }

    /**
     * Adds an individual evaluation score to the list.
     *
     * @param score the evaluation score to add
     */
    public void addIndividualScore(EvaluationScore score) {
        this.individualScores.add(score);
    }

    /**
     * Gets the average price score across all evaluators.
     *
     * @return the average price score
     */
    public Double getAveragePriceScore() {
        return averagePriceScore;
    }

    /**
     * Sets the average price score across all evaluators.
     *
     * @param averagePriceScore the average price score to set
     */
    public void setAveragePriceScore(Double averagePriceScore) {
        this.averagePriceScore = averagePriceScore;
    }

    /**
     * Gets the average technical compliance score across all evaluators.
     *
     * @return the average technical score
     */
    public Double getAverageTechnicalScore() {
        return averageTechnicalScore;
    }

    /**
     * Sets the average technical compliance score across all evaluators.
     *
     * @param averageTechnicalScore the average technical score to set
     */
    public void setAverageTechnicalScore(Double averageTechnicalScore) {
        this.averageTechnicalScore = averageTechnicalScore;
    }

    /**
     * Gets the average timeline score across all evaluators.
     *
     * @return the average timeline score
     */
    public Double getAverageTimelineScore() {
        return averageTimelineScore;
    }

    /**
     * Sets the average timeline score across all evaluators.
     *
     * @param averageTimelineScore the average timeline score to set
     */
    public void setAverageTimelineScore(Double averageTimelineScore) {
        this.averageTimelineScore = averageTimelineScore;
    }

    /**
     * Gets the final weighted score calculated from the averages.
     *
     * @return the final weighted score
     */
    public Double getFinalWeightedScore() {
        return finalWeightedScore;
    }

    /**
     * Sets the final weighted score calculated from the averages.
     *
     * @param finalWeightedScore the final weighted score to set
     */
    public void setFinalWeightedScore(Double finalWeightedScore) {
        this.finalWeightedScore = finalWeightedScore;
    }

    /**
     * Gets the rank of this bid based on final score.
     *
     * @return the rank (1 is best), or null if not ranked
     */
    public Integer getRank() {
        return rank;
    }

    /**
     * Sets the rank of this bid based on final score.
     *
     * @param rank the rank to set
     */
    public void setRank(Integer rank) {
        this.rank = rank;
    }

    /**
     * Gets the total number of evaluators assigned.
     *
     * @return the evaluator count
     */
    public int getEvaluatorCount() {
        return evaluatorCount;
    }

    /**
     * Sets the total number of evaluators assigned.
     *
     * @param evaluatorCount the evaluator count to set
     */
    public void setEvaluatorCount(int evaluatorCount) {
        this.evaluatorCount = evaluatorCount;
    }

    /**
     * Gets the number of evaluators who have submitted their scores.
     *
     * @return the number of scores submitted
     */
    public int getScoresSubmitted() {
        return scoresSubmitted;
    }

    /**
     * Sets the number of evaluators who have submitted their scores.
     *
     * @param scoresSubmitted the number of scores submitted to set
     */
    public void setScoresSubmitted(int scoresSubmitted) {
        this.scoresSubmitted = scoresSubmitted;
    }

    /**
     * Checks if all evaluators have submitted their scores. Evaluation is
     * complete when the number of scores submitted meets or exceeds the total
     * evaluator count.
     *
     * @return true if all evaluators have submitted scores, false otherwise
     */
    public boolean isComplete() {
        return scoresSubmitted >= evaluatorCount;
    }

    /**
     * Gets the completion percentage of evaluation for this bid. Calculated as
     * (scoresSubmitted / evaluatorCount) × 100.
     *
     * @return completion percentage (0-100), returns 0 if no evaluators
     * assigned
     */
    public int getCompletionPercentage() {
        if (evaluatorCount == 0) {
            return 0;
        }
        return (scoresSubmitted * 100) / evaluatorCount;
    }

    /**
     * Formats the final weighted score for display.
     *
     * @return formatted final score (e.g., "85.50"), or "Pending" if not yet
     * calculated
     */
    public String getFormattedFinalScore() {
        if (finalWeightedScore == null) {
            return "Pending";
        }
        return String.format("%.2f", finalWeightedScore);
    }

    /**
     * Formats the bid amount as currency for display.
     *
     * @return formatted bid amount (e.g., "M1,500,000.00"), or "M0.00" if null
     */
    public String getFormattedBidAmount() {
        if (bidAmount == null) {
            return "M0.00";
        }
        return String.format("M%,.2f", bidAmount);
    }
}
