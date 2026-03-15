package seedu.RLAD.budget;

import seedu.RLAD.exception.RLADException;

/**
 * Represents the predefined budget categories.
 */
public enum BudgetCategory {
    FOOD(1, "Food"),
    TRANSPORT(2, "Transport"),
    UTILITIES(3, "Utilities"),
    HOUSING(4, "Housing"),
    HEALTH_INSURANCE(5, "Health & Insurance"),
    DEBT_OBLIGATION(6, "Debt & Financial Obligation"),
    CHILD_CARE(7, "Child & Financial Dependent Care"),
    SHOPPING(8, "Shopping & Personal Care"),
    GIFTS(9, "Gifts & Donations"),
    INVESTMENTS(10, "Investments"),
    EMERGENCY_FUND(11, "Emergency Fund"),
    SAVINGS(12, "Savings");

    private final int code;
    private final String displayName;

    BudgetCategory(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BudgetCategory fromCode(int code) throws RLADException {
        for (BudgetCategory category : values()) {
            if (category.code == code) {
                return category;
            }
        }
        throw new RLADException("Invalid category code: " + code + ". Must be between 1-12.");
    }

    public static BudgetCategory fromDisplayName(String displayName) {
        for (BudgetCategory category : values()) {
            if (category.displayName.equalsIgnoreCase(displayName)) {
                return category;
            }
        }
        return null;
    }
}
