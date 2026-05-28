package dao.interfaces;

import dao.DAOException;
import java.time.LocalDateTime;
import model.Tender;
import model.enums.TenderCategory;
import model.enums.TenderStatus;

import java.util.List;

/**
 * Data Access Object interface for Tender entities.
 *
 * @author YourName
 * @version 1.0
 */
public interface TenderDAO {

    /**
     * Creates a new tender.
     *
     * @param tender Tender object with all required fields
     * @return Generated tender ID
     * @throws DAOException if database error occurs
     */
    int create(Tender tender) throws DAOException;

    /**
     * Finds a tender by its database ID.
     */
    Tender findById(int tenderId) throws DAOException;

    /**
     * Finds a tender by reference number.
     */
    Tender findByReferenceNumber(String referenceNumber) throws DAOException;

    /**
     * Retrieves all tenders.
     */
    List<Tender> findAll() throws DAOException;

    /**
     * Retrieves tenders with optional status and category filters.
     */
    List<Tender> findWithFilters(TenderStatus status, TenderCategory category)
            throws DAOException;

    /**
     * Retrieves tenders by status only.
     */
    List<Tender> findByStatus(TenderStatus status) throws DAOException;

    /**
     * Retrieves tenders visible to suppliers (OPEN, CLOSED, UNDER_EVALUATION,
     * etc.)
     */
    List<Tender> findVisibleToSuppliers() throws DAOException;

    /**
     * Retrieves open tenders (status = OPEN and closing date in future).
     */
    List<Tender> findOpenTenders() throws DAOException;

    /**
     * Retrieves tenders created by a specific officer.
     */
    List<Tender> findByCreatedBy(int officerId) throws DAOException;

    /**
     * Updates tender information (only allowed in DRAFT status).
     */
    boolean update(Tender tender) throws DAOException;

    /**
     * Updates the status of a tender.
     */
    boolean updateStatus(int tenderId, TenderStatus newStatus) throws DAOException;

    /**
     * Generates the next tender reference number. Format: MPW-YYYY-NNNN (e.g.,
     * MPW-2026-0042)
     */
    String generateNextReferenceNumber() throws DAOException;

    /**
     * Checks for tenders past their closing date and updates status to CLOSED.
     *
     * @return Number of tenders closed
     */
    int closeExpiredTenders() throws DAOException;

    /**
     * Awards a tender to a specific bid.
     */
    boolean awardTender(int tenderId, int bidId, java.math.BigDecimal awardValue,
            String justification) throws DAOException;

    /**
     * Counts bids for a specific tender.
     */
    int countBids(int tenderId) throws DAOException;

    /**
     * Deletes a tender (only if in DRAFT status).
     */
    boolean delete(int tenderId) throws DAOException;

    // ==================== ADDED METHODS ====================
    /**
     * Updates the awarded value of a tender.
     *
     * @param tenderId The ID of the tender
     * @param awardedValue The awarded contract value
     * @return true if update was successful, false otherwise
     * @throws DAOException if database error occurs
     */
    boolean updateAwardedValue(int tenderId, java.math.BigDecimal awardedValue) throws DAOException;

    /**
     * Gets the winning bid ID for an awarded tender.
     *
     * @param tenderId The ID of the tender
     * @return The winning bid ID, or null if not awarded
     * @throws DAOException if database error occurs
     */
    Integer getWinningBidId(int tenderId) throws DAOException;

    /**
     * Checks if a tender has been awarded.
     *
     * @param tenderId The ID of the tender
     * @return true if the tender has been awarded, false otherwise
     * @throws DAOException if database error occurs
     */
    boolean isAwarded(int tenderId) throws DAOException;
    
    boolean updateEvaluationCompletedDate(int tenderId, LocalDateTime completedDate) throws DAOException;
boolean completeEvaluation(int tenderId, LocalDateTime completedDate) throws DAOException;
}
