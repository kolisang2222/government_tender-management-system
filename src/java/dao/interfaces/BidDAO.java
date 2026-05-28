package dao.interfaces;

import dao.DAOException;
import model.Bid;
import model.BidStatistics;
import java.math.BigDecimal;
import java.util.List;

/**
 * Data Access Object interface for Bid entities.
 *
 * @author YourName
 * @version 1.0
 */
public interface BidDAO {

    int create(Bid bid) throws DAOException;

    Bid findById(int bidId) throws DAOException;

    List<Bid> findByTenderId(int tenderId) throws DAOException;

    List<Bid> findBySupplierId(int supplierId) throws DAOException;

    List<Bid> findBySupplierIdWithFilters(int supplierId, String status, String dateFrom, String dateTo) throws DAOException;

    boolean hasSupplierBid(int tenderId, int supplierId) throws DAOException;

    BigDecimal findLowestBidAmount(int tenderId) throws DAOException;

    int findShortestTimeline(int tenderId) throws DAOException;

    boolean update(Bid bid) throws DAOException;

    List<Bid> findRankedBidsByTenderId(int tenderId) throws DAOException;

    int countBidsByTenderId(int tenderId) throws DAOException;

    int countBidsBySupplier(int supplierId) throws DAOException;

    BidStatistics getBidStatistics(int tenderId) throws DAOException;

    boolean delete(int bidId) throws DAOException;

    void updateBidFinalScore(int bidId, Double averageScore) throws DAOException;
}
