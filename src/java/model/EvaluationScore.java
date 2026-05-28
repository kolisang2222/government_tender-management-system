package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an individual evaluator's scoring for a specific bid. Stores both
 * auto-calculated scores (price, timeline) and manual scores (technical).
 * Implements {@link Serializable} for session storage and caching.
 *
 * @author YourName
 * @version 1.0
 */
public class EvaluationScore implements Serializable {

    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    // Database fields
    /**
     * Unique identifier for this evaluation score record
     */
    private int scoreId;
    /**
     * ID of the bid being evaluated
     */
    private int bidId;
    /**
     * User ID of the evaluator who submitted this score
     */
    private int evaluatorId;
    /**
     * Manual technical compliance score entered by evaluator (0-100)
     */
    private Double technicalComplianceScore;
    /**
     * Auto-calculated price score based on bid amount comparison
     */
    private Double priceScore;
    /**
     * Auto-calculated timeline score based on proposed timeline comparison
     */
    private Double timelineScore;
    /**
     * Weighted total score combining all three component scores
     */
    private Double weightedTotal;
    /**
     * Timestamp when this evaluation score was submitted
     */
    private LocalDateTime submittedAt;

    // Display fields (joined from other tables)
    /**
     * Display name of the evaluator (joined from users table)
     */
    private String evaluatorName;
    /**
     * Display name of the supplier who submitted the bid (joined from users
     * table)
     */
    private String supplierName;
    /**
     * Reference number of the tender (joined from tenders table)
     */
    private String tenderReference;
    /**
     * The bid amount submitted by the supplier
     */
    private java.math.BigDecimal bidAmount;
    /**
     * The proposed timeline in days from the bid
     */
    private int proposedTimelineDays;
    /**
     * Rank of this bid based on final score (lower is better)
     */
    private Integer rank;

    // Constants for scoring weights
    /**
     * Weight for price score in weighted total calculation (40%)
     */
    public static final double PRICE_WEIGHT = 0.40;
    /**
     * Weight for technical compliance score in weighted total calculation (35%)
     */
    public static final double TECHNICAL_WEIGHT = 0.35;
    /**
     * Weight for timeline score in weighted total calculation (25%)
     */
    public static final double TIMELINE_WEIGHT = 0.25;

    /**
     * Default constructor. Initializes the submitted timestamp to the current
     * date and time.
     */
    public EvaluationScore() {
        this.submittedAt = LocalDateTime.now();
    }

    /**
     * Constructor with required fields. Initializes the bid ID, evaluator ID,
     * and sets the submitted timestamp to now.
     *
     * @param bidId the ID of the bid being evaluated
     * @param evaluatorId the user ID of the evaluator
     */
    public EvaluationScore(int bidId, int evaluatorId) {
        this.bidId = bidId;
        this.evaluatorId = evaluatorId;
        this.submittedAt = LocalDateTime.now();
    }

    // ============================================================
    // DATABASE FIELD GETTERS AND SETTERS
    // ============================================================
    /**
     * Gets the unique identifier for this evaluation score.
     *
     * @return the score ID
     */
    public int getScoreId() {
        return scoreId;
    }

    /**
     * Sets the unique identifier for this evaluation score.
     *
     * @param scoreId the score ID to set
     */
    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
    }

    /**
     * Gets the ID of the bid being evaluated.
     *
     * @return the bid ID
     */
    public int getBidId() {
        return bidId;
    }

    /**
     * Sets the ID of the bid being evaluated.
     *
     * @param bidId the bid ID to set
     */
    public void setBidId(int bidId) {
        this.bidId = bidId;
    }

    /**
     * Gets the user ID of the evaluator who submitted this score.
     *
     * @return the evaluator ID
     */
    public int getEvaluatorId() {
        return evaluatorId;
    }

    /**
     * Sets the user ID of the evaluator who submitted this score.
     *
     * @param evaluatorId the evaluator ID to set
     */
    public void setEvaluatorId(int evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    /**
     * Gets the technical compliance score (manual entry by evaluator).
     *
     * @return the technical compliance score (0-100)
     */
    public Double getTechnicalComplianceScore() {
        return technicalComplianceScore;
    }

    /**
     * Sets the technical compliance score, clamping to the valid range 0-100.
     * Values below 0 are set to 0.0, values above 100 are set to 100.0.
     *
     * @param technicalComplianceScore the technical compliance score to set
     */
    public void setTechnicalComplianceScore(Double technicalComplianceScore) {
        if (technicalComplianceScore != null) {
            if (technicalComplianceScore < 0) {
                this.technicalComplianceScore = 0.0;
            } else if (technicalComplianceScore > 100) {
                this.technicalComplianceScore = 100.0;
            } else {
                this.technicalComplianceScore = technicalComplianceScore;
            }
        } else {
            this.technicalComplianceScore = null;
        }
    }

    /**
     * Gets the auto-calculated price score.
     *
     * @return the price score
     */
    public Double getPriceScore() {
        return priceScore;
    }

    /**
     * Sets the auto-calculated price score.
     *
     * @param priceScore the price score to set
     */
    public void setPriceScore(Double priceScore) {
        this.priceScore = priceScore;
    }

    /**
     * Gets the auto-calculated timeline score.
     *
     * @return the timeline score
     */
    public Double getTimelineScore() {
        return timelineScore;
    }

    /**
     * Sets the auto-calculated timeline score.
     *
     * @param timelineScore the timeline score to set
     */
    public void setTimelineScore(Double timelineScore) {
        this.timelineScore = timelineScore;
    }

    /**
     * Gets the weighted total score combining all three component scores.
     *
     * @return the weighted total score
     */
    public Double getWeightedTotal() {
        return weightedTotal;
    }

    /**
     * Sets the weighted total score.
     *
     * @param weightedTotal the weighted total to set
     */
    public void setWeightedTotal(Double weightedTotal) {
        this.weightedTotal = weightedTotal;
    }

    /**
     * Gets the timestamp when this evaluation was submitted.
     *
     * @return the submitted timestamp
     */
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    /**
     * Sets the timestamp when this evaluation was submitted.
     *
     * @param submittedAt the submitted timestamp to set
     */
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    // ============================================================
    // DISPLAY FIELD GETTERS AND SETTERS
    // ============================================================
    /**
     * Gets the display name of the evaluator.
     *
     * @return the evaluator's name
     */
    public String getEvaluatorName() {
        return evaluatorName;
    }

    /**
     * Sets the display name of the evaluator.
     *
     * @param evaluatorName the evaluator's name to set
     */
    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }

    /**
     * Gets the display name of the supplier who submitted the bid.
     *
     * @return the supplier's name
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * Sets the display name of the supplier who submitted the bid.
     *
     * @param supplierName the supplier's name to set
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * Gets the reference number of the tender.
     *
     * @return the tender reference number
     */
    public String getTenderReference() {
        return tenderReference;
    }

    /**
     * Sets the reference number of the tender.
     *
     * @param tenderReference the tender reference to set
     */
    public void setTenderReference(String tenderReference) {
        this.tenderReference = tenderReference;
    }

    /**
     * Gets the bid amount submitted by the supplier.
     *
     * @return the bid amount
     */
    public java.math.BigDecimal getBidAmount() {
        return bidAmount;
    }

    /**
     * Sets the bid amount submitted by the supplier.
     *
     * @param bidAmount the bid amount to set
     */
    public void setBidAmount(java.math.BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    /**
     * Gets the proposed timeline in days from the bid.
     *
     * @return the proposed timeline in days
     */
    public int getProposedTimelineDays() {
        return proposedTimelineDays;
    }

    /**
     * Sets the proposed timeline in days from the bid.
     *
     * @param proposedTimelineDays the proposed timeline days to set
     */
    public void setProposedTimelineDays(int proposedTimelineDays) {
        this.proposedTimelineDays = proposedTimelineDays;
    }

    /**
     * Gets the rank of this bid based on final score.
     *
     * @return the rank, where 1 is the highest-scoring bid
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

    // ============================================================
    // FORMATTING METHODS
    // ============================================================
    /**
     * Formats the bid amount as currency with "M" prefix.
     *
     * @return formatted bid amount (e.g., "M 1,500,000.00"), or "M 0.00" if
     * null
     */
    public String getFormattedBidAmount() {
        if (bidAmount == null) {
            return "M 0.00";
        }
        return String.format("M %,.2f", bidAmount);
    }

    /**
     * Formats the price score to two decimal places.
     *
     * @return formatted price score (e.g., "85.50"), or "0.00" if null
     */
    public String getFormattedPriceScore() {
        if (priceScore == null) {
            return "0.00";
        }
        return String.format("%.2f", priceScore);
    }

    /**
     * Formats the technical compliance score to two decimal places.
     *
     * @return formatted technical score (e.g., "90.00"), or "0.00" if null
     */
    public String getFormattedTechnicalScore() {
        if (technicalComplianceScore == null) {
            return "0.00";
        }
        return String.format("%.2f", technicalComplianceScore);
    }

    /**
     * Formats the timeline score to two decimal places.
     *
     * @return formatted timeline score (e.g., "75.00"), or "0.00" if null
     */
    public String getFormattedTimelineScore() {
        if (timelineScore == null) {
            return "0.00";
        }
        return String.format("%.2f", timelineScore);
    }

    /**
     * Formats the weighted total score to two decimal places.
     *
     * @return formatted weighted total (e.g., "83.50"), or "0.00" if null
     */
    public String getFormattedWeightedTotal() {
        if (weightedTotal == null) {
            return "0.00";
        }
        return String.format("%.2f", weightedTotal);
    }

    /**
     * Formats the weighted total as a percentage string with one decimal place.
     *
     * @return formatted percentage (e.g., "83.5%"), or "0%" if null
     */
    public String getWeightedTotalAsPercentage() {
        if (weightedTotal == null) {
            return "0%";
        }
        return String.format("%.1f%%", weightedTotal);
    }

    /**
     * Gets the letter grade based on the weighted total score. Grading scale: A
     * (90+), B (80-89), C (70-79), D (60-69), F (below 60).
     *
     * @return letter grade (A, B, C, D, F), or "N/A" if weighted total is null
     */
    public String getLetterGrade() {
        if (weightedTotal == null) {
            return "N/A";
        }

        if (weightedTotal >= 90) {
            return "A";
        }
        if (weightedTotal >= 80) {
            return "B";
        }
        if (weightedTotal >= 70) {
            return "C";
        }
        if (weightedTotal >= 60) {
            return "D";
        }
        return "F";
    }

    /**
     * Returns the submitted date as a formatted string for display.
     *
     * @return Formatted submitted date (e.g., "23 Apr 2026 14:30") or "N/A" if
     * null
     */
    public String getSubmittedAtFormatted() {
        if (submittedAt == null) {
            return "N/A";
        }
        return submittedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    // ============================================================
    // CALCULATION METHODS
    // ============================================================
    /**
     * Calculates the price score automatically. Formula: (Lowest Bid Amount /
     * This Bid Amount) × 100
     *
     * @param lowestBidAmount The lowest bid amount among all bids for this
     * tender
     * @param thisBidAmount This bid's amount
     * @return Calculated price score (0-100), clamped to valid range
     */
    public static double calculatePriceScore(java.math.BigDecimal lowestBidAmount,
            java.math.BigDecimal thisBidAmount) {
        if (lowestBidAmount == null || thisBidAmount == null) {
            return 0.0;
        }
        if (thisBidAmount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return 0.0;
        }

        double score = lowestBidAmount.divide(thisBidAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new java.math.BigDecimal("100"))
                .doubleValue();

        return Math.min(100.0, Math.max(0.0, score));
    }

    /**
     * Calculates the timeline score automatically. Formula: (Shortest Proposed
     * Timeline / This Bid's Timeline) × 100
     *
     * @param shortestTimeline The shortest timeline among all bids (in days)
     * @param thisTimeline This bid's proposed timeline (in days)
     * @return Calculated timeline score (0-100), clamped to valid range
     */
    public static double calculateTimelineScore(int shortestTimeline, int thisTimeline) {
        if (shortestTimeline <= 0 || thisTimeline <= 0) {
            return 0.0;
        }

        double score = ((double) shortestTimeline / thisTimeline) * 100.0;

        return Math.min(100.0, Math.max(0.0, score));
    }

    /**
     * Calculates the weighted total score. Formula: (Price Score × 0.40) +
     * (Technical Score × 0.35) + (Timeline Score × 0.25)
     *
     * @param priceScore The calculated price score
     * @param technicalScore The manual technical compliance score
     * @param timelineScore The calculated timeline score
     * @return Weighted total score, rounded to 2 decimal places
     */
    public static double calculateWeightedTotal(double priceScore,
            double technicalScore,
            double timelineScore) {
        double total = (priceScore * PRICE_WEIGHT)
                + (technicalScore * TECHNICAL_WEIGHT)
                + (timelineScore * TIMELINE_WEIGHT);

        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Calculates and sets all scores for this evaluation in one operation.
     * Computes price score, timeline score, and weighted total, then stores
     * them.
     *
     * @param lowestBidAmount The lowest bid amount for this tender
     * @param thisBidAmount This bid's amount
     * @param shortestTimeline The shortest timeline for this tender
     * @param thisTimeline This bid's timeline
     * @param technicalScore The manual technical score entered by evaluator
     */
    public void calculateAllScores(java.math.BigDecimal lowestBidAmount,
            java.math.BigDecimal thisBidAmount,
            int shortestTimeline,
            int thisTimeline,
            double technicalScore) {

        this.priceScore = calculatePriceScore(lowestBidAmount, thisBidAmount);
        this.timelineScore = calculateTimelineScore(shortestTimeline, thisTimeline);
        this.technicalComplianceScore = technicalScore;
        this.weightedTotal = calculateWeightedTotal(this.priceScore,
                this.technicalComplianceScore,
                this.timelineScore);
    }

    // ============================================================
    // VALIDATION METHODS
    // ============================================================
    /**
     * Checks if the technical score is within the valid range (0-100).
     *
     * @return true if technical score is set and within 0-100 range
     */
    public boolean hasValidTechnicalScore() {
        return technicalComplianceScore != null
                && technicalComplianceScore >= 0
                && technicalComplianceScore <= 100;
    }

    /**
     * Checks if all component scores and the weighted total have been
     * calculated.
     *
     * @return true if price, technical, timeline, and weighted total are all
     * set
     */
    public boolean isComplete() {
        return priceScore != null
                && technicalComplianceScore != null
                && timelineScore != null
                && weightedTotal != null;
    }

    // ============================================================
    // OBJECT METHODS
    // ============================================================
    /**
     * Returns a string representation of this EvaluationScore.
     *
     * @return formatted string with all score details
     */
    @Override
    public String toString() {
        return "EvaluationScore{"
                + "scoreId=" + scoreId
                + ", bidId=" + bidId
                + ", evaluatorId=" + evaluatorId
                + ", priceScore=" + priceScore
                + ", technicalScore=" + technicalComplianceScore
                + ", timelineScore=" + timelineScore
                + ", weightedTotal=" + weightedTotal
                + ", submittedAt=" + submittedAt
                + '}';
    }

    /**
     * Compares this evaluation score to another object for equality based on
     * score ID.
     *
     * @param obj the object to compare against
     * @return true if the score IDs match
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        EvaluationScore that = (EvaluationScore) obj;

        return scoreId == that.scoreId;
    }

    /**
     * Generates a hash code based on the score ID.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(scoreId);
    }

    // ============================================================
    // ADDED METHOD FOR INTEGER PARAMETER (FIXES THE ERROR)
    // ============================================================
    /**
     * Sets the technical compliance score using an integer value. This is an
     * overloaded method that converts int to Double and delegates to the
     * {@link #setTechnicalComplianceScore(Double)} method for validation.
     *
     * @param technicalScore The technical compliance score (0-100) as an
     * integer
     */
    public void setTechnicalComplianceScore(int technicalScore) {
        // Convert int to Double and call the existing Double setter
        setTechnicalComplianceScore((double) technicalScore);
    }
}
