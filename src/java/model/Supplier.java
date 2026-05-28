package model;

import model.enums.UserRole;

/**
 * Supplier entity class extending User. Represents a registered company or
 * individual submitting bids. Contains supplier-specific fields such as
 * verification status and bid statistics.
 *
 * @author YourName
 * @version 1.0
 */
public class Supplier extends User {

    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    /**
     * Company registration number from the business registry
     */
    private String companyRegistrationNumber;
    /**
     * Tax clearance certificate number
     */
    private String taxClearanceNumber;
    /**
     * Whether the supplier has been verified by procurement officers
     */
    private boolean isVerified;
    /**
     * Total number of bids submitted by this supplier
     */
    private int totalBidsSubmitted;
    /**
     * Total number of bids won by this supplier
     */
    private int totalBidsWon;

    /**
     * Default constructor. Initializes the supplier with SUPPLIER role,
     * unverified status, and zero bid counts.
     */
    public Supplier() {
        super();
        this.setRole(UserRole.SUPPLIER);
        this.isVerified = false;
        this.totalBidsSubmitted = 0;
        this.totalBidsWon = 0;
    }

    /**
     * Constructor with essential fields. Creates a new supplier with the given
     * email and full name, setting role to SUPPLIER.
     *
     * @param email the supplier's email address
     * @param fullName the supplier's company or individual name
     */
    public Supplier(String email, String fullName) {
        super(email, fullName, UserRole.SUPPLIER);
        this.isVerified = false;
        this.totalBidsSubmitted = 0;
        this.totalBidsWon = 0;
    }

    // Getters and Setters
    /**
     * Gets the company registration number from the business registry.
     *
     * @return the company registration number
     */
    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    /**
     * Sets the company registration number from the business registry.
     *
     * @param companyRegistrationNumber the company registration number to set
     */
    public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
        this.companyRegistrationNumber = companyRegistrationNumber;
    }

    /**
     * Gets the tax clearance certificate number.
     *
     * @return the tax clearance number
     */
    public String getTaxClearanceNumber() {
        return taxClearanceNumber;
    }

    /**
     * Sets the tax clearance certificate number.
     *
     * @param taxClearanceNumber the tax clearance number to set
     */
    public void setTaxClearanceNumber(String taxClearanceNumber) {
        this.taxClearanceNumber = taxClearanceNumber;
    }

    /**
     * Checks if the supplier has been verified by procurement officers.
     *
     * @return true if the supplier is verified, false otherwise
     */
    public boolean isVerified() {
        return isVerified;
    }

    /**
     * Sets the verification status of the supplier.
     *
     * @param verified the verification status to set
     */
    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    /**
     * Gets the total number of bids submitted by this supplier.
     *
     * @return total bids submitted count
     */
    public int getTotalBidsSubmitted() {
        return totalBidsSubmitted;
    }

    /**
     * Sets the total number of bids submitted by this supplier.
     *
     * @param totalBidsSubmitted the total bids submitted to set
     */
    public void setTotalBidsSubmitted(int totalBidsSubmitted) {
        this.totalBidsSubmitted = totalBidsSubmitted;
    }

    /**
     * Gets the total number of bids won by this supplier.
     *
     * @return total bids won count
     */
    public int getTotalBidsWon() {
        return totalBidsWon;
    }

    /**
     * Sets the total number of bids won by this supplier.
     *
     * @param totalBidsWon the total bids won to set
     */
    public void setTotalBidsWon(int totalBidsWon) {
        this.totalBidsWon = totalBidsWon;
    }

    /**
     * Calculates the win rate percentage based on bids won vs bids submitted.
     * Formula: (totalBidsWon / totalBidsSubmitted) × 100
     *
     * @return Win rate as a percentage value, or 0.0 if no bids have been
     * submitted
     */
    public double getWinRate() {
        if (totalBidsSubmitted == 0) {
            return 0.0;
        }
        return (double) totalBidsWon / totalBidsSubmitted * 100;
    }

    /**
     * Gets the win rate formatted as a percentage string with one decimal
     * place.
     *
     * @return Formatted win rate (e.g., "75.0%")
     */
    public String getFormattedWinRate() {
        return String.format("%.1f%%", getWinRate());
    }

    /**
     * Increments the total bids submitted counter by one. Should be called each
     * time the supplier submits a new bid.
     */
    public void incrementBidsSubmitted() {
        this.totalBidsSubmitted++;
    }

    /**
     * Increments the total bids won counter by one. Should be called when the
     * supplier wins a tender award.
     */
    public void incrementBidsWon() {
        this.totalBidsWon++;
    }

    /**
     * Returns a string representation of the Supplier object including user ID,
     * email, full name, registration number, verification status, bid
     * statistics, and win rate.
     *
     * @return formatted string with supplier details
     */
    @Override
    public String toString() {
        return "Supplier{"
                + "userId=" + getUserId()
                + ", email='" + getEmail() + '\''
                + ", fullName='" + getFullName() + '\''
                + ", registrationNumber='" + getRegistrationNumber() + '\''
                + ", companyRegistrationNumber='" + companyRegistrationNumber + '\''
                + ", isVerified=" + isVerified
                + ", totalBidsSubmitted=" + totalBidsSubmitted
                + ", totalBidsWon=" + totalBidsWon
                + ", winRate=" + getFormattedWinRate()
                + '}';
    }
}
