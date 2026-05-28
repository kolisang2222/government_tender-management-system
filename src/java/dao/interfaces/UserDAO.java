package dao.interfaces;

import dao.DAOException;
import model.User;
import model.enums.UserRole;
import java.util.List;

/**
 * Data Access Object interface for User entities. Defines all database
 * operations related to user management.
 *
 * @author kolisang
 * @version 1.0
 */
public interface UserDAO {

    /**
     * Creates a new user in the database.
     *
     * @param user User object with all required fields
     * @return Generated user ID
     * @throws DAOException if database error occurs
     */
    int create(User user) throws DAOException;

    /**
     * Finds a user by their database ID.
     *
     * @param userId The user ID to search for
     * @return User object if found, null otherwise
     * @throws DAOException if database error occurs
     */
    User findById(int userId) throws DAOException;

    /**
     * Finds a user by email address.
     *
     * @param email The email to search for
     * @return User object if found, null otherwise
     * @throws DAOException if database error occurs
     */
    User findByEmail(String email) throws DAOException;

    /**
     * Authenticates a user with email and password hash.
     *
     * @param email The user's email
     * @param passwordHash The SHA-256 hashed password
     * @return User object if credentials match, null otherwise
     * @throws DAOException if database error occurs
     */
    User findByEmailAndPassword(String email, String passwordHash) throws DAOException;

    /**
     * Retrieves all users with a specific role.
     *
     * @param role The role to filter by
     * @return List of users with the specified role
     * @throws DAOException if database error occurs
     */
    List<User> findByRole(UserRole role) throws DAOException;

    /**
     * Retrieves all users in the system.
     *
     * @return List of all users
     * @throws DAOException if database error occurs
     */
    List<User> findAll() throws DAOException;

    /**
     * Updates user information.
     *
     * @param user User object with updated fields
     * @return true if update was successful
     * @throws DAOException if database error occurs
     */
    boolean update(User user) throws DAOException;

    /**
     * Deletes a user by ID.
     *
     * @param userId The user ID to delete
     * @return true if deletion was successful
     * @throws DAOException if database error occurs
     */
    boolean delete(int userId) throws DAOException;

    /**
     * Increments the failed login attempt counter.
     *
     * @param email The user's email
     * @throws DAOException if database error occurs
     */
    void incrementFailedAttempts(String email) throws DAOException;

    /**
     * Resets failed login attempts counter to zero.
     *
     * @param email The user's email
     * @throws DAOException if database error occurs
     */
    void resetFailedAttempts(String email) throws DAOException;

    /**
     * Locks a user account until a specified timestamp.
     *
     * @param email The user's email
     * @param lockedUntil Timestamp when lock expires
     * @throws DAOException if database error occurs
     */
    void lockAccount(String email, java.sql.Timestamp lockedUntil) throws DAOException;

    /**
     * Unlocks a user account.
     *
     * @param email The user's email
     * @throws DAOException if database error occurs
     */
    void unlockAccount(String email) throws DAOException;

    /**
     * Generates a unique registration number for a new supplier. Format:
     * SUP-YYYY-XXXX (e.g., SUP-2026-0001)
     *
     * @return Unique registration number
     * @throws DAOException if database error occurs
     */
    String generateSupplierRegistrationNumber() throws DAOException;

    /**
     * Checks if an email is already registered.
     *
     * @param email The email to check
     * @return true if email exists
     * @throws DAOException if database error occurs
     */
    boolean emailExists(String email) throws DAOException;

    /**
     * Updates the user's password.
     *
     * @param userId The user ID
     * @param newPasswordHash The new SHA-256 hashed password
     * @return true if update was successful
     * @throws DAOException if database error occurs
     */
    boolean updatePassword(int userId, String newPasswordHash) throws DAOException;

    /**
     * Counts users by role.
     *
     * @param role The role to count
     * @return Number of users with the specified role
     * @throws DAOException if database error occurs
     */
    int countByRole(UserRole role) throws DAOException;

    /**
     * Checks if a user account is currently locked.
     *
     * @param email The user's email
     * @return true if account is locked
     * @throws DAOException if database error occurs
     */
    boolean isAccountLocked(String email) throws DAOException;

    /**
     * Gets the number of failed login attempts for a user.
     *
     * @param email The user's email
     * @return Number of failed attempts
     * @throws DAOException if database error occurs
     */
    int getFailedAttempts(String email) throws DAOException;

    // ============================================================
    // SUPPLIER PROFILE METHODS
    // ============================================================
    /**
     * Updates the user's password using the User object.
     *
     * @param user User object with new password hash
     * @return true if update was successful
     * @throws DAOException if database error occurs
     */
    boolean updatePassword(User user) throws DAOException;

    /**
     * Updates the last login timestamp for a user.
     *
     * @param userId The user ID
     * @throws DAOException if database error occurs
     */
    void updateLastLogin(int userId) throws DAOException;

    /**
     * Updates supplier company information.
     *
     * @param user User object with updated company fields
     * @return true if update was successful
     * @throws DAOException if database error occurs
     */
    boolean updateCompanyInfo(User user) throws DAOException;

    /**
     * Updates supplier contact person information.
     *
     * @param user User object with updated contact fields
     * @return true if update was successful
     * @throws DAOException if database error occurs
     */
    boolean updateContactInfo(User user) throws DAOException;

    /**
     * Updates supplier document paths.
     *
     * @param userId The user ID
     * @param documentType The type of document (tax, license, profile)
     * @param filePath The server file path
     * @return true if update was successful
     * @throws DAOException if database error occurs
     */
    boolean updateDocumentPath(int userId, String documentType, String filePath) throws DAOException;

    /**
     * Finds a user by registration number.
     *
     * @param registrationNumber The registration number to search for
     * @return User object if found, null otherwise
     * @throws DAOException if database error occurs
     */
    User findByRegistrationNumber(String registrationNumber) throws DAOException;

    /**
     * Searches suppliers by keyword (name, email, or registration number).
     *
     * @param keyword The search keyword
     * @return List of matching supplier users
     * @throws DAOException if database error occurs
     */
    List<User> searchSuppliers(String keyword) throws DAOException;

    /**
     * Gets the total count of registered suppliers.
     *
     * @return Number of supplier users
     * @throws DAOException if database error occurs
     */
    int countSuppliers() throws DAOException;

    /**
     * Gets recently registered suppliers.
     *
     * @param limit Maximum number of results
     * @return List of recently registered suppliers
     * @throws DAOException if database error occurs
     */
    List<User> getRecentSuppliers(int limit) throws DAOException;

    /**
     * Checks if a registration number already exists.
     *
     * @param registrationNumber The registration number to check
     * @return true if registration number exists
     * @throws DAOException if database error occurs
     */
    boolean registrationNumberExists(String registrationNumber) throws DAOException;

    /**
     * Gets suppliers with incomplete profiles (missing required documents).
     *
     * @return List of suppliers with incomplete profiles
     * @throws DAOException if database error occurs
     */
    List<User> getSuppliersWithIncompleteProfiles() throws DAOException;
}
