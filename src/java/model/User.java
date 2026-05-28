/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import model.enums.UserRole;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a user in the ProcureGov system. Contains both database-mapped
 * fields and additional supplier profile fields. Implements
 * {@link Serializable} for session storage and caching.
 *
 * @author kolisang
 */
public class User implements Serializable {

    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    // ============================================================
    // DATABASE FIELDS
    // ============================================================
    /**
     * Unique identifier for the user
     */
    private int userId;
    /**
     * User's email address (used as login username)
     */
    private String email;
    /**
     * Hashed password for authentication
     */
    private String passwordHash;
    /**
     * User's full name
     */
    private String fullName;
    /**
     * User's physical address
     */
    private String address;
    /**
     * User's contact phone number
     */
    private String contactNumber;
    /**
     * User's role in the system (Supplier, Procurement Officer, etc.)
     */
    private UserRole role;
    /**
     * Registration number for suppliers
     */
    private String registrationNumber;
    /**
     * Number of consecutive failed login attempts
     */
    private int failedAttempts;
    /**
     * Timestamp until which the account is locked
     */
    private Timestamp lockedUntil;
    /**
     * Timestamp when the user account was created
     */
    private Timestamp createdAt;
    /**
     * Timestamp when the user account was last updated
     */
    private Timestamp updatedAt;
    /**
     * Timestamp of the user's last successful login
     */
    private Timestamp lastLogin;

    // ============================================================
    // SUPPLIER PROFILE FIELDS (ADDITIONAL)
    // ============================================================
    /**
     * Supplier's tax identification number
     */
    private String taxNumber;
    /**
     * Supplier's business license number
     */
    private String businessLicense;
    /**
     * Number of years the supplier has been in business
     */
    private int yearsInBusiness;
    /**
     * Number of employees in the supplier's organization
     */
    private int employeeCount;

    // Contact Person Fields
    /**
     * Name of the primary contact person for the supplier
     */
    private String contactName;
    /**
     * Position/title of the contact person
     */
    private String contactPosition;
    /**
     * Email address of the contact person
     */
    private String contactEmail;
    /**
     * Phone number of the contact person
     */
    private String contactPhone;
    /**
     * Alternative phone number for the supplier
     */
    private String alternativePhone;

    // Document Paths
    /**
     * Server file path to the uploaded tax certificate document
     */
    private String taxCertificatePath;
    /**
     * Server file path to the uploaded business license document
     */
    private String licensePath;
    /**
     * Server file path to the uploaded company profile document
     */
    private String profilePath;
    /**
     * Date when the tax certificate was uploaded
     */
    private LocalDateTime taxCertificateDate;
    /**
     * Date when the business license was uploaded
     */
    private LocalDateTime licenseDate;
    /**
     * Date when the company profile was uploaded
     */
    private LocalDateTime profileDate;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    /**
     * Default constructor. Initializes failed attempts counter to zero.
     */
    public User() {
        this.failedAttempts = 0;
    }

    /**
     * Constructs a minimal User with email, full name, and role.
     *
     * @param email the user's email address
     * @param fullName the user's full name
     * @param role the user's role in the system
     */
    public User(String email, String fullName, UserRole role) {
        this();
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    /**
     * Constructs a User with all core fields specified.
     *
     * @param userId the user's unique identifier
     * @param passwordHash the hashed password
     * @param fullName the user's full name
     * @param address the user's physical address
     * @param contactNumber the user's contact number
     * @param role the user's role
     * @param registrationNumber the supplier registration number
     */
    public User(int userId, String passwordHash, String fullName, String address, String contactNumber, UserRole role, String registrationNumber) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.role = role;
        this.registrationNumber = registrationNumber;
        this.failedAttempts = 0;
    }

    // ============================================================
    // FORMATTING METHODS
    // ============================================================
    /**
     * Returns the account creation date formatted as "dd MMM yyyy".
     *
     * @return formatted creation date string, or "N/A" if not set
     */
    public String getFormattedCreatedAt() {
        if (createdAt == null) {
            return "N/A";
        }
        return createdAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Returns the last login timestamp formatted as "dd MMM yyyy, HH:mm".
     *
     * @return formatted last login string, or "Never" if not set
     */
    public String getFormattedLastLogin() {
        if (lastLogin == null) {
            return "Never";
        }
        return lastLogin.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
    }

    /**
     * Returns the tax certificate upload date formatted as "dd MMM yyyy".
     *
     * @return formatted tax certificate date, or "N/A" if not set
     */
    public String getFormattedTaxCertificateDate() {
        if (taxCertificateDate == null) {
            return "N/A";
        }
        return taxCertificateDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Returns the business license upload date formatted as "dd MMM yyyy".
     *
     * @return formatted license date, or "N/A" if not set
     */
    public String getFormattedLicenseDate() {
        if (licenseDate == null) {
            return "N/A";
        }
        return licenseDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Returns the company profile upload date formatted as "dd MMM yyyy".
     *
     * @return formatted profile date, or "N/A" if not set
     */
    public String getFormattedProfileDate() {
        if (profileDate == null) {
            return "N/A";
        }
        return profileDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    // ============================================================
    // GETTERS AND SETTERS - DATABASE FIELDS
    // ============================================================
    /**
     * Gets the user's unique identifier.
     *
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user's unique identifier.
     *
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the user's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's hashed password.
     *
     * @return the password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the user's hashed password.
     *
     * @param passwordHash the password hash to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Gets the user's full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the user's full name.
     *
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the user's physical address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the user's physical address.
     *
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the user's contact phone number.
     *
     * @return the contact number
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Sets the user's contact phone number.
     *
     * @param contactNumber the contact number to set
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * Gets the user's role in the system.
     *
     * @return the user role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Sets the user's role in the system.
     *
     * @param role the role to set
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * Gets the supplier registration number.
     *
     * @return the registration number
     */
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    /**
     * Sets the supplier registration number.
     *
     * @param registrationNumber the registration number to set
     */
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    /**
     * Gets the number of consecutive failed login attempts.
     *
     * @return the failed attempts count
     */
    public int getFailedAttempts() {
        return failedAttempts;
    }

    /**
     * Sets the number of consecutive failed login attempts.
     *
     * @param failedAttempts the failed attempts count to set
     */
    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    /**
     * Gets the timestamp until which the account is locked.
     *
     * @return the locked until timestamp, or null if not locked
     */
    public Timestamp getLockedUntil() {
        return lockedUntil;
    }

    /**
     * Sets the timestamp until which the account is locked.
     *
     * @param lockedUntil the locked until timestamp to set
     */
    public void setLockedUntil(Timestamp lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    /**
     * Gets the account creation timestamp.
     *
     * @return the created at timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the account creation timestamp.
     *
     * @param createdAt the created at timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp.
     *
     * @return the updated at timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp.
     *
     * @param updatedAt the updated at timestamp to set
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the last successful login timestamp.
     *
     * @return the last login timestamp
     */
    public Timestamp getLastLogin() {
        return lastLogin;
    }

    /**
     * Sets the last successful login timestamp.
     *
     * @param lastLogin the last login timestamp to set
     */
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    // ============================================================
    // GETTERS AND SETTERS - SUPPLIER PROFILE FIELDS
    // ============================================================
    /**
     * Gets the supplier's tax identification number.
     *
     * @return the tax number
     */
    public String getTaxNumber() {
        return taxNumber;
    }

    /**
     * Sets the supplier's tax identification number.
     *
     * @param taxNumber the tax number to set
     */
    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    /**
     * Gets the supplier's business license number.
     *
     * @return the business license number
     */
    public String getBusinessLicense() {
        return businessLicense;
    }

    /**
     * Sets the supplier's business license number.
     *
     * @param businessLicense the business license number to set
     */
    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    /**
     * Gets the number of years the supplier has been in business.
     *
     * @return years in business
     */
    public int getYearsInBusiness() {
        return yearsInBusiness;
    }

    /**
     * Sets the number of years the supplier has been in business.
     *
     * @param yearsInBusiness years in business to set
     */
    public void setYearsInBusiness(int yearsInBusiness) {
        this.yearsInBusiness = yearsInBusiness;
    }

    /**
     * Gets the number of employees in the supplier's organization.
     *
     * @return employee count
     */
    public int getEmployeeCount() {
        return employeeCount;
    }

    /**
     * Sets the number of employees in the supplier's organization.
     *
     * @param employeeCount employee count to set
     */
    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    /**
     * Gets the name of the primary contact person.
     *
     * @return the contact name
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Sets the name of the primary contact person.
     *
     * @param contactName the contact name to set
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * Gets the position/title of the contact person.
     *
     * @return the contact position
     */
    public String getContactPosition() {
        return contactPosition;
    }

    /**
     * Sets the position/title of the contact person.
     *
     * @param contactPosition the contact position to set
     */
    public void setContactPosition(String contactPosition) {
        this.contactPosition = contactPosition;
    }

    /**
     * Gets the email address of the contact person.
     *
     * @return the contact email
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Sets the email address of the contact person.
     *
     * @param contactEmail the contact email to set
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /**
     * Gets the phone number of the contact person.
     *
     * @return the contact phone number
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * Sets the phone number of the contact person.
     *
     * @param contactPhone the contact phone number to set
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    /**
     * Gets the alternative phone number for the supplier.
     *
     * @return the alternative phone number
     */
    public String getAlternativePhone() {
        return alternativePhone;
    }

    /**
     * Sets the alternative phone number for the supplier.
     *
     * @param alternativePhone the alternative phone number to set
     */
    public void setAlternativePhone(String alternativePhone) {
        this.alternativePhone = alternativePhone;
    }

    /**
     * Gets the server file path to the uploaded tax certificate.
     *
     * @return the tax certificate file path
     */
    public String getTaxCertificatePath() {
        return taxCertificatePath;
    }

    /**
     * Sets the server file path to the uploaded tax certificate.
     *
     * @param taxCertificatePath the file path to set
     */
    public void setTaxCertificatePath(String taxCertificatePath) {
        this.taxCertificatePath = taxCertificatePath;
    }

    /**
     * Gets the server file path to the uploaded business license.
     *
     * @return the license file path
     */
    public String getLicensePath() {
        return licensePath;
    }

    /**
     * Sets the server file path to the uploaded business license.
     *
     * @param licensePath the file path to set
     */
    public void setLicensePath(String licensePath) {
        this.licensePath = licensePath;
    }

    /**
     * Gets the server file path to the uploaded company profile.
     *
     * @return the profile file path
     */
    public String getProfilePath() {
        return profilePath;
    }

    /**
     * Sets the server file path to the uploaded company profile.
     *
     * @param profilePath the file path to set
     */
    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    /**
     * Gets the date when the tax certificate was uploaded.
     *
     * @return the tax certificate upload date
     */
    public LocalDateTime getTaxCertificateDate() {
        return taxCertificateDate;
    }

    /**
     * Sets the date when the tax certificate was uploaded.
     *
     * @param taxCertificateDate the upload date to set
     */
    public void setTaxCertificateDate(LocalDateTime taxCertificateDate) {
        this.taxCertificateDate = taxCertificateDate;
    }

    /**
     * Gets the date when the business license was uploaded.
     *
     * @return the license upload date
     */
    public LocalDateTime getLicenseDate() {
        return licenseDate;
    }

    /**
     * Sets the date when the business license was uploaded.
     *
     * @param licenseDate the upload date to set
     */
    public void setLicenseDate(LocalDateTime licenseDate) {
        this.licenseDate = licenseDate;
    }

    /**
     * Gets the date when the company profile was uploaded.
     *
     * @return the profile upload date
     */
    public LocalDateTime getProfileDate() {
        return profileDate;
    }

    /**
     * Sets the date when the company profile was uploaded.
     *
     * @param profileDate the upload date to set
     */
    public void setProfileDate(LocalDateTime profileDate) {
        this.profileDate = profileDate;
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================
    /**
     * Checks if the user has a specific role by name (case-insensitive).
     *
     * @param roleName the role name to check against
     * @return true if the user's role matches the given name
     */
    public boolean hasRole(String roleName) {
        return this.role != null && this.role.name().equalsIgnoreCase(roleName);
    }

    /**
     * Checks if the user is a supplier.
     *
     * @return true if the user has the SUPPLIER role
     */
    public boolean isSupplier() {
        return this.role == UserRole.SUPPLIER;
    }

    /**
     * Checks if the user is a Procurement Officer.
     *
     * @return true if user is a Procurement Officer
     */
    public boolean isProcurementOfficer() {
        return this.role == UserRole.PROCUREMENT_OFFICER;
    }

    /**
     * Checks if the user is an Evaluation Committee Member.
     *
     * @return true if user is an Evaluation Committee Member
     */
    public boolean isEvaluationCommitteeMember() {
        return this.role == UserRole.EVALUATION_COMMITTEE;
    }

    /**
     * Checks if the user account is currently locked. An account is locked if
     * the lockedUntil timestamp is after the current time.
     *
     * @return true if account is locked, false otherwise
     */
    public boolean isLocked() {
        if (lockedUntil == null) {
            return false;
        }
        return lockedUntil.after(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Checks if the account lock has expired. An account lock is expired if the
     * lockedUntil timestamp is before the current time.
     *
     * @return true if lock has expired or no lock was set, false if still
     * locked
     */
    public boolean isLockExpired() {
        if (lockedUntil == null) {
            return true;
        }
        return lockedUntil.before(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Gets the remaining lock time in minutes.
     *
     * @return Minutes remaining until the account is unlocked, or 0 if not
     * locked
     */
    public long getRemainingLockMinutes() {
        if (!isLocked()) {
            return 0;
        }
        long diff = lockedUntil.getTime() - System.currentTimeMillis();
        return Math.max(0, diff / (60 * 1000));
    }

    /**
     * Gets the number of remaining login attempts before lockout. Maximum
     * allowed attempts is 3.
     *
     * @return Remaining attempts (0-3)
     */
    public int getRemainingAttempts() {
        return Math.max(0, 3 - failedAttempts);
    }

    /**
     * Checks if the maximum number of failed login attempts has been reached.
     * The maximum is 3 failed attempts.
     *
     * @return true if max attempts (3) reached, false otherwise
     */
    public boolean hasReachedMaxAttempts() {
        return failedAttempts >= 3;
    }

    /**
     * Gets the display name for the user's role.
     *
     * @return Role display name, or "Unknown" if role is null
     */
    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : "Unknown";
    }

    /**
     * Gets a masked version of the email for privacy in display. Example:
     * "test@domain.com" becomes "t***t@domain.com"
     *
     * @return Masked email string, or empty string if email is null
     */
    public String getMaskedEmail() {
        if (email == null || email.isEmpty()) {
            return "";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***" + domain;
        }

        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domain;
    }

    /**
     * Gets the initials from the full name. Example: "Thabo Mokoena" returns
     * "TM"
     *
     * @return Uppercase initials string, or "?" if name is null or empty
     */
    public String getInitials() {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "?";
        }

        String[] parts = fullName.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
            }
        }

        return initials.toString().toUpperCase();
    }

    /**
     * Validates that all required core fields are present. Required fields:
     * email, fullName, role, and passwordHash.
     *
     * @return true if all required fields are valid and non-empty
     */
    public boolean isValid() {
        return email != null && !email.trim().isEmpty()
                && fullName != null && !fullName.trim().isEmpty()
                && role != null
                && passwordHash != null && !passwordHash.isEmpty();
    }

    /**
     * Validates that supplier-specific required fields are present. Checks core
     * validity plus address, contact number, and registration number.
     *
     * @return true if all supplier fields are valid
     */
    public boolean isValidSupplier() {
        return isValid()
                && address != null && !address.trim().isEmpty()
                && contactNumber != null && !contactNumber.trim().isEmpty()
                && registrationNumber != null && !registrationNumber.isEmpty();
    }

    /**
     * Checks if a tax certificate document has been uploaded.
     *
     * @return true if tax certificate path is set and not empty
     */
    public boolean hasTaxCertificate() {
        return taxCertificatePath != null && !taxCertificatePath.trim().isEmpty();
    }

    /**
     * Checks if a business license document has been uploaded.
     *
     * @return true if license path is set and not empty
     */
    public boolean hasBusinessLicense() {
        return licensePath != null && !licensePath.trim().isEmpty();
    }

    /**
     * Checks if a company profile document has been uploaded.
     *
     * @return true if profile path is set and not empty
     */
    public boolean hasCompanyProfile() {
        return profilePath != null && !profilePath.trim().isEmpty();
    }

    /**
     * Checks if all required documents (tax certificate and business license)
     * are uploaded.
     *
     * @return true if both tax certificate and business license have been
     * uploaded
     */
    public boolean hasAllDocuments() {
        return hasTaxCertificate() && hasBusinessLicense();
    }

    /**
     * Returns a string representation of the User object with masked email for
     * privacy.
     *
     * @return formatted string containing user details
     */
    @Override
    public String toString() {
        return "User{"
                + "userId=" + userId
                + ", email='" + (email != null ? getMaskedEmail() : "null") + '\''
                + ", fullName='" + fullName + '\''
                + ", role=" + (role != null ? role.getDisplayName() : "null")
                + ", registrationNumber='" + registrationNumber + '\''
                + ", failedAttempts=" + failedAttempts
                + ", isLocked=" + isLocked()
                + '}';
    }

    /**
     * Compares this user to another object for equality. Two users are
     * considered equal if they have the same userId and email.
     *
     * @param obj the object to compare against
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        User user = (User) obj;

        if (userId != user.userId) {
            return false;
        }
        return email != null ? email.equals(user.email) : user.email == null;
    }

    /**
     * Generates a hash code for this User based on userId and email.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
