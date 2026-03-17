package seedu.RLAD.budget;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.command.Command;
import seedu.RLAD.exception.RLADException;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BudgetCommand extends Command {
    private static final int PROGRESS_BAR_LENGTH = 20;

    private enum SubCommand {
        SET, VIEW, EDIT, DELETE
    }

    public BudgetCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        // This method is required by the abstract Command class
        // We need a BudgetManager to work, so throw an error
        throw new RLADException("BudgetCommand requires a BudgetManager. Please use the version with BudgetManager.");
    }

    // New method that accepts BudgetManager
    public void execute(TransactionManager transactions, Ui ui, BudgetManager budgetManager) throws RLADException {
        Map<String, String> parsedArgs = parseArguments(rawArgs);

        String subCommandStr = parsedArgs.getOrDefault("subcommand", "").toUpperCase();
        SubCommand subCommand;

        try {
            subCommand = SubCommand.valueOf(subCommandStr);
        } catch (IllegalArgumentException e) {
            throw new RLADException("Invalid budget subcommand. Use: set, view, edit, or delete");
        }

        switch (subCommand) {
        case SET:
            handleSet(budgetManager, parsedArgs, ui);
            break;
        case VIEW:
            handleView(budgetManager, parsedArgs, ui);
            break;
        case EDIT:
            handleEdit(budgetManager, parsedArgs, ui);
            break;
        case DELETE:
            handleDelete(budgetManager, parsedArgs, ui);
            break;
        default:
            throw new RLADException("Unknown budget subcommand: " + subCommandStr);
        }
    }

    private void handleSet(BudgetManager budgetManager, Map<String, String> args, Ui ui)
            throws RLADException {
        validateRequiredFields(args, "--month", "--category", "--amount");

        YearMonth month = parseMonth(args.get("--month"));
        int categoryCode = parseCategoryCode(args.get("--category"));
        double amount = parseAmount(args.get("--amount"));

        budgetManager.setBudget(month, categoryCode, amount);

        ui.showResult(String.format(
                "✅ Budget set successfully for %s\n   Category [%d]: $%.2f",
                month, categoryCode, amount
        ));
    }

    private void handleView(BudgetManager budgetManager, Map<String, String> args, Ui ui)
            throws RLADException {
        if (args.containsKey("--month")) {
            // View specific month
            YearMonth month = parseMonth(args.get("--month"));
            displayMonthlyBudget(budgetManager, month, ui);
        } else {
            // View all months
            displayAllBudgets(budgetManager, ui);
        }
    }

    private void handleEdit(BudgetManager budgetManager, Map<String, String> args, Ui ui)
            throws RLADException {
        validateRequiredFields(args, "--month", "--category", "--amount");

        YearMonth month = parseMonth(args.get("--month"));
        int categoryCode = parseCategoryCode(args.get("--category"));
        double amount = parseAmount(args.get("--amount"));

        budgetManager.editBudget(month, categoryCode, amount);

        ui.showResult(String.format(
                "✅ Budget updated for %s\n   Category [%d]: $%.2f",
                month, categoryCode, amount
        ));
    }

    private void handleDelete(BudgetManager budgetManager, Map<String, String> args, Ui ui)
            throws RLADException {
        validateRequiredFields(args, "--month", "--category");

        YearMonth month = parseMonth(args.get("--month"));
        int categoryCode = parseCategoryCode(args.get("--category"));

        budgetManager.deleteBudget(month, categoryCode);

        ui.showResult(String.format(
                "✅ Budget deleted for %s\n   Category [%d]",
                month, categoryCode
        ));
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
            return YearMonth.parse(monthStr);
        } catch (DateTimeParseException e) {
            throw new RLADException("Invalid month format. Please use YYYY-MM (e.g., 2026-03)");
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
            return Math.round(amount * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount format. Please enter a valid number (e.g., 500.00)");
        }
    }

    private void validateRequiredFields(Map<String, String> args, String... requiredFields)
            throws RLADException {
        for (String field : requiredFields) {
            if (!args.containsKey(field) || args.get(field).trim().isEmpty()) {
                throw new RLADException("Missing required field: " + field);
            }
        }
    }

    private Map<String, String> parseArguments(String rawArgs) {
        Map<String, String> argsMap = new HashMap<>();

        if (rawArgs == null || rawArgs.trim().isEmpty()) {
            return argsMap;
        }

        // Extract subcommand (first word)
        String[] parts = rawArgs.trim().split("\\s+", 2);
        argsMap.put("subcommand", parts[0].toLowerCase());

        if (parts.length < 2) {
            return argsMap;
        }

        // Parse remaining flags
        String remaining = parts[1];
        String[] tokens = remaining.split("\\s+(?=--|$)");

        for (String token : tokens) {
            if (token.startsWith("--")) {
                String[] keyValue = token.split("\\s+", 2);
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : "";
                argsMap.put(key, value);
            }
        }

        return argsMap;
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
