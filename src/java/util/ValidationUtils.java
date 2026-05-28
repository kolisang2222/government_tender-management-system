package util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for performing comprehensive validation operations across the
 * ProcureGov application.
 * <p>
 * This class provides static methods for validating user inputs including email
 * addresses, phone numbers, passwords, reference numbers, monetary amounts,
 * dates, and various string formats. Each validation method follows the
 * business rules defined in the system requirements, with detailed error
 * messaging available for user feedback.
 * </p>
 * <p>
 * The class also includes input sanitization methods to prevent Cross-Site
 * Scripting (XSS) attacks and helper methods for common validation patterns
 * used throughout the application.
 * </p>
 *
 * @author Kolisang Phatela
 * @version 1.0
 * @see java.util.regex.Pattern
 */
public class ValidationUtils {

    // ============================================================
    // Regular Expression Patterns
    // ============================================================
    /**
     * Regular expression pattern for validating email address format. Pattern:
     * {@code ^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$}
     */
    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Regular expression pattern for validating phone number format. Allows
     * digits, spaces, hyphens, parentheses, and an optional leading plus sign.
     */
    private static final Pattern PHONE_PATTERN
            = Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{8,20}$");

    /**
     * Regular expression pattern for validating strong password requirements.
     * Requires minimum 8 characters with uppercase, lowercase, digit, and
     * special character.
     */
    private static final Pattern STRONG_PASSWORD_PATTERN
            = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    /**
     * Regular expression pattern for validating tender reference numbers.
     * Format: MPW-YYYY-NNNN
     */
    private static final Pattern REFERENCE_NUMBER_PATTERN
            = Pattern.compile("^MPW-\\d{4}-\\d{4}$");

    /**
     * Regular expression pattern for validating supplier registration numbers.
     * Format: SUP-YYYY-NNNN
     */
    private static final Pattern SUPPLIER_REGISTRATION_PATTERN
            = Pattern.compile("^SUP-\\d{4}-\\d{4}$");

    /**
     * Regular expression pattern for validating monetary amount format. Allows
     * whole numbers or up to two decimal places.
     */
    private static final Pattern AMOUNT_PATTERN
            = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    /**
     * Regular expression pattern for validating full names. Allows letters,
     * numbers, spaces, hyphens, apostrophes, periods, parentheses, commas, and
     * ampersands.
     */
    private static final Pattern NAME_PATTERN
            = Pattern.compile("^[a-zA-Z0-9\\s\\-\\'\\.\\(\\)\\,&]+$");

    /**
     * Regular expression pattern for validating alphanumeric strings with
     * spaces and hyphens.
     */
    private static final Pattern ALPHANUMERIC_PATTERN
            = Pattern.compile("^[a-zA-Z0-9\\s\\-]+$");

    /**
     * Regular expression pattern for validating numeric-only strings.
     */
    private static final Pattern NUMERIC_PATTERN
            = Pattern.compile("^\\d+$");

    // ============================================================
    // Email Validation
    // ============================================================
    /**
     * Validates an email address format according to standard email pattern
     * requirements.
     * <p>
     * Requirements:
     * <ul>
     * <li>Cannot be null or empty</li>
     * <li>Must contain @ symbol</li>
     * <li>Must have valid domain with at least one dot</li>
     * <li>TLD must be at least 2 characters</li>
     * <li>Maximum length of 100 characters</li>
     * </ul>
     * </p>
     * <p>
     * Valid examples:
     * <ul>
     * <li>user@example.com</li>
     * <li>info@lesothoconstruction.co.ls</li>
     * <li>firstname.lastname@domain.com</li>
     * </ul>
     * </p>
     * <p>
     * Invalid examples:
     * <ul>
     * <li>user@.com</li>
     * <li>@domain.com</li>
     * <li>user@domain</li>
     * <li>user@domain.c</li>
     * </ul>
     * </p>
     *
     * @param email The email address to validate
     * @return {@code true} if email meets all validation criteria;
     * {@code false} otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String trimmedEmail = email.trim().toLowerCase();

        if (trimmedEmail.length() > 100) {
            return false;
        }

        return EMAIL_PATTERN.matcher(trimmedEmail).matches();
    }

    /**
     * Validates an email address and returns a detailed, user-friendly error
     * message describing why the validation failed.
     * <p>
     * This method performs step-by-step validation and returns specific error
     * messages suitable for display to the user in a form validation context.
     * </p>
     *
     * @param email The email address to validate
     * @return A descriptive error message if validation fails; {@code null} if
     * the email is valid
     */
    public static String getEmailError(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email address is required.";
        }

        String trimmedEmail = email.trim();

        if (trimmedEmail.length() > 100) {
            return "Email address cannot exceed 100 characters.";
        }

        if (!trimmedEmail.contains("@")) {
            return "Email address must contain '@' symbol.";
        }

        String[] parts = trimmedEmail.split("@");
        if (parts.length != 2) {
            return "Email address must contain exactly one '@' symbol.";
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.isEmpty()) {
            return "Email address must have a local part before '@'.";
        }

        if (domainPart.isEmpty()) {
            return "Email address must have a domain after '@'.";
        }

        if (!domainPart.contains(".")) {
            return "Email domain must contain a dot (.).";
        }

        String tld = domainPart.substring(domainPart.lastIndexOf('.') + 1);
        if (tld.length() < 2) {
            return "Email domain TLD must be at least 2 characters.";
        }

        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return "Please enter a valid email address.";
        }

        return null;
    }

    // ============================================================
    // Phone Number Validation
    // ============================================================
    /**
     * Validates a phone number format according to flexible international and
     * local standards.
     * <p>
     * Requirements:
     * <ul>
     * <li>Cannot be null or empty</li>
     * <li>8-20 characters in length</li>
     * <li>Can include digits, spaces, hyphens, parentheses, and an optional
     * leading '+'</li>
     * <li>Must contain at least 8 numeric digits</li>
     * </ul>
     * </p>
     * <p>
     * Valid examples:
     * <ul>
     * <li>+266 2231 5678</li>
     * <li>22315678</li>
     * <li>+266-2231-5678</li>
     * <li>(02) 2231 5678</li>
     * </ul>
     * </p>
     *
     * @param phoneNumber The phone number to validate
     * @return {@code true} if phone number meets all validation criteria;
     * {@code false} otherwise
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
    }

    /**
     * Validates a phone number and returns a detailed, user-friendly error
     * message describing why the validation failed.
     *
     * @param phoneNumber The phone number to validate
     * @return A descriptive error message if validation fails; {@code null} if
     * the phone number is valid
     */
    public static String getPhoneNumberError(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "Contact number is required.";
        }

        String trimmed = phoneNumber.trim();

        if (trimmed.length() < 8) {
            return "Contact number must be at least 8 digits.";
        }

        if (trimmed.length() > 20) {
            return "Contact number cannot exceed 20 characters.";
        }

        String digitsOnly = trimmed.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 8) {
            return "Contact number must contain at least 8 digits.";
        }

        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            return "Please enter a valid contact number.";
        }

        return null;
    }

    // ============================================================
    // Password Validation
    // ============================================================
    /**
     * Validates password strength according to security best practices.
     * <p>
     * Requirements:
     * <ul>
     * <li>At least 8 characters in length</li>
     * <li>At least one uppercase letter (A-Z)</li>
     * <li>At least one lowercase letter (a-z)</li>
     * <li>At least one digit (0-9)</li>
     * <li>At least one special character (@$!%*?&amp;)</li>
     * </ul>
     * </p>
     *
     * @param password The password to validate
     * @return {@code true} if password meets all strength requirements;
     * {@code false} otherwise
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validates password strength and returns a detailed, user-friendly error
     * message describing which specific requirement was not met.
     *
     * @param password The password to validate
     * @return A descriptive error message if validation fails; {@code null} if
     * the password is valid
     */
    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required.";
        }

        if (password.length() < 8) {
            return "Password must be at least 8 characters.";
        }

        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }

        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }

        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number.";
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            return "Password must contain at least one special character (@$!%*?&).";
        }

        return null;
    }

    /**
     * Compares two password strings for exact equality.
     * <p>
     * This method is typically used during registration to verify that the user
     * correctly typed their intended password in a confirmation field.
     * </p>
     *
     * @param password The original password string
     * @param confirmPassword The confirmation password string to compare
     * @return {@code true} if both passwords are non-null and exactly equal;
     * {@code false} otherwise
     */
    public static boolean passwordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    // ============================================================
    // Reference Number Validation
    // ============================================================
    /**
     * Validates a tender reference number against the required format.
     * <p>
     * Format: {@code MPW-YYYY-NNNN}
     * </p>
     * <p>
     * Examples of valid reference numbers:
     * <ul>
     * <li>MPW-2026-0042</li>
     * <li>MPW-2025-0156</li>
     * </ul>
     * </p>
     *
     * @param referenceNumber The reference number to validate
     * @return {@code true} if reference number matches the required format;
     * {@code false} otherwise
     */
    public static boolean isValidReferenceNumber(String referenceNumber) {
        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            return false;
        }
        return REFERENCE_NUMBER_PATTERN.matcher(referenceNumber.trim()).matches();
    }

    /**
     * Validates a supplier registration number against the required format.
     * <p>
     * Format: {@code SUP-YYYY-NNNN}
     * </p>
     * <p>
     * Examples of valid registration numbers:
     * <ul>
     * <li>SUP-2026-0001</li>
     * <li>SUP-2025-0042</li>
     * </ul>
     * </p>
     *
     * @param registrationNumber The registration number to validate
     * @return {@code true} if registration number matches the required format;
     * {@code false} otherwise
     */
    public static boolean isValidSupplierRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            return false;
        }
        return SUPPLIER_REGISTRATION_PATTERN.matcher(registrationNumber.trim()).matches();
    }

    // ============================================================
    // Name Validation
    // ============================================================
    /**
     * Validates a full name according to system business rules.
     * <p>
     * Requirements:
     * <ul>
     * <li>3-100 characters in length (after trimming)</li>
     * <li>Only letters, numbers, spaces, hyphens, apostrophes, periods,
     * parentheses, commas, and ampersands</li>
     * </ul>
     * </p>
     *
     * @param fullName The full name to validate
     * @return {@code true} if name meets all validation criteria; {@code false}
     * otherwise
     */
    public static boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.trim().length() < 3) {
            return false;
        }

        String trimmed = fullName.trim();

        if (trimmed.length() > 100) {
            return false;
        }

        return NAME_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Validates a full name and returns a detailed, user-friendly error message
     * describing why the validation failed.
     *
     * @param fullName The full name to validate
     * @return A descriptive error message if validation fails; {@code null} if
     * the name is valid
     */
    public static String getFullNameError(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Full name is required.";
        }

        String trimmed = fullName.trim();

        if (trimmed.length() < 3) {
            return "Full name must be at least 3 characters.";
        }

        if (trimmed.length() > 100) {
            return "Full name cannot exceed 100 characters.";
        }

        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            return "Full name contains invalid characters.";
        }

        return null;
    }

    // ============================================================
    // Address Validation
    // ============================================================
    /**
     * Validates a physical address according to system business rules.
     * <p>
     * Requirements:
     * <ul>
     * <li>10-500 characters in length (after trimming)</li>
     * </ul>
     * </p>
     *
     * @param address The address to validate
     * @return {@code true} if address meets length requirements; {@code false}
     * otherwise
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().length() < 10) {
            return false;
        }
        return address.trim().length() <= 500;
    }

    /**
     * Validates a physical address and returns a detailed, user-friendly error
     * message describing why the validation failed.
     *
     * @param address The address to validate
     * @return A descriptive error message if validation fails; {@code null} if
     * the address is valid
     */
    public static String getAddressError(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "Physical address is required.";
        }

        String trimmed = address.trim();

        if (trimmed.length() < 10) {
            return "Physical address must be at least 10 characters.";
        }

        if (trimmed.length() > 500) {
            return "Physical address cannot exceed 500 characters.";
        }

        return null;
    }

    // ============================================================
    // Amount Validation
    // ============================================================
    /**
     * Validates a monetary amount string format.
     * <p>
     * Accepts whole numbers or decimal values with up to two decimal places.
     * Examples: "1000", "1500.50", "25000.00"
     * </p>
     *
     * @param amount The amount string to validate
     * @return {@code true} if amount string matches valid monetary format;
     * {@code false} otherwise
     */
    public static boolean isValidAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            return false;
        }
        return AMOUNT_PATTERN.matcher(amount.trim()).matches();
    }

    /**
     * Validates that a BigDecimal amount is strictly positive (greater than
     * zero).
     *
     * @param amount The BigDecimal amount to validate
     * @return {@code true} if amount is non-null and greater than zero;
     * {@code false} otherwise
     */
    public static boolean isPositiveAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    // ============================================================
    // Score Validation
    // ============================================================
    /**
     * Validates that a numeric score falls within the acceptable range of 0.0
     * to 100.0 inclusive.
     * <p>
     * This method is used for validating technical compliance scores during bid
     * evaluation.
     * </p>
     *
     * @param score The score value to validate
     * @return {@code true} if score is between 0.0 and 100.0 inclusive;
     * {@code false} otherwise
     */
    public static boolean isValidScore(double score) {
        return score >= 0.0 && score <= 100.0;
    }

    /**
     * Validates that a score string represents a numeric value within the range
     * 0.0 to 100.0.
     *
     * @param score The score string to validate
     * @return {@code true} if string can be parsed to a valid score within
     * range; {@code false} otherwise
     */
    public static boolean isValidScore(String score) {
        if (score == null || score.trim().isEmpty()) {
            return false;
        }

        try {
            double value = Double.parseDouble(score.trim());
            return isValidScore(value);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ============================================================
    // Date Validation
    // ============================================================
    /**
     * Validates that a given date and time is in the future relative to the
     * current system time.
     * <p>
     * This method is used for validating tender closing dates to ensure they
     * are set in the future.
     * </p>
     *
     * @param dateTime The LocalDateTime to validate
     * @return {@code true} if dateTime is non-null and after the current system
     * time; {@code false} otherwise
     */
    public static boolean isFutureDate(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Validates that an end date occurs after a start date.
     *
     * @param startDate The start date to compare
     * @param endDate The end date to compare
     * @return {@code true} if both dates are non-null and endDate is after
     * startDate; {@code false} otherwise
     */
    public static boolean isValidDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return endDate.isAfter(startDate);
    }

    /**
     * Validates that a date string conforms to the ISO date format
     * (yyyy-MM-dd).
     *
     * @param dateStr The date string to validate
     * @return {@code true} if string can be parsed as a valid date in
     * yyyy-MM-dd format; {@code false} otherwise
     */
    public static boolean isValidDateFormat(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime.parse(dateStr.trim() + "T00:00:00");
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // ============================================================
    // General String Validation
    // ============================================================
    /**
     * Checks whether a string is non-null and contains at least one
     * non-whitespace character.
     *
     * @param value The string to check
     * @return {@code true} if string is non-null and not empty after trimming;
     * {@code false} otherwise
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Checks whether a string is null or contains only whitespace characters.
     *
     * @param value The string to check
     * @return {@code true} if string is null or empty after trimming;
     * {@code false} otherwise
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Validates that a string's trimmed length falls within specified inclusive
     * bounds.
     *
     * @param value The string to validate (may be null)
     * @param min The minimum acceptable length (inclusive)
     * @param max The maximum acceptable length (inclusive)
     * @return {@code true} if trimmed length is between min and max inclusive;
     * {@code false} otherwise
     */
    public static boolean isWithinLength(String value, int min, int max) {
        if (value == null) {
            return min == 0;
        }
        int length = value.trim().length();
        return length >= min && length <= max;
    }

    /**
     * Validates that a string contains only alphanumeric characters, spaces,
     * and hyphens.
     *
     * @param value The string to validate
     * @return {@code true} if string matches alphanumeric pattern;
     * {@code false} otherwise
     */
    public static boolean isAlphanumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return ALPHANUMERIC_PATTERN.matcher(value.trim()).matches();
    }

    /**
     * Validates that a string contains only numeric digit characters (0-9).
     *
     * @param value The string to validate
     * @return {@code true} if string contains only digits; {@code false}
     * otherwise
     */
    public static boolean isNumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return NUMERIC_PATTERN.matcher(value.trim()).matches();
    }

    // ============================================================
    // Timeline Validation
    // ============================================================
    /**
     * Validates a proposed timeline in days.
     * <p>
     * Acceptable values are positive integers up to 3650 days (approximately 10
     * years).
     * </p>
     *
     * @param days The number of days to validate
     * @return {@code true} if days is positive and within reasonable bounds;
     * {@code false} otherwise
     */
    public static boolean isValidTimelineDays(int days) {
        return days > 0 && days <= 3650; // Max 10 years
    }

    /**
     * Validates a timeline days string by parsing to integer and checking
     * bounds.
     *
     * @param daysStr The days string to validate
     * @return {@code true} if string parses to a valid positive integer within
     * bounds; {@code false} otherwise
     */
    public static boolean isValidTimelineDays(String daysStr) {
        if (daysStr == null || daysStr.trim().isEmpty()) {
            return false;
        }

        try {
            int days = Integer.parseInt(daysStr.trim());
            return isValidTimelineDays(days);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ============================================================
    // Sanitization
    // ============================================================
    /**
     * Sanitizes user input by escaping HTML special characters to prevent
     * Cross-Site Scripting (XSS) attacks.
     * <p>
     * The following character replacements are performed:
     * <ul>
     * <li>&amp; → &amp;amp;</li>
     * <li>&lt; → &amp;lt;</li>
     * <li>&gt; → &amp;gt;</li>
     * <li>" → &amp;quot;</li>
     * <li>' → &amp;#x27;</li>
     * <li>/ → &amp;#x2F;</li>
     * </ul>
     * </p>
     *
     * @param input The raw input string to sanitize
     * @return A sanitized string safe for HTML display, or {@code null} if
     * input was null
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * Truncates a string to a specified maximum length, appending an ellipsis
     * if truncation occurs.
     *
     * @param value The string to truncate
     * @param maxLength The maximum length of the returned string (including
     * ellipsis if truncated)
     * @return The truncated string with ellipsis if original exceeded
     * maxLength, or the original string if not; returns {@code null} if input
     * value is null
     */
    public static String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    // ============================================================
    // Combined Validation
    // ============================================================
    /**
     * Performs comprehensive validation of all required supplier registration
     * fields.
     * <p>
     * This convenience method validates full name, email, address, contact
     * number, password strength, and password confirmation in a single call. It
     * returns the first encountered error message, suitable for displaying to
     * the user during the registration process.
     * </p>
     *
     * @param fullName The supplier's full name to validate
     * @param email The supplier's email address to validate
     * @param address The supplier's physical address to validate
     * @param contactNumber The supplier's contact number to validate
     * @param password The proposed password to validate
     * @param confirmPassword The password confirmation to validate against the
     * original
     * @return An error message if any validation fails; {@code null} if all
     * validations pass
     */
    public static String validateRegistration(String fullName, String email,
            String address, String contactNumber,
            String password, String confirmPassword) {

        String error = getFullNameError(fullName);
        if (error != null) {
            return error;
        }

        error = getEmailError(email);
        if (error != null) {
            return error;
        }

        error = getAddressError(address);
        if (error != null) {
            return error;
        }

        error = getPhoneNumberError(contactNumber);
        if (error != null) {
            return error;
        }

        error = getPasswordError(password);
        if (error != null) {
            return error;
        }

        if (!passwordsMatch(password, confirmPassword)) {
            return "Passwords do not match.";
        }

        return null;
    }
}
