package model;

import model.enums.TenderCategory;
import model.enums.TenderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a government tender in the ProcureGov system. Implements
 * Serializable for session/persistence compatibility.
 *
 * @author kolisang
 * @version 1.0
 */
public class Tender implements Serializable {

    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    // ============================================================
    // DATABASE FIELDS (PERSISTED)
    // ============================================================
    /**
     * Unique identifier for the tender
     */
    private int tenderId;
    /**
     * Auto-generated reference number for the tender
     */
    private String referenceNumber;
    /**
     * Title of the tender
     */
    private String title;
    /**
     * Category of the tender (Construction, Roads, etc.)
     */
    private TenderCategory category;
    /**
     * Detailed description of the tender requirements
     */
    private String description;
    /**
     * Estimated value of the tender contract
     */
    private BigDecimal estimatedValue;
    /**
     * Date and time when bidding closes
     */
    private LocalDateTime closingDateTime;
    /**
     * Current status in the tender lifecycle
     */
    private TenderStatus status;
    /**
     * Server file path to the uploaded tender notice document
     */
    private String tenderNoticePath;
    /**
     * User ID of the procurement officer who created this tender
     */
    private int createdBy;
    /**
     * Justification text for the tender award decision
     */
    private String awardJustification;
    /**
     * ID of the winning bid, null if not yet awarded
     */
    private Integer awardedBidId;
    /**
     * The awarded contract value
     */
    private BigDecimal awardedValue;
    /**
     * Date when the tender was awarded
     */
    private LocalDateTime awardDate;
    /**
     * Date when the tender was created
     */
    private LocalDateTime createdAt;
    /**
     * Date when evaluation was completed
     */
    private LocalDateTime evaluationCompletedDate;
    /**
     * Justification for the evaluation outcome (ADDED THIS FIELD)
     */
    private String evaluationJustification;

    // ============================================================
    // DISPLAY FIELDS (JOINED FROM OTHER TABLES - NOT PERSISTED)
    // ============================================================
    /**
     * Name of the procurement officer who created the tender (for display)
     */
    private String createdByName;
    /**
     * Number of bids submitted for this tender (for display)
     */
    private int bidCount;
    /**
     * Number of bids that have been scored (for display)
     */
    private int scoredBids;
    /**
     * Progress percentage of evaluator submissions (for display)
     */
    private int evaluatorProgressPercent;
    /**
     * Whether the current supplier has submitted a bid (for display)
     */
    private boolean hasBid;
    /**
     * Whether the tender is marked as urgent (for display)
     */
    private boolean urgent;

    // ============================================================
    // EVALUATION FIELDS (NOT PERSISTED - FOR UI DISPLAY)
    // ============================================================
    /**
     * Number of evaluation scores submitted so far
     */
    private int scoresSubmitted;
    /**
     * Expected total number of evaluation scores
     */
    private int expectedScores;
    /**
     * Progress percentage of evaluation completion
     */
    private int progressPercent;
    /**
     * List of evaluator progress details for display
     */
    private List<Map<String, Object>> evaluatorProgressList;
    /**
     * List of bids ranked by final score (highest first)
     */
    private List<Bid> rankedBids;
    /**
     * List of evaluation scores from all evaluators
     */
    private List<EvaluationScore> evaluatorScores;
    /**
     * Average score across all evaluators (formatted for display)
     */
    private String evaluatorAvgScore;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    /**
     * Default constructor. Initializes the tender with DRAFT status.
     */
    public Tender() {
        this.status = TenderStatus.DRAFT;
    }

    /**
     * Constructs a new Tender with the specified details.
     *
     * @param title the tender title
     * @param category the tender category
     * @param description the tender description
     * @param estimatedValue the estimated contract value
     * @param closingDateTime the closing date and time for bid submissions
     * @param createdBy the user ID of the creating procurement officer
     */
    public Tender(String title, TenderCategory category, String description,
            BigDecimal estimatedValue, LocalDateTime closingDateTime, int createdBy) {
        this();
        this.title = title;
        this.category = category;
        this.description = description;
        this.estimatedValue = estimatedValue;
        this.closingDateTime = closingDateTime;
        this.createdBy = createdBy;
    }

    // ============================================================
    // FORMATTING METHODS
    // ============================================================
    /**
     * Formats the closing date for display (date only, no time). Used in the
     * supplier sidebar upcoming deadlines section.
     *
     * @return Formatted closing date string (e.g., "20 Apr 2026")
     */
    public String getFormattedClosingDate() {
        if (closingDateTime == null) {
            return "N/A";
        }
        return closingDateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Formats the closing time for display (time only).
     *
     * @return Formatted closing time (e.g., "14:30"), or "N/A" if closing date
     * is null
     */
    public String getFormattedClosingTime() {
        if (closingDateTime == null) {
            return "N/A";
        }
        return closingDateTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Formats the creation date for display (date only).
     *
     * @return Formatted creation date (e.g., "20 Apr 2026"), or "N/A" if null
     */
    public String getFormattedCreatedAt() {
        if (createdAt == null) {
            return "N/A";
        }
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Formats the evaluation completed date for display.
     *
     * @return Formatted evaluation completed date, or "N/A" if null
     */
    public String getFormattedEvaluationCompletedDate() {
        if (evaluationCompletedDate == null) {
            return "N/A";
        }
        return evaluationCompletedDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Formats the award date for display.
     *
     * @return Formatted award date, or "N/A" if null
     */
    public String getFormattedAwardDate() {
        if (awardDate == null) {
            return "N/A";
        }
        return awardDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Formats the estimated value as currency for display.
     *
     * @return Formatted estimated value (e.g., "M 1,500,000.00")
     */
    public String getFormattedEstimatedValue() {
        return formatCurrency(estimatedValue);
    }

    /**
     * Formats the awarded value as currency for display.
     *
     * @return Formatted awarded value (e.g., "M 750,000.00")
     */
    public String getFormattedAwardedValue() {
        return formatCurrency(awardedValue);
    }

    /**
     * Formats the closing date and time for display.
     *
     * @return Formatted closing date and time (e.g., "20 Apr 2026, 14:30"), or
     * "N/A" if null
     */
    public String getFormattedClosingDateTime() {
        if (closingDateTime == null) {
            return "N/A";
        }
        return closingDateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
    }

    // ============================================================
    // ADDED FORMATTED DATE METHODS - FIX FOR LocalDateTime ERROR
    // These methods provide String formatted dates to avoid fmt:formatDate errors
    // ============================================================
    /**
     * Returns the award date as a formatted string for display. This avoids the
     * LocalDateTime to Date conversion issue with fmt:formatDate.
     *
     * @return Formatted award date (e.g., "23 Apr 2026") or "N/A" if null
     */
    public String getAwardDateFormatted() {
        if (awardDate == null) {
            return "N/A";
        }
        return awardDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Returns the award date with time as a formatted string.
     *
     * @return Formatted award date and time (e.g., "23 Apr 2026 14:30") or
     * "N/A" if null
     */
    public String getAwardDateTimeFormatted() {
        if (awardDate == null) {
            return "N/A";
        }
        return awardDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    /**
     * Returns the closing date as a formatted string.
     *
     * @return Formatted closing date (e.g., "23 Apr 2026") or "N/A" if null
     */
    public String getClosingDateFormatted() {
        if (closingDateTime == null) {
            return "N/A";
        }
        return closingDateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Returns the closing date and time as a formatted string.
     *
     * @return Formatted closing date and time (e.g., "23 Apr 2026 14:30") or
     * "N/A" if null
     */
    public String getClosingDateTimeFormatted() {
        if (closingDateTime == null) {
            return "N/A";
        }
        return closingDateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    /**
     * Returns the creation date as a formatted string.
     *
     * @return Formatted creation date (e.g., "23 Apr 2026") or "N/A" if null
     */
    public String getCreatedAtFormatted() {
        if (createdAt == null) {
            return "N/A";
        }
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Returns the creation date with time as a formatted string.
     *
     * @return Formatted creation date and time (e.g., "23 Apr 2026 14:30") or
     * "N/A" if null
     */
    public String getCreatedAtDateTimeFormatted() {
        if (createdAt == null) {
            return "N/A";
        }
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    /**
     * Returns the evaluation completed date as a formatted string.
     *
     * @return Formatted evaluation completed date (e.g., "23 Apr 2026") or
     * "N/A" if null
     */
    public String getEvaluationCompletedDateFormatted() {
        if (evaluationCompletedDate == null) {
            return "N/A";
        }
        return evaluationCompletedDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    // ============================================================
    // GETTERS AND SETTERS - SUPPLIER FIELDS
    // ============================================================
    /**
     * Checks if the current supplier has already submitted a bid for this
     * tender.
     *
     * @return true if a bid has been submitted by the current supplier
     */
    public boolean hasBid() {
        return hasBid;
    }

    /**
     * Sets whether the current supplier has submitted a bid.
     *
     * @param hasBid true if a bid has been submitted
     */
    public void setHasBid(boolean hasBid) {
        this.hasBid = hasBid;
    }

    /**
     * EL compatibility getter for hasBid.
     *
     * @return true if a bid has been submitted
     */
    public boolean getHasBid() {
        return hasBid;
    }

    /**
     * Checks if the tender is marked as urgent.
     *
     * @return true if the tender is urgent
     */
    public boolean isUrgent() {
        return urgent;
    }

    /**
     * Sets whether the tender is marked as urgent.
     *
     * @param urgent true if the tender is urgent
     */
    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    /**
     * EL compatibility getter for urgent.
     *
     * @return true if the tender is urgent
     */
    public boolean getUrgent() {
        return urgent;
    }

    // ============================================================
    // GETTERS AND SETTERS - DATABASE FIELDS
    // ============================================================
    /**
     * Gets the tender's unique identifier.
     *
     * @return the tender ID
     */
    public int getTenderId() {
        return tenderId;
    }

    /**
     * Sets the tender's unique identifier.
     *
     * @param tenderId the tender ID to set
     */
    public void setTenderId(int tenderId) {
        this.tenderId = tenderId;
    }

    /**
     * Gets the auto-generated reference number.
     *
     * @return the reference number
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * Sets the reference number.
     *
     * @param referenceNumber the reference number to set
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * Gets the tender title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the tender title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the tender category.
     *
     * @return the category
     */
    public TenderCategory getCategory() {
        return category;
    }

    /**
     * Sets the tender category.
     *
     * @param category the category to set
     */
    public void setCategory(TenderCategory category) {
        this.category = category;
    }

    /**
     * Gets the tender description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the tender description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the estimated contract value.
     *
     * @return the estimated value as BigDecimal
     */
    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    /**
     * Sets the estimated contract value.
     *
     * @param estimatedValue the estimated value to set
     */
    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    /**
     * Gets the closing date and time for bid submissions.
     *
     * @return the closing date and time
     */
    public LocalDateTime getClosingDateTime() {
        return closingDateTime;
    }

    /**
     * Sets the closing date and time for bid submissions.
     *
     * @param closingDateTime the closing date and time to set
     */
    public void setClosingDateTime(LocalDateTime closingDateTime) {
        this.closingDateTime = closingDateTime;
    }

    /**
     * Gets the current tender status.
     *
     * @return the status
     */
    public TenderStatus getStatus() {
        return status;
    }

    /**
     * Sets the tender status.
     *
     * @param status the status to set
     */
    public void setStatus(TenderStatus status) {
        this.status = status;
    }

    /**
     * Gets the server file path to the tender notice document.
     *
     * @return the tender notice file path
     */
    public String getTenderNoticePath() {
        return tenderNoticePath;
    }

    /**
     * Sets the server file path to the tender notice document.
     *
     * @param tenderNoticePath the file path to set
     */
    public void setTenderNoticePath(String tenderNoticePath) {
        this.tenderNoticePath = tenderNoticePath;
    }

    /**
     * Gets the user ID of the procurement officer who created this tender.
     *
     * @return the creator's user ID
     */
    public int getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user ID of the procurement officer who created this tender.
     *
     * @param createdBy the creator's user ID
     */
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the award justification text.
     *
     * @return the award justification
     */
    public String getAwardJustification() {
        return awardJustification;
    }

    /**
     * Sets the award justification text.
     *
     * @param awardJustification the justification to set
     */
    public void setAwardJustification(String awardJustification) {
        this.awardJustification = awardJustification;
    }

    /**
     * Gets the ID of the winning bid.
     *
     * @return the awarded bid ID, or null if not yet awarded
     */
    public Integer getAwardedBidId() {
        return awardedBidId;
    }

    /**
     * Sets the ID of the winning bid.
     *
     * @param awardedBidId the awarded bid ID to set
     */
    public void setAwardedBidId(Integer awardedBidId) {
        this.awardedBidId = awardedBidId;
    }

    /**
     * Gets the awarded contract value.
     *
     * @return the awarded value
     */
    public BigDecimal getAwardedValue() {
        return awardedValue;
    }

    /**
     * Sets the awarded contract value.
     *
     * @param awardedValue the awarded value to set
     */
    public void setAwardedValue(BigDecimal awardedValue) {
        this.awardedValue = awardedValue;
    }

    /**
     * Gets the date when the tender was awarded.
     *
     * @return the award date
     */
    public LocalDateTime getAwardDate() {
        return awardDate;
    }

    /**
     * Sets the date when the tender was awarded.
     *
     * @param awardDate the award date to set
     */
    public void setAwardDate(LocalDateTime awardDate) {
        this.awardDate = awardDate;
    }

    /**
     * Gets the date when the tender was created.
     *
     * @return the creation date
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the date when the tender was created.
     *
     * @param createdAt the creation date to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the date when evaluation was completed.
     *
     * @return the evaluation completed date
     */
    public LocalDateTime getEvaluationCompletedDate() {
        return evaluationCompletedDate;
    }

    /**
     * Sets the date when evaluation was completed.
     *
     * @param evaluationCompletedDate the evaluation completed date to set
     */
    public void setEvaluationCompletedDate(LocalDateTime evaluationCompletedDate) {
        this.evaluationCompletedDate = evaluationCompletedDate;
    }

    // ============================================================
    // GETTER AND SETTER FOR evaluationJustification (ADDED)
    // ============================================================
    /**
     * Gets the evaluation justification text.
     *
     * @return the evaluation justification
     */
    public String getEvaluationJustification() {
        return evaluationJustification;
    }

    /**
     * Sets the evaluation justification text.
     *
     * @param evaluationJustification the evaluation justification to set
     */
    public void setEvaluationJustification(String evaluationJustification) {
        this.evaluationJustification = evaluationJustification;
    }

    // ============================================================
    // GETTERS AND SETTERS - DISPLAY FIELDS
    // ============================================================
    /**
     * Gets the name of the procurement officer who created this tender.
     *
     * @return the creator's display name
     */
    public String getCreatedByName() {
        return createdByName;
    }

    /**
     * Sets the name of the procurement officer who created this tender.
     *
     * @param createdByName the creator's display name
     */
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    /**
     * Gets the number of bids submitted for this tender.
     *
     * @return the bid count
     */
    public int getBidCount() {
        return bidCount;
    }

    /**
     * Sets the number of bids submitted for this tender.
     *
     * @param bidCount the bid count to set
     */
    public void setBidCount(int bidCount) {
        this.bidCount = bidCount;
    }

    /**
     * Gets the number of bids that have been scored.
     *
     * @return the scored bids count
     */
    public int getScoredBids() {
        return scoredBids;
    }

    /**
     * Sets the number of bids that have been scored.
     *
     * @param scoredBids the scored bids count to set
     */
    public void setScoredBids(int scoredBids) {
        this.scoredBids = scoredBids;
    }

    /**
     * Gets the evaluator progress percentage.
     *
     * @return the evaluator progress percent
     */
    public int getEvaluatorProgressPercent() {
        return evaluatorProgressPercent;
    }

    /**
     * Sets the evaluator progress percentage.
     *
     * @param evaluatorProgressPercent the percentage to set
     */
    public void setEvaluatorProgressPercent(int evaluatorProgressPercent) {
        this.evaluatorProgressPercent = evaluatorProgressPercent;
    }

    /**
     * Alias for getEvaluatorProgressPercent().
     *
     * @return the evaluator progress percentage
     */
    public int getEvaluatorProgress() {
        return evaluatorProgressPercent;
    }

    /**
     * Alias for setEvaluatorProgressPercent().
     *
     * @param evaluatorProgress the progress percentage to set
     */
    public void setEvaluatorProgress(int evaluatorProgress) {
        this.evaluatorProgressPercent = evaluatorProgress;
    }

    // ============================================================
    // GETTERS AND SETTERS - EVALUATION FIELDS
    // ============================================================
    /**
     * Gets the number of evaluation scores submitted so far.
     *
     * @return the scores submitted count
     */
    public int getScoresSubmitted() {
        return scoresSubmitted;
    }

    /**
     * Sets the number of evaluation scores submitted.
     *
     * @param scoresSubmitted the count to set
     */
    public void setScoresSubmitted(int scoresSubmitted) {
        this.scoresSubmitted = scoresSubmitted;
    }

    /**
     * Gets the expected total number of evaluation scores.
     *
     * @return the expected scores count
     */
    public int getExpectedScores() {
        return expectedScores;
    }

    /**
     * Sets the expected total number of evaluation scores.
     *
     * @param expectedScores the expected count to set
     */
    public void setExpectedScores(int expectedScores) {
        this.expectedScores = expectedScores;
    }

    /**
     * Gets the evaluation progress percentage.
     *
     * @return the progress percentage
     */
    public int getProgressPercent() {
        return progressPercent;
    }

    /**
     * Sets the evaluation progress percentage.
     *
     * @param progressPercent the percentage to set
     */
    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    /**
     * Gets the list of evaluator progress details.
     *
     * @return the evaluator progress list
     */
    public List<Map<String, Object>> getEvaluatorProgressList() {
        return evaluatorProgressList;
    }

    /**
     * Sets the list of evaluator progress details.
     *
     * @param evaluatorProgressList the list to set
     */
    public void setEvaluatorProgressList(List<Map<String, Object>> evaluatorProgressList) {
        this.evaluatorProgressList = evaluatorProgressList;
    }

    /**
     * Gets the list of bids ranked by final score.
     *
     * @return the ranked bids list
     */
    public List<Bid> getRankedBids() {
        return rankedBids;
    }

    /**
     * Sets the list of bids ranked by final score.
     *
     * @param rankedBids the ranked bids list to set
     */
    public void setRankedBids(List<Bid> rankedBids) {
        this.rankedBids = rankedBids;
    }

    /**
     * Gets the evaluation scores from all evaluators.
     *
     * @return the evaluator scores list
     */
    public List<EvaluationScore> getEvaluatorScores() {
        return evaluatorScores;
    }

    /**
     * Sets the evaluation scores from all evaluators.
     *
     * @param evaluatorScores the scores list to set
     */
    public void setEvaluatorScores(List<EvaluationScore> evaluatorScores) {
        this.evaluatorScores = evaluatorScores;
    }

    /**
     * Gets the average score across all evaluators (formatted).
     *
     * @return the formatted average score
     */
    public String getEvaluatorAvgScore() {
        return evaluatorAvgScore;
    }

    /**
     * Sets the average score across all evaluators.
     *
     * @param evaluatorAvgScore the average score to set
     */
    public void setEvaluatorAvgScore(String evaluatorAvgScore) {
        this.evaluatorAvgScore = evaluatorAvgScore;
    }

    /**
     * Sets the evaluator progress list (proper implementation).
     *
     * @param evaluatorProgress the list of evaluator progress maps
     */
    public void setEvaluatorProgress(List<Map<String, Object>> evaluatorProgress) {
        this.evaluatorProgressList = evaluatorProgress;
    }

    // ============================================================
    // EVALUATION HELPER METHODS
    // ============================================================
    /**
     * Calculates the progress percentage based on scores submitted vs expected
     * scores. Sets the progressPercent field to (scoresSubmitted * 100) /
     * expectedScores.
     */
    public void calculateProgressPercent() {
        if (expectedScores > 0) {
            this.progressPercent = (scoresSubmitted * 100) / expectedScores;
        } else {
            this.progressPercent = 0;
        }
    }

    /**
     * Gets a formatted progress string showing submitted vs expected scores.
     *
     * @return formatted progress string (e.g., "5 / 10 scores submitted")
     */
    public String getProgressString() {
        return scoresSubmitted + " / " + expectedScores + " scores submitted";
    }

    /**
     * Checks if evaluation is fully complete (all expected scores submitted).
     *
     * @return true if scores submitted meets or exceeds expected scores
     */
    public boolean isEvaluationFullyComplete() {
        return scoresSubmitted >= expectedScores && expectedScores > 0;
    }

    /**
     * Gets the number of evaluators who have completed their submissions.
     *
     * @return count of evaluators marked as completed
     */
    public int getEvaluatorsSubmittedCount() {
        if (evaluatorProgressList == null) {
            return 0;
        }
        int count = 0;
        for (Map<String, Object> progress : evaluatorProgressList) {
            Boolean completed = (Boolean) progress.get("completed");
            if (completed != null && completed) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the highest-ranked bid (rank 1).
     *
     * @return the top-ranked bid, or null if no bids exist
     */
    public Bid getTopRankedBid() {
        if (rankedBids == null || rankedBids.isEmpty()) {
            return null;
        }
        return rankedBids.get(0);
    }

    /**
     * Gets the winning bid amount formatted as currency.
     *
     * @return formatted winning bid amount, or "N/A" if no winning bid
     */
    public String getWinningBidAmountFormatted() {
        Bid topBid = getTopRankedBid();
        if (topBid == null) {
            return "N/A";
        }
        return topBid.getFormattedBidAmount();
    }

    // ============================================================
    // CRITICAL: These methods fix the PropertyNotFoundException
    // ============================================================
    /**
     * Gets the name of the winning supplier. This method is used by sidebar.jsp
     * to display the winning supplier.
     *
     * @return The winning supplier's name, or "No bids" if none
     */
    public String getWinningSupplierName() {
        Bid topBid = getTopRankedBid();
        if (topBid == null) {
            return "No bids";
        }
        return topBid.getSupplierName();
    }

    /**
     * Gets the winning supplier name (alias for getWinningSupplierName). This
     * method is used by sidebar.jsp when calling ${tender.winningSupplier}.
     *
     * @return The winning supplier's name
     */
    public String getWinningSupplier() {
        return getWinningSupplierName();
    }

    /**
     * Gets the formatted winning bid score. This method is used by sidebar.jsp
     * to display the winning score.
     *
     * @return Formatted final score, or "N/A" if none
     */
    public String getWinningBidScoreFormatted() {
        Bid topBid = getTopRankedBid();
        if (topBid == null || topBid.getFinalScore() == null) {
            return "N/A";
        }
        return topBid.getFormattedFinalScore();
    }

    /**
     * Checks if any bids have been submitted for this tender.
     *
     * @return true if bid count is greater than 0
     */
    public boolean hasBids() {
        return bidCount > 0;
    }

    /**
     * Checks if the tender is ready for evaluation (CLOSED status with bids).
     *
     * @return true if ready for evaluation
     */
    public boolean isReadyForEvaluation() {
        return status == TenderStatus.CLOSED && hasBids();
    }

    /**
     * Checks if the tender is currently under evaluation.
     *
     * @return true if status is UNDER_EVALUATION
     */
    public boolean isUnderEvaluation() {
        return status == TenderStatus.UNDER_EVALUATION;
    }

    /**
     * Checks if evaluation is complete (EVALUATED or AWARDED status).
     *
     * @return true if evaluation is complete
     */
    public boolean isEvaluationComplete() {
        return status == TenderStatus.EVALUATED || status == TenderStatus.AWARDED;
    }

    /**
     * Gets a descriptive evaluation status string for display.
     *
     * @return human-readable evaluation status description
     */
    public String getEvaluationStatus() {
        if (status == TenderStatus.CLOSED && hasBids()) {
            return "Ready for Evaluation";
        } else if (status == TenderStatus.UNDER_EVALUATION) {
            return "Evaluation in Progress (" + progressPercent + "%)";
        } else if (status == TenderStatus.EVALUATED) {
            return "Evaluation Completed - Ready for Award";
        } else if (status == TenderStatus.AWARDED) {
            return "Awarded";
        } else if (status == TenderStatus.CLOSED && !hasBids()) {
            return "No Bids Received";
        }
        return status.getDisplayName();
    }

    /**
     * Gets the CSS class for the evaluation status badge.
     *
     * @return CSS class name for styling
     */
    public String getEvaluationStatusClass() {
        if (status == TenderStatus.CLOSED && hasBids()) {
            return "status-ready";
        } else if (status == TenderStatus.UNDER_EVALUATION) {
            return "status-progress";
        } else if (status == TenderStatus.EVALUATED) {
            return "status-completed";
        } else if (status == TenderStatus.AWARDED) {
            return "status-awarded";
        }
        return "status-default";
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================
    /**
     * Checks if the tender is currently open for bidding.
     *
     * @return true if status is OPEN and closing date has not passed
     */
    public boolean isOpenForBidding() {
        return status == TenderStatus.OPEN && LocalDateTime.now().isBefore(closingDateTime);
    }

    /**
     * Checks if the tender can still be edited (only in DRAFT status).
     *
     * @return true if status is DRAFT
     */
    public boolean isEditable() {
        return status == TenderStatus.DRAFT;
    }

    /**
     * Checks if the tender's closing date has passed.
     *
     * @return true if closing date is in the past
     */
    public boolean isClosed() {
        return closingDateTime != null && LocalDateTime.now().isAfter(closingDateTime);
    }

    /**
     * Checks if the tender has been awarded.
     *
     * @return true if status is AWARDED
     */
    public boolean isAwarded() {
        return status == TenderStatus.AWARDED;
    }

    /**
     * Checks if a supplier can submit a bid for this tender.
     *
     * @return true if tender is OPEN and closing date is in the future
     */
    public boolean canSubmitBid() {
        return status == TenderStatus.OPEN && closingDateTime != null && LocalDateTime.now().isBefore(closingDateTime);
    }

    /**
     * EL compatibility getter for canSubmitBid.
     *
     * @return true if bids can be submitted
     */
    public boolean getCanSubmitBid() {
        return canSubmitBid();
    }

    /**
     * Checks if a tender notice document has been uploaded.
     *
     * @return true if tender notice path is set and not empty
     */
    public boolean hasTenderNotice() {
        return tenderNoticePath != null && !tenderNoticePath.trim().isEmpty();
    }

    /**
     * Gets whether the tender has a notice document (EL compatibility). This
     * method is used by JSP EL to conditionally show the download link.
     *
     * @return true if tender notice exists, false otherwise
     */
    public boolean getHasTenderNotice() {
        return hasTenderNotice();
    }

    /**
     * Extracts just the filename from the tender notice file path.
     *
     * @return the filename portion of the path, or null if no notice exists
     */
    public String getTenderNoticeFileName() {
        if (!hasTenderNotice()) {
            return null;
        }
        int lastSeparator = tenderNoticePath.lastIndexOf('/');
        if (lastSeparator == -1) {
            lastSeparator = tenderNoticePath.lastIndexOf('\\');
        }
        if (lastSeparator == -1) {
            return tenderNoticePath;
        }
        return tenderNoticePath.substring(lastSeparator + 1);
    }

    /**
     * Gets a human-readable string of the time remaining until the closing
     * date.
     *
     * @return time remaining string (e.g., "5 days remaining"), or "Closed" if
     * past due, or "N/A" if closing date is null
     */
    public String getTimeRemaining() {
        if (closingDateTime == null) {
            return "N/A";
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(closingDateTime)) {
            return "Closed";
        }
        java.time.Duration duration = java.time.Duration.between(now, closingDateTime);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s") + " remaining";
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " remaining";
        } else {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " remaining";
        }
    }

    /**
     * Gets the status display name with its emoji icon.
     *
     * @return status with icon (e.g., "🟢 Open")
     */
    public String getStatusWithIcon() {
        if (status == null) {
            return "Unknown";
        }
        return status.getIcon() + " " + status.getDisplayName();
    }

    /**
     * Gets the category display name with its emoji icon.
     *
     * @return category with icon (e.g., "🏗️ Construction")
     */
    public String getCategoryWithIcon() {
        if (category == null) {
            return "Unknown";
        }
        return category.getIcon() + " " + category.getDisplayName();
    }

    /**
     * Validates that all required fields for the tender are present and valid.
     *
     * @return true if all required fields are valid
     */
    public boolean isValid() {
        return title != null && !title.trim().isEmpty()
                && category != null
                && description != null && !description.trim().isEmpty()
                && estimatedValue != null && estimatedValue.compareTo(BigDecimal.ZERO) > 0
                && closingDateTime != null && closingDateTime.isAfter(LocalDateTime.now())
                && createdBy > 0;
    }

    /**
     * Gets a string containing all validation error messages.
     *
     * @return concatenated validation errors, or empty string if valid
     */
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();
        if (title == null || title.trim().isEmpty()) {
            errors.append("Title is required. ");
        }
        if (category == null) {
            errors.append("Category is required. ");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.append("Description is required. ");
        }
        if (estimatedValue == null || estimatedValue.compareTo(BigDecimal.ZERO) <= 0) {
            errors.append("Estimated value must be greater than zero. ");
        }
        if (closingDateTime == null) {
            errors.append("Closing date is required. ");
        } else if (closingDateTime.isBefore(LocalDateTime.now())) {
            errors.append("Closing date must be in the future. ");
        }
        if (createdBy <= 0) {
            errors.append("Invalid creator ID. ");
        }
        return errors.toString();
    }

    // ============================================================
    // PRIVATE HELPER METHODS
    // ============================================================
    /**
     * Formats a BigDecimal amount as currency with "M" prefix.
     *
     * @param amount the monetary amount to format
     * @return formatted currency string (e.g., "M 1,500,000.00"), or "M 0.00"
     * if null
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "M 0.00";
        }
        return String.format("M %,.2f", amount);
    }

    // ============================================================
    // OBJECT OVERRIDES
    // ============================================================
    /**
     * Returns a string representation of the Tender object.
     *
     * @return formatted string with tender ID, reference number, and title
     */
    @Override
    public String toString() {
        return "Tender{tenderId=" + tenderId + ", referenceNumber='" + referenceNumber + "', title='" + title + "'}";
    }

    /**
     * Compares this tender to another object for equality based on tender ID.
     *
     * @param obj the object to compare against
     * @return true if the tenders have the same ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Tender tender = (Tender) obj;
        return tenderId == tender.tenderId;
    }

    /**
     * Generates a hash code based on the tender ID.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(tenderId);
    }

    // ============================================================
    // DOCUMENT PATH METHODS (ADDED FOR DOWNLOAD FUNCTIONALITY)
    // ============================================================
    /**
     * Gets the document path of the tender notice file. This is an alias method
     * for getTenderNoticePath() for compatibility with the download servlet.
     *
     * @return The file path to the tender notice document
     */
    public String getDocumentPath() {
        return this.tenderNoticePath;
    }

    /**
     * Sets the document path of the tender notice file. This is an alias method
     * for setTenderNoticePath() for compatibility with the download servlet.
     *
     * @param documentPath The file path to the tender notice document
     */
    public void setDocumentPath(String documentPath) {
        this.tenderNoticePath = documentPath;
    }
}
