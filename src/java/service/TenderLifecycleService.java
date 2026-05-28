package service;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.EvaluationDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.EvaluationDAO;
import dao.interfaces.TenderDAO;
import model.Tender;
import model.enums.TenderStatus;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing tender lifecycle and status transitions. Enforces
 * business rules for moving tenders through their lifecycle states.
 *
 * @author YourName
 * @version 1.0
 */
public class TenderLifecycleService {

    /**
     * Logger for recording service operations and errors
     */
    private static final Logger logger = Logger.getLogger(TenderLifecycleService.class.getName());

    /**
     * Data access object for tender operations
     */
    private TenderDAO tenderDAO;
    /**
     * Data access object for bid operations
     */
    private BidDAO bidDAO;
    /**
     * Data access object for evaluation operations
     */
    private EvaluationDAO evaluationDAO;

    /**
     * Constructor - initializes DAO dependencies with default implementations.
     * Creates new instances of {@link TenderDAOImpl}, {@link BidDAOImpl}, and
     * {@link EvaluationDAOImpl}.
     *
     * @throws RuntimeException if DAO initialization fails due to a
     * {@link DAOException}
     */
    public TenderLifecycleService() {
        try {
            this.tenderDAO = new TenderDAOImpl();
            this.bidDAO = new BidDAOImpl();
            this.evaluationDAO = new EvaluationDAOImpl();
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize TenderLifecycleService", e);
            throw new RuntimeException("Service initialization failed", e);
        }
    }

    /**
     * Constructor with dependency injection for testing and flexibility. Allows
     * injection of mock or custom DAO implementations.
     *
     * @param tenderDAO the tender data access object to use
     * @param bidDAO the bid data access object to use
     * @param evaluationDAO the evaluation data access object to use
     */
    public TenderLifecycleService(TenderDAO tenderDAO, BidDAO bidDAO, EvaluationDAO evaluationDAO) {
        this.tenderDAO = tenderDAO;
        this.bidDAO = bidDAO;
        this.evaluationDAO = evaluationDAO;
    }

    // ============================================================
    // STATUS TRANSITION METHODS
    // ============================================================
    /**
     * Publishes a tender from DRAFT to OPEN status. Validates that the tender
     * is in DRAFT status, has all required fields, and has a closing date in
     * the future before publishing.
     *
     * @param tenderId The tender ID
     * @param officerId The procurement officer performing the action
     * @return TransitionResult containing success status and message
     */
    public TransitionResult publishTender(int tenderId, int officerId) {
        logger.info("Publishing tender: ID=" + tenderId + ", Officer=" + officerId);

        try {
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                return TransitionResult.failure("Tender not found.");
            }

            // Validate current status
            if (tender.getStatus() != TenderStatus.DRAFT) {
                return TransitionResult.failure(
                        "Only draft tenders can be published. Current status: " + tender.getStatus().getDisplayName()
                );
            }

            // Validate required fields
            ValidationResult validation = validateTenderForPublishing(tender);
            if (!validation.isValid()) {
                return TransitionResult.failure(validation.getMessage());
            }

            // Validate closing date is in the future
            if (tender.getClosingDateTime().isBefore(LocalDateTime.now())) {
                return TransitionResult.failure("Closing date must be in the future.");
            }

            // Update status
            boolean success = tenderDAO.updateStatus(tenderId, TenderStatus.OPEN);

            if (success) {
                logger.info("Tender published successfully: " + tender.getReferenceNumber());
                return TransitionResult.success(
                        "Tender " + tender.getReferenceNumber() + " published successfully.",
                        tender.getReferenceNumber()
                );
            } else {
                return TransitionResult.failure("Failed to update tender status.");
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error publishing tender: " + tenderId, e);
            return TransitionResult.failure("Database error: " + e.getMessage());
        }
    }

    /**
     * Closes a tender (automatic transition from OPEN to CLOSED). This should
     * be called by a scheduled job or when checking tender status. Only tenders
     * that have passed their closing date can be closed.
     *
     * @param tenderId The tender ID
     * @return TransitionResult containing success status and message
     */
    public TransitionResult closeTender(int tenderId) {
        logger.info("Closing tender: ID=" + tenderId);

        try {
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                return TransitionResult.failure("Tender not found.");
            }

            // Validate current status
            if (tender.getStatus() != TenderStatus.OPEN) {
                return TransitionResult.failure(
                        "Only open tenders can be closed. Current status: " + tender.getStatus().getDisplayName()
                );
            }

            // Validate closing date has passed
            if (tender.getClosingDateTime().isAfter(LocalDateTime.now())) {
                return TransitionResult.failure(
                        "Cannot close tender before its closing date: " + tender.getClosingDateTime()
                );
            }

            // Update status
            boolean success = tenderDAO.updateStatus(tenderId, TenderStatus.CLOSED);

            if (success) {
                logger.info("Tender closed automatically: " + tender.getReferenceNumber());
                return TransitionResult.success(
                        "Tender " + tender.getReferenceNumber() + " closed successfully.",
                        tender.getReferenceNumber()
                );
            } else {
                return TransitionResult.failure("Failed to update tender status.");
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error closing tender: " + tenderId, e);
            return TransitionResult.failure("Database error: " + e.getMessage());
        }
    }

    /**
     * Starts the evaluation process (CLOSED to UNDER_EVALUATION). Validates
     * that the tender is in CLOSED status before starting evaluation.
     *
     * @param tenderId The tender ID
     * @param officerId The procurement officer performing the action
     * @return TransitionResult containing success status and message
     */
    public TransitionResult startEvaluation(int tenderId, int officerId) {
        logger.info("Starting evaluation for tender: ID=" + tenderId + ", Officer=" + officerId);

        try {
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                return TransitionResult.failure("Tender not found.");
            }

            // Validate current status
            if (tender.getStatus() != TenderStatus.CLOSED) {
                return TransitionResult.failure(
                        "Only closed tenders can move to evaluation. Current status: " + tender.getStatus().getDisplayName()
                );
            }

            /* // Check if there are bids to evaluate
            int bidCount = bidDAO.countBidsByTenderId(tenderId);
            if (bidCount == 0) {
                return TransitionResult.failure(
                    "Cannot start evaluation: No bids have been submitted for this tender."
                );
            }*/
            // Update status
            boolean success = tenderDAO.updateStatus(tenderId, TenderStatus.UNDER_EVALUATION);

            if (success) {
                logger.info("Evaluation started for tender: " + tender.getReferenceNumber());
                return TransitionResult.success(
                        "Evaluation started for tender " + tender.getReferenceNumber() + ".",
                        tender.getReferenceNumber()
                );
            } else {
                return TransitionResult.failure("Failed to update tender status.");
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error starting evaluation: " + tenderId, e);
            return TransitionResult.failure("Database error: " + e.getMessage());
        }
    }

    /**
     * Completes evaluation (UNDER_EVALUATION to EVALUATED). This is triggered
     * automatically when all evaluators have submitted scores. Verifies that
     * evaluation is actually complete before transitioning.
     *
     * @param tenderId The tender ID
     * @return TransitionResult containing success status and message
     */
    public TransitionResult completeEvaluation(int tenderId) {
        logger.info("Completing evaluation for tender: ID=" + tenderId);

        try {
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                return TransitionResult.failure("Tender not found.");
            }

            // Validate current status
            if (tender.getStatus() != TenderStatus.UNDER_EVALUATION) {
                return TransitionResult.failure(
                        "Only tenders under evaluation can be completed. Current status: " + tender.getStatus().getDisplayName()
                );
            }

            // Verify evaluation is actually complete
            boolean evaluationComplete = evaluationDAO.isEvaluationComplete(tenderId);
            if (!evaluationComplete) {
                return TransitionResult.failure(
                        "Cannot complete evaluation: Not all evaluators have submitted their scores."
                );
            }

            // Update status
            boolean success = tenderDAO.updateStatus(tenderId, TenderStatus.EVALUATED);

            if (success) {
                logger.info("Evaluation completed for tender: " + tender.getReferenceNumber());
                return TransitionResult.success(
                        "Evaluation completed for tender " + tender.getReferenceNumber() + ".",
                        tender.getReferenceNumber()
                );
            } else {
                return TransitionResult.failure("Failed to update tender status.");
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error completing evaluation: " + tenderId, e);
            return TransitionResult.failure("Database error: " + e.getMessage());
        }
    }

    /**
     * Awards the tender (EVALUATED to AWARDED). Requires a winning bid ID,
     * award value, and justification of at least 20 characters. The award value
     * must be greater than zero.
     *
     * @param tenderId The tender ID
     * @param winningBidId The ID of the winning bid
     * @param awardValue The awarded contract value, must be greater than zero
     * @param justification The award justification, must be at least 20
     * characters
     * @param officerId The procurement officer performing the action
     * @return TransitionResult containing success status and message
     */
    public TransitionResult awardTender(int tenderId, int winningBidId,
            java.math.BigDecimal awardValue,
            String justification, int officerId) {
        logger.info("Awarding tender: ID=" + tenderId + ", Winning Bid=" + winningBidId + ", Officer=" + officerId);

        try {
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                return TransitionResult.failure("Tender not found.");
            }

            // Validate current status
            if (tender.getStatus() != TenderStatus.EVALUATED) {
                return TransitionResult.failure(
                        "Only evaluated tenders can be awarded. Current status: " + tender.getStatus().getDisplayName()
                );
            }

            // Validate justification
            if (justification == null || justification.trim().length() < 20) {
                return TransitionResult.failure(
                        "Award justification must be at least 20 characters."
                );
            }

            // Validate award value
            if (awardValue == null || awardValue.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return TransitionResult.failure(
                        "Award value must be greater than zero."
                );
            }

            // Award the tender
            boolean success = tenderDAO.awardTender(tenderId, winningBidId, awardValue, justification);

            if (success) {
                logger.info("Tender awarded successfully: " + tender.getReferenceNumber());
                return TransitionResult.success(
                        "Tender " + tender.getReferenceNumber() + " awarded successfully.",
                        tender.getReferenceNumber()
                );
            } else {
                return TransitionResult.failure("Failed to award tender.");
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error awarding tender: " + tenderId, e);
            return TransitionResult.failure("Database error: " + e.getMessage());
        }
    }

    /**
     * Cancels a tender (can only be done from DRAFT or OPEN status). Requires a
     * cancellation reason of at least 10 characters. Logs the cancellation for
     * audit purposes.
     *
     * @param tenderId The tender ID
     * @param reason The cancellation reason, must be at least 10 characters
     * @param officerId The procurement officer performing the action
     * @return TransitionResult containing success status and message
     */
    public TransitionResult cancelTender(int tenderId, String reason, int officerId) {
        logger.info("Cancelling tender: ID=" + tenderId + ", Officer=" + officerId + ", Reason=" + reason);

        try {
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                return TransitionResult.failure("Tender not found.");
            }

            // Validate current status
            if (tender.getStatus() != TenderStatus.DRAFT && tender.getStatus() != TenderStatus.OPEN) {
                return TransitionResult.failure(
                        "Only draft or open tenders can be cancelled. Current status: " + tender.getStatus().getDisplayName()
                );
            }

            // Validate reason
            if (reason == null || reason.trim().length() < 10) {
                return TransitionResult.failure(
                        "Cancellation reason must be at least 10 characters."
                );
            }

            // Log cancellation (could be stored in a separate table)
            logger.warning("Tender cancelled: " + tender.getReferenceNumber() + " - Reason: " + reason);

            // For now, we'll just return success (you may want to add a CANCELLED status)
            return TransitionResult.success(
                    "Tender " + tender.getReferenceNumber() + " cancelled successfully.",
                    tender.getReferenceNumber()
            );

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error cancelling tender: " + tenderId, e);
            return TransitionResult.failure("Database error: " + e.getMessage());
        }
    }

    // ============================================================
    // VALIDATION METHODS
    // ============================================================
    /**
     * Validates that a tender has all required fields for publishing. Checks
     * title length, category presence, description length, estimated value,
     * closing date, and tender notice document.
     *
     * @param tender the {@link Tender} object to validate
     * @return {@link ValidationResult} indicating whether the tender is valid
     * for publishing
     */
    private ValidationResult validateTenderForPublishing(Tender tender) {
        if (tender.getTitle() == null || tender.getTitle().trim().length() < 10) {
            return ValidationResult.invalid("Tender title must be at least 10 characters.");
        }

        if (tender.getCategory() == null) {
            return ValidationResult.invalid("Tender category is required.");
        }

        if (tender.getDescription() == null || tender.getDescription().trim().length() < 50) {
            return ValidationResult.invalid("Tender description must be at least 50 characters.");
        }

        if (tender.getEstimatedValue() == null || tender.getEstimatedValue().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return ValidationResult.invalid("Estimated value must be greater than zero.");
        }

        if (tender.getClosingDateTime() == null) {
            return ValidationResult.invalid("Closing date and time are required.");
        }

        if (tender.getTenderNoticePath() == null || tender.getTenderNoticePath().trim().isEmpty()) {
            return ValidationResult.invalid("Tender notice document is required.");
        }

        return ValidationResult.valid();
    }

    // ============================================================
    // STATUS CHECKING METHODS
    // ============================================================
    /**
     * Checks if a tender has passed its closing date and should be closed. Only
     * checks tenders that are currently in OPEN status.
     *
     * @param tenderId The tender ID
     * @return true if tender should be closed, false otherwise
     */
    public boolean shouldCloseTender(int tenderId) {
        try {
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null || tender.getStatus() != TenderStatus.OPEN) {
                return false;
            }

            return tender.getClosingDateTime().isBefore(LocalDateTime.now());

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error checking if tender should close: " + tenderId, e);
            return false;
        }
    }

    /**
     * Checks if a tender's evaluation is complete and should move to EVALUATED
     * status. Only checks tenders that are currently in UNDER_EVALUATION
     * status.
     *
     * @param tenderId The tender ID
     * @return true if evaluation is complete, false otherwise
     */
    public boolean shouldCompleteEvaluation(int tenderId) {
        try {
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null || tender.getStatus() != TenderStatus.UNDER_EVALUATION) {
                return false;
            }

            return evaluationDAO.isEvaluationComplete(tenderId);

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error checking evaluation completion: " + tenderId, e);
            return false;
        }
    }

    /**
     * Gets the current allowed transitions for a tender based on its current
     * status. Returns the list of statuses the tender can legally transition
     * to.
     *
     * @param tenderId The tender ID
     * @return List of allowed next statuses; empty list if tender not found or
     * in terminal state
     */
    public java.util.List<TenderStatus> getAllowedTransitions(int tenderId) {
        java.util.List<TenderStatus> allowed = new java.util.ArrayList<>();

        try {
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                return allowed;
            }

            switch (tender.getStatus()) {
                case DRAFT:
                    allowed.add(TenderStatus.OPEN);
                    break;
                case OPEN:
                    allowed.add(TenderStatus.CLOSED);
                    break;
                case CLOSED:
                    allowed.add(TenderStatus.UNDER_EVALUATION);
                    break;
                case UNDER_EVALUATION:
                    allowed.add(TenderStatus.EVALUATED);
                    break;
                case EVALUATED:
                    allowed.add(TenderStatus.AWARDED);
                    break;
                case AWARDED:
                    // Terminal state - no transitions
                    break;
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error getting allowed transitions: " + tenderId, e);
        }

        return allowed;
    }

    /**
     * Processes all tenders that need automatic status updates. Checks all open
     * tenders for closing date expiry and all tenders under evaluation for
     * evaluation completion. This should be called by a scheduled job.
     *
     * @return Summary of processed tenders containing counts of closed,
     * evaluated, and failed transitions
     */
    public ProcessSummary processAutomaticTransitions() {
        ProcessSummary summary = new ProcessSummary();

        try {
            java.util.List<Tender> openTenders = tenderDAO.findByStatus(TenderStatus.OPEN);

            for (Tender tender : openTenders) {
                if (tender.getClosingDateTime().isBefore(LocalDateTime.now())) {
                    TransitionResult result = closeTender(tender.getTenderId());
                    if (result.isSuccess()) {
                        summary.addClosed();
                    } else {
                        summary.addFailed(tender.getTenderId(), result.getMessage());
                    }
                }
            }

            java.util.List<Tender> evaluatingTenders = tenderDAO.findByStatus(TenderStatus.UNDER_EVALUATION);

            for (Tender tender : evaluatingTenders) {
                if (evaluationDAO.isEvaluationComplete(tender.getTenderId())) {
                    TransitionResult result = completeEvaluation(tender.getTenderId());
                    if (result.isSuccess()) {
                        summary.addEvaluated();
                    } else {
                        summary.addFailed(tender.getTenderId(), result.getMessage());
                    }
                }
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error processing automatic transitions", e);
        }

        return summary;
    }

    // ============================================================
    // INNER CLASSES
    // ============================================================
    /**
     * Result object for status transition operations. Encapsulates the success
     * status, message, and reference number of a transition attempt.
     */
    public static class TransitionResult {

        /**
         * Whether the transition was successful
         */
        private final boolean success;
        /**
         * Message describing the result of the transition
         */
        private final String message;
        /**
         * Reference number of the tender involved in the transition
         */
        private final String referenceNumber;

        /**
         * Private constructor to enforce use of static factory methods.
         *
         * @param success whether the transition was successful
         * @param message descriptive message about the result
         * @param referenceNumber the reference number of the tender
         */
        private TransitionResult(boolean success, String message, String referenceNumber) {
            this.success = success;
            this.message = message;
            this.referenceNumber = referenceNumber;
        }

        /**
         * Creates a successful transition result.
         *
         * @param message success message describing the transition
         * @param referenceNumber the reference number of the tender that was
         * transitioned
         * @return a {@code TransitionResult} indicating success
         */
        public static TransitionResult success(String message, String referenceNumber) {
            return new TransitionResult(true, message, referenceNumber);
        }

        /**
         * Creates a failed transition result.
         *
         * @param message failure message describing why the transition failed
         * @return a {@code TransitionResult} indicating failure
         */
        public static TransitionResult failure(String message) {
            return new TransitionResult(false, message, null);
        }

        /**
         * Returns whether the transition was successful.
         *
         * @return {@code true} if the transition succeeded, {@code false}
         * otherwise
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Returns the message describing the transition result.
         *
         * @return the result message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Returns the reference number of the tender involved.
         *
         * @return the tender reference number, or {@code null} if the
         * transition failed
         */
        public String getReferenceNumber() {
            return referenceNumber;
        }
    }

    /**
     * Result object for validation operations. Encapsulates whether validation
     * passed and an associated message.
     */
    private static class ValidationResult {

        /**
         * Whether the validation passed
         */
        private final boolean valid;
        /**
         * Message describing the validation result or error
         */
        private final String message;

        /**
         * Private constructor to enforce use of static factory methods.
         *
         * @param valid whether the validation passed
         * @param message descriptive message about the validation result
         */
        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        /**
         * Creates a successful validation result.
         *
         * @return a {@code ValidationResult} indicating validation passed
         */
        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        /**
         * Creates a failed validation result.
         *
         * @param message description of why validation failed
         * @return a {@code ValidationResult} indicating validation failed
         */
        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }

        /**
         * Returns whether the validation passed.
         *
         * @return {@code true} if validation passed, {@code false} otherwise
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Returns the validation message.
         *
         * @return the validation error message, or {@code null} if validation
         * passed
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * Summary of automatic transition processing. Tracks counts of closed
     * tenders, evaluated tenders, and failures during automatic processing.
     */
    public static class ProcessSummary {

        /**
         * Count of tenders successfully closed
         */
        private int closedCount = 0;
        /**
         * Count of tenders successfully evaluated
         */
        private int evaluatedCount = 0;
        /**
         * Map of failed tender IDs to their failure reasons
         */
        private final java.util.Map<Integer, String> failures = new java.util.HashMap<>();

        /**
         * Increments the count of closed tenders.
         */
        public void addClosed() {
            closedCount++;
        }

        /**
         * Increments the count of evaluated tenders.
         */
        public void addEvaluated() {
            evaluatedCount++;
        }

        /**
         * Records a failed transition for a specific tender.
         *
         * @param tenderId the ID of the tender that failed to transition
         * @param reason the reason why the transition failed
         */
        public void addFailed(int tenderId, String reason) {
            failures.put(tenderId, reason);
        }

        /**
         * Returns the count of tenders successfully closed.
         *
         * @return number of closed tenders
         */
        public int getClosedCount() {
            return closedCount;
        }

        /**
         * Returns the count of tenders successfully evaluated.
         *
         * @return number of evaluated tenders
         */
        public int getEvaluatedCount() {
            return evaluatedCount;
        }

        /**
         * Returns the total number of tenders processed (closed + evaluated).
         *
         * @return total processed count
         */
        public int getTotalProcessed() {
            return closedCount + evaluatedCount;
        }

        /**
         * Returns the map of failed tender IDs to their failure reasons.
         *
         * @return map of failures
         */
        public java.util.Map<Integer, String> getFailures() {
            return failures;
        }

        /**
         * Returns a string representation of the process summary.
         *
         * @return formatted string showing closed, evaluated, and failed counts
         */
        @Override
        public String toString() {
            return String.format("Processed: %d closed, %d evaluated, %d failed",
                    closedCount, evaluatedCount, failures.size());
        }
    }
}
