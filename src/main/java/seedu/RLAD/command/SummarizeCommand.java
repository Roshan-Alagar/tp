package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SummarizeCommand extends Command {
    public SummarizeCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        List<Transaction> filtered = transactions.getTransactions();
        if (rawArgs != null && !rawArgs.trim().isEmpty()) {
            filtered = FilterCommand.applyColonFilters(filtered, rawArgs.trim());
        }

        if (filtered.isEmpty()) {
            ui.showResult("No transactions found for the given filters.");
            return;
        }

        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal totalDebit = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();

        for (Transaction t : filtered) {
            BigDecimal amount = BigDecimal.valueOf(t.getAmount()).setScale(2, RoundingMode.HALF_UP);
            String category = (t.getCategory() == null || t.getCategory().isBlank())
                    ? "(none)"
                    : t.getCategory();

            if ("credit".equals(t.getType())) {
                totalCredit = totalCredit.add(amount);
            } else {
                totalDebit = totalDebit.add(amount);
            }

            categoryTotals.merge(category, amount, BigDecimal::add);
        }

        BigDecimal net = totalCredit.subtract(totalDebit);

        StringBuilder sb = new StringBuilder();
        sb.append("===== Financial Summary =====\n");
        sb.append(String.format("  Total Credit : $%,.2f%n", totalCredit.doubleValue()));
        sb.append(String.format("  Total Debit  : $%,.2f%n", totalDebit.doubleValue()));
        sb.append(String.format("  Net Balance  : $%,.2f%n", net.doubleValue()));
        sb.append("\n--- Category Breakdown ---\n");
        for (Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()) {
            sb.append(String.format("  %-20s $%,.2f%n", entry.getKey() + ":", entry.getValue().doubleValue()));
        }
        sb.append("=============================");

        ui.showResult(sb.toString());
    }

    @Override
    public boolean hasValidArgs() {
        return true;
    }
}
