package model.enums;

/**
 * Represents the mandatory lifecycle states of a tender. Transitions must be
 * enforced in order and cannot skip stages. The lifecycle order is: DRAFT →
 * OPEN → CLOSED → UNDER_EVALUATION → EVALUATED → AWARDED
 *
 * @author YourName
 * @version 1.0
 */
public enum TenderStatus {

    /**
     * Initial state where the tender is being prepared, not visible to
     * suppliers
     */
    DRAFT("Draft", false, false),
    /**
     * Tender is published and accepting bids from suppliers
     */
    OPEN("Open", true, true),
    /**
     * Bidding period has ended, tender is no longer accepting new bids
     */
    CLOSED("Closed", true, false),
    /**
     * Evaluation committee is reviewing and scoring submitted bids
     */
    UNDER_EVALUATION("Under Evaluation", true, false),
    /**
     * All evaluations are complete and scores have been finalized
     */
    EVALUATED("Evaluated", true, false),
    /**
     * Tender has been awarded to a winning supplier, terminal state
     */
    AWARDED("Awarded", true, false);

    /**
     * The human-readable display name for the status
     */
    private final String displayName;
    /**
     * Whether suppliers can see tenders in this status in the portal
     */
    private final boolean visibleToSuppliers;
    /**
     * Whether new bids can be submitted for tenders in this status
     */
    private final boolean acceptingBids;

    /**
     * Constructor for TenderStatus enum constants.
     *
     * @param displayName Human-readable name for UI display
     * @param visibleToSuppliers Whether suppliers can see tenders in this
     * status
     * @param acceptingBids Whether new bids can be submitted for tenders in
     * this status
     */
    TenderStatus(String displayName, boolean visibleToSuppliers, boolean acceptingBids) {
        this.displayName = displayName;
        this.visibleToSuppliers = visibleToSuppliers;
        this.acceptingBids = acceptingBids;
    }

    /**
     * Returns the human-readable display name for this status.
     *
     * @return the display name (e.g., "Open", "Under Evaluation")
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns whether suppliers can see tenders in this status in the portal.
     *
     * @return true if the status is visible to suppliers, false otherwise
     */
    public boolean isVisibleToSuppliers() {
        return visibleToSuppliers;
    }

    /**
     * Returns whether new bids can be submitted for tenders in this status.
     *
     * @return true if the tender is currently accepting bids, false otherwise
     */
    public boolean isAcceptingBids() {
        return acceptingBids;
    }

    /**
     * Validates if a transition from the current status to a new status is
     * allowed. Status transitions must follow the defined lifecycle order
     * without skipping stages. The valid transitions are:
     * <ul>
     * <li>DRAFT → OPEN</li>
     * <li>OPEN → CLOSED</li>
     * <li>CLOSED → UNDER_EVALUATION</li>
     * <li>UNDER_EVALUATION → EVALUATED</li>
     * <li>EVALUATED → AWARDED</li>
     * <li>AWARDED → (no further transitions, terminal state)</li>
     * </ul>
     *
     * @param newStatus The target status to transition to
     * @return true if transition is valid, false otherwise
     */
    public boolean canTransitionTo(TenderStatus newStatus) {
        if (newStatus == null) {
            return false;
        }

        switch (this) {
            case DRAFT:
                return newStatus == OPEN;

            case OPEN:
                return newStatus == CLOSED;

            case CLOSED:
                return newStatus == UNDER_EVALUATION;

            case UNDER_EVALUATION:
                return newStatus == EVALUATED;

            case EVALUATED:
                return newStatus == AWARDED;

            case AWARDED:
                return false; // Terminal state - no further transitions

            default:
                return false;
        }
    }

    /**
     * Gets the next logical status in the tender lifecycle. Follows the defined
     * progression: DRAFT → OPEN → CLOSED → UNDER_EVALUATION → EVALUATED →
     * AWARDED
     *
     * @return The next status in the lifecycle, or null if this is the terminal
     * state (AWARDED)
     */
    public TenderStatus getNextStatus() {
        switch (this) {
            case DRAFT:
                return OPEN;
            case OPEN:
                return CLOSED;
            case CLOSED:
                return UNDER_EVALUATION;
            case UNDER_EVALUATION:
                return EVALUATED;
            case EVALUATED:
                return AWARDED;
            case AWARDED:
                return null;
            default:
                return null;
        }
    }

    /**
     * Gets the previous status in the tender lifecycle. Returns the status that
     * comes before this one in the lifecycle order.
     *
     * @return The previous status in the lifecycle, or null if this is the
     * initial state (DRAFT)
     */
    public TenderStatus getPreviousStatus() {
        switch (this) {
            case OPEN:
                return DRAFT;
            case CLOSED:
                return OPEN;
            case UNDER_EVALUATION:
                return CLOSED;
            case EVALUATED:
                return UNDER_EVALUATION;
            case AWARDED:
                return EVALUATED;
            case DRAFT:
                return null;
            default:
                return null;
        }
    }

    /**
     * Checks if this status comes before the evaluation phase begins.
     * Pre-evaluation statuses are DRAFT, OPEN, and CLOSED.
     *
     * @return true if this status is before the evaluation phase, false
     * otherwise
     */
    public boolean isPreEvaluation() {
        return this == DRAFT || this == OPEN || this == CLOSED;
    }

    /**
     * Checks if this status is during or after evaluation. Evaluation phase
     * statuses are UNDER_EVALUATION and EVALUATED.
     *
     * @return true if this status is in the evaluation phase, false otherwise
     */
    public boolean isEvaluationPhase() {
        return this == UNDER_EVALUATION || this == EVALUATED;
    }

    /**
     * Checks if this status is a terminal state where no further transitions
     * are possible. Currently, only AWARDED is a terminal state.
     *
     * @return true if this is a terminal state, false otherwise
     */
    public boolean isTerminal() {
        return this == AWARDED;
    }

    /**
     * Checks if bids can be viewed in this status. Bids remain sealed until the
     * tender is CLOSED. Once closed, bids can be viewed during evaluation and
     * after awarding.
     *
     * @return true if bids can be viewed in this status, false otherwise
     */
    public boolean canViewBids() {
        return this == CLOSED || this == UNDER_EVALUATION
                || this == EVALUATED || this == AWARDED;
    }

    /**
     * Gets the CSS class name for styling status badges in the user interface.
     * Each status has a unique CSS class for visual differentiation.
     *
     * @return the CSS class name string (e.g., "status-open", "status-awarded")
     */
    public String getCssClass() {
        switch (this) {
            case DRAFT:
                return "status-draft";
            case OPEN:
                return "status-open";
            case CLOSED:
                return "status-closed";
            case UNDER_EVALUATION:
                return "status-evaluation";
            case EVALUATED:
                return "status-evaluated";
            case AWARDED:
                return "status-awarded";
            default:
                return "status-default";
        }
    }

    /**
     * Gets the emoji icon associated with this status for visual
     * representation. Each status has a unique icon:
     * <ul>
     * <li>DRAFT - 📝</li>
     * <li>OPEN - 🟢</li>
     * <li>CLOSED - 🔒</li>
     * <li>UNDER_EVALUATION - 🔍</li>
     * <li>EVALUATED - 📊</li>
     * <li>AWARDED - 🏆</li>
     * </ul>
     *
     * @return the emoji icon string for this status
     */
    public String getIcon() {
        switch (this) {
            case DRAFT:
                return "📝";
            case OPEN:
                return "🟢";
            case CLOSED:
                return "🔒";
            case UNDER_EVALUATION:
                return "🔍";
            case EVALUATED:
                return "📊";
            case AWARDED:
                return "🏆";
            default:
                return "📋";
        }
    }

    /**
     * Converts a string to its corresponding {@code TenderStatus} enum
     * constant. Matching is case-insensitive and checks both the enum name and
     * display name.
     *
     * @param status the string representation of the status (either enum name
     * or display name)
     * @return the matching {@code TenderStatus}, or null if the input is null
     * or empty
     * @throws IllegalArgumentException if no matching status is found for the
     * given string
     */
    public static TenderStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }

        for (TenderStatus ts : TenderStatus.values()) {
            if (ts.name().equalsIgnoreCase(status)
                    || ts.displayName.equalsIgnoreCase(status)) {
                return ts;
            }
        }

        throw new IllegalArgumentException("Invalid tender status: " + status);
    }

    /**
     * Returns the display name as the string representation of this status.
     *
     * @return the display name (e.g., "Open", "Awarded")
     */
    @Override
    public String toString() {
        return displayName;
    }
}
