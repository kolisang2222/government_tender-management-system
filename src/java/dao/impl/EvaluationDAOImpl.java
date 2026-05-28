package dao.impl;

import dao.DAOException;
import dao.interfaces.EvaluationDAO;
import model.EvaluationScore;
import model.User;
import model.Bid;
import model.enums.UserRole;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of EvaluationDAO interface. Handles all evaluation-related
 * database operations.
 *
 * @author YourName
 * @version 1.0
 */
public class EvaluationDAOImpl implements EvaluationDAO {

    private static final Logger logger = Logger.getLogger(EvaluationDAOImpl.class.getName());
    private DataSource dataSource;

    // Direct JDBC connection parameters (fallback)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kolisangphatela_2334120?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Set your MySQL password if any
    private boolean useDirectConnection = false;

    /**
     * Constructor - initializes database connection pool with fallback.
     */
    public EvaluationDAOImpl() throws DAOException {
        // Try JNDI first
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            dataSource = (DataSource) envCtx.lookup("jdbc/ProcureGovDB");
            // Test the connection
            try (Connection conn = dataSource.getConnection()) {
                logger.info("EvaluationDAOImpl initialized with JNDI DataSource - Connection successful!");
            }
        } catch (NamingException e) {
            logger.log(Level.WARNING, "JNDI DataSource not found, falling back to direct JDBC connection", e);
            useDirectConnection = true;
            // Test direct connection
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    logger.info("EvaluationDAOImpl initialized with Direct JDBC Connection - Connection successful!");
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
    public int submitScore(EvaluationScore score) throws DAOException {
        String sql = "INSERT INTO evaluation_scores "
                + "(bid_id, evaluator_id, technical_compliance_score, price_score, "
                + "timeline_score, weighted_total, submitted_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, score.getBidId());
            ps.setInt(2, score.getEvaluatorId());
            ps.setDouble(3, score.getTechnicalComplianceScore());
            ps.setDouble(4, score.getPriceScore());
            ps.setDouble(5, score.getTimelineScore());
            ps.setDouble(6, score.getWeightedTotal());
            ps.setTimestamp(7, Timestamp.valueOf(score.getSubmittedAt()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("Submitting score failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int scoreId = generatedKeys.getInt(1);
                    logger.info("Evaluation score submitted: ID=" + scoreId
                            + ", Bid=" + score.getBidId()
                            + ", Evaluator=" + score.getEvaluatorId());
                    return scoreId;
                } else {
                    throw new DAOException("Submitting score failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error submitting evaluation score", e);
            throw new DAOException("Failed to submit evaluation score", e);
        }
    }

    @Override
    public EvaluationScore findById(int scoreId) throws DAOException {
        String sql = "SELECT es.*, u.full_name as evaluator_name, "
                + "s.full_name as supplier_name, b.bid_amount, b.proposed_timeline_days "
                + "FROM evaluation_scores es "
                + "JOIN users u ON es.evaluator_id = u.user_id "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "JOIN users s ON b.supplier_id = s.user_id "
                + "WHERE es.score_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scoreId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvaluationScore(rs);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding evaluation score by ID: " + scoreId, e);
            throw new DAOException("Failed to retrieve evaluation score", e);
        }

        return null;
    }

    @Override
    public List<EvaluationScore> findByEvaluatorAndTender(int evaluatorId, int tenderId)
            throws DAOException {

        String sql = "SELECT es.*, u.full_name as evaluator_name, "
                + "s.full_name as supplier_name, b.bid_amount, b.proposed_timeline_days "
                + "FROM evaluation_scores es "
                + "JOIN users u ON es.evaluator_id = u.user_id "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "JOIN users s ON b.supplier_id = s.user_id "
                + "WHERE es.evaluator_id = ? AND b.tender_id = ? "
                + "ORDER BY es.submitted_at DESC";

        List<EvaluationScore> scores = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, evaluatorId);
            ps.setInt(2, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    scores.add(mapResultSetToEvaluationScore(rs));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding scores by evaluator and tender", e);
            throw new DAOException("Failed to retrieve evaluation scores", e);
        }

        return scores;
    }

    @Override
    public List<EvaluationScore> findByBidId(int bidId) throws DAOException {
        String sql = "SELECT es.*, u.full_name as evaluator_name, "
                + "s.full_name as supplier_name, b.bid_amount, b.proposed_timeline_days "
                + "FROM evaluation_scores es "
                + "JOIN users u ON es.evaluator_id = u.user_id "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "JOIN users s ON b.supplier_id = s.user_id "
                + "WHERE es.bid_id = ? "
                + "ORDER BY es.submitted_at DESC";

        List<EvaluationScore> scores = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bidId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    scores.add(mapResultSetToEvaluationScore(rs));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding scores by bid ID: " + bidId, e);
            throw new DAOException("Failed to retrieve evaluation scores", e);
        }

        return scores;
    }

    @Override
    public List<EvaluationScore> findByTenderId(int tenderId) throws DAOException {
        String sql = "SELECT es.*, u.full_name as evaluator_name, "
                + "s.full_name as supplier_name, b.bid_amount, b.proposed_timeline_days "
                + "FROM evaluation_scores es "
                + "JOIN users u ON es.evaluator_id = u.user_id "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "JOIN users s ON b.supplier_id = s.user_id "
                + "WHERE b.tender_id = ? "
                + "ORDER BY b.bid_id, es.evaluator_id";

        List<EvaluationScore> scores = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    scores.add(mapResultSetToEvaluationScore(rs));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding scores by tender ID: " + tenderId, e);
            throw new DAOException("Failed to retrieve evaluation scores", e);
        }

        return scores;
    }

    @Override
    public List<EvaluationScore> findByEvaluatorId(int evaluatorId) throws DAOException {
        String sql = "SELECT es.*, u.full_name as evaluator_name, "
                + "s.full_name as supplier_name, b.bid_amount, b.proposed_timeline_days, "
                + "t.reference_number as tender_reference "
                + "FROM evaluation_scores es "
                + "JOIN users u ON es.evaluator_id = u.user_id "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "JOIN users s ON b.supplier_id = s.user_id "
                + "JOIN tenders t ON b.tender_id = t.tender_id "
                + "WHERE es.evaluator_id = ? "
                + "ORDER BY es.submitted_at DESC";

        List<EvaluationScore> scores = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, evaluatorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EvaluationScore score = mapResultSetToEvaluationScore(rs);
                    score.setTenderReference(rs.getString("tender_reference"));
                    scores.add(score);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding scores by evaluator ID: " + evaluatorId, e);
            throw new DAOException("Failed to retrieve evaluation scores", e);
        }

        return scores;
    }

    @Override
    public boolean hasEvaluatorScoredBid(int evaluatorId, int bidId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM evaluation_scores "
                + "WHERE evaluator_id = ? AND bid_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, evaluatorId);
            ps.setInt(2, bidId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if evaluator scored bid", e);
            throw new DAOException("Failed to check evaluation status", e);
        }

        return false;
    }

    @Override
    public int countSubmittedScores(int tenderId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM evaluation_scores es "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "WHERE b.tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting submitted scores", e);
            throw new DAOException("Failed to count evaluation scores", e);
        }

        return 0;
    }

    @Override
    public int countEvaluatorsWhoScored(int tenderId) throws DAOException {
        String sql = "SELECT COUNT(DISTINCT es.evaluator_id) FROM evaluation_scores es "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "WHERE b.tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting evaluators who scored", e);
            throw new DAOException("Failed to count evaluators", e);
        }

        return 0;
    }

    @Override
    public Double calculateAverageScore(int bidId) throws DAOException {
        String sql = "SELECT AVG(weighted_total) FROM evaluation_scores WHERE bid_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bidId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    if (rs.wasNull()) {
                        return null;
                    }
                    return Math.round(avg * 100.0) / 100.0;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error calculating average score for bid: " + bidId, e);
            throw new DAOException("Failed to calculate average score", e);
        }

        return null;
    }

    @Override
    public List<User> findEvaluatorsForTender(int tenderId) throws DAOException {
        String sql = "SELECT DISTINCT u.* FROM users u "
                + "WHERE u.role = 'EVALUATION_COMMITTEE' "
                + "ORDER BY u.full_name";

        List<User> evaluators = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User evaluator = new User();
                evaluator.setUserId(rs.getInt("user_id"));
                evaluator.setEmail(rs.getString("email"));
                evaluator.setFullName(rs.getString("full_name"));
                evaluator.setRole(UserRole.valueOf(rs.getString("role")));
                evaluators.add(evaluator);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding evaluators for tender", e);
            throw new DAOException("Failed to retrieve evaluators", e);
        }

        return evaluators;
    }

    @Override
    public boolean isEvaluationComplete(int tenderId) throws DAOException {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM bids WHERE tender_id = ?) as total_bids, "
                + "(SELECT COUNT(*) FROM users WHERE role = 'EVALUATION_COMMITTEE') as total_evaluators, "
                + "(SELECT COUNT(*) FROM evaluation_scores es "
                + "JOIN bids b ON es.bid_id = b.bid_id WHERE b.tender_id = ?) as total_scores";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);
            ps.setInt(2, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int totalBids = rs.getInt("total_bids");
                    int totalEvaluators = rs.getInt("total_evaluators");
                    int totalScores = rs.getInt("total_scores");

                    int expectedScores = totalBids * totalEvaluators;

                    return totalBids > 0 && totalEvaluators > 0 && totalScores >= expectedScores;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking evaluation completion", e);
            throw new DAOException("Failed to check evaluation status", e);
        }

        return false;
    }

    @Override
    public boolean updateScore(EvaluationScore score) throws DAOException {
        String sql = "UPDATE evaluation_scores SET "
                + "technical_compliance_score = ?, price_score = ?, "
                + "timeline_score = ?, weighted_total = ? "
                + "WHERE score_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, score.getTechnicalComplianceScore());
            ps.setDouble(2, score.getPriceScore());
            ps.setDouble(3, score.getTimelineScore());
            ps.setDouble(4, score.getWeightedTotal());
            ps.setInt(5, score.getScoreId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Evaluation score updated: ID=" + score.getScoreId());
            }

            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating evaluation score", e);
            throw new DAOException("Failed to update evaluation score", e);
        }
    }

    @Override
    public boolean deleteScore(int scoreId) throws DAOException {
        String sql = "DELETE FROM evaluation_scores WHERE score_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scoreId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Evaluation score deleted: ID=" + scoreId);
            }

            return affectedRows > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting evaluation score", e);
            throw new DAOException("Failed to delete evaluation score", e);
        }
    }

    @Override
    public Map<String, Integer> getEvaluationProgress(int tenderId) throws DAOException {
        Map<String, Integer> progress = new HashMap<>();

        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM bids WHERE tender_id = ?) as total_bids, "
                + "(SELECT COUNT(*) FROM users WHERE role = 'EVALUATION_COMMITTEE') as total_evaluators, "
                + "(SELECT COUNT(DISTINCT es.evaluator_id) FROM evaluation_scores es "
                + "JOIN bids b ON es.bid_id = b.bid_id WHERE b.tender_id = ?) as evaluators_submitted, "
                + "(SELECT COUNT(*) FROM evaluation_scores es "
                + "JOIN bids b ON es.bid_id = b.bid_id WHERE b.tender_id = ?) as total_scores";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);
            ps.setInt(2, tenderId);
            ps.setInt(3, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    progress.put("totalBids", rs.getInt("total_bids"));
                    progress.put("totalEvaluators", rs.getInt("total_evaluators"));
                    progress.put("evaluatorsSubmitted", rs.getInt("evaluators_submitted"));
                    progress.put("totalScores", rs.getInt("total_scores"));

                    int expectedScores = progress.get("totalBids") * progress.get("totalEvaluators");
                    progress.put("expectedScores", expectedScores);
                    progress.put("remainingScores", expectedScores - progress.get("totalScores"));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting evaluation progress", e);
            throw new DAOException("Failed to get evaluation progress", e);
        }

        return progress;
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
                    bid.setSupplierName(rs.getString("supplier_name"));
                    double finalScore = rs.getDouble("final_score");
                    bid.setFinalScore(rs.wasNull() ? 0 : finalScore);
                    bid.setRank(rs.getInt("rank"));

                    rankedBids.add(bid);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding ranked bids for tender: " + tenderId, e);
            throw new DAOException("Failed to retrieve ranked bids", e);
        }

        return rankedBids;
    }

    // ==================== ADDED METHODS ====================
    @Override
    public boolean hasEvaluatorScored(int evaluatorId, int bidId) throws DAOException {
        return hasEvaluatorScoredBid(evaluatorId, bidId);
    }

    @Override
    public int countEvaluatorsForTender(int tenderId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'EVALUATION_COMMITTEE'";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting evaluators for tender", e);
            throw new DAOException("Failed to count evaluators", e);
        }

        return 0;
    }

    @Override
    public int countCompletedEvaluators(int tenderId) throws DAOException {
        String sql = "SELECT COUNT(DISTINCT es.evaluator_id) FROM evaluation_scores es "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "WHERE b.tender_id = ? "
                + "GROUP BY es.evaluator_id "
                + "HAVING COUNT(DISTINCT es.bid_id) = (SELECT COUNT(*) FROM bids WHERE tender_id = ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);
            ps.setInt(2, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                }
                return count;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting completed evaluators", e);
            throw new DAOException("Failed to count completed evaluators", e);
        }
    }

    @Override
    public EvaluationScore findByEvaluatorAndBid(int evaluatorId, int bidId) throws DAOException {
        String sql = "SELECT es.*, u.full_name as evaluator_name, "
                + "s.full_name as supplier_name, b.bid_amount, b.proposed_timeline_days "
                + "FROM evaluation_scores es "
                + "JOIN users u ON es.evaluator_id = u.user_id "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "JOIN users s ON b.supplier_id = s.user_id "
                + "WHERE es.evaluator_id = ? AND es.bid_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, evaluatorId);
            ps.setInt(2, bidId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvaluationScore(rs);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding score by evaluator and bid", e);
            throw new DAOException("Failed to retrieve evaluation score", e);
        }

        return null;
    }

    @Override
    public List<EvaluationScore> findDetailedScoresByTenderId(int tenderId) throws DAOException {
        String sql = "SELECT es.*, u.full_name as evaluator_name, b.bid_amount, "
                + "s.full_name as supplier_name "
                + "FROM evaluation_scores es "
                + "JOIN users u ON es.evaluator_id = u.user_id "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "JOIN users s ON b.supplier_id = s.user_id "
                + "WHERE b.tender_id = ? "
                + "ORDER BY es.bid_id, es.evaluator_id";

        List<EvaluationScore> scores = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    scores.add(mapResultSetToEvaluationScore(rs));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding detailed scores", e);
            throw new DAOException("Failed to retrieve detailed scores", e);
        }

        return scores;
    }

    @Override
    public boolean hasAnyScores(int tenderId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM evaluation_scores es "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "WHERE b.tender_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if any scores exist", e);
            throw new DAOException("Failed to check scores existence", e);
        }

        return false;
    }

    @Override
    public int countFullyEvaluatedBids(int tenderId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM ( "
                + "SELECT b.bid_id FROM bids b "
                + "WHERE b.tender_id = ? "
                + "AND (SELECT COUNT(*) FROM evaluation_scores es "
                + "WHERE es.bid_id = b.bid_id) = "
                + "(SELECT COUNT(*) FROM users WHERE role = 'EVALUATION_COMMITTEE') "
                + ") AS fully_evaluated";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting fully evaluated bids", e);
            throw new DAOException("Failed to count fully evaluated bids", e);
        }

        return 0;
    }

    // ==================== END OF ADDED METHODS ====================
    /**
     * Maps a ResultSet row to an EvaluationScore object.
     */
    private EvaluationScore mapResultSetToEvaluationScore(ResultSet rs) throws SQLException {
        EvaluationScore score = new EvaluationScore();

        score.setScoreId(rs.getInt("score_id"));
        score.setBidId(rs.getInt("bid_id"));
        score.setEvaluatorId(rs.getInt("evaluator_id"));
        score.setTechnicalComplianceScore(rs.getDouble("technical_compliance_score"));
        score.setPriceScore(rs.getDouble("price_score"));
        score.setTimelineScore(rs.getDouble("timeline_score"));
        score.setWeightedTotal(rs.getDouble("weighted_total"));

        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        if (submittedAt != null) {
            score.setSubmittedAt(submittedAt.toLocalDateTime());
        }

        // Display fields
        try {
            score.setEvaluatorName(rs.getString("evaluator_name"));
        } catch (SQLException e) {
            // Column may not be present in all queries
        }

        try {
            score.setSupplierName(rs.getString("supplier_name"));
        } catch (SQLException e) {
            // Column may not be present in all queries
        }

        try {
            score.setBidAmount(rs.getBigDecimal("bid_amount"));
        } catch (SQLException e) {
            // Column may not be present in all queries
        }

        try {
            score.setProposedTimelineDays(rs.getInt("proposed_timeline_days"));
        } catch (SQLException e) {
            // Column may not be present in all queries
        }

        return score;
    }
}
