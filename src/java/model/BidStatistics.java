package model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Helper class for bid statistics. Encapsulates statistical data about bids for
 * a specific tender, including bid amounts, timelines, and averages. Implements
 * {@link Serializable} for session storage and caching.
 *
 * @author kolisang
 * @version 1.0
 */
public class BidStatistics implements Serializable {

    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    /**
     * Total number of bids submitted for the tender
     */
    private int totalBids;
    /**
     * The lowest bid amount among all submitted bids
     */
    private BigDecimal lowestBid;
    /**
     * The highest bid amount among all submitted bids
     */
    private BigDecimal highestBid;
    /**
     * The average bid amount across all submitted bids
     */
    private BigDecimal averageBid;
    /**
     * The shortest proposed timeline in days
     */
    private int shortestTimeline;
    /**
     * The longest proposed timeline in days
     */
    private int longestTimeline;
    /**
     * The average proposed timeline in days
     */
    private double averageTimeline;

    /**
     * Default constructor. Initializes all counts and values to zero.
     */
    public BidStatistics() {
        this.totalBids = 0;
        this.lowestBid = BigDecimal.ZERO;
        this.highestBid = BigDecimal.ZERO;
        this.averageBid = BigDecimal.ZERO;
        this.shortestTimeline = 0;
        this.longestTimeline = 0;
        this.averageTimeline = 0.0;
    }

    // Getters and Setters
    /**
     * Gets the total number of bids submitted.
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
     * Gets the lowest bid amount among all bids.
     *
     * @return the lowest bid amount
     */
    public BigDecimal getLowestBid() {
        return lowestBid;
    }

    /**
     * Sets the lowest bid amount among all bids.
     *
     * @param lowestBid the lowest bid amount to set
     */
    public void setLowestBid(BigDecimal lowestBid) {
        this.lowestBid = lowestBid;
    }

    /**
     * Gets the highest bid amount among all bids.
     *
     * @return the highest bid amount
     */
    public BigDecimal getHighestBid() {
        return highestBid;
    }

    /**
     * Sets the highest bid amount among all bids.
     *
     * @param highestBid the highest bid amount to set
     */
    public void setHighestBid(BigDecimal highestBid) {
        this.highestBid = highestBid;
    }

    /**
     * Gets the average bid amount across all bids.
     *
     * @return the average bid amount
     */
    public BigDecimal getAverageBid() {
        return averageBid;
    }

    /**
     * Sets the average bid amount across all bids.
     *
     * @param averageBid the average bid amount to set
     */
    public void setAverageBid(BigDecimal averageBid) {
        this.averageBid = averageBid;
    }

    /**
     * Gets the shortest proposed timeline in days.
     *
     * @return the shortest timeline in days
     */
    public int getShortestTimeline() {
        return shortestTimeline;
    }

    /**
     * Sets the shortest proposed timeline in days.
     *
     * @param shortestTimeline the shortest timeline to set
     */
    public void setShortestTimeline(int shortestTimeline) {
        this.shortestTimeline = shortestTimeline;
    }

    /**
     * Gets the longest proposed timeline in days.
     *
     * @return the longest timeline in days
     */
    public int getLongestTimeline() {
        return longestTimeline;
    }

    /**
     * Sets the longest proposed timeline in days.
     *
     * @param longestTimeline the longest timeline to set
     */
    public void setLongestTimeline(int longestTimeline) {
        this.longestTimeline = longestTimeline;
    }

    /**
     * Gets the average proposed timeline in days.
     *
     * @return the average timeline in days
     */
    public double getAverageTimeline() {
        return averageTimeline;
    }

    /**
     * Sets the average proposed timeline in days.
     *
     * @param averageTimeline the average timeline to set
     */
    public void setAverageTimeline(double averageTimeline) {
        this.averageTimeline = averageTimeline;
    }

    /**
     * Formats the lowest bid amount as currency with "M" prefix.
     *
     * @return formatted lowest bid (e.g., "M 1,500,000.00"), or "M 0.00" if
     * null
     */
    public String getFormattedLowestBid() {
        if (lowestBid == null) {
            return "M 0.00";
        }
        return String.format("M %,.2f", lowestBid);
    }

    /**
     * Formats the highest bid amount as currency with "M" prefix.
     *
     * @return formatted highest bid (e.g., "M 2,000,000.00"), or "M 0.00" if
     * null
     */
    public String getFormattedHighestBid() {
        if (highestBid == null) {
            return "M 0.00";
        }
        return String.format("M %,.2f", highestBid);
    }

    /**
     * Formats the average bid amount as currency with "M" prefix.
     *
     * @return formatted average bid (e.g., "M 1,750,000.00"), or "M 0.00" if
     * null
     */
    public String getFormattedAverageBid() {
        if (averageBid == null) {
            return "M 0.00";
        }
        return String.format("M %,.2f", averageBid);
    }

    /**
     * Formats the average timeline with one decimal place and "days" suffix.
     *
     * @return formatted average timeline (e.g., "45.5 days")
     */
    public String getFormattedAverageTimeline() {
        return String.format("%.1f days", averageTimeline);
    }
}
