package service;

import model.Bid;
import model.EvaluationScore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for bid evaluation scoring calculations. Handles all scoring
 * logic including price score, timeline score, weighted total, and
 * multi-evaluator averaging.
 *
 * @author kolisang
 * @version 1.0
 */
public class ScoringService {

    /**
     * Logger for recording scoring operations and warnings
     */
    private static final Logger logger = Logger.getLogger(ScoringService.class.getName());

    // Scoring weights as specified in exam requirements
    /**
     * Weight for price score in weighted total calculation (40%)
     */
    private static final double PRICE_WEIGHT = 0.40;      // 40%
    /**
     * Weight for technical compliance score in weighted total calculation (35%)
     */
    private static final double TECHNICAL_WEIGHT = 0.35;  // 35%
    /**
     * Weight for timeline score in weighted total calculation (25%)
     */
    private static final double TIMELINE_WEIGHT = 0.25;   // 25%

    /**
     * Calculates the price score for a bid. Formula: (Lowest Bid Amount / This
     * Bid Amount) × 100
     *
     * @param lowestBidAmount The lowest bid amount among all bids for this
     * tender
     * @param thisBidAmount This bid's amount
     * @return Calculated price score (0-100), rounded to 2 decimal places
     */
    public static double calculatePriceScore(BigDecimal lowestBidAmount, BigDecimal thisBidAmount) {
        if (lowestBidAmount == null || thisBidAmount == null) {
            logger.warning("Null bid amount provided for price score calculation");
            return 0.0;
        }

        if (thisBidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warning("Bid amount must be greater than zero");
            return 0.0;
        }

        if (lowestBidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warning("Lowest bid amount must be greater than zero");
            return 0.0;
        }

        // Calculate: (Lowest / This) × 100
        double score = lowestBidAmount
                .divide(thisBidAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();

        // Cap at 100 and floor at 0
        double finalScore = Math.min(100.0, Math.max(0.0, score));

        return roundToTwoDecimals(finalScore);
    }

    /**
     * Calculates the timeline score for a bid. Formula: (Shortest Proposed
     * Timeline / This Bid's Timeline) × 100
     *
     * @param shortestTimeline The shortest timeline among all bids (in days)
     * @param thisTimeline This bid's proposed timeline (in days)
     * @return Calculated timeline score (0-100), rounded to 2 decimal places
     */
    public static double calculateTimelineScore(int shortestTimeline, int thisTimeline) {
        if (shortestTimeline <= 0 || thisTimeline <= 0) {
            logger.warning("Invalid timeline values: shortest=" + shortestTimeline + ", this=" + thisTimeline);
            return 0.0;
        }

        // Calculate: (Shortest / This) × 100
        double score = ((double) shortestTimeline / thisTimeline) * 100.0;

        // Cap at 100 and floor at 0
        double finalScore = Math.min(100.0, Math.max(0.0, score));

        return roundToTwoDecimals(finalScore);
    }

    /**
     * Calculates the weighted total score for a bid. Formula: (Price Score ×
     * 0.40) + (Technical Score × 0.35) + (Timeline Score × 0.25)
     *
     * @param priceScore The calculated price score (0-100)
     * @param technicalScore The manual technical compliance score (0-100)
     * @param timelineScore The calculated timeline score (0-100)
     * @return Weighted total score, rounded to 2 decimal places
     */
    public static double calculateWeightedTotal(double priceScore, double technicalScore,
            double timelineScore) {
        // Validate inputs
        priceScore = validateScore(priceScore, "Price");
        technicalScore = validateScore(technicalScore, "Technical");
        timelineScore = validateScore(timelineScore, "Timeline");

        // Calculate weighted total
        double total = (priceScore * PRICE_WEIGHT)
                + (technicalScore * TECHNICAL_WEIGHT)
                + (timelineScore * TIMELINE_WEIGHT);

        return roundToTwoDecimals(total);
    }

    /**
     * Calculates the weighted total score using an EvaluationScore object.
     * Extracts the price, technical, and timeline scores from the object and
     * delegates to {@link #calculateWeightedTotal(double, double, double)}.
     *
     * @param score EvaluationScore object containing the three component scores
     * @return Weighted total score, rounded to 2 decimal places; returns 0.0 if
     * score is null
     */
    public static double calculateWeightedTotal(EvaluationScore score) {
        if (score == null) {
            logger.warning("Null EvaluationScore provided");
            return 0.0;
        }

        double priceScore = score.getPriceScore() != null ? score.getPriceScore() : 0.0;
        double technicalScore = score.getTechnicalComplianceScore() != null
                ? score.getTechnicalComplianceScore() : 0.0;
        double timelineScore = score.getTimelineScore() != null ? score.getTimelineScore() : 0.0;

        return calculateWeightedTotal(priceScore, technicalScore, timelineScore);
    }

    /**
     * Calculates the final score by averaging weighted totals from multiple
     * evaluators. Only includes non-null weighted totals in the average
     * calculation.
     *
     * @param scores List of EvaluationScore objects from different evaluators
     * @return Average weighted total score, rounded to 2 decimal places;
     * returns 0.0 if no valid scores
     */
    public static double calculateFinalScore(List<EvaluationScore> scores) {
        if (scores == null || scores.isEmpty()) {
            logger.warning("No scores provided for final score calculation");
            return 0.0;
        }

        double sum = 0.0;
        int validCount = 0;

        for (EvaluationScore score : scores) {
            if (score != null && score.getWeightedTotal() != null) {
                sum += score.getWeightedTotal();
                validCount++;
            }
        }

        if (validCount == 0) {
            logger.warning("No valid weighted totals found for final score calculation");
            return 0.0;
        }

        double average = sum / validCount;
        return roundToTwoDecimals(average);
    }

    /**
     * Calculates the final score for a bid by averaging all evaluators'
     * weighted totals. Delegates to {@link #calculateFinalScore(List)}.
     *
     * @param bid The bid with its evaluation scores
     * @param allScores List of all evaluation scores for this bid
     * @return Average final score, rounded to 2 decimal places; returns 0.0 if
     * bid is null
     */
    public static double calculateBidFinalScore(Bid bid, List<EvaluationScore> allScores) {
        if (bid == null) {
            logger.warning("Null bid provided for final score calculation");
            return 0.0;
        }

        return calculateFinalScore(allScores);
    }

    /**
     * Ranks bids based on their final scores (highest score = rank 1). Sorts
     * bids by final score in descending order and assigns rank values. Handles
     * ties by assigning the same rank to bids with equal scores.
     *
     * @param bids List of bids with final scores populated
     * @return The same list with rank values set; returns unchanged list if
     * null or empty
     */
    public static List<Bid> rankBidsByFinalScore(List<Bid> bids) {
        if (bids == null || bids.isEmpty()) {
            return bids;
        }

        // Sort by final score descending
        bids.sort((b1, b2) -> {
            Double score1 = b1.getFinalScore() != null ? b1.getFinalScore() : 0.0;
            Double score2 = b2.getFinalScore() != null ? b2.getFinalScore() : 0.0;
            return score2.compareTo(score1);
        });

        // Assign ranks
        int rank = 1;
        Double previousScore = null;

        for (int i = 0; i < bids.size(); i++) {
            Bid bid = bids.get(i);
            Double currentScore = bid.getFinalScore() != null ? bid.getFinalScore() : 0.0;

            if (previousScore != null && !currentScore.equals(previousScore)) {
                rank = i + 1;
            }

            bid.setRank(rank);
            previousScore = currentScore;
        }

        return bids;
    }

    /**
     * Gets the winning bid (rank 1) from a ranked list. Returns the first bid
     * with rank 1, or the first bid in the list if no rank 1 is found.
     *
     * @param bids List of ranked bids
     * @return The winning bid, or null if list is empty or null
     */
    public static Bid getWinningBid(List<Bid> bids) {
        if (bids == null || bids.isEmpty()) {
            return null;
        }

        return bids.stream()
                .filter(b -> b.getRank() != null && b.getRank() == 1)
                .findFirst()
                .orElse(bids.get(0));
    }

    /**
     * Validates that a score is within the acceptable range (0-100). Clamps the
     * score to 0 if below range or 100 if above range, and logs a warning.
     *
     * @param score The score to validate
     * @param scoreType The type of score (for logging purposes, e.g., "Price",
     * "Technical")
     * @return The validated score (clamped to 0-100)
     */
    private static double validateScore(double score, String scoreType) {
        if (score < 0) {
            logger.warning(scoreType + " score below 0: " + score + ". Clamping to 0.");
            return 0.0;
        }
        if (score > 100) {
            logger.warning(scoreType + " score above 100: " + score + ". Clamping to 100.");
            return 100.0;
        }
        return score;
    }

    /**
     * Rounds a double to two decimal places using standard rounding.
     *
     * @param value The value to round
     * @return Rounded value to 2 decimal places
     */
    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Formats a score for display with two decimal places.
     *
     * @param score The score to format
     * @return Formatted string (e.g., "85.50")
     */
    public static String formatScore(double score) {
        return String.format("%.2f", score);
    }

    /**
     * Formats a score as a percentage string with one decimal place.
     *
     * @param score The score to format
     * @return Formatted string with percentage symbol (e.g., "85.5%")
     */
    public static String formatScoreAsPercentage(double score) {
        return String.format("%.1f%%", score);
    }

    /**
     * Gets the letter grade for a score based on standard grading scale. A:
     * 90-100, B: 80-89, C: 70-79, D: 60-69, F: below 60.
     *
     * @param score The score (0-100)
     * @return Letter grade (A, B, C, D, or F)
     */
    public static String getLetterGrade(double score) {
        if (score >= 90) {
            return "A";
        }
        if (score >= 80) {
            return "B";
        }
        if (score >= 70) {
            return "C";
        }
        if (score >= 60) {
            return "D";
        }
        return "F";
    }

    /**
     * Checks if all evaluators have submitted scores for all bids. The total
     * number of expected scores is calculated as bidCount × evaluatorCount.
     *
     * @param bidCount Number of bids
     * @param evaluatorCount Number of evaluators
     * @param scoresSubmitted Number of scores submitted
     * @return true if evaluation is complete (all expected scores submitted),
     * false otherwise
     */
    public static boolean isEvaluationComplete(int bidCount, int evaluatorCount, int scoresSubmitted) {
        if (bidCount == 0 || evaluatorCount == 0) {
            return false;
        }

        int expectedScores = bidCount * evaluatorCount;
        return scoresSubmitted >= expectedScores;
    }

    /**
     * Calculates the completion percentage for evaluation. The percentage is
     * calculated as (scoresSubmitted / (bidCount × evaluatorCount)) × 100.
     *
     * @param bidCount Number of bids
     * @param evaluatorCount Number of evaluators
     * @param scoresSubmitted Number of scores submitted
     * @return Completion percentage (0-100); returns 0 if bid count or
     * evaluator count is 0
     */
    public static int calculateCompletionPercentage(int bidCount, int evaluatorCount, int scoresSubmitted) {
        if (bidCount == 0 || evaluatorCount == 0) {
            return 0;
        }

        int expectedScores = bidCount * evaluatorCount;
        if (expectedScores == 0) {
            return 0;
        }

        return (scoresSubmitted * 100) / expectedScores;
    }

    /**
     * Gets the score breakdown as a formatted string showing each component
     * score with its weight contribution and the weighted total.
     *
     * @param priceScore The price score (0-100)
     * @param technicalScore The technical compliance score (0-100)
     * @param timelineScore The timeline score (0-100)
     * @return Formatted breakdown string showing individual scores, weighted
     * contributions, and total
     */
    public static String getScoreBreakdown(double priceScore, double technicalScore, double timelineScore) {
        double total = calculateWeightedTotal(priceScore, technicalScore, timelineScore);

        return String.format(
                "Price: %.2f (40%% = %.2f) | Technical: %.2f (35%% = %.2f) | Timeline: %.2f (25%% = %.2f) | TOTAL: %.2f",
                priceScore, priceScore * PRICE_WEIGHT,
                technicalScore, technicalScore * TECHNICAL_WEIGHT,
                timelineScore, timelineScore * TIMELINE_WEIGHT,
                total
        );
    }
}
