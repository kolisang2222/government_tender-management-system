package service;

import dao.DAOException;
import dao.impl.UserDAOImpl;
import dao.interfaces.UserDAO;
import model.User;
import model.enums.UserRole;
import util.PasswordHasher;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Service class handling supplier registration business logic. Separates
 * business logic from controller layer as required by exam.
 *
 * @author YourName
 * @version 1.0
 */
public class SupplierRegistrationService {

    /**
     * Logger for recording registration operations and events
     */
    private static final Logger logger = Logger.getLogger(SupplierRegistrationService.class.getName());

    /**
     * Data access object for user-related database operations
     */
    private final UserDAO userDAO;

    /**
     * Constructs a new {@code SupplierRegistrationService} with a default
     * {@link UserDAOImpl} instance.
     */
    public SupplierRegistrationService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Registers a new supplier with the provided form data. Handles password
     * hashing and registration number generation. Converts the email to
     * lowercase for case-insensitive uniqueness checks.
     *
     * @param formData Map of sanitized form data containing keys: "email",
     * "fullName", "address", "contactNumber", and "password"
     * @return The newly created {@link User} object with generated ID and
     * registration number
     * @throws DAOException if database operation fails or if the email address
     * already exists
     */
    public User registerNewSupplier(Map<String, String> formData) throws DAOException {

        String email = formData.get("email").toLowerCase();

        if (userDAO.emailExists(email)) {
            throw new DAOException("Email address already registered: " + email);
        }

        User supplier = new User();
        supplier.setFullName(formData.get("fullName"));
        supplier.setEmail(email);
        supplier.setAddress(formData.get("address"));
        supplier.setContactNumber(formData.get("contactNumber"));
        supplier.setRole(UserRole.SUPPLIER);

        String hashedPassword = PasswordHasher.hash(formData.get("password"));
        supplier.setPasswordHash(hashedPassword);

        String registrationNumber = userDAO.generateSupplierRegistrationNumber();
        supplier.setRegistrationNumber(registrationNumber);

        int userId = userDAO.create(supplier);
        supplier.setUserId(userId);

        logger.info("Created new supplier: " + email + " with registration number: " + registrationNumber);

        return supplier;
    }
}
