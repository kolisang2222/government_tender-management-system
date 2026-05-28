package model;

import model.enums.TenderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Encapsulates statistical data across all tenders. Used for dashboard
 * overviews and reporting. Implements {@link Serializable} for session storage
 * and caching.
 *
 * @author YourName
 * @version 1.0
 */
public class TenderStatistics implements Serializable {

    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    // ============================================================
    // FIELDS
    // ============================================================
    /**
     * Total number of tenders in the system
     */
    private int totalTenders;
    /**
     * Number of tenders in DRAFT status
     */
    private int draftCount;
    /**
     * Number of tenders in OPEN status
     */
    private int openCount;
    /**
     * Number of tenders in CLOSED status
     */
    private int closedCount;
    /**
     * Number of tenders in UNDER_EVALUATION status
     */
    private int underEvaluationCount;
    /**
     * Number of tenders in EVALUATED status
     */
    private int evaluatedCount;
    /**
     * Number of tenders in AWARDED status
     */
    private int awardedCount;

    /**
     * Total number of bids submitted across all tenders
     */
    private int totalBids;
    /**
     * Total estimated value of all tenders
     */
    private BigDecimal totalTenderValue;
    /**
     * Total awarded value across all awarded tenders
     */
    private BigDecimal totalAwardedValue;

    /**
     * Map of tender counts grouped by category name
     */
    private Map<String, Integer> tendersByCategory;
    /**
     * Map of tender counts grouped by month
     */
    private Map<String, Integer> tendersByMonth;

    /**
     * Percentage rate of tenders that have been awarded (0.0 to 1.0)
     */
    private double awardRate;
    /**
     * Average number of bids received per tender
     */
    private double averageBidsPerTender;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    /**
     * Default constructor initializing all counts and values to zero.
     */
    public TenderStatistics() {
        this.totalTenders = 0;
        this.draftCount = 0;
        this.openCount = 0;
        this.closedCount = 0;
        this.underEvaluationCount = 0;
        this.evaluatedCount = 0;
        this.awardedCount = 0;
        this.totalBids = 0;
        this.totalTenderValue = BigDecimal.ZERO;
        this.totalAwardedValue = BigDecimal.ZERO;
        this.awardRate = 0.0;
        this.averageBidsPerTender = 0.0;
    }

    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    /**
     * Gets the total number of tenders.
     *
     * @return total tenders count
     */
    public int getTotalTenders() {
        return totalTenders;
    }

    /**
     * Sets the total number of tenders.
     *
     * @param totalTenders the total tenders count to set
     */
    public void setTotalTenders(int totalTenders) {
        this.totalTenders = totalTenders;
    }

    /**
     * Gets the number of tenders in DRAFT status.
     *
     * @return draft tenders count
     */
    public int getDraftCount() {
        return draftCount;
    }

    /**
     * Sets the number of tenders in DRAFT status.
     *
     * @param draftCount the draft count to set
     */
    public void setDraftCount(int draftCount) {
        this.draftCount = draftCount;
    }

    /**
     * Gets the number of tenders in OPEN status.
     *
     * @return open tenders count
     */
    public int getOpenCount() {
        return openCount;
    }

    /**
     * Sets the number of tenders in OPEN status.
     *
     * @param openCount the open count to set
     */
    public void setOpenCount(int openCount) {
        this.openCount = openCount;
    }

    /**
     * Gets the number of tenders in CLOSED status.
     *
     * @return closed tenders count
     */
    public int getClosedCount() {
        return closedCount;
    }

    /**
     * Sets the number of tenders in CLOSED status.
     *
     * @param closedCount the closed count to set
     */
    public void setClosedCount(int closedCount) {
        this.closedCount = closedCount;
    }

    /**
     * Gets the number of tenders in UNDER_EVALUATION status.
     *
     * @return under evaluation tenders count
     */
    public int getUnderEvaluationCount() {
        return underEvaluationCount;
    }

    /**
     * Sets the number of tenders in UNDER_EVALUATION status.
     *
     * @param underEvaluationCount the under evaluation count to set
     */
    public void setUnderEvaluationCount(int underEvaluationCount) {
        this.underEvaluationCount = underEvaluationCount;
    }

    /**
     * Gets the number of tenders in EVALUATED status.
     *
     * @return evaluated tenders count
     */
    public int getEvaluatedCount() {
        return evaluatedCount;
    }

    /**
     * Sets the number of tenders in EVALUATED status.
     *
     * @param evaluatedCount the evaluated count to set
     */
    public void setEvaluatedCount(int evaluatedCount) {
        this.evaluatedCount = evaluatedCount;
    }

    /**
     * Gets the number of tenders in AWARDED status.
     *
     * @return awarded tenders count
     */
    public int getAwardedCount() {
        return awardedCount;
    }

    /**
     * Sets the number of tenders in AWARDED status.
     *
     * @param awardedCount the awarded count to set
     */
    public void setAwardedCount(int awardedCount) {
        this.awardedCount = awardedCount;
    }

    /**
     * Gets the total number of bids submitted across all tenders.
     *
     * @return total bids count
     */
    public int getTotalBids() {
        return totalBids;
    }

    /**
     * Sets the total number of bids submitted.
     *
     * @param totalBids the total bids count to set
     */
    public void setTotalBids(int totalBids) {
        this.totalBids = totalBids;
    }

    /**
     * Gets the total estimated value of all tenders.
     *
     * @return total tender value as BigDecimal
     */
    public BigDecimal getTotalTenderValue() {
        return totalTenderValue;
    }

    /**
     * Sets the total estimated value of all tenders.
     *
     * @param totalTenderValue the total tender value to set
     */
    public void setTotalTenderValue(BigDecimal totalTenderValue) {
        this.totalTenderValue = totalTenderValue;
    }

    /**
     * Gets the total awarded value across all awarded tenders.
     *
     * @return total awarded value as BigDecimal
     */
    public BigDecimal getTotalAwardedValue() {
        return totalAwardedValue;
    }

    /**
     * Sets the total awarded value across all awarded tenders.
     *
     * @param totalAwardedValue the total awarded value to set
     */
    public void setTotalAwardedValue(BigDecimal totalAwardedValue) {
        this.totalAwardedValue = totalAwardedValue;
    }

    /**
     * Gets the map of tender counts grouped by category name.
     *
     * @return map of category names to tender counts
     */
    public Map<String, Integer> getTendersByCategory() {
        return tendersByCategory;
    }

    /**
     * Sets the map of tender counts grouped by category name.
     *
     * @param tendersByCategory map of category names to tender counts
     */
    public void setTendersByCategory(Map<String, Integer> tendersByCategory) {
        this.tendersByCategory = tendersByCategory;
    }

    /**
     * Gets the map of tender counts grouped by month.
     *
     * @return map of month strings to tender counts
     */
    public Map<String, Integer> getTendersByMonth() {
        return tendersByMonth;
    }

    /**
     * Sets the map of tender counts grouped by month.
     *
     * @param tendersByMonth map of month strings to tender counts
     */
    public void setTendersByMonth(Map<String, Integer> tendersByMonth) {
        this.tendersByMonth = tendersByMonth;
    }

    /**
     * Gets the award rate as a decimal value between 0.0 and 1.0.
     *
     * @return the award rate (e.g., 0.75 for 75%)
     */
    public double getAwardRate() {
        return awardRate;
    }

    /**
     * Sets the award rate as a decimal value between 0.0 and 1.0.
     *
     * @param awardRate the award rate to set
     */
    public void setAwardRate(double awardRate) {
        this.awardRate = awardRate;
    }

    /**
     * Gets the average number of bids received per tender.
     *
     * @return average bids per tender
     */
    public double getAverageBidsPerTender() {
        return averageBidsPerTender;
    }

    /**
     * Sets the average number of bids received per tender.
     *
     * @param averageBidsPerTender the average bids per tender to set
     */
    public void setAverageBidsPerTender(double averageBidsPerTender) {
        this.averageBidsPerTender = averageBidsPerTender;
    }

    // ============================================================
    // FORMATTED GETTERS
    // ============================================================
    /**
     * Gets the award rate formatted as a percentage string with one decimal
     * place.
     *
     * @return formatted award rate (e.g., "75.5%")
     */
    public String getFormattedAwardRate() {
        return String.format("%.1f%%", awardRate * 100);
    }

    /**
     * Gets the average bids per tender formatted with one decimal place.
     *
     * @return formatted average (e.g., "3.5")
     */
    public String getFormattedAverageBidsPerTender() {
        return String.format("%.1f", averageBidsPerTender);
    }

    /**
     * Gets the total tender value formatted as currency with thousands
     * separator.
     *
     * @return formatted total tender value (e.g., "M 1,500,000.00")
     */
    public String getFormattedTotalTenderValue() {
        return String.format("M %,.2f", totalTenderValue);
    }

    /**
     * Gets the total awarded value formatted as currency with thousands
     * separator.
     *
     * @return formatted total awarded value (e.g., "M 750,000.00")
     */
    public String getFormattedTotalAwardedValue() {
        return String.format("M %,.2f", totalAwardedValue);
    }

    /**
     * Gets the tender count for a specific {@link TenderStatus}. Returns the
     * count corresponding to the given status.
     *
     * @param status the tender status to get the count for
     * @return the number of tenders in the specified status, or 0 if status is
     * null
     */
    public int getCountForStatus(TenderStatus status) {
        switch (status) {
            case DRAFT:
                return draftCount;
            case OPEN:
                return openCount;
            case CLOSED:
                return closedCount;
            case UNDER_EVALUATION:
                return underEvaluationCount;
            case EVALUATED:
                return evaluatedCount;
            case AWARDED:
                return awardedCount;
            default:
                return 0;
        }
    }
}
