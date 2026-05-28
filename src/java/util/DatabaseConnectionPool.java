package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for managing database connections via a JNDI DataSource.
 * <p>
 * This class provides a centralized mechanism for obtaining database
 * connections from a connection pool configured in the application server's
 * context. It initializes the DataSource statically upon class loading and
 * exposes methods for retrieving connections, accessing the DataSource
 * directly, and testing database connectivity.
 * </p>
 * <p>
 * The JNDI resource name used is {@code jdbc/kolisangphatela_2334120}, which
 * must be defined in the {@code context.xml} or server configuration.
 * </p>
 *
 * @author Kolisang Phatela
 * @version 1.0
 * @see javax.sql.DataSource
 * @see javax.naming.InitialContext
 */
public class DatabaseConnectionPool {

    /**
     * Logger instance for recording database pool events and errors.
     */
    private static final Logger logger = Logger.getLogger(DatabaseConnectionPool.class.getName());

    /**
     * The JNDI DataSource instance providing pooled database connections.
     * Initialized statically when the class is loaded.
     */
    private static DataSource dataSource;

    static {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/kolisangphatela_2334120");
            logger.info("Database connection pool initialized successfully");
        } catch (NamingException e) {
            logger.log(Level.SEVERE, "Failed to initialize database connection pool", e);
            throw new RuntimeException("Database connection pool initialization failed", e);
        }
    }

    /**
     * Obtains a connection from the JNDI connection pool.
     * <p>
     * This method delegates to the configured DataSource's
     * {@code getConnection()} method. Callers are responsible for closing the
     * returned connection in a finally block or using try-with-resources to
     * return it to the pool.
     * </p>
     *
     * @return A database connection from the pool
     * @throws SQLException If a database access error occurs or the pool cannot
     * provide a connection
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Retrieves the statically initialized DataSource instance.
     * <p>
     * This method provides direct access to the DataSource for advanced use
     * cases where methods beyond {@code getConnection()} are required.
     * </p>
     *
     * @return The configured JNDI DataSource instance
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Tests whether a valid database connection can be established through the
     * pool.
     * <p>
     * This method attempts to obtain a connection and validates it using the
     * JDBC driver's {@code isValid()} method with a 5-second timeout. The
     * connection is automatically closed via try-with-resources.
     * </p>
     *
     * @return {@code true} if a connection was successfully obtained and
     * validated; {@code false} otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(5);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Database connection test failed", e);
            return false;
        }
    }
}
