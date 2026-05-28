package model.enums;

/**
 * Represents the procurement categories for government tenders. Each category
 * has a display name and description for use in the user interface.
 *
 * @author YourName
 * @version 1.0
 */
public enum TenderCategory {

    /**
     * Construction category covering building and structural works
     */
    CONSTRUCTION("Construction", "Building and structural works"),
    /**
     * Roads and infrastructure category covering road construction and
     * maintenance
     */
    ROADS("Roads & Infrastructure", "Road construction and maintenance"),
    /**
     * Electrical works category covering electrical installations and repairs
     */
    ELECTRICAL("Electrical Works", "Electrical installations and repairs"),
    /**
     * Plumbing and sanitation category covering water and sanitation systems
     */
    PLUMBING("Plumbing & Sanitation", "Water and sanitation systems"),
    /**
     * General services category covering consulting and other professional
     * services
     */
    GENERAL_SERVICES("General Services", "Consulting and other services");

    /**
     * The human-readable display name for the category
     */
    private final String displayName;
    /**
     * A brief description of what the category encompasses
     */
    private final String description;

    /**
     * Constructs a {@code TenderCategory} enum constant with the specified
     * display name and description.
     *
     * @param displayName the human-readable display name for the category
     * @param description a brief description of the category's scope
     */
    TenderCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Returns the human-readable display name for this category.
     *
     * @return the display name (e.g., "Construction", "Roads & Infrastructure")
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the description of what this category encompasses.
     *
     * @return the category description (e.g., "Building and structural works")
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the emoji icon associated with this category for visual
     * representation. Each category has a unique icon:
     * <ul>
     * <li>CONSTRUCTION - 🏗️</li>
     * <li>ROADS - 🛣️</li>
     * <li>ELECTRICAL - ⚡</li>
     * <li>PLUMBING - 🚰</li>
     * <li>GENERAL_SERVICES - 📋</li>
     * </ul>
     *
     * @return the emoji icon string for this category
     */
    public String getIcon() {
        switch (this) {
            case CONSTRUCTION:
                return "🏗️";
            case ROADS:
                return "🛣️";
            case ELECTRICAL:
                return "⚡";
            case PLUMBING:
                return "🚰";
            case GENERAL_SERVICES:
                return "📋";
            default:
                return "📦";
        }
    }

    /**
     * Converts a string to its corresponding {@code TenderCategory} enum
     * constant. Matching is case-insensitive and checks both the enum name and
     * display name.
     *
     * @param category the string representation of the category (either enum
     * name or display name)
     * @return the matching {@code TenderCategory}, or null if the input is null
     * or empty
     * @throws IllegalArgumentException if no matching category is found for the
     * given string
     */
    public static TenderCategory fromString(String category) {
        if (category == null || category.trim().isEmpty()) {
            return null;
        }

        for (TenderCategory tc : TenderCategory.values()) {
            if (tc.name().equalsIgnoreCase(category)
                    || tc.displayName.equalsIgnoreCase(category)) {
                return tc;
            }
        }

        throw new IllegalArgumentException("Invalid tender category: " + category);
    }

    /**
     * Returns the display name as the string representation of this category.
     *
     * @return the display name (e.g., "Construction")
     */
    @Override
    public String toString() {
        return displayName;
    }
}
