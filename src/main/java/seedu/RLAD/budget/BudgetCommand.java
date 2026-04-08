package seedu.RLAD.budget;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.command.Command;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;  // ADD THIS IMPORT
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
 * @version 2.0
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

    private void handleSet(String[] parts, BudgetManager budgetManager, Ui ui) throws RLADException {
        if (parts.length < 4) {
            throw new RLADException("Usage: budget set <YYYY-MM> <category_code> <amount>");
        }

        YearMonth month = parseMonth(parts[1]);
        int categoryCode = parseCategoryCode(parts[2]);
        double amount = parseAmount(parts[3]);

        budgetManager.setBudget(month, categoryCode, amount);
        ui.showResult(String.format("✅ Budget set: %s - Category %d: $%.2f", month, categoryCode, amount));
    }

    private void handleView(String[] parts, BudgetManager budgetManager, Ui ui) throws RLADException {
        if (parts.length >= 2) {
            YearMonth month = parseMonth(parts[1]);
            displayMonthlyBudget(budgetManager, month, ui);
        } else {
            displayAllBudgets(budgetManager, ui);
        }
    }

    private void handleEdit(String[] parts, BudgetManager budgetManager, Ui ui) throws RLADException {
        if (parts.length < 4) {
            throw new RLADException("Usage: budget edit <YYYY-MM> <category_code> <amount>");
        }

        YearMonth month = parseMonth(parts[1]);
        int categoryCode = parseCategoryCode(parts[2]);
        double amount = parseAmount(parts[3]);

        budgetManager.editBudget(month, categoryCode, amount);
        ui.showResult(String.format("✅ Budget updated: %s - Category %d: $%.2f", month, categoryCode, amount));
    }

    private void handleDelete(String[] parts, BudgetManager budgetManager, Ui ui) throws RLADException {
        if (parts.length < 3) {
            throw new RLADException("Usage: budget delete <YYYY-MM> <category_code>");
        }

        YearMonth month = parseMonth(parts[1]);
        int categoryCode = parseCategoryCode(parts[2]);

        budgetManager.deleteBudget(month, categoryCode);
        ui.showResult(String.format("✅ Budget deleted: %s - Category %d", month, categoryCode));
    }

    private void handleYearly(String[] parts, BudgetManager budgetManager, Ui ui) throws RLADException {
        int year;
        if (parts.length >= 2) {
            year = parseYear(parts[1]);
        } else {
            year = LocalDate.now().getYear();  // FIXED: Added LocalDate import
        }
        String summary = budgetManager.getYearlySummary(year);
        ui.showResult(summary);
    }

    private int parseYear(String yearStr) throws RLADException {
        try {
            int year = Integer.parseInt(yearStr.trim());
            if (year < 2000 || year > 2100) {
                throw new RLADException("Year must be between 2000 and 2100");
            }
            return year;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid year format. Use YYYY (e.g., 2026)");
        }
    }

    private void displayMonthlyBudget(BudgetManager budgetManager, YearMonth month, Ui ui) {
        Optional<MonthlyBudget> budgetOpt = budgetManager.getBudget(month);

        if (budgetOpt.isEmpty()) {
            ui.showResult("No budget set for " + month);
            return;
        }

        MonthlyBudget budget = budgetOpt.get();
        List<String> output = new ArrayList<>();

        // Header
        output.add("=== BUDGET SUMMARY FOR " + month.toString().toUpperCase() + " ===");
        output.add(String.format("%-25s | %10s | %10s | %10s | %s",
                "Category", "Budget", "Spent", "Remaining", "Progress"));
        output.add("---------------------------+------------+------------+------------+----------------------");

        // Category rows
        double totalAllocated = 0;
        double totalSpent = 0;

        List<BudgetCategory> categories = Arrays.asList(BudgetCategory.values());
        categories.sort(Comparator.comparingInt(BudgetCategory::getCode));

        for (BudgetCategory category : categories) {
            if (budget.hasBudget(category)) {
                BudgetManager.BudgetProgress progress = budgetManager.getProgress(month, category);
                String progressBar = progress.getProgressBar(PROGRESS_BAR_LENGTH);

                output.add(String.format("[%d] %-21s | $%8.2f | $%8.2f | $%8.2f | %s %3d%%",
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

        output.add(String.format("%-25s | $%8.2f | $%8.2f | $%8.2f | %s %3d%%",
                "Disposable Income",
                disposableProgress.getAllocated(),
                disposableProgress.getSpent(),
                disposableProgress.getRemaining(),
                disposableBar,
                disposableProgress.getPercentage()
        ));

        // Total row
        double totalBudget = totalAllocated + disposableProgress.getAllocated();
        double totalSpentAll = totalSpent + disposableProgress.getSpent();
        double totalRemaining = totalBudget - totalSpentAll;
        int totalPercentage = totalBudget > 0 ? (int) ((totalSpentAll / totalBudget) * 100) : 0;
        String totalBar = createProgressBar(totalPercentage, PROGRESS_BAR_LENGTH);

        output.add(String.format("%-25s | $%8.2f | $%8.2f | $%8.2f | %s %3d%%",
                "TOTAL",
                totalBudget,
                totalSpentAll,
                totalRemaining,
                totalBar,
                totalPercentage
        ));

        // Display
        output.forEach(ui::showResult);
    }

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
                output.add(String.format("%-10s | [%d] %-18s | $%8.2f | $%8.2f | $%8.2f",
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
            output.add(String.format("%-10s | %-22s | $%8.2f | $%8.2f | $%8.2f",
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
            throw new RLADException("Invalid month format. Use YYYY-MM (e.g., 2026-03)");
        }
    }

    private int parseCategoryCode(String codeStr) throws RLADException {
        try {
            int code = Integer.parseInt(codeStr);
            if (code < 1 || code > 12) {
                throw new RLADException("Category code must be between 1 and 12");
            }
            return code;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid category code. Please enter a number (1-12)");
        }
    }

    private double parseAmount(String amountStr) throws RLADException {
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                throw new RLADException("Amount must be greater than 0");
            }
            if (amount > 10000000) {
                throw new RLADException("Amount must not exceed 10,000,000");
            }
            return Math.round(amount * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount format. Please enter a number (e.g., 500.00)");
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
