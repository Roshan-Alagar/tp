package seedu.RLAD.budget;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.command.Command;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Command to manage monthly budgets.
 *
 * <p>Format: budget set|view|edit|delete|yearly [parameters]
 *
 * @version 2.1
 */
public class BudgetCommand extends Command {
    private static final int PROGRESS_BAR_LENGTH = 20;

    private enum SubCommand {
        SET, VIEW, EDIT, DELETE, YEARLY
    }

    public BudgetCommand(String rawArgs) {
        super(rawArgs);
    }

    /**
     * Required by Command abstract class - calls the version with BudgetManager.
     */
    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        throw new RLADException("BudgetCommand requires BudgetManager. Internal error - this shouldn't happen.");
    }

    /**
     * Executes budget command with BudgetManager access.
     */
    public void execute(TransactionManager transactions, Ui ui, BudgetManager budgetManager) throws RLADException {
        if (!hasValidArgs()) {
            printUsage(ui);
            return;
        }

        String[] parts = rawArgs.trim().split("\\s+");
        String subCommand = parts[0].toLowerCase();

        switch (subCommand) {
        case "set":
            handleSet(parts, budgetManager, ui);
            break;

        case "view":
            handleView(parts, budgetManager, ui);
            break;

        case "edit":
            handleEdit(parts, budgetManager, ui);
            break;

        case "delete":
            handleDelete(parts, budgetManager, ui);
            break;

        case "yearly":
            handleYearly(parts, budgetManager, ui);
            break;

        default:
            throw new RLADException("Unknown budget command: " + subCommand +
                    ". Use: set, view, edit, delete, yearly");
        }
    }

    // ========== FIXED handleSet METHOD (Issue #100) ==========
    /**
     * Handles the "budget set" subcommand.
     *
     * <p>Sets a new budget for a category in a specific month.
     * If a budget already exists for this category and month, the command
     * will be rejected and the user will be instructed to use 'budget edit'.
     *
     * <p>Fixes Issue #100: budget set silently overwrites existing budgets.
     *
     * @param parts The command parts (set, month, category_code, amount)
     * @param budgetManager The budget manager instance
     * @param ui The UI component for displaying results
     * @throws RLADException If arguments are invalid or budget already exists
     */
    private void handleSet(String[] parts, BudgetManager budgetManager, Ui ui) throws RLADException {
        if (parts.length < 4) {
            throw new RLADException("Usage: budget set <YYYY-MM> <category_code> <amount>\n" +
                    "Type 'help budget' for details.");
        }

        YearMonth month = parseMonth(parts[1]);
        int categoryCode = parseCategoryCode(parts[2]);
        double amount = parseAmount(parts[3]);
        BudgetCategory category = BudgetCategory.fromCode(categoryCode);

        // Check if budget already exists (Issue #100 fix)
        Optional<MonthlyBudget> existingBudget = budgetManager.getBudget(month);
        if (existingBudget.isPresent() && existingBudget.get().hasBudget(category)) {
            double existingAmount = existingBudget.get().getBudgetForCategory(category);
            throw new RLADException(String.format(
                    "A budget already exists for %s - Category %d ($%.2f).\n" +
                            "Use 'budget edit' to modify an existing budget.\n" +
                            "Type 'help budget' for more details.",
                    month, categoryCode, existingAmount));
        }

        budgetManager.setBudget(month, categoryCode, amount);
        ui.showResult(String.format("✅ Budget set: %s - Category %d: $%,.2f", month, categoryCode, amount));
    }

    // ========== handleView METHOD ==========
    private void handleView(String[] parts, BudgetManager budgetManager, Ui ui) throws RLADException {
        if (parts.length >= 2) {
            YearMonth month = parseMonth(parts[1]);
            displayMonthlyBudget(budgetManager, month, ui);
        } else {
            displayAllBudgets(budgetManager, ui);
        }
    }

    // ========== handleEdit METHOD ==========
    private void handleEdit(String[] parts, BudgetManager budgetManager, Ui ui) throws RLADException {
        if (parts.length < 4) {
            throw new RLADException("Usage: budget edit <YYYY-MM> <category_code> <amount>\n" +
                    "Type 'help budget' for details.");
        }

        YearMonth month = parseMonth(parts[1]);
        int categoryCode = parseCategoryCode(parts[2]);
        double amount = parseAmount(parts[3]);

        budgetManager.editBudget(month, categoryCode, amount);
        ui.showResult(String.format("✅ Budget updated: %s - Category %d: $%,.2f", month, categoryCode, amount));
    }

    // ========== handleDelete METHOD ==========
    private void handleDelete(String[] parts, BudgetManager budgetManager, Ui ui) throws RLADException {
        if (parts.length < 3) {
            throw new RLADException("Usage: budget delete <YYYY-MM> <category_code>\nType 'help budget' for details.");
        }

        YearMonth month = parseMonth(parts[1]);
        int categoryCode = parseCategoryCode(parts[2]);

        budgetManager.deleteBudget(month, categoryCode);
        ui.showResult(String.format("✅ Budget deleted: %s - Category %d", month, categoryCode));
    }

    // ========== handleYearly METHOD ==========
    private void handleYearly(String[] parts, BudgetManager budgetManager, Ui ui) throws RLADException {
        int year;
        if (parts.length >= 2) {
            year = parseYear(parts[1]);
        } else {
            year = LocalDate.now().getYear();
        }
        String summary = budgetManager.getYearlySummary(year);
        ui.showResult(summary);
    }

    // ========== FIXED displayMonthlyBudget METHOD (Issue #111) ==========
    /**
     * Displays a monthly budget summary in a formatted table.
     *
     * <p>The summary includes:
     * <ul>
     *   <li>Each budgeted category with budget, spent, remaining, and progress</li>
     *   <li>Disposable income (total income - allocated budgets)</li>
     *   <li>TOTAL row showing sum of all budgeted categories (excluding disposable income)</li>
     * </ul>
     *
     * <p>Fixes Issue #111: TOTAL row displays monthly income instead of total budget allocation.
     *
     * @param budgetManager The budget manager instance
     * @param month The month to display
     * @param ui The UI component for displaying results
     */
    private void displayMonthlyBudget(BudgetManager budgetManager, YearMonth month, Ui ui) {
        Optional<MonthlyBudget> budgetOpt = budgetManager.getBudget(month);

        if (budgetOpt.isEmpty()) {
            ui.showResult("No budget set for " + month);
            return;
        }

        MonthlyBudget budget = budgetOpt.get();
        List<String> output = new ArrayList<>();

        output.add("=== BUDGET SUMMARY FOR " + month.toString().toUpperCase() + " ===");
        output.add(String.format("%-25s | %10s | %10s | %10s | %s",
                "Category", "Budget", "Spent", "Remaining", "Progress"));
        output.add("---------------------------+------------+------------+------------+----------------------");

        double totalAllocated = 0;
        double totalSpent = 0;

        List<BudgetCategory> categories = Arrays.asList(BudgetCategory.values());
        categories.sort(Comparator.comparingInt(BudgetCategory::getCode));

        for (BudgetCategory category : categories) {
            if (budget.hasBudget(category)) {
                BudgetManager.BudgetProgress progress = budgetManager.getProgress(month, category);
                String progressBar = progress.getProgressBar(PROGRESS_BAR_LENGTH);

                output.add(String.format("[%d] %-21s | $%,12.2f | $%,12.2f | $%,12.2f | %s %3d%%",
                        category.getCode(),
                        category.getDisplayName(),
                        progress.getAllocated(),
                        progress.getSpent(),
                        progress.getRemaining(),
                        progressBar,
                        progress.getPercentage()
                ));

                totalAllocated += progress.getAllocated();
                totalSpent += progress.getSpent();
            }
        }

        output.add("---------------------------+------------+------------+------------+----------------------");

        // Disposable Income row
        BudgetManager.BudgetProgress disposableProgress = budgetManager.getDisposableIncomeProgress(month);
        String disposableBar = disposableProgress.getProgressBar(PROGRESS_BAR_LENGTH);

        output.add(String.format("%-25s | $%,12.2f | $%,12.2f | $%,12.2f | %s %3d%%",
                "Disposable Income",
                disposableProgress.getAllocated(),
                disposableProgress.getSpent(),
                disposableProgress.getRemaining(),
                disposableBar,
                disposableProgress.getPercentage()
        ));

        // TOTAL row - Fix Issue #111: Only sum category budgets, NOT including Disposable Income
        double totalRemaining = totalAllocated - totalSpent;
        int totalPercentage = totalAllocated > 0 ? (int) ((totalSpent / totalAllocated) * 100) : 0;
        String totalBar = createProgressBar(totalPercentage, PROGRESS_BAR_LENGTH);

        output.add(String.format("%-25s | $%,12.2f | $%,12.2f | $%,12.2f | %s %3d%%",
                "TOTAL (Budgeted Categories)",
                totalAllocated,
                totalSpent,
                totalRemaining,
                totalBar,
                totalPercentage
        ));

        output.forEach(ui::showResult);
    }

    // ========== displayAllBudgets METHOD ==========
    private void displayAllBudgets(BudgetManager budgetManager, Ui ui) {
        Set<YearMonth> months = budgetManager.getMonthsWithBudgets();

        if (months.isEmpty()) {
            ui.showResult("No budgets set for any month.");
            return;
        }

        List<YearMonth> sortedMonths = new ArrayList<>(months);
        sortedMonths.sort(Comparator.naturalOrder());

        List<String> output = new ArrayList<>();
        output.add("=== ALL MONTHLY BUDGETS ===");
        output.add(String.format("%-10s | %-22s | %10s | %10s | %10s",
                "Month", "Category", "Budget", "Spent", "Remaining"));
        output.add("------------+------------------------+------------+------------+------------");

        for (YearMonth month : sortedMonths) {
            MonthlyBudget budget = budgetManager.getBudget(month).orElse(null);
            if (budget == null) {
                continue;
            }

            boolean firstRow = true;

            for (Map.Entry<BudgetCategory, Double> entry : budget.getCategoryBudgets().entrySet()) {
                BudgetCategory category = entry.getKey();
                BudgetManager.BudgetProgress progress = budgetManager.getProgress(month, category);

                String monthStr = firstRow ? month.toString() : "";
                output.add(String.format("%-10s | [%d] %-18s | $%,12.2f | $%,12.2f | $%,12.2f",
                        monthStr,
                        category.getCode(),
                        category.getDisplayName(),
                        progress.getAllocated(),
                        progress.getSpent(),
                        progress.getRemaining()
                ));
                firstRow = false;
            }

            // Disposable income for this month
            BudgetManager.BudgetProgress disposableProgress = budgetManager.getDisposableIncomeProgress(month);
            output.add(String.format("%-10s | %-22s | $%,12.2f | $%,12.2f | $%,12.2f",
                    "",
                    "Disposable Income",
                    disposableProgress.getAllocated(),
                    disposableProgress.getSpent(),
                    disposableProgress.getRemaining()
            ));
            output.add("------------+------------------------+------------+------------+------------");
        }

        output.forEach(ui::showResult);
    }

    // ========== Helper Methods ==========
    private String createProgressBar(int percentage, int length) {
        int filled = (int) Math.round((percentage / 100.0) * length);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < length; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        return bar.toString();
    }

    private YearMonth parseMonth(String monthStr) throws RLADException {
        try {
            return YearMonth.parse(monthStr, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (DateTimeParseException e) {
            throw new RLADException("Invalid month format. Use YYYY-MM (e.g., 2026-03)\n" +
                    "Type 'help budget' for details.");
        }
    }

    private int parseCategoryCode(String codeStr) throws RLADException {
        try {
            int code = Integer.parseInt(codeStr);
            if (code < 1 || code > 12) {
                throw new RLADException("Category code must be between 1 and 12\nType 'help budget' for details.");
            }
            return code;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid category code. Please enter a number (1-12)\n" +
                    "Type 'help budget' for details.");
        }
    }

    private double parseAmount(String amountStr) throws RLADException {
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                throw new RLADException("Amount must be greater than 0\nType 'help budget' for details.");
            }
            if (amount > 10000000) {
                throw new RLADException("Amount must not exceed 10,000,000\nType 'help budget' for details.");
            }
            return Math.round(amount * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount format. Please enter a number (e.g., 500.00)\n" +
                    "Type 'help budget' for details.");
        }
    }

    private int parseYear(String yearStr) throws RLADException {
        try {
            int year = Integer.parseInt(yearStr.trim());
            if (year < 2000 || year > 2100) {
                throw new RLADException("Year must be between 2000 and 2100\nType 'help budget' for details.");
            }
            return year;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid year format. Use YYYY (e.g., 2026)\nType 'help budget' for details.");
        }
    }

    private void printUsage(Ui ui) {
        ui.showResult("\n📝 Budget Commands:\n" +
                "  budget set <YYYY-MM> <category_code> <amount>     - Set a budget\n" +
                "  budget view [YYYY-MM]                            - View budget(s)\n" +
                "  budget edit <YYYY-MM> <category_code> <amount>   - Edit a budget\n" +
                "  budget delete <YYYY-MM> <category_code>          - Delete a budget\n" +
                "  budget yearly [YYYY]                             - Yearly summary\n" +
                "\nCategory codes:\n" +
                "  1=Food, 2=Transport, 3=Utilities, 4=Housing\n" +
                "  5=Health, 6=Debt, 7=Childcare, 8=Shopping\n" +
                "  9=Gifts, 10=Investments, 11=Emergency, 12=Savings");
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
