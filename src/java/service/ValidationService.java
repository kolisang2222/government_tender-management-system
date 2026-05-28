/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.impl.UserDAOImpl;
import dao.interfaces.UserDAO;
import util.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class responsible for validating supplier registration form data.
 * Provides methods to validate various fields such as full name, email,
 * address, contact number, password, and other registration-related inputs.
 */
public class ValidationService {

    private final UserDAO userDAO;

    /**
     * Constructs a new {@code ValidationService} with a default
     * {@link UserDAOImpl} instance.
     */
    public ValidationService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Validates all fields required for supplier registration. Performs
     * validation on full name, email, address, contact number, password, terms
     * agreement, and accuracy confirmation fields.
     *
     * @param formData a {@code Map} containing the registration form field
     * names as keys and their corresponding values
     * @return a {@code Map} containing field names as keys and error messages
     * as values; an empty map indicates successful validation with no errors
     */
    public Map<String, String> validateSupplierRegistration(Map<String, String> formData) {
        Map<String, String> errors = new HashMap<>();
        validateFullName(formData.get("fullName"), errors);
        validateEmail(formData.get("email"), errors);
        validateAddress(formData.get("address"), errors);
        validateContactNumber(formData.get("contactNumber"), errors);
        validatePassword(formData.get("password"), formData.get("confirmPassword"), errors);
        validateTermsAgreement(formData.get("agreeTerms"), errors);
        validateAccuracyConfirmation(formData.get("confirmAccuracy"), errors);

        return errors;
    }

    /**
     * Validates the full name field for length and character constraints. The
     * full name must be at least 3 characters, no more than 100 characters, and
     * contain only valid characters.
     *
     * @param fullName the full name string to validate
     * @param errors a {@code Map} to populate with error messages if validation
     * fails, keyed by the field name "fullName"
     */
    private void validateFullName(String fullName, Map<String, String> errors) {
        if (fullName == null || fullName.trim().length() < 3) {
            errors.put("fullName", "full name must be atleast 3 characters.");
        } else if (fullName.trim().length() > 100) {
            errors.put("fullName", "full name can not exceet 100 characters.");
        } else if (!fullName.matches("^[a-zA-Z0-9\\s\\-\\'\\.\\(\\)\\,&]+$")) {
            errors.put("fullName", "full name contains invalid characters.");
        }
    }

    /**
     * Validates the email address field for presence and format. Checks that
     * the email is not empty and matches a valid email pattern.
     *
     * @param email the email address string to validate
     * @param errors a {@code Map} to populate with error messages if validation
     * fails, keyed by the field name "email"
     */
    private void validateEmail(String email, Map<String, String> errors) {
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "email address is required.");
        } else if (!ValidationUtils.isValidEmail(email)) {
            errors.put("email", "please enter a valid email address.");
        }
    }

    /**
     * Validates the physical address field for minimum and maximum length
     * constraints. The address must be at least 10 characters and no more than
     * 500 characters.
     *
     * @param address the address string to validate
     * @param errors a {@code Map} to populate with error messages if validation
     * fails, keyed by the field name "address"
     */
    private void validateAddress(String address, Map<String, String> errors) {
        if (address == null || address.trim().length() < 10) {
            errors.put("address", "physical address must be atleast 10 characters.");
        } else if (address.trim().length() > 500) {
            errors.put("address", "address can not exceed 500 characters.");
        }
    }

    /**
     * Validates the contact number field for presence and format. Checks that
     * the contact number is not empty and matches a valid phone number pattern.
     *
     * @param contactNumber the contact number string to validate
     * @param errors a {@code Map} to populate with error messages if validation
     * fails, keyed by the field name "contactNumber"
     */
    private void validateContactNumber(String contactNumber, Map<String, String> errors) {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            errors.put("contactNumber", "contact number is requiered.");
        } else if (!ValidationUtils.isValidPhoneNumber(contactNumber)) {
            errors.put("contactNumber", "please enter a valid contact number.");
        }
    }

    /**
     * Validates the password field for strength and confirmation match. Checks
     * that the password is not empty, meets strength requirements, and matches
     * the confirmation password.
     *
     * @param password the password string to validate
     * @param confirmPassword the password confirmation string to compare
     * against
     * @param errors a {@code Map} to populate with error messages if validation
     * fails, keyed by the field names "password" and "confirmPassword"
     */
    private void validatePassword(String password, String confirmPassword, Map<String, String> errors) {
        if (password == null || password.isEmpty()) {
            errors.put("[assword", "password is required");
        } else if (!ValidationUtils.isStrongPassword(password)) {
            errors.put("password", "password must be 8+ characters with uppe case, lowercase, number, and special characters.");
        } else if (!password.equals(confirmPassword)) {
            errors.put("confirmPassword", "passwords do not match.");
        }
    }

    /**
     * Validates that the terms and conditions agreement has been accepted.
     * Checks that the agreeTerms field is not null, indicating the user has
     * agreed.
     *
     * @param agreeTerms the terms agreement value to validate, expected to be
     * non-null if accepted
     * @param errors a {@code Map} to populate with error messages if validation
     * fails, keyed by the field name "agreeTerms"
     */
    private void validateTermsAgreement(String agreeTerms, Map<String, String> errors) {
        if (agreeTerms == null) {
            errors.put("agreeTerms", "You must agree to the terms and conditions.");
        }
    }

    /**
     * Validates that the user has confirmed the accuracy of their information.
     * Checks that the confirmAccuracy field is not null, indicating
     * confirmation.
     *
     * @param confirmAccuracy the accuracy confirmation value to validate,
     * expected to be non-null if confirmed
     * @param errors a {@code Map} to populate with error messages if validation
     * fails, keyed by the field name "confirmAccuracy"
     */
    private void validateAccuracyConfirmation(String confirmAccuracy, Map<String, String> errors) {
        if (confirmAccuracy == null) {
            errors.put("confirmAccuracy", "you must confirm the accuracy of your information.");
        }
    }

    /**
     * Sanitizes user input by trimming whitespace and applying additional
     * sanitization through {@link ValidationUtils#sanitizeInput(String)}.
     *
     * @param input the input string to sanitize, may be {@code null}
     * @return the sanitized string, or {@code null} if the input was
     * {@code null}
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return ValidationUtils.sanitizeInput(input.trim());
    }
}
