package dao.impl;

import dao.DAOException;
import dao.interfaces.TenderDAO;
import model.Tender;
import model.enums.TenderCategory;
import model.enums.TenderStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TenderDAOImpl implements TenderDAO {

    private static final Logger logger = Logger.getLogger(TenderDAOImpl.class.getName());

    // Direct JDBC connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kolisangphatela_2334120?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Set your MySQL password if any

    public TenderDAOImpl() throws DAOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                logger.info("TenderDAOImpl initialized with Direct JDBC Connection - Connection successful!");
            }
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new DAOException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Direct database connection failed", e);
            throw new DAOException("Database connection failed", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    public Tender findById(int tenderId) throws DAOException {
        String sql = "SELECT t.*, u.full_name as created_by_name "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTender(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding tender by ID: " + tenderId, e);
            throw new DAOException("Failed to retrieve tender", e);
        }
        return null;
    }

    @Override
    public Tender findByReferenceNumber(String referenceNumber) throws DAOException {
        String sql = "SELECT t.*, u.full_name as created_by_name "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.reference_number = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, referenceNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTender(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding tender by reference: " + referenceNumber, e);
            throw new DAOException("Failed to retrieve tender", e);
        }
        return null;
    }

    @Override
    public boolean updateStatus(int tenderId, TenderStatus newStatus) throws DAOException {
        String sql = "UPDATE tenders SET status = ? WHERE tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setInt(2, tenderId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Tender status updated: ID=" + tenderId + ", Status=" + newStatus);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating tender status: " + tenderId, e);
            throw new DAOException("Failed to update tender status", e);
        }
    }

    @Override
    public List<Tender> findByStatus(TenderStatus status) throws DAOException {
        String sql = "SELECT t.*, u.full_name as created_by_name "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.status = ? "
                + "ORDER BY t.created_at DESC";

        List<Tender> tenders = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tenders.add(mapResultSetToTender(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding tenders by status: " + status, e);
            throw new DAOException("Failed to retrieve tenders by status", e);
        }
        return tenders;
    }

    @Override
    public List<Tender> findOpenTenders() throws DAOException {
        return findByStatus(TenderStatus.OPEN);
    }

    @Override
    public List<Tender> findVisibleToSuppliers() throws DAOException {
        String sql = "SELECT t.*, u.full_name as created_by_name "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.status IN ('OPEN', 'CLOSED', 'UNDER_EVALUATION', 'EVALUATED', 'AWARDED') "
                + "ORDER BY t.created_at DESC";

        List<Tender> tenders = new ArrayList<>();

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tenders.add(mapResultSetToTender(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding visible tenders", e);
            throw new DAOException("Failed to retrieve visible tenders", e);
        }
        return tenders;
    }

    @Override
    public List<Tender> findByCreatedBy(int officerId) throws DAOException {
        String sql = "SELECT t.*, u.full_name as created_by_name "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.created_by = ? "
                + "ORDER BY t.created_at DESC";

        List<Tender> tenders = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, officerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tenders.add(mapResultSetToTender(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding tenders by officer: " + officerId, e);
            throw new DAOException("Failed to retrieve tenders", e);
        }
        return tenders;
    }

    @Override
    public boolean awardTender(int tenderId, int winningBidId, BigDecimal awardValue,
            String justification) throws DAOException {
        String sql = "UPDATE tenders SET status = ?, awarded_bid_id = ?, "
                + "awarded_value = ?, award_justification = ?, award_date = ? "
                + "WHERE tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, TenderStatus.AWARDED.name());
            ps.setInt(2, winningBidId);
            ps.setBigDecimal(3, awardValue);
            ps.setString(4, justification);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, tenderId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Tender awarded: ID=" + tenderId + ", Winning Bid=" + winningBidId);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error awarding tender: " + tenderId, e);
            throw new DAOException("Failed to award tender", e);
        }
    }

    @Override
    public int create(Tender tender) throws DAOException {
        String sql = "INSERT INTO tenders (reference_number, title, category, description, "
                + "estimated_value, closing_datetime, status, tender_notice_path, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tender.getReferenceNumber());
            ps.setString(2, tender.getTitle());
            ps.setString(3, tender.getCategory().name());
            ps.setString(4, tender.getDescription());
            ps.setBigDecimal(5, tender.getEstimatedValue());
            ps.setTimestamp(6, Timestamp.valueOf(tender.getClosingDateTime()));
            ps.setString(7, tender.getStatus().name());
            ps.setString(8, tender.getTenderNoticePath());
            ps.setInt(9, tender.getCreatedBy());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Creating tender failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating tender failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating tender", e);
            throw new DAOException("Failed to create tender", e);
        }
    }

    @Override
    public boolean update(Tender tender) throws DAOException {
        String sql = "UPDATE tenders SET title = ?, category = ?, description = ?, "
                + "estimated_value = ?, closing_datetime = ? "
                + "WHERE tender_id = ? AND status = 'DRAFT'";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tender.getTitle());
            ps.setString(2, tender.getCategory().name());
            ps.setString(3, tender.getDescription());
            ps.setBigDecimal(4, tender.getEstimatedValue());
            ps.setTimestamp(5, Timestamp.valueOf(tender.getClosingDateTime()));
            ps.setInt(6, tender.getTenderId());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Tender updated: ID=" + tender.getTenderId());
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating tender", e);
            throw new DAOException("Failed to update tender", e);
        }
    }

    @Override
    public String generateNextReferenceNumber() throws DAOException {
        int currentYear = Year.now().getValue();
        String prefix = "MPW-" + currentYear + "-";
        String sql = "SELECT MAX(CAST(SUBSTRING_INDEX(reference_number, '-', -1) AS UNSIGNED)) "
                + "FROM tenders WHERE reference_number LIKE ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                int nextNumber = 1;
                if (rs.next()) {
                    String maxStr = rs.getString(1);
                    if (maxStr != null) {
                        nextNumber = Integer.parseInt(maxStr) + 1;
                    }
                }
                return prefix + String.format("%04d", nextNumber);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error generating reference number", e);
            throw new DAOException("Failed to generate tender reference number", e);
        }
    }

    @Override
    public int closeExpiredTenders() throws DAOException {
        String sql = "UPDATE tenders SET status = ? "
                + "WHERE status = ? AND closing_datetime < ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, TenderStatus.CLOSED.name());
            ps.setString(2, TenderStatus.OPEN.name());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Closed " + affectedRows + " expired tenders");
            }
            return affectedRows;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error closing expired tenders", e);
            throw new DAOException("Failed to close expired tenders", e);
        }
    }

    @Override
    public List<Tender> findAll() throws DAOException {
        String sql = "SELECT t.*, u.full_name as created_by_name "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "ORDER BY t.created_at DESC";

        List<Tender> tenders = new ArrayList<>();

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tenders.add(mapResultSetToTender(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all tenders", e);
            throw new DAOException("Failed to retrieve tenders", e);
        }
        return tenders;
    }

    @Override
    public List<Tender> findWithFilters(TenderStatus status, TenderCategory category)
            throws DAOException {
        StringBuilder sql = new StringBuilder(
                "SELECT t.*, u.full_name as created_by_name "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (status != null) {
            sql.append("AND t.status = ? ");
            params.add(status.name());
        }
        if (category != null) {
            sql.append("AND t.category = ? ");
            params.add(category.name());
        }
        sql.append("ORDER BY t.created_at DESC");

        List<Tender> tenders = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tenders.add(mapResultSetToTender(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding tenders with filters", e);
            throw new DAOException("Failed to retrieve filtered tenders", e);
        }
        return tenders;
    }

    @Override
    public int countBids(int tenderId) throws DAOException {
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
            throw new DAOException("Failed to count bids", e);
        }
        return 0;
    }

    @Override
    public boolean delete(int tenderId) throws DAOException {
        String sql = "DELETE FROM tenders WHERE tender_id = ? AND status = 'DRAFT'";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenderId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Tender deleted: ID=" + tenderId);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting tender: " + tenderId, e);
            throw new DAOException("Failed to delete tender", e);
        }
    }

    public String getDocumentPath(int tenderId) throws DAOException {
        String sql = "SELECT tender_notice_path FROM tenders WHERE tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tender_notice_path");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting document path for tender: " + tenderId, e);
            throw new DAOException("Failed to retrieve document path", e);
        }
        return null;
    }

    private Tender mapResultSetToTender(ResultSet rs) throws SQLException {
        Tender tender = new Tender();

        tender.setTenderId(rs.getInt("tender_id"));
        tender.setReferenceNumber(rs.getString("reference_number"));
        tender.setTitle(rs.getString("title"));

        String categoryStr = rs.getString("category");
        if (categoryStr != null) {
            tender.setCategory(TenderCategory.valueOf(categoryStr));
        }

        tender.setDescription(rs.getString("description"));
        tender.setEstimatedValue(rs.getBigDecimal("estimated_value"));

        Timestamp closingDateTime = rs.getTimestamp("closing_datetime");
        if (closingDateTime != null) {
            tender.setClosingDateTime(closingDateTime.toLocalDateTime());
        }

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            tender.setStatus(TenderStatus.valueOf(statusStr));
        }

        tender.setTenderNoticePath(rs.getString("tender_notice_path"));
        tender.setCreatedBy(rs.getInt("created_by"));
        tender.setAwardJustification(rs.getString("award_justification"));

        int awardedBidId = rs.getInt("awarded_bid_id");
        if (!rs.wasNull()) {
            tender.setAwardedBidId(awardedBidId);
        }

        tender.setAwardedValue(rs.getBigDecimal("awarded_value"));

        Timestamp awardDate = rs.getTimestamp("award_date");
        if (awardDate != null) {
            tender.setAwardDate(awardDate.toLocalDateTime());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            tender.setCreatedAt(createdAt.toLocalDateTime());
        }

        try {
            tender.setCreatedByName(rs.getString("created_by_name"));
        } catch (SQLException e) {
            // Column may not be present in all queries
        }

        return tender;
    }

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
    public boolean updateAwardedValue(int tenderId, BigDecimal awardedValue) throws DAOException {
        String sql = "UPDATE tenders SET awarded_value = ? WHERE tender_id = ? AND status = 'AWARDED'";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, awardedValue);
            ps.setInt(2, tenderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating awarded value for tender: " + tenderId, e);
            throw new DAOException("Failed to update awarded value", e);
        }
    }

    @Override
    public Integer getWinningBidId(int tenderId) throws DAOException {
        String sql = "SELECT awarded_bid_id FROM tenders WHERE tender_id = ? AND status = 'AWARDED'";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int bidId = rs.getInt("awarded_bid_id");
                    return rs.wasNull() ? null : bidId;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting winning bid for tender: " + tenderId, e);
            throw new DAOException("Failed to get winning bid", e);
        }
        return null;
    }

    @Override
    public boolean isAwarded(int tenderId) throws DAOException {
        String sql = "SELECT status FROM tenders WHERE tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return TenderStatus.AWARDED.name().equals(rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if tender is awarded: " + tenderId, e);
            throw new DAOException("Failed to check award status", e);
        }
        return false;
    }

    @Override
    public boolean updateEvaluationCompletedDate(int tenderId, LocalDateTime completedDate) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean completeEvaluation(int tenderId, LocalDateTime completedDate) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
