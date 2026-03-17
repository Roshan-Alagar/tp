package seedu.RLAD.budget;

import seedu.RLAD.exception.RLADException;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a monthly budget with allocations for different categories.
 */
public class MonthlyBudget {
    private final YearMonth month;
    private final Map<BudgetCategory, Double> categoryBudgets;
    private double totalIncome;

    public MonthlyBudget(YearMonth month) {
        this.month = month;
        this.categoryBudgets = new HashMap<>();
        this.totalIncome = 0.0;
    }

    public YearMonth getMonth() {
        return month;
    }

    public Map<BudgetCategory, Double> getCategoryBudgets() {
        return new HashMap<>(categoryBudgets);
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    /**
     * Sets a budget for a specific category.
     * @param category The budget category
     * @param amount The budget amount
     * @throws RLADException if amount is invalid
     */
    public void setBudget(BudgetCategory category, double amount) throws RLADException {
        if (amount <= 0) {
            throw new RLADException("Budget amount must be greater than 0");
        }
        categoryBudgets.put(category, amount);
    }

    /**
     * Edits an existing budget for a category.
     * @param category The budget category
     * @param amount The new budget amount
     * @throws RLADException if category doesn't exist or amount is invalid
     */
    public void editBudget(BudgetCategory category, double amount) throws RLADException {
        if (!categoryBudgets.containsKey(category)) {
            throw new RLADException("No budget set for category: " + category.getDisplayName());
        }
        if (amount <= 0) {
            throw new RLADException("Budget amount must be greater than 0");
        }
        categoryBudgets.put(category, amount);
    }

    /**
     * Deletes a budget for a category.
     * @param category The budget category to delete
     * @throws RLADException if category doesn't exist
     */
    public void deleteBudget(BudgetCategory category) throws RLADException {
        if (!categoryBudgets.containsKey(category)) {
            throw new RLADException("No budget set for category: " + category.getDisplayName());
        }
        categoryBudgets.remove(category);
    }

    /**
     * Gets the budget amount for a specific category.
     * @param category The budget category
     * @return The budget amount, or 0 if not set
     */
    public double getBudgetForCategory(BudgetCategory category) {
        return categoryBudgets.getOrDefault(category, 0.0);
    }

    /**
     * Calculates the total allocated budget across all categories.
     * @return The sum of all category budgets
     */
    public double getTotalAllocatedBudget() {
        return categoryBudgets.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    /**
     * Calculates disposable income (total income - allocated budgets).
     * @return The disposable income amount
     */
    public double getDisposableIncome() {
        return totalIncome - getTotalAllocatedBudget();
    }

    /**
     * Checks if a budget is set for a category.
     * @param category The category to check
     * @return true if budget exists
     */
    public boolean hasBudget(BudgetCategory category) {
        return categoryBudgets.containsKey(category);
    }

    /**
     * Gets the number of categories with budgets set.
     * @return The count of budgeted categories
     */
    public int getBudgetedCategoryCount() {
        return categoryBudgets.size();
    }
}
