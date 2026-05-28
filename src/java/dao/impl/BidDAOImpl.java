package dao.impl;

import dao.DAOException;
import dao.interfaces.BidDAO;
import model.Bid;
import model.BidStatistics;
import model.enums.TenderStatus;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of BidDAO interface.
 *
 * @author kolisang phatela
 * @version 1.0
 */
public class BidDAOImpl implements BidDAO {

    private static final Logger logger = Logger.getLogger(BidDAOImpl.class.getName());
    private DataSource dataSource;

    // Direct JDBC connection parameters (fallback)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kolisangphatela_2334120?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Set your MySQL password if any
    private boolean useDirectConnection = false;

    public BidDAOImpl() throws DAOException {
        // Try JNDI first
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            dataSource = (DataSource) envCtx.lookup("jdbc/ProcureGovDB");
            // Test the connection
            try (Connection conn = dataSource.getConnection()) {
                logger.info("BidDAOImpl initialized with JNDI DataSource - Connection successful!");
            }
        } catch (NamingException e) {
            logger.log(Level.WARNING, "JNDI DataSource not found, falling back to direct JDBC connection", e);
            useDirectConnection = true;
            // Test direct connection
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    logger.info("BidDAOImpl initialized with Direct JDBC Connection - Connection successful!");
                }
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "MySQL JDBC Driver not found", ex);
                throw new DAOException("MySQL JDBC Driver not found", ex);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Direct database connection failed", ex);
                throw new DAOException("Database connection failed", ex);
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
    public int create(Bid bid) throws DAOException {
        String sql = "INSERT INTO bids (tender_id, supplier_id, bid_amount, "
                + "technical_compliance_statement, proposed_timeline_days, "
                + "supporting_document_path, submitted_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bid.getTenderId());
            ps.setInt(2, bid.getSupplierId());
            ps.setBigDecimal(3, bid.getBidAmount());
            ps.setString(4, bid.getTechnicalComplianceStatement());
            ps.setInt(5, bid.getProposedTimelineDays());
            ps.setString(6, bid.getSupportingDocumentPath());
            ps.setTimestamp(7, Timestamp.valueOf(bid.getSubmittedAt()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("Creating bid failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating bid failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating bid", e);
            throw new DAOException("Failed to create bid", e);
        }
    }

    @Override
    public Bid findById(int bidId) throws DAOException {
        String sql = "SELECT b.*, u.full_name as supplier_name, t.reference_number as tender_reference, "
                + "t.title as tender_title, t.status as tender_status "
                + "FROM bids b "
                + "JOIN users u ON b.supplier_id = u.user_id "
                + "JOIN tenders t ON b.tender_id = t.tender_id "
                + "WHERE b.bid_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bidId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBid(rs);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding bid by ID: " + bidId, e);
            throw new DAOException("Failed to retrieve bid", e);
        }

        return null;
    }

    @Override
    public List<Bid> findByTenderId(int tenderId) throws DAOException {
        String sql = "SELECT b.*, u.full_name as supplier_name, t.reference_number as tender_reference, "
                + "t.title as tender_title, t.status as tender_status "
                + "FROM bids b "
                + "JOIN users u ON b.supplier_id = u.user_id "
                + "JOIN tenders t ON b.tender_id = t.tender_id "
                + "WHERE b.tender_id = ? "
                + "ORDER BY b.submitted_at DESC";

        List<Bid> bids = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bids.add(mapResultSetToBid(rs));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding bids by tender ID: " + tenderId, e);
            throw new DAOException("Failed to retrieve bids", e);
        }

        return bids;
    }

    @Override
    public List<Bid> findBySupplierId(int supplierId) throws DAOException {
        String sql = "SELECT b.*, u.full_name as supplier_name, t.reference_number as tender_reference, "
                + "t.title as tender_title, t.status as tender_status "
                + "FROM bids b "
                + "JOIN users u ON b.supplier_id = u.user_id "
                + "JOIN tenders t ON b.tender_id = t.tender_id "
                + "WHERE b.supplier_id = ? "
                + "ORDER BY b.submitted_at DESC";

        List<Bid> bids = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bids.add(mapResultSetToBid(rs));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding bids by supplier ID: " + supplierId, e);
            throw new DAOException("Failed to retrieve bids", e);
        }

        return bids;
    }

    @Override
    public boolean hasSupplierBid(int tenderId, int supplierId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM bids WHERE tender_id = ? AND supplier_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);
            ps.setInt(2, supplierId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if supplier has bid", e);
            throw new DAOException("Failed to check bid existence", e);
        }

        return false;
    }

    @Override
    public BigDecimal findLowestBidAmount(int tenderId) throws DAOException {
        String sql = "SELECT MIN(bid_amount) FROM bids WHERE tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal lowest = rs.getBigDecimal(1);
                    return lowest != null ? lowest : BigDecimal.ZERO;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding lowest bid amount", e);
            throw new DAOException("Failed to find lowest bid", e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public int findShortestTimeline(int tenderId) throws DAOException {
        String sql = "SELECT MIN(proposed_timeline_days) FROM bids WHERE tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int shortest = rs.getInt(1);
                    return rs.wasNull() ? Integer.MAX_VALUE : shortest;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding shortest timeline", e);
            throw new DAOException("Failed to find shortest timeline", e);
        }

        return Integer.MAX_VALUE;
    }

    @Override
    public boolean update(Bid bid) throws DAOException {
        String sql = "UPDATE bids SET bid_amount = ?, technical_compliance_statement = ?, "
                + "proposed_timeline_days = ?, supporting_document_path = ? "
                + "WHERE bid_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, bid.getBidAmount());
            ps.setString(2, bid.getTechnicalComplianceStatement());
            ps.setInt(3, bid.getProposedTimelineDays());
            ps.setString(4, bid.getSupportingDocumentPath());
            ps.setInt(5, bid.getBidId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating bid", e);
            throw new DAOException("Failed to update bid", e);
        }
    }

    @Override
    public List<Bid> findRankedBidsByTenderId(int tenderId) throws DAOException {
        String sql = "SELECT b.*, u.full_name as supplier_name, "
                + "AVG(es.weighted_total) as final_score, "
                + "RANK() OVER (ORDER BY AVG(es.weighted_total) DESC) as rank "
                + "FROM bids b "
                + "JOIN users u ON b.supplier_id = u.user_id "
                + "LEFT JOIN evaluation_scores es ON b.bid_id = es.bid_id "
                + "WHERE b.tender_id = ? "
                + "GROUP BY b.bid_id "
                + "ORDER BY final_score DESC";

        List<Bid> rankedBids = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bid bid = mapResultSetToBidBasic(rs);
                    bid.setSupplierName(rs.getString("supplier_name"));
                    double finalScore = rs.getDouble("final_score");
                    if (!rs.wasNull()) {
                        bid.setFinalScore(finalScore);
                    }
                    bid.setRank(rs.getInt("rank"));
                    rankedBids.add(bid);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding ranked bids", e);
            throw new DAOException("Failed to retrieve ranked bids", e);
        }

        return rankedBids;
    }

    @Override
    public int countBidsByTenderId(int tenderId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM bids WHERE tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting bids for tender: " + tenderId, e);
            throw new DAOException("Failed to count bids for tender", e);
        }

        return 0;
    }

    @Override
    public int countBidsBySupplier(int supplierId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM bids WHERE supplier_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting bids for supplier: " + supplierId, e);
            throw new DAOException("Failed to count bids for supplier", e);
        }

        return 0;
    }

    @Override
    public BidStatistics getBidStatistics(int tenderId) throws DAOException {
        String sql = "SELECT "
                + "COUNT(*) as total_bids, "
                + "MIN(bid_amount) as lowest_bid, "
                + "MAX(bid_amount) as highest_bid, "
                + "AVG(bid_amount) as average_bid, "
                + "MIN(proposed_timeline_days) as shortest_timeline, "
                + "MAX(proposed_timeline_days) as longest_timeline, "
                + "AVG(proposed_timeline_days) as average_timeline "
                + "FROM bids WHERE tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BidStatistics stats = new BidStatistics();
                    stats.setTotalBids(rs.getInt("total_bids"));
                    stats.setLowestBid(rs.getBigDecimal("lowest_bid"));
                    stats.setHighestBid(rs.getBigDecimal("highest_bid"));
                    stats.setAverageBid(rs.getBigDecimal("average_bid"));
                    stats.setShortestTimeline(rs.getInt("shortest_timeline"));
                    stats.setLongestTimeline(rs.getInt("longest_timeline"));
                    stats.setAverageTimeline(rs.getDouble("average_timeline"));
                    return stats;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting bid statistics for tender: " + tenderId, e);
            throw new DAOException("Failed to get bid statistics", e);
        }

        return new BidStatistics();
    }

    @Override
    public List<Bid> findBySupplierIdWithFilters(int supplierId, String status, String dateFrom, String dateTo) throws DAOException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b.*, u.full_name as supplier_name, t.reference_number as tender_reference, ");
        sql.append("t.title as tender_title, t.status as tender_status, t.closing_datetime ");
        sql.append("FROM bids b ");
        sql.append("JOIN users u ON b.supplier_id = u.user_id ");
        sql.append("JOIN tenders t ON b.tender_id = t.tender_id ");
        sql.append("WHERE b.supplier_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(supplierId);

        // Add status filter
        if (status != null && !status.trim().isEmpty()) {
            switch (status.toUpperCase()) {
                case "PENDING":
                    sql.append("AND t.status = 'OPEN' AND t.closing_datetime > NOW() ");
                    break;
                case "EVALUATING":
                    sql.append("AND t.status IN ('UNDER_EVALUATION', 'EVALUATED') ");
                    break;
                case "WON":
                    sql.append("AND t.status = 'AWARDED' AND t.awarded_bid_id = b.bid_id ");
                    break;
                case "LOST":
                    sql.append("AND t.status = 'AWARDED' AND t.awarded_bid_id != b.bid_id ");
                    break;
            }
        }

        // Add date filters
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND DATE(b.submitted_at) >= ? ");
            params.add(dateFrom);
        }

        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND DATE(b.submitted_at) <= ? ");
            params.add(dateTo);
        }

        sql.append("ORDER BY b.submitted_at DESC");

        List<Bid> bids = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bid bid = mapResultSetToBid(rs);
                    bids.add(bid);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding bids by supplier with filters", e);
            throw new DAOException("Failed to retrieve filtered bids", e);
        }

        return bids;
    }

    @Override
    public boolean delete(int bidId) throws DAOException {
        String sql = "DELETE FROM bids WHERE bid_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bidId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Bid deleted: ID=" + bidId);
            }

            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting bid", e);
            throw new DAOException("Failed to delete bid", e);
        }
    }

    // ============================================================
    // UPDATE BID FINAL SCORE METHOD - CORRECTED
    // ============================================================
    @Override
    public void updateBidFinalScore(int bidId, Double averageScore) throws DAOException {
        String sql = "UPDATE bids SET final_score = ? WHERE bid_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            if (averageScore != null) {
                ps.setDouble(1, averageScore);
            } else {
                ps.setNull(1, Types.DOUBLE);
            }
            ps.setInt(2, bidId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Updated final score for bid ID: " + bidId + " to " + averageScore);
            } else {
                logger.warning("No bid found with ID: " + bidId + " to update final score");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating final score for bid: " + bidId, e);
            throw new DAOException("Failed to update bid final score", e);
        }
    }

    /**
     * Maps a ResultSet to a Bid object with all display fields.
     */
    private Bid mapResultSetToBid(ResultSet rs) throws SQLException {
        Bid bid = mapResultSetToBidBasic(rs);

        try {
            bid.setSupplierName(rs.getString("supplier_name"));
        } catch (SQLException e) {
            // Column may not be present
        }

        try {
            bid.setTenderReference(rs.getString("tender_reference"));
        } catch (SQLException e) {
            // Column may not be present
        }

        try {
            bid.setTenderTitle(rs.getString("tender_title"));
        } catch (SQLException e) {
            // Column may not be present
        }

        try {
            String statusStr = rs.getString("tender_status");
            if (statusStr != null) {
                bid.setTenderStatus(TenderStatus.valueOf(statusStr));
            }
        } catch (SQLException e) {
            // Column may not be present
        }

        return bid;
    }

    /**
     * Maps a ResultSet to a Bid object with basic fields only.
     */
    private Bid mapResultSetToBidBasic(ResultSet rs) throws SQLException {
        Bid bid = new Bid();

        bid.setBidId(rs.getInt("bid_id"));
        bid.setTenderId(rs.getInt("tender_id"));
        bid.setSupplierId(rs.getInt("supplier_id"));
        bid.setBidAmount(rs.getBigDecimal("bid_amount"));
        bid.setTechnicalComplianceStatement(rs.getString("technical_compliance_statement"));
        bid.setProposedTimelineDays(rs.getInt("proposed_timeline_days"));
        bid.setSupportingDocumentPath(rs.getString("supporting_document_path"));

        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        if (submittedAt != null) {
            bid.setSubmittedAt(submittedAt.toLocalDateTime());
        }

        return bid;
    }
}
