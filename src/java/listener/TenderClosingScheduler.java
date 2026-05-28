package listener;

import dao.DAOException;
import dao.impl.TenderDAOImpl;
import dao.interfaces.TenderDAO;
import model.Tender;
import model.enums.TenderStatus;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scheduler that automatically closes tenders when their closing date/time
 * passes. Runs every hour to check for expired tenders. Implements
 * {@link ServletContextListener} to start and stop with the web application
 * lifecycle.
 *
 * @author kolisang
 * @version 1.0
 */
@WebListener
public class TenderClosingScheduler implements ServletContextListener {

    /**
     * Logger for recording scheduler operations and errors
     */
    private static final Logger logger = Logger.getLogger(TenderClosingScheduler.class.getName());
    /**
     * Scheduled executor service for running periodic tender closing checks
     */
    private ScheduledExecutorService scheduler;
    /**
     * Data access object for tender operations
     */
    private TenderDAO tenderDAO;

    /**
     * Called when the web application is initialized. Initializes the TenderDAO
     * and starts the scheduled task to check for expired tenders every hour.
     *
     * @param sce the ServletContextEvent containing the servlet context
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing Tender Closing Scheduler...");

        try {
            tenderDAO = new TenderDAOImpl();
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize TenderDAO for scheduler", e);
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Run every hour to check for tenders that need closing
        scheduler.scheduleAtFixedRate(() -> {
            closeExpiredTenders();
        }, 1, 1, TimeUnit.HOURS);

        logger.info("Tender Closing Scheduler started. Will check for expired tenders every hour.");
    }

    /**
     * Called when the web application is being shut down. Gracefully shuts down
     * the scheduled executor service, waiting up to 5 seconds for running tasks
     * to complete before forcing shutdown.
     *
     * @param sce the ServletContextEvent containing the servlet context
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Shutting down Tender Closing Scheduler...");

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        logger.info("Tender Closing Scheduler stopped.");
    }

    /**
     * Checks for and closes all tenders that have passed their closing date.
     * Queries all tenders in OPEN status and transitions any whose closing
     * date/time is before the current time to CLOSED status. Logs the number of
     * tenders that were auto-closed.
     */
    private void closeExpiredTenders() {
        try {
            logger.fine("Checking for expired tenders...");

            // Get all OPEN tenders
            List<Tender> openTenders = tenderDAO.findByStatus(TenderStatus.OPEN);
            LocalDateTime now = LocalDateTime.now();

            int closedCount = 0;

            for (Tender tender : openTenders) {
                if (tender.getClosingDateTime() != null && tender.getClosingDateTime().isBefore(now)) {
                    boolean closed = tenderDAO.updateStatus(tender.getTenderId(), TenderStatus.CLOSED);

                    if (closed) {
                        closedCount++;
                        logger.info("Auto-closed tender: " + tender.getReferenceNumber()
                                + " (Closing date: " + tender.getClosingDateTime() + ")");
                    }
                }
            }

            if (closedCount > 0) {
                logger.info("Auto-closed " + closedCount + " expired tender(s)");
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error checking for expired tenders", e);
        }
    }
}
