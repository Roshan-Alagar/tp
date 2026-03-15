package seedu.RLAD.budget;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.exception.RLADException;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Manages all monthly budgets and handles progress calculations.
 */
public class BudgetManager {
    private final Map<YearMonth, MonthlyBudget> budgets;
    private final TransactionManager transactionManager;

    public BudgetManager(TransactionManager transactionManager) {
        this.budgets = new HashMap<>();
        this.transactionManager = transactionManager;
    }

    /**
     * Gets or creates a monthly budget.
     * @param month The month to retrieve
     * @return The MonthlyBudget object
     */
    public MonthlyBudget getOrCreateBudget(YearMonth month) {
        return budgets.computeIfAbsent(month, MonthlyBudget::new);
    }

    /**
     * Gets a monthly budget if it exists.
     * @param month The month to retrieve
     * @return Optional containing the budget if exists
     */
    public Optional<MonthlyBudget> getBudget(YearMonth month) {
        return Optional.ofNullable(budgets.get(month));
    }

    /**
     * Sets a budget for a category in a specific month.
     * @param month The target month
     * @param categoryCode The category code (1-12)
     * @param amount The budget amount
     * @throws RLADException if validation fails
     */
    public void setBudget(YearMonth month, int categoryCode, double amount) throws RLADException {
        BudgetCategory category = BudgetCategory.fromCode(categoryCode);
        MonthlyBudget budget = getOrCreateBudget(month);
        budget.setBudget(category, amount);

        // Update total income for the month
        updateTotalIncome(month);
    }

    /**
     * Edits an existing budget.
     * @param month The target month
     * @param categoryCode The category code (1-12)
     * @param amount The new budget amount
     * @throws RLADException if validation fails
     */
    public void editBudget(YearMonth month, int categoryCode, double amount) throws RLADException {
        BudgetCategory category = BudgetCategory.fromCode(categoryCode);
        MonthlyBudget budget = getBudget(month)
                .orElseThrow(() -> new RLADException("No budget found for " + month));
        budget.editBudget(category, amount);
    }

    /**
     * Deletes a budget category.
     * @param month The target month
     * @param categoryCode The category code (1-12)
     * @throws RLADException if validation fails
     */
    public void deleteBudget(YearMonth month, int categoryCode) throws RLADException {
        BudgetCategory category = BudgetCategory.fromCode(categoryCode);
        MonthlyBudget budget = getBudget(month)
                .orElseThrow(() -> new RLADException("No budget found for " + month));
        budget.deleteBudget(category);

        // Remove the month entirely if no budgets left
        if (budget.getBudgetedCategoryCount() == 0) {
            budgets.remove(month);
        }
    }

    /**
     * Updates total income for a month based on transactions.
     * @param month The month to update
     */
    private void updateTotalIncome(YearMonth month) {
        double totalIncome = transactionManager.getTransactions().stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(month))
                .filter(t -> "credit".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        MonthlyBudget budget = getOrCreateBudget(month);
        budget.setTotalIncome(totalIncome);
    }

    /**
     * Calculates spent amount for a category in a specific month.
     * @param month The target month
     * @param category The budget category
     * @return The total spent amount
     */
    public double getSpentForCategory(YearMonth month, BudgetCategory category) {
        return transactionManager.getTransactions().stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(month))
                .filter(t -> "debit".equalsIgnoreCase(t.getType()))
                .filter(t -> category.getDisplayName().equalsIgnoreCase(t.getCategory()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Add this method to update budgets when a transaction is added
    public void onTransactionAdded(Transaction transaction) {
        YearMonth month = YearMonth.from(transaction.getDate());

        if (transaction.getType().equalsIgnoreCase("credit")) {
            // This is income - update total income for the month
            updateTotalIncome(month);
        } else if (transaction.getType().equalsIgnoreCase("debit")) {
            // This is expense - find matching budget category and update
            updateCategorySpending(month, transaction);
        }
    }

    // Update category spending when a debit transaction is added
    private void updateCategorySpending(YearMonth month, Transaction transaction) {
        String transactionCategory = transaction.getCategory();
        if (transactionCategory == null) {
            return;
        }

        // Find matching budget category
        for (BudgetCategory category : BudgetCategory.values()) {
            if (category.getDisplayName().equalsIgnoreCase(transactionCategory)) {
                // Found a matching category - budget progress will be recalculated on next view
                // No need to store anything, as getProgress() calculates on the fly
                break;
            }
        }
    }

    // Also add this method to handle transaction deletion (optional)
    public void onTransactionDeleted(Transaction transaction) {
        YearMonth month = YearMonth.from(transaction.getDate());

        if (transaction.getType().equalsIgnoreCase("credit")) {
            updateTotalIncome(month);
        }
        // For debits, the spent amount will automatically update on next view
    }

    /**
     * Gets budget progress for a category.
     * @param month The target month
     * @param category The budget category
     * @return BudgetProgress object with details
     */
    public BudgetProgress getProgress(YearMonth month, BudgetCategory category) {
        Optional<MonthlyBudget> budgetOpt = getBudget(month);
        if (budgetOpt.isEmpty()) {
            return new BudgetProgress(category, 0, 0, 0, 0);
        }

        MonthlyBudget budget = budgetOpt.get();
        double allocated = budget.getBudgetForCategory(category);
        double spent = getSpentForCategory(month, category);
        double remaining = allocated - spent;
        int percentage = allocated > 0 ? (int) ((spent / allocated) * 100) : 0;

        return new BudgetProgress(category, allocated, spent, remaining, percentage);
    }

    /**
     * Gets the disposable income progress for a month.
     * @param month The target month
     * @return BudgetProgress for disposable income
     */
    public BudgetProgress getDisposableIncomeProgress(YearMonth month) {
        Optional<MonthlyBudget> budgetOpt = getBudget(month);
        if (budgetOpt.isEmpty()) {
            return new BudgetProgress(null, 0, 0, 0, 0);
        }

        MonthlyBudget budget = budgetOpt.get();
        double disposableIncome = budget.getDisposableIncome();
        double spent = transactionManager.getTransactions().stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(month))
                .filter(t -> "debit".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum() - getTotalSpentOnBudgetedCategories(month);

        double remaining = disposableIncome - spent;
        int percentage = disposableIncome > 0 ? (int) ((spent / disposableIncome) * 100) : 0;

        return new BudgetProgress(null, disposableIncome, spent, remaining, percentage);
    }

    /**
     * Gets total spent on budgeted categories for a month.
     * @param month The target month
     * @return Total spent amount
     */
    private double getTotalSpentOnBudgetedCategories(YearMonth month) {
        return getBudget(month)
                .map(budget -> budget.getCategoryBudgets().keySet().stream()
                        .mapToDouble(cat -> getSpentForCategory(month, cat))
                        .sum())
                .orElse(0.0);
    }

    /**
     * Gets all months that have budgets set.
     * @return Set of YearMonth with budgets
     */
    public Set<YearMonth> getMonthsWithBudgets() {
        return budgets.keySet();
    }

    /**
     * Inner class to hold budget progress data.
     */
    public static class BudgetProgress {
        private final BudgetCategory category;
        private final double allocated;
        private final double spent;
        private final double remaining;
        private final int percentage;

        public BudgetProgress(BudgetCategory category, double allocated,
                              double spent, double remaining, int percentage) {
            this.category = category;
            this.allocated = allocated;
            this.spent = spent;
            this.remaining = remaining;
            this.percentage = percentage;
        }

        public BudgetCategory getCategory() {
            return category;
        }

        public double getAllocated() {
            return allocated;
        }

        public double getSpent() {
            return spent;
        }

        public double getRemaining() {
            return remaining;
        }

        public int getPercentage() {
            return percentage;
        }

        public String getProgressBar(int length) {
            int filled = (int) Math.round((percentage / 100.0) * length);
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < length; i++) {
                bar.append(i < filled ? "█" : "░");
            }
            return bar.toString();
        }
    }
}
