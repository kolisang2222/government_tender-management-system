package listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database initializer that runs when the web application starts. Creates the
 * application database if it does not already exist. Implements
 * {@link ServletContextListener} to hook into the application lifecycle.
 *
 * @author kolisang
 */
@WebListener
public class DatabaseInitializerListener implements ServletContextListener {

    /**
     * Logger for recording database initialization operations
     */
    private static final Logger logger = Logger.getLogger(DatabaseInitializerListener.class.getName());

    /**
     * Database host address
     */
    private static final String DB_HOST = "localhost";
    /**
     * Database port number
     */
    private static final String DB_PORT = "3306";
    /**
     * Name of the application database
     */
    private static final String DB_NAME = "kolisangphatela_2334120";
    /**
     * Database username for authentication
     */
    private static final String DB_USER = "root";
    /**
     * Database password for authentication
     */
    private static final String DB_PASSWORD = "";

    /**
     * Called when the web application is initialized. Loads the MySQL JDBC
     * driver and creates the application database if it does not exist.
     *
     * @param sce the ServletContextEvent containing the servlet context
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("========================================");
        logger.info("Starting ProcureGov Database Initializer");
        logger.info("========================================");

        try {
            // Step 1: Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL JDBC Driver loaded successfully");

            // Step 2: Create database if it doesn't exist
            createDatabaseIfNotExists();

            logger.info("========================================");
            logger.info("Database is ready for JNDI DataSource!");
            logger.info("========================================");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Database initialization failed", e);
        }
    }

    /**
     * Called when the web application is being shut down. Logs the shutdown
     * event for auditing purposes.
     *
     * @param sce the ServletContextEvent containing the servlet context
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down");
    }

    /**
     * Creates the application database if it does not already exist. Connects
     * to the MySQL server without specifying a database and executes a CREATE
     * DATABASE IF NOT EXISTS statement with UTF-8 character set configuration.
     *
     * @throws RuntimeException if the database cannot be created
     */
    private void createDatabaseIfNotExists() {
        String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/?useSSL=false&serverTimezone=UTC";

        try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD); Statement stmt = conn.createStatement()) {

            String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME
                    + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(sql);

            logger.info("Database '" + DB_NAME + "' created or already exists");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create database", e);
            throw new RuntimeException("Cannot create database", e);
        }
    }
}
