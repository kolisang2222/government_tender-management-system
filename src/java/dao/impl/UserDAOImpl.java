package dao.impl;

import dao.DAOException;
import dao.interfaces.UserDAO;
import model.User;
import model.enums.UserRole;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of UserDAO interface using JDBC and JNDI DataSource. All
 * database operations for User entities.
 *
 * @author kolisang
 * @version 1.0
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAOImpl.class.getName());
    private DataSource dataSource;

    // Direct JDBC connection parameters (fallback)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kolisangphatela_2334120?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Set your MySQL password if any
    private boolean useDirectConnection = false;

    /**
     * Constructor - initializes JNDI DataSource with fallback to direct
     * connection.
     */
    public UserDAOImpl() {
        // Try JNDI first
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            this.dataSource = (DataSource) envContext.lookup("jdbc/ProcureGovDB");
            // Test the connection
            try (Connection conn = dataSource.getConnection()) {
                logger.info("UserDAOImpl initialized with JNDI DataSource - Connection successful!");
            }
        } catch (NamingException e) {
            logger.log(Level.WARNING, "JNDI DataSource not found, falling back to direct JDBC connection", e);
            useDirectConnection = true;
            // Test direct connection
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    logger.info("UserDAOImpl initialized with Direct JDBC Connection - Connection successful!");
                }
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "MySQL JDBC Driver not found", ex);
                throw new RuntimeException("MySQL JDBC Driver not found", ex);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Direct database connection failed", ex);
                throw new RuntimeException("Database connection pool initialization failed", ex);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "JNDI DataSource connection test failed", e);
            useDirectConnection = true;
            logger.info("Falling back to direct JDBC connection");
        }
    }

    /**
     * Gets a database connection. Uses JNDI DataSource if available, otherwise
     * direct JDBC.
     */
    private Connection getConnection() throws SQLException {
        if (!useDirectConnection && dataSource != null) {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "JNDI connection failed, falling back to direct", e);
                useDirectConnection = true;
            }
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    public int create(User user) throws DAOException {
        String sql = "INSERT INTO users (email, password_hash, full_name, address, "
                + "contact_number, role, registration_number, failed_attempts) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getEmail().toLowerCase());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getContactNumber());
            ps.setString(6, user.getRole().name());
            ps.setString(7, user.getRegistrationNumber());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    logger.info("User created successfully: " + user.getEmail() + " (ID: " + userId + ")");
                    return userId;
                } else {
                    throw new DAOException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error creating user: " + user.getEmail(), e);
            throw new DAOException("Failed to create user", e);
        }
    }

    @Override
    public User findById(int userId) throws DAOException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error finding user by ID: " + userId, e);
            throw new DAOException("Failed to find user by ID", e);
        }

        return null;
    }

    @Override
    public User findByEmail(String email) throws DAOException {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error finding user by email: " + email, e);
            throw new DAOException("Failed to find user by email", e);
        }

        return null;
    }

    @Override
    public User findByEmailAndPassword(String email, String passwordHash) throws DAOException {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?) AND password_hash = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);

                    // Check if account is locked
                    if (user.getLockedUntil() != null) {
                        Timestamp now = new Timestamp(System.currentTimeMillis());
                        if (user.getLockedUntil().after(now)) {
                            logger.warning("Login attempt on locked account: " + email);
                            return null;
                        } else {
                            // Lock has expired, unlock the account
                            unlockAccount(email);
                            user.setLockedUntil(null);
                            user.setFailedAttempts(0);
                        }
                    }

                    return user;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during authentication: " + email, e);
            throw new DAOException("Authentication failed", e);
        }

        return null;
    }

    @Override
    public List<User> findByRole(UserRole role) throws DAOException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY full_name";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error finding users by role: " + role, e);
            throw new DAOException("Failed to find users by role", e);
        }

        return users;
    }

    @Override
    public List<User> findAll() throws DAOException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error finding all users", e);
            throw new DAOException("Failed to retrieve users", e);
        }

        return users;
    }

    @Override
    public boolean update(User user) throws DAOException {
        String sql = "UPDATE users SET full_name = ?, address = ?, contact_number = ?, "
                + "email = ? WHERE user_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getAddress());
            ps.setString(3, user.getContactNumber());
            ps.setString(4, user.getEmail().toLowerCase());
            ps.setInt(5, user.getUserId());

            int affectedRows = ps.executeUpdate();

            logger.info("User updated: ID=" + user.getUserId());
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error updating user: " + user.getUserId(), e);
            throw new DAOException("Failed to update user", e);
        }
    }

    @Override
    public boolean delete(int userId) throws DAOException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            int affectedRows = ps.executeUpdate();

            logger.warning("User deleted: ID=" + userId);
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error deleting user: " + userId, e);
            throw new DAOException("Failed to delete user", e);
        }
    }

    @Override
    public void incrementFailedAttempts(String email) throws DAOException {
        String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 "
                + "WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.executeUpdate();

            logger.fine("Failed attempts incremented for: " + email);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error incrementing failed attempts: " + email, e);
            throw new DAOException("Failed to increment login attempts", e);
        }
    }

    @Override
    public void resetFailedAttempts(String email) throws DAOException {
        String sql = "UPDATE users SET failed_attempts = 0, locked_until = NULL "
                + "WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.executeUpdate();

            logger.fine("Failed attempts reset for: " + email);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error resetting failed attempts: " + email, e);
            throw new DAOException("Failed to reset login attempts", e);
        }
    }

    @Override
    public void lockAccount(String email, Timestamp lockedUntil) throws DAOException {
        String sql = "UPDATE users SET locked_until = ? WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, lockedUntil);
            ps.setString(2, email);
            ps.executeUpdate();

            logger.warning("Account locked: " + email + " until " + lockedUntil);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error locking account: " + email, e);
            throw new DAOException("Failed to lock account", e);
        }
    }

    @Override
    public void unlockAccount(String email) throws DAOException {
        String sql = "UPDATE users SET locked_until = NULL, failed_attempts = 0 "
                + "WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.executeUpdate();

            logger.info("Account unlocked: " + email);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error unlocking account: " + email, e);
            throw new DAOException("Failed to unlock account", e);
        }
    }

    @Override
    public String generateSupplierRegistrationNumber() throws DAOException {
        String prefix = "SUP-" + java.time.Year.now().getValue() + "-";
        String sql = "SELECT MAX(CAST(SUBSTRING_INDEX(registration_number, '-', -1) AS UNSIGNED)) "
                + "FROM users WHERE registration_number LIKE ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");

            try (ResultSet rs = ps.executeQuery()) {
                int nextNumber = 1;

                if (rs.next()) {
                    String maxNumber = rs.getString(1);
                    if (maxNumber != null) {
                        nextNumber = Integer.parseInt(maxNumber) + 1;
                    }
                }

                String registrationNumber = prefix + String.format("%04d", nextNumber);
                logger.fine("Generated registration number: " + registrationNumber);
                return registrationNumber;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error generating registration number", e);
            throw new DAOException("Failed to generate registration number", e);
        }
    }

    @Override
    public boolean emailExists(String email) throws DAOException {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error checking email existence: " + email, e);
            throw new DAOException("Failed to check email existence", e);
        }

        return false;
    }

    @Override
    public boolean updatePassword(int userId, String newPasswordHash) throws DAOException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);

            int affectedRows = ps.executeUpdate();

            logger.info("Password updated for user ID: " + userId);
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error updating password for user: " + userId, e);
            throw new DAOException("Failed to update password", e);
        }
    }

    @Override
    public boolean updatePassword(User user) throws DAOException {
        return updatePassword(user.getUserId(), user.getPasswordHash());
    }

    @Override
    public void updateLastLogin(int userId) throws DAOException {
        String sql = "UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

            logger.fine("Last login updated for user ID: " + userId);

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to update last login for user: " + userId, e);
            // Non-critical - don't throw exception
        }
    }

    @Override
    public int countByRole(UserRole role) throws DAOException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error counting users by role: " + role, e);
            throw new DAOException("Failed to count users by role", e);
        }

        return 0;
    }

    @Override
    public boolean isAccountLocked(String email) throws DAOException {
        String sql = "SELECT locked_until FROM users WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp lockedUntil = rs.getTimestamp("locked_until");
                    if (lockedUntil != null) {
                        return lockedUntil.after(new Timestamp(System.currentTimeMillis()));
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error checking account lock status: " + email, e);
            throw new DAOException("Failed to check account lock status", e);
        }

        return false;
    }

    @Override
    public int getFailedAttempts(String email) throws DAOException {
        String sql = "SELECT failed_attempts FROM users WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("failed_attempts");
                }
            }

            return 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting failed attempts for: " + email, e);
            throw new DAOException("Database error while getting failed attempts", e);
        }
    }

    @Override
    public boolean updateCompanyInfo(User user) throws DAOException {
        String sql = "UPDATE users SET full_name = ?, address = ?, contact_number = ?, "
                + "tax_number = ?, business_license = ?, years_in_business = ?, employee_count = ? "
                + "WHERE user_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getAddress());
            ps.setString(3, user.getContactNumber());
            ps.setString(4, user.getTaxNumber());
            ps.setString(5, user.getBusinessLicense());
            ps.setObject(6, user.getYearsInBusiness());
            ps.setObject(7, user.getEmployeeCount());
            ps.setInt(8, user.getUserId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error updating company info", e);
            throw new DAOException("Failed to update company information", e);
        }
    }

    @Override
    public boolean updateContactInfo(User user) throws DAOException {
        String sql = "UPDATE users SET contact_name = ?, contact_position = ?, "
                + "contact_email = ?, contact_phone = ?, alternative_phone = ? "
                + "WHERE user_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getContactName());
            ps.setString(2, user.getContactPosition());
            ps.setString(3, user.getContactEmail());
            ps.setString(4, user.getContactPhone());
            ps.setString(5, user.getAlternativePhone());
            ps.setInt(6, user.getUserId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error updating contact info", e);
            throw new DAOException("Failed to update contact information", e);
        }
    }

    @Override
    public boolean updateDocumentPath(int userId, String documentType, String filePath) throws DAOException {
        String column;
        switch (documentType.toLowerCase()) {
            case "tax":
                column = "tax_certificate_path";
                break;
            case "license":
                column = "license_path";
                break;
            case "profile":
                column = "profile_path";
                break;
            default:
                throw new DAOException("Invalid document type: " + documentType);
        }

        String sql = "UPDATE users SET " + column + " = ? WHERE user_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, filePath);
            ps.setInt(2, userId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error updating document path", e);
            throw new DAOException("Failed to update document path", e);
        }
    }

    @Override
    public User findByRegistrationNumber(String registrationNumber) throws DAOException {
        String sql = "SELECT * FROM users WHERE registration_number = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, registrationNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error finding user by registration number", e);
            throw new DAOException("Failed to find user by registration number", e);
        }

        return null;
    }

    @Override
    public List<User> searchSuppliers(String keyword) throws DAOException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'SUPPLIER' AND "
                + "(LOWER(full_name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) "
                + "OR registration_number LIKE ?) ORDER BY full_name";
        String searchPattern = "%" + keyword + "%";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error searching suppliers", e);
            throw new DAOException("Failed to search suppliers", e);
        }

        return users;
    }

    @Override
    public int countSuppliers() throws DAOException {
        return countByRole(UserRole.SUPPLIER);
    }

    @Override
    public List<User> getRecentSuppliers(int limit) throws DAOException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'SUPPLIER' ORDER BY created_at DESC LIMIT ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error getting recent suppliers", e);
            throw new DAOException("Failed to get recent suppliers", e);
        }

        return users;
    }

    @Override
    public boolean registrationNumberExists(String registrationNumber) throws DAOException {
        String sql = "SELECT COUNT(*) FROM users WHERE registration_number = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, registrationNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error checking registration number", e);
            throw new DAOException("Failed to check registration number", e);
        }

        return false;
    }

    @Override
    public List<User> getSuppliersWithIncompleteProfiles() throws DAOException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'SUPPLIER' AND "
                + "(tax_certificate_path IS NULL OR license_path IS NULL)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error getting suppliers with incomplete profiles", e);
            throw new DAOException("Failed to get suppliers with incomplete profiles", e);
        }

        return users;
    }

    /**
     * Maps a ResultSet row to a User object.
     *
     * @param rs The ResultSet positioned at the current row
     * @return Populated User object
     * @throws SQLException if column access fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setAddress(rs.getString("address"));
        user.setContactNumber(rs.getString("contact_number"));

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            try {
                user.setRole(UserRole.fromString(roleStr));
            } catch (IllegalArgumentException e) {
                logger.warning("Invalid role in database: " + roleStr);
                user.setRole(null);
            }
        }

        user.setRegistrationNumber(rs.getString("registration_number"));
        user.setFailedAttempts(rs.getInt("failed_attempts"));
        user.setLockedUntil(rs.getTimestamp("locked_until"));
        user.setCreatedAt(rs.getTimestamp("created_at"));

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt);
        }

        // Additional fields (may be null)
        try {
            user.setTaxNumber(rs.getString("tax_number"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setBusinessLicense(rs.getString("business_license"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setYearsInBusiness(rs.getInt("years_in_business"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setEmployeeCount(rs.getInt("employee_count"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setContactName(rs.getString("contact_name"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setContactPosition(rs.getString("contact_position"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setContactEmail(rs.getString("contact_email"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setContactPhone(rs.getString("contact_phone"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setAlternativePhone(rs.getString("alternative_phone"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setTaxCertificatePath(rs.getString("tax_certificate_path"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setLicensePath(rs.getString("license_path"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        try {
            user.setProfilePath(rs.getString("profile_path"));
        } catch (SQLException e) {
            /* Column may not exist */ }

        return user;
    }
}
