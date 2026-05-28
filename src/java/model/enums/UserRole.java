package model.enums;

/**
 * Enum representing the three distinct user roles in the ProcureGov system.
 * Each role has specific permissions and access levels as defined in the
 * Ministry of Public Works tender management workflow.
 * 
 * Roles:
 * - SUPPLIER: Registered companies/individuals submitting bids
 * - PROCUREMENT_OFFICER: Ministry officials managing tender lifecycle
 * - EVALUATION_COMMITTEE: Appointed officials scoring bids
 * 
 * @author kolisang
 * @version 1.0
 */
public enum UserRole {
    
    SUPPLIER("Supplier", "Registered companies or individuals submitting bids for published tenders"),
    
    PROCUREMENT_OFFICER("Procurement Officer", "Ministry official who manages the tender process from creation to award"),
    
    EVALUATION_COMMITTEE("Evaluation Committee Member", "Ministry official appointed to score bids");
    
    private final String displayName;
    private final String description;
    
    /**
     * Constructor for UserRole enum.
     * 
     * @param displayName Human-readable name for display
     * @param description Brief description of the role's responsibilities
     */
    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the human-readable display name of the role.
     * 
     * @return Display name (e.g., "Procurement Officer")
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of the role's responsibilities.
     * 
     * @return Role description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Converts a string to its corresponding UserRole enum value.
     * Matches against both enum name and display name (case-insensitive).
     * 
     * @param role The string representation of the role
     * @return UserRole enum value
     * @throws IllegalArgumentException if the string doesn't match any role
     */
    public static UserRole fromString(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        
        String normalizedRole = role.trim();
        
        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equalsIgnoreCase(normalizedRole) || 
                userRole.displayName.equalsIgnoreCase(normalizedRole)) {
                return userRole;
            }
        }
        
        throw new IllegalArgumentException("Invalid role: " + role + 
            ". Valid roles are: SUPPLIER, PROCUREMENT_OFFICER, EVALUATION_COMMITTEE");
    }
    
    /**
     * Safely converts a string to UserRole, returning null if invalid.
     * Use this when you don't want to throw an exception.
     * 
     * @param role The string representation of the role
     * @return UserRole enum value or null if invalid
     */
    public static UserRole fromStringOrNull(String role) {
        try {
            return fromString(role);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Checks if this role can create tenders.
     * Only PROCUREMENT_OFFICER can create tenders.
     * 
     * @return true if role can create tenders
     */
    public boolean canCreateTenders() {
        return this == PROCUREMENT_OFFICER;
    }
    
    /**
     * Checks if this role can submit bids.
     * Only SUPPLIER can submit bids.
     * 
     * @return true if role can submit bids
     */
    public boolean canSubmitBids() {
        return this == SUPPLIER;
    }
    
    /**
     * Checks if this role can evaluate bids.
     * PROCUREMENT_OFFICER and EVALUATION_COMMITTEE can evaluate.
     * 
     * @return true if role can evaluate bids
     */
    public boolean canEvaluateBids() {
        return this == PROCUREMENT_OFFICER || this == EVALUATION_COMMITTEE;
    }
    
    /**
     * Checks if this role can award tenders.
     * Only PROCUREMENT_OFFICER can award tenders.
     * 
     * @return true if role can award tenders
     */
    public boolean canAwardTenders() {
        return this == PROCUREMENT_OFFICER;
    }
    
    /**
     * Checks if this role can view all bids.
     * Only PROCUREMENT_OFFICER and EVALUATION_COMMITTEE can view all bids.
     * Suppliers can only view their own bids.
     * 
     * @return true if role can view all bids
     */
    public boolean canViewAllBids() {
        return this == PROCUREMENT_OFFICER || this == EVALUATION_COMMITTEE;
    }
    
    /**
     * Checks if this role can manage users.
     * Only PROCUREMENT_OFFICER can manage users.
     * 
     * @return true if role can manage users
     */
    public boolean canManageUsers() {
        return this == PROCUREMENT_OFFICER;
    }
    
    /**
     * Checks if this role can view evaluation scores before completion.
     * Evaluation committee members cannot see other evaluators' scores
     * until they have submitted their own.
     * 
     * @return true if role can view all scores at any time
     */
    public boolean canViewAllScoresAnytime() {
        return this == PROCUREMENT_OFFICER;
    }
    
    /**
     * Gets the dashboard redirect URL for this role.
     * 
     * @return Dashboard URL path
     */
    public String getDashboardUrl() {
        switch (this) {
            case SUPPLIER:
                return "/supplier/dashboard";
            case PROCUREMENT_OFFICER:
                return "/officer/dashboard";
            case EVALUATION_COMMITTEE:
                return "/evaluator/dashboard";
            default:
                return "/login.jsp";
        }
    }
    
    /**
     * Checks if this is a ministry staff role.
     * 
     * @return true for PROCUREMENT_OFFICER and EVALUATION_COMMITTEE
     */
    public boolean isMinistryStaff() {
        return this == PROCUREMENT_OFFICER || this == EVALUATION_COMMITTEE;
    }
    
    /**
     * Checks if this role requires registration number.
     * Only SUPPLIER has a registration number.
     * 
     * @return true if role requires registration number
     */
    public boolean requiresRegistrationNumber() {
        return this == SUPPLIER;
    }
    
    /**
     * Gets the default landing page title for this role.
     * 
     * @return Dashboard title
     */
    public String getDashboardTitle() {
        switch (this) {
            case SUPPLIER:
                return "Supplier Dashboard";
            case PROCUREMENT_OFFICER:
                return "Procurement Officer Dashboard";
            case EVALUATION_COMMITTEE:
                return "Evaluation Committee Dashboard";
            default:
                return "Dashboard";
        }
    }
    
    /**
     * Gets a CSS class name for styling based on role.
     * 
     * @return CSS class name
     */
    public String getCssClass() {
        switch (this) {
            case SUPPLIER:
                return "role-supplier";
            case PROCUREMENT_OFFICER:
                return "role-officer";
            case EVALUATION_COMMITTEE:
                return "role-evaluator";
            default:
                return "role-default";
        }
    }
    
    /**
     * Gets a badge color for the role (for UI display).
     * 
     * @return Hex color code
     */
    public String getBadgeColor() {
        switch (this) {
            case SUPPLIER:
                return "#28a745";  // Green
            case PROCUREMENT_OFFICER:
                return "#00529B";  // Lesotho Blue
            case EVALUATION_COMMITTEE:
                return "#ffc107";  // Yellow/Gold
            default:
                return "#6c757d";  // Gray
        }
    }
    
    /**
     * Gets an emoji icon representing the role.
     * 
     * @return Emoji character
     */
    public String getIcon() {
        switch (this) {
            case SUPPLIER:
                return "🏢";
            case PROCUREMENT_OFFICER:
                return "📋";
            case EVALUATION_COMMITTEE:
                return "⭐";
            default:
                return "👤";
        }
    }
    
    /**
     * Returns a formatted string with icon and display name.
     * 
     * @return Formatted string (e.g., "🏢 Supplier")
     */
    public String getDisplayWithIcon() {
        return getIcon() + " " + displayName;
    }
}