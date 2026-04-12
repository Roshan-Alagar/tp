package seedu.RLAD.budget;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.exception.RLADException;
import seedu.RLAD.Ui;

import seedu.RLAD.storage.CsvStorageManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Manages all monthly budgets and handles progress calculations.
 */
public class BudgetManager {
    // Threshold constants
    private static final double THRESHOLD_80 = 0.80;
    private static final double THRESHOLD_90 = 0.90;
    private static final double THRESHOLD_100 = 1.00;

    private static final String SAVE_DIR = "data";
    private static final String SAVE_FILE = "data" + File.separator + "budgets.csv";
    private static final String CSV_HEADER = "Month,CategoryCode,Amount";
    private static final Logger logger = Logger.getLogger(BudgetManager.class.getName());

    /**
     * Maps user-friendly keywords to BudgetCategory display names.
     *
     * <p>This mapping allows users to enter simple keywords like "health"
     * instead of the full display name "Health & Insurance". It improves
     * usability by reducing the need to remember exact category names.
     *
     * <p>Fixes Issue #93: Multi-word budget categories showing $0.00 spent.
     *
     * @see #getSpentForCategory(YearMonth, BudgetCategory)
     */
    private static final Map<String, BudgetCategory> CATEGORY_KEYWORDS = new HashMap<>();

    static {
        // Initialize keyword mappings
        CATEGORY_KEYWORDS.put("food", BudgetCategory.FOOD);
        CATEGORY_KEYWORDS.put("transport", BudgetCategory.TRANSPORT);
        CATEGORY_KEYWORDS.put("transportation", BudgetCategory.TRANSPORT);
        CATEGORY_KEYWORDS.put("bus", BudgetCategory.TRANSPORT);
        CATEGORY_KEYWORDS.put("utilities", BudgetCategory.UTILITIES);
        CATEGORY_KEYWORDS.put("utility", BudgetCategory.UTILITIES);
        CATEGORY_KEYWORDS.put("electricity", BudgetCategory.UTILITIES);
        CATEGORY_KEYWORDS.put("water", BudgetCategory.UTILITIES);
        CATEGORY_KEYWORDS.put("housing", BudgetCategory.HOUSING);
        CATEGORY_KEYWORDS.put("rent", BudgetCategory.HOUSING);
        CATEGORY_KEYWORDS.put("health", BudgetCategory.HEALTH_INSURANCE);
        CATEGORY_KEYWORDS.put("insurance", BudgetCategory.HEALTH_INSURANCE);
        CATEGORY_KEYWORDS.put("medical", BudgetCategory.HEALTH_INSURANCE);
        CATEGORY_KEYWORDS.put("debt", BudgetCategory.DEBT_OBLIGATION);
        CATEGORY_KEYWORDS.put("loan", BudgetCategory.DEBT_OBLIGATION);
        CATEGORY_KEYWORDS.put("childcare", BudgetCategory.CHILD_CARE);
        CATEGORY_KEYWORDS.put("child", BudgetCategory.CHILD_CARE);
        CATEGORY_KEYWORDS.put("shopping", BudgetCategory.SHOPPING);
        CATEGORY_KEYWORDS.put("personal", BudgetCategory.SHOPPING);
        CATEGORY_KEYWORDS.put("gifts", BudgetCategory.GIFTS);
        CATEGORY_KEYWORDS.put("donations", BudgetCategory.GIFTS);
        CATEGORY_KEYWORDS.put("investments", BudgetCategory.INVESTMENTS);
        CATEGORY_KEYWORDS.put("investing", BudgetCategory.INVESTMENTS);
        CATEGORY_KEYWORDS.put("emergency", BudgetCategory.EMERGENCY_FUND);
        CATEGORY_KEYWORDS.put("savings", BudgetCategory.SAVINGS);
        CATEGORY_KEYWORDS.put("save", BudgetCategory.SAVINGS);
    }

    private final Map<String, Set<Integer>> notifiedThresholds = new HashMap<>();
    private final Map<YearMonth, MonthlyBudget> budgets;
    private final TransactionManager transactionManager;
    private Ui ui;

    public BudgetManager(TransactionManager transactionManager) {
        this.budgets = new HashMap<>();
        this.transactionManager = transactionManager;
    }

    public void setUi(Ui ui) {
        this.ui = ui;
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
        save();
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
        save();
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
        save();
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
     * Gets spent amount for a category, with keyword matching support.
     *
     * <p>This method matches transaction categories against budget categories
     * using both exact display name matching and keyword mapping. For example,
     * a transaction with category "health" will match the "Health & Insurance"
     * budget category.
     *
     * <p>Fixes Issue #93: Multi-word budget categories showing $0.00 spent.
     *
     * @param month The target month
     * @param category The budget category to check
     * @return The total spent amount for transactions matching this category
     */
    public double getSpentForCategory(YearMonth month, BudgetCategory category) {
        String displayName = category.getDisplayName();

        return transactionManager.getTransactions().stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(month))
                .filter(t -> "debit".equalsIgnoreCase(t.getType()))
                .filter(t -> {
                    String tCategory = t.getCategory();
                    if (tCategory == null) {
                        return false;
                    }

                    // Direct match with display name
                    if (displayName.equalsIgnoreCase(tCategory)) {
                        return true;
                    }

                    // Check keyword mappings (Issue #93 fix)
                    BudgetCategory mappedCategory = CATEGORY_KEYWORDS.get(tCategory.toLowerCase());
                    return mappedCategory != null && mappedCategory == category;
                })
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Checks if a transaction category matches a budget category.
     *
     * @param transactionCategory The transaction's category (may be null)
     * @param budgetCategory The budget category to match against
     * @param displayName The display name of the budget category
     * @return true if the transaction category matches the budget category
     */
    private boolean matchesCategory(String transactionCategory, BudgetCategory budgetCategory, String displayName) {
        if (transactionCategory == null) {
            return false;
        }

        // Direct match with display name
        if (displayName.equalsIgnoreCase(transactionCategory)) {
            return true;
        }

        // Check keyword mappings
        BudgetCategory mappedCategory = CATEGORY_KEYWORDS.get(transactionCategory.toLowerCase());
        return mappedCategory != null && mappedCategory == budgetCategory;
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

            //Check thresholds for this month
            checkBudgetThresholds(month);
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
        } else if (transaction.getType().equalsIgnoreCase("debit")) {
            // Reset notifications for this month to allow re-firing (Issue #92 fix)
            resetNotificationsForMonth(month);
            checkBudgetThresholds(month);
        }
    }

    public void onTransactionUpdated(Transaction oldTransaction, Transaction newTransaction) {
        YearMonth oldMonth = YearMonth.from(oldTransaction.getDate());
        YearMonth newMonth = YearMonth.from(newTransaction.getDate());

        // Reset notifications for affected months to allow re-firing (Issue #92 fix)
        resetNotificationsForMonth(oldMonth);
        if (!oldMonth.equals(newMonth)) {
            resetNotificationsForMonth(newMonth);
        }

        if (!oldMonth.equals(newMonth)) {
            updateTotalIncome(oldMonth);
            updateTotalIncome(newMonth);
        } else if (oldTransaction.getType().equalsIgnoreCase("credit") !=
                newTransaction.getType().equalsIgnoreCase("credit")) {
            updateTotalIncome(oldMonth);
        }

        checkBudgetThresholds(oldMonth);
        if (!oldMonth.equals(newMonth)) {
            checkBudgetThresholds(newMonth);
        }
    }

    /**
     * Resets all notification records for a specific month.
     *
     * <p>This method is called when transactions are deleted or updated,
     * allowing thresholds to be re-triggered if spending crosses them again.
     *
     * <p>Fixes Issue #92: Budget threshold warning does not re-fire.
     *
     * @param month The month to reset notifications for
     */
    private void resetNotificationsForMonth(YearMonth month) {
        String prefix = month.toString() + "_";
        notifiedThresholds.keySet().removeIf(key -> key.startsWith(prefix));
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

    /**
     * Resets all notification tracking when all transaction data is cleared.
     * Budget allocations are preserved; only the notified-thresholds map is cleared
     * because spending has been reset to zero.
     */
    public void onAllDataCleared() {
        notifiedThresholds.clear();
    }

    /**
     * Checks all budgets for a given month and sends notifications if thresholds are crossed.
     *
     * <p>This method checks spending against budget thresholds (80%, 90%, 100%)
     * and sends notifications when thresholds are crossed. It also handles
     * resetting notifications when spending drops below a threshold.
     *
     * <p>Fixes Issue #92: Budget threshold warning does not re-fire after a
     * transaction is deleted and re-added.
     *
     * @param month The month to check budgets for
     */
    public void checkBudgetThresholds(YearMonth month) {
        if (ui == null) {
            return;
        }

        MonthlyBudget budget = budgets.get(month);
        if (budget == null) {
            return;
        }

        for (Map.Entry<BudgetCategory, Double> entry : budget.getCategoryBudgets().entrySet()) {
            BudgetCategory category = entry.getKey();
            double allocated = entry.getValue();
            double spent = getSpentForCategory(month, category);

            if (allocated <= 0) {
                continue;
            }

            double percentage = (spent / allocated) * 100;
            String key = month.toString() + "_" + category.getCode();
            Set<Integer> notified = notifiedThresholds.getOrDefault(key, new HashSet<>());

            // Find the highest threshold crossed (80, 90, or 100)
            int highestThresholdCrossed = -1;
            for (int threshold : new int[]{100, 90, 80}) {  // Check from highest to lowest
                if (percentage >= threshold) {
                    highestThresholdCrossed = threshold;
                    break;  // Found the highest one
                }
            }

            if (highestThresholdCrossed != -1) {
                boolean wasNotified = notified.contains(highestThresholdCrossed);

                if (!wasNotified) {
                    // Only send ONE notification for the highest threshold crossed
                    sendNotification(category, month, percentage, spent, allocated);
                    notified.add(highestThresholdCrossed);
                }

                // Also mark lower thresholds as notified to prevent future notifications
                // when they would have been triggered by incremental spending
                for (int threshold : new int[]{80, 90, 100}) {
                    if (threshold <= highestThresholdCrossed && !notified.contains(threshold)) {
                        notified.add(threshold);
                    }
                }
            } else {
                // Below all thresholds - check if we need to reset any notifications
                boolean hadAnyNotification = false;
                for (int threshold : new int[]{80, 90, 100}) {
                    if (notified.contains(threshold)) {
                        hadAnyNotification = true;
                        break;
                    }
                }

                if (hadAnyNotification) {
                    // Spending dropped below all thresholds - clear all notifications for this category
                    notified.clear();
                }
            }

            if (notified.isEmpty()) {
                notifiedThresholds.remove(key);
            } else {
                notifiedThresholds.put(key, notified);
            }
        }
    }

    /**
     * Sends a notification for a budget threshold.
     *
     * @param category The budget category
     * @param month The month
     * @param percentage The percentage spent
     * @param spent Amount spent
     * @param budget Total budget amount
     */
    private void sendNotification(BudgetCategory category, YearMonth month,
                                  double percentage, double spent, double budget) {
        // Format month string (e.g., "March 2026")
        String monthStr = month.getMonth().toString() + " " + month.getYear();
        int percentInt = (int) Math.round(percentage);

        // Round to 2 decimal places
        double spentRounded = Math.round(spent * 100.0) / 100.0;
        double budgetRounded = Math.round(budget * 100.0) / 100.0;

        if (category == BudgetCategory.SAVINGS) {
            // Positive message for savings
            String message;
            if (percentInt >= 100) {
                message = String.format(
                        "🎉 AMAZING! You have reached or exceeded your savings goal for %s!\n" +
                                "   Saved: $%,.2f / $%,.2f. Outstanding dedication to your financial future!",
                        monthStr, spentRounded, budgetRounded
                );
            } else {
                message = String.format(
                        "🎉 GREAT JOB! You have reached %d%% of your savings goal for %s!\n" +
                                "   Saved: $%,.2f / $%,.2f. Keep up the great work!",
                        percentInt, monthStr, spentRounded, budgetRounded
                );
            }
            ui.showResult(message);
        } else {
            // Warning message for other categories
            String warningType = percentInt >= 100 ? "EXCEEDED" : "WARNING";
            String message = String.format(
                    "⚠️ %s: You have used %d%% of your %s budget for %s!\n" +
                            "   Spent: $%,.2f / $%,.2f. Consider reducing your spending.",
                    warningType, percentInt, category.getDisplayName(), monthStr, spentRounded, budgetRounded
            );
            ui.showResult(message);
        }
    }
    /**
     * Saves all budget entries to data/budget.csv.
     * Uses CSV format with header: Month,CategoryCode,Amount
     * Fields are escaped via CsvStorageManager so commas in future fields are handled correctly.
     */
    public void save() {
        try {
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE));
            writer.write(CSV_HEADER);
            writer.newLine();
            for (Map.Entry<YearMonth, MonthlyBudget> entry : budgets.entrySet()) {
                YearMonth month = entry.getKey();
                for (Map.Entry<BudgetCategory, Double> catEntry
                        : entry.getValue().getCategoryBudgets().entrySet()) {
                    String line = CsvStorageManager.escapeCsvField(month.toString())
                            + "," + CsvStorageManager.escapeCsvField(
                                    String.valueOf(catEntry.getKey().getCode()))
                            + "," + CsvStorageManager.escapeCsvField(
                                    String.format("%.2f", catEntry.getValue()));
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.close();
        } catch (IOException e) {
            logger.warning("Budget save failed: " + e.getMessage());
        }
    }

    /**
     * Loads budget entries from data/rlad_budget.csv on startup.
     * Uses CsvStorageManager.parseCsvLine() so quoted commas are handled correctly.
     * Silently skips the header and any malformed lines.
     */
    public void load() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = CsvStorageManager.parseCsvLine(line);
                if (parts.length != 3) {
                    logger.warning("Skipping invalid budget line: " + line);
                    continue;
                }
                try {
                    YearMonth month = YearMonth.parse(parts[0].trim());
                    int code = Integer.parseInt(parts[1].trim());
                    double amount = Double.parseDouble(parts[2].trim());
                    BudgetCategory category = BudgetCategory.fromCode(code);
                    getOrCreateBudget(month).setBudget(category, amount);
                } catch (Exception e) {
                    logger.warning("Skipping invalid budget line: " + line);
                }
            }
            reader.close();
        } catch (IOException e) {
            logger.warning("Budget load failed: " + e.getMessage());
        }
    }

    public String getYearlySummary(int year) {
        List<String> monthNames = Arrays.asList(
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December");

        StringBuilder sb = new StringBuilder();
        sb.append("=== YEARLY BUDGET SUMMARY FOR ").append(year).append(" ===\n\n");
        sb.append(String.format("%-10s | %-9s | %-9s | %-9s | %-5s | %s%n",
                "Month", "Budget", "Spent", "Remaining", "Used", "Trend"));
        sb.append("-----------+-----------+-----------+-----------+-------+------------------\n");

        double annualBudget = 0;
        double annualSpent = 0;
        int monthsWithData = 0;

        for (int m = 1; m <= 12; m++) {
            YearMonth month = YearMonth.of(year, m);
            double budget = getTotalBudgetForMonth(month);
            double spent = getTotalSpentForMonth(month);
            double remaining = budget - spent;
            int percentage = budget > 0 ? (int) Math.min(100, (spent / budget) * 100) : 0;
            String bar = createProgressBar(percentage, 14);
            sb.append(String.format("%-10s | $%,11.2f | $%,11.2f | $%,11.2f | %3d%%  | %s %d%%%n",
                    monthNames.get(m - 1), budget, spent, remaining, percentage, bar, percentage));
            annualBudget += budget;
            annualSpent += spent;
            if (budget > 0 || spent > 0) {
                monthsWithData++;
            }
        }

        sb.append("\n=== PER CATEGORY BREAKDOWN ===\n\n");
        sb.append(String.format("%-30s | %-11s | %-11s | %-10s | %s%n",
                "Category", "Budget", "Spent", "Difference", "Status"));
        sb.append("--------------------------------+-------------+-------------+------------+-----------\n");

        for (BudgetCategory category : BudgetCategory.values()) {
            double catBudget = getYearlyBudgetForCategory(year, category);
            double catSpent = getYearlySpentForCategory(year, category);
            if (catBudget == 0 && catSpent == 0) {
                continue;
            }
            double diff = catBudget - catSpent;
            String status = diff >= 0 ? "✓ Under" : "⚠ Over";
            String diffStr = diff >= 0
                    ? String.format("+$%,.2f", diff)
                    : String.format("-$%,.2f", Math.abs(diff));
            sb.append(String.format("%-30s | $%,13.2f | $%,13.2f | %-10s | %s%n",
                    category.getDisplayName(), catBudget, catSpent, diffStr, status));
        }

        double netBalance = annualBudget - annualSpent;
        double avgMonthly = monthsWithData > 0 ? annualSpent / monthsWithData : 0;
        sb.append("\n=== ANNUAL TOTALS ===\n\n");
        sb.append(String.format("Total Budget:     $%,.2f%n", annualBudget));
        sb.append(String.format("Total Spent:      $%,.2f%n", annualSpent));
        sb.append(String.format("Net Balance:      %s$%,.2f%n",
                netBalance >= 0 ? "+" : "-", Math.abs(netBalance)));
        sb.append(String.format("Average Monthly:  $%,.2f%n", avgMonthly));
        return sb.toString();
    }

    private double getTotalBudgetForMonth(YearMonth month) {
        return getBudget(month)
                .map(b -> b.getTotalAllocatedBudget() + b.getDisposableIncome())
                .orElse(0.0);
    }

    private double getTotalSpentForMonth(YearMonth month) {
        return transactionManager.getTransactions().stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(month))
                .filter(t -> "debit".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private double getYearlyBudgetForCategory(int year, BudgetCategory category) {
        double total = 0;
        for (int m = 1; m <= 12; m++) {
            YearMonth month = YearMonth.of(year, m);
            total += getBudget(month)
                    .map(b -> b.getBudgetForCategory(category))
                    .orElse(0.0);
        }
        return total;
    }

    private double getYearlySpentForCategory(int year, BudgetCategory category) {
        double total = 0;
        for (int m = 1; m <= 12; m++) {
            YearMonth month = YearMonth.of(year, m);
            total += getSpentForCategory(month, category);
        }
        return total;
    }

    private String createProgressBar(int percentage, int length) {
        int filled = (int) Math.round((percentage / 100.0) * length);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < length; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        return bar.toString();
    }
}
