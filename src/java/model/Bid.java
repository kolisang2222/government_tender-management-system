package model;

import model.enums.TenderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a sealed bid submitted by a supplier for a specific tender.
 * Contains bid amount, technical compliance details, proposed timeline, and
 * evaluation results. Implements Serializable for session/persistence
 * compatibility.
 *
 * @author YourName
 * @version 1.0
 */
public class Bid implements Serializable {

    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    // ============================================================
    // DATABASE FIELDS (PERSISTED)
    // ============================================================
    /**
     * Unique identifier for the bid
     */
    private int bidId;
    /**
     * ID of the tender this bid is submitted for
     */
    private int tenderId;
    /**
     * User ID of the supplier who submitted this bid
     */
    private int supplierId;
    /**
     * The monetary amount of the bid
     */
    private BigDecimal bidAmount;
    /**
     * Statement describing technical compliance with tender requirements
     */
    private String technicalComplianceStatement;
    /**
     * Proposed timeline for completion in days
     */
    private int proposedTimelineDays;
    /**
     * Server file path to the uploaded supporting document
     */
    private String supportingDocumentPath;
    /**
     * Date and time when the bid was submitted
     */
    private LocalDateTime submittedAt;

    // ============================================================
    // DISPLAY FIELDS (JOINED FROM OTHER TABLES - NOT PERSISTED)
    // ============================================================
    /**
     * Reference number of the associated tender (for display)
     */
    private String tenderReference;
    /**
     * Title of the associated tender (for display)
     */
    private String tenderTitle;
    /**
     * Name of the supplier who submitted the bid (for display)
     */
    private String supplierName;
    /**
     * Current status of the associated tender (for display)
     */
    private TenderStatus tenderStatus;
    /**
     * Final weighted score after evaluation (for display)
     */
    private Double finalScore;
    /**
     * Rank of this bid based on final score (1 is best, for display)
     */
    private Integer rank;

    // Award-related fields from tender
    /**
     * ID of the winning bid on the tender, null if not yet awarded
     */
    private Integer tenderAwardedBidId;
    /**
     * Date when the tender was awarded
     */
    private LocalDateTime tenderAwardDate;

    // Evaluation field - tracks if current user has scored this bid
    /**
     * Whether the currently logged-in evaluator has scored this bid
     */
    private boolean hasCurrentUserScored;

    // Score fields for display (ADDED - FIXES PropertyNotFoundException)
    /**
     * Technical compliance score for display
     */
    private Double technicalScore;
    /**
     * Price score for display
     */
    private Double priceScore;
    /**
     * Timeline score for display
     */
    private Double timelineScore;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    /**
     * Default constructor.
     */
    public Bid() {
    }

    /**
     * Constructs a new Bid with the essential fields.
     *
     * @param tenderId the ID of the tender this bid is for
     * @param supplierId the user ID of the supplier
     * @param bidAmount the monetary bid amount
     * @param technicalComplianceStatement the technical compliance description
     * @param proposedTimelineDays the proposed completion timeline in days
     */
    public Bid(int tenderId, int supplierId, BigDecimal bidAmount,
            String technicalComplianceStatement, int proposedTimelineDays) {
        this.tenderId = tenderId;
        this.supplierId = supplierId;
        this.bidAmount = bidAmount;
        this.technicalComplianceStatement = technicalComplianceStatement;
        this.proposedTimelineDays = proposedTimelineDays;
    }

    // ============================================================
    // FORMATTING METHODS
    // ============================================================
    /**
     * Formats the submission date for display (date only).
     *
     * @return formatted submission date (e.g., "20 Apr 2026"), or "N/A" if null
     */
    public String getFormattedSubmittedAt() {
        if (submittedAt == null) {
            return "N/A";
        }
        return submittedAt.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Formats the submission date and time for display.
     *
     * @return formatted submission date and time (e.g., "20 Apr 2026 14:30"),
     * or "N/A" if null
     */
    public String getFormattedSubmittedDateTime() {
        if (submittedAt == null) {
            return "N/A";
        }
        return submittedAt.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    /**
     * Formats the tender award date for display.
     *
     * @return formatted award date (e.g., "20 Apr 2026"), or "N/A" if null
     */
    public String getFormattedAwardDate() {
        if (tenderAwardDate == null) {
            return "N/A";
        }
        return tenderAwardDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    // ============================================================
    // STATUS METHODS
    // ============================================================
    /**
     * Gets the CSS class for styling the bid status badge.
     *
     * @return CSS class name ("won", "lost", "evaluating", or "pending")
     */
    public String getStatusClass() {
        if (isWinner()) {
            return "won";
        }
        if (tenderStatus == TenderStatus.AWARDED) {
            return "lost";
        }
        if (tenderStatus == TenderStatus.UNDER_EVALUATION
                || tenderStatus == TenderStatus.EVALUATED) {
            return "evaluating";
        }
        return "pending";
    }

    /**
     * Gets the human-readable status display text.
     *
     * @return status display string (e.g., "Won", "Under Evaluation",
     * "Pending")
     */
    public String getStatusDisplay() {
        if (isWinner()) {
            return "Won";
        }
        if (tenderStatus == TenderStatus.AWARDED) {
            return "Not Awarded";
        }
        if (tenderStatus == TenderStatus.UNDER_EVALUATION) {
            return "Under Evaluation";
        }
        if (tenderStatus == TenderStatus.EVALUATED) {
            return "Evaluated";
        }
        if (tenderStatus == TenderStatus.CLOSED) {
            return "Closed";
        }
        return "Pending";
    }

    /**
     * Gets the status filter key for use in UI filtering.
     *
     * @return status filter string ("WON", "LOST", "EVALUATING", or "PENDING")
     */
    public String getStatusFilter() {
        if (isWinner()) {
            return "WON";
        }
        if (tenderStatus == TenderStatus.AWARDED) {
            return "LOST";
        }
        if (tenderStatus == TenderStatus.UNDER_EVALUATION
                || tenderStatus == TenderStatus.EVALUATED) {
            return "EVALUATING";
        }
        return "PENDING";
    }

    /**
     * Checks if this bid is the winner of the tender. A bid is considered the
     * winner if the tender is AWARDED and this bid's ID matches the tender's
     * awarded bid ID.
     *
     * @return true if this bid won the tender, false otherwise
     */
    public boolean isWinner() {
        return tenderStatus == TenderStatus.AWARDED
                && tenderAwardedBidId != null
                && bidId == tenderAwardedBidId;
    }

    /**
     * Checks if the evaluation score can be viewed for this bid. Scores are
     * visible when the tender is EVALUATED or AWARDED.
     *
     * @return true if scores can be viewed, false otherwise
     */
    public boolean canViewScore() {
        return tenderStatus == TenderStatus.EVALUATED
                || tenderStatus == TenderStatus.AWARDED;
    }

    /**
     * Checks if this bid can be withdrawn. Bids can only be withdrawn while the
     * tender is still OPEN.
     *
     * @return true if the bid can be withdrawn, false otherwise
     */
    public boolean canWithdraw() {
        return tenderStatus == TenderStatus.OPEN;
    }

    // ============================================================
    // EL COMPATIBILITY GETTERS (for JSP access)
    // ============================================================
    /**
     * EL compatibility getter for canViewScore().
     *
     * @return true if scores can be viewed
     */
    public boolean getCanViewScore() {
        return canViewScore();
    }

    /**
     * EL compatibility getter for canWithdraw().
     *
     * @return true if the bid can be withdrawn
     */
    public boolean getCanWithdraw() {
        return canWithdraw();
    }

    /**
     * EL compatibility getter for isWinner().
     *
     * @return true if this bid is the winner
     */
    public boolean getIsWinner() {
        return isWinner();
    }

    // ============================================================
    // GETTERS AND SETTERS - DATABASE FIELDS
    // ============================================================
    /**
     * Gets the bid's unique identifier.
     *
     * @return the bid ID
     */
    public int getBidId() {
        return bidId;
    }

    /**
     * Sets the bid's unique identifier.
     *
     * @param bidId the bid ID to set
     */
    public void setBidId(int bidId) {
        this.bidId = bidId;
    }

    /**
     * Gets the ID of the tender this bid is for.
     *
     * @return the tender ID
     */
    public int getTenderId() {
        return tenderId;
    }

    /**
     * Sets the ID of the tender this bid is for.
     *
     * @param tenderId the tender ID to set
     */
    public void setTenderId(int tenderId) {
        this.tenderId = tenderId;
    }

    /**
     * Gets the user ID of the supplier who submitted this bid.
     *
     * @return the supplier ID
     */
    public int getSupplierId() {
        return supplierId;
    }

    /**
     * Sets the user ID of the supplier who submitted this bid.
     *
     * @param supplierId the supplier ID to set
     */
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * Gets the monetary bid amount.
     *
     * @return the bid amount
     */
    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    /**
     * Sets the monetary bid amount.
     *
     * @param bidAmount the bid amount to set
     */
    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    /**
     * Gets the technical compliance statement.
     *
     * @return the technical compliance statement
     */
    public String getTechnicalComplianceStatement() {
        return technicalComplianceStatement;
    }

    /**
     * Sets the technical compliance statement.
     *
     * @param technicalComplianceStatement the statement to set
     */
    public void setTechnicalComplianceStatement(String technicalComplianceStatement) {
        this.technicalComplianceStatement = technicalComplianceStatement;
    }

    /**
     * Gets the proposed timeline for completion in days.
     *
     * @return the proposed timeline in days
     */
    public int getProposedTimelineDays() {
        return proposedTimelineDays;
    }

    /**
     * Sets the proposed timeline for completion in days.
     *
     * @param proposedTimelineDays the timeline days to set
     */
    public void setProposedTimelineDays(int proposedTimelineDays) {
        this.proposedTimelineDays = proposedTimelineDays;
    }

    /**
     * Gets the server file path to the supporting document.
     *
     * @return the supporting document file path
     */
    public String getSupportingDocumentPath() {
        return supportingDocumentPath;
    }

    /**
     * Sets the server file path to the supporting document.
     *
     * @param supportingDocumentPath the file path to set
     */
    public void setSupportingDocumentPath(String supportingDocumentPath) {
        this.supportingDocumentPath = supportingDocumentPath;
    }

    /**
     * Gets the date and time when the bid was submitted.
     *
     * @return the submission timestamp
     */
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    /**
     * Sets the date and time when the bid was submitted.
     *
     * @param submittedAt the submission timestamp to set
     */
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    // ============================================================
    // GETTERS AND SETTERS - DISPLAY FIELDS
    // ============================================================
    /**
     * Gets the reference number of the associated tender.
     *
     * @return the tender reference number
     */
    public String getTenderReference() {
        return tenderReference;
    }

    /**
     * Sets the reference number of the associated tender.
     *
     * @param tenderReference the tender reference to set
     */
    public void setTenderReference(String tenderReference) {
        this.tenderReference = tenderReference;
    }

    /**
     * Gets the title of the associated tender.
     *
     * @return the tender title
     */
    public String getTenderTitle() {
        return tenderTitle;
    }

    /**
     * Sets the title of the associated tender.
     *
     * @param tenderTitle the tender title to set
     */
    public void setTenderTitle(String tenderTitle) {
        this.tenderTitle = tenderTitle;
    }

    /**
     * Gets the name of the supplier who submitted this bid.
     *
     * @return the supplier's name
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * Sets the name of the supplier who submitted this bid.
     *
     * @param supplierName the supplier's name to set
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * Gets the current status of the associated tender.
     *
     * @return the tender status
     */
    public TenderStatus getTenderStatus() {
        return tenderStatus;
    }

    /**
     * Sets the current status of the associated tender.
     *
     * @param tenderStatus the tender status to set
     */
    public void setTenderStatus(TenderStatus tenderStatus) {
        this.tenderStatus = tenderStatus;
    }

    /**
     * Gets the final weighted score after evaluation.
     *
     * @return the final score, or null if not yet evaluated
     */
    public Double getFinalScore() {
        return finalScore;
    }

    /**
     * Sets the final weighted score after evaluation.
     *
     * @param finalScore the final score to set
     */
    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    /**
     * Gets the rank of this bid based on final score.
     *
     * @return the rank (1 is highest), or null if not ranked
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
     * Gets the ID of the winning bid on the tender.
     *
     * @return the awarded bid ID, or null if not yet awarded
     */
    public Integer getTenderAwardedBidId() {
        return tenderAwardedBidId;
    }

    /**
     * Sets the ID of the winning bid on the tender.
     *
     * @param tenderAwardedBidId the awarded bid ID to set
     */
    public void setTenderAwardedBidId(Integer tenderAwardedBidId) {
        this.tenderAwardedBidId = tenderAwardedBidId;
    }

    /**
     * Gets the date when the tender was awarded.
     *
     * @return the tender award date, or null if not yet awarded
     */
    public LocalDateTime getTenderAwardDate() {
        return tenderAwardDate;
    }

    /**
     * Sets the date when the tender was awarded.
     *
     * @param tenderAwardDate the award date to set
     */
    public void setTenderAwardDate(LocalDateTime tenderAwardDate) {
        this.tenderAwardDate = tenderAwardDate;
    }

    // ============================================================
    // GETTERS AND SETTERS - EVALUATION FIELD
    // ============================================================
    /**
     * Checks if the current user has scored this bid.
     *
     * @return true if current user has scored this bid, false otherwise
     */
    public boolean isHasCurrentUserScored() {
        return hasCurrentUserScored;
    }

    /**
     * Sets whether the current user has scored this bid. Used in the evaluation
     * dashboard to track scoring status.
     *
     * @param hasScored true if the current user has scored this bid, false
     * otherwise
     */
    public void setHasCurrentUserScored(boolean hasScored) {
        this.hasCurrentUserScored = hasScored;
    }

    // ============================================================
    // GETTERS AND SETTERS - SCORE FIELDS (ADDED)
    // ============================================================
    /**
     * Gets the technical compliance score for display.
     *
     * @return the technical score
     */
    public Double getTechnicalScore() {
        return technicalScore;
    }

    /**
     * Sets the technical compliance score for display.
     *
     * @param technicalScore the technical score to set
     */
    public void setTechnicalScore(Double technicalScore) {
        this.technicalScore = technicalScore;
    }

    /**
     * Gets the price score for display.
     *
     * @return the price score
     */
    public Double getPriceScore() {
        return priceScore;
    }

    /**
     * Sets the price score for display.
     *
     * @param priceScore the price score to set
     */
    public void setPriceScore(Double priceScore) {
        this.priceScore = priceScore;
    }

    /**
     * Gets the timeline score for display.
     *
     * @return the timeline score
     */
    public Double getTimelineScore() {
        return timelineScore;
    }

    /**
     * Sets the timeline score for display.
     *
     * @param timelineScore the timeline score to set
     */
    public void setTimelineScore(Double timelineScore) {
        this.timelineScore = timelineScore;
    }

    // ============================================================
    // UTILITY METHODS
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
     * Formats the final score for display with two decimal places.
     *
     * @return formatted final score (e.g., "85.50"), or "N/A" if null
     */
    public String getFormattedFinalScore() {
        if (finalScore == null) {
            return "N/A";
        }
        return String.format("%.2f", finalScore);
    }

    /**
     * Gets the rank as an ordinal display string.
     *
     * @return rank with ordinal suffix (e.g., "1st", "2nd", "3rd", "4th"), or
     * "Unranked" if null
     */
    public String getRankDisplay() {
        if (rank == null) {
            return "Unranked";
        }

        if (rank % 100 >= 11 && rank % 100 <= 13) {
            return rank + "th";
        }

        switch (rank % 10) {
            case 1:
                return rank + "st";
            case 2:
                return rank + "nd";
            case 3:
                return rank + "rd";
            default:
                return rank + "th";
        }
    }

    /**
     * Checks if a supporting document has been uploaded.
     *
     * @return true if supporting document path is set and not empty
     */
    public boolean hasSupportingDocument() {
        return supportingDocumentPath != null && !supportingDocumentPath.trim().isEmpty();
    }

    /**
     * Extracts just the filename from the supporting document path.
     *
     * @return the filename portion of the path, or null if no document exists
     */
    public String getSupportingDocumentName() {
        if (!hasSupportingDocument()) {
            return null;
        }

        int lastSeparator = supportingDocumentPath.lastIndexOf('/');
        if (lastSeparator == -1) {
            lastSeparator = supportingDocumentPath.lastIndexOf('\\');
        }

        if (lastSeparator == -1) {
            return supportingDocumentPath;
        }

        return supportingDocumentPath.substring(lastSeparator + 1);
    }

    /**
     * Validates that all required bid fields are present and valid.
     *
     * @return true if all required fields are valid
     */
    public boolean isValid() {
        return tenderId > 0
                && supplierId > 0
                && bidAmount != null
                && bidAmount.compareTo(BigDecimal.ZERO) > 0
                && technicalComplianceStatement != null
                && !technicalComplianceStatement.trim().isEmpty()
                && proposedTimelineDays > 0;
    }

    /**
     * Gets a string containing all validation error messages.
     *
     * @return concatenated validation errors, or empty string if valid
     */
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        if (tenderId <= 0) {
            errors.append("Invalid tender ID. ");
        }
        if (supplierId <= 0) {
            errors.append("Invalid supplier ID. ");
        }
        if (bidAmount == null || bidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            errors.append("Bid amount must be greater than zero. ");
        }
        if (technicalComplianceStatement == null || technicalComplianceStatement.trim().isEmpty()) {
            errors.append("Technical compliance statement is required. ");
        }
        if (technicalComplianceStatement != null && technicalComplianceStatement.length() > 600) {
            errors.append("Technical compliance statement exceeds 600 characters. ");
        }
        if (proposedTimelineDays <= 0) {
            errors.append("Proposed timeline must be at least 1 day. ");
        }

        return errors.toString();
    }

    // ============================================================
    // OBJECT OVERRIDES
    // ============================================================
    /**
     * Returns a string representation of the Bid object.
     *
     * @return formatted string with bid details
     */
    @Override
    public String toString() {
        return "Bid{"
                + "bidId=" + bidId
                + ", tenderId=" + tenderId
                + ", supplierId=" + supplierId
                + ", bidAmount=" + bidAmount
                + ", proposedTimelineDays=" + proposedTimelineDays
                + ", submittedAt=" + submittedAt
                + ", finalScore=" + finalScore
                + ", rank=" + rank
                + '}';
    }

    /**
     * Compares this bid to another object for equality based on bid ID.
     *
     * @param obj the object to compare against
     * @return true if the bid IDs match
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Bid bid = (Bid) obj;
        return bidId == bid.bidId;
    }

    /**
     * Generates a hash code based on the bid ID.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(bidId);
    }

    /**
     * Gets the detailed display status of this bid considering tender status
     * and award information.
     *
     * @return Detailed status description string
     */
    public String getStatus() {
        // If awarded and this bid is the winner
        if (tenderStatus == TenderStatus.AWARDED && isWinner()) {
            return "Won - Contract Awarded";
        }

        // If awarded but not winner
        if (tenderStatus == TenderStatus.AWARDED && !isWinner()) {
            return "Not Won - Contract Awarded to Another Supplier";
        }

        // If evaluated but not yet awarded
        if (tenderStatus == TenderStatus.EVALUATED) {
            if (finalScore > 0) {
                return "Evaluated - Score: " + String.format("%.2f", finalScore);
            }
            return "Evaluation Complete";
        }

        // If under evaluation
        if (tenderStatus == TenderStatus.UNDER_EVALUATION) {
            return "Under Evaluation";
        }

        // If tender closed but evaluation not started
        if (tenderStatus == TenderStatus.CLOSED) {
            return "Tender Closed - Awaiting Evaluation";
        }

        // If tender is open
        if (tenderStatus == TenderStatus.OPEN) {
            return "Pending - Tender Open for Bidding";
        }

        // Default for submitted bids
        return "Bid Submitted";
    }
}
