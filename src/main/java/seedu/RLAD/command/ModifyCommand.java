// ModifyCommand.java - Redesigned
package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ModifyCommand extends Command {

    public ModifyCommand(String action, String rawArgs) {
        super(action, rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException("Usage: modify <hashID> [field=value ...]\n" +
                    "  Fields: type, amount, date, category, description\n" +
                    "  Example: modify a7b2c3 amount=25.00 description='New description'");
        }

        String[] parts = rawArgs.trim().split("\\s+", 2);
        String id = parts[0];
        String updatesStr = parts.length > 1 ? parts[1] : "";

        Transaction existing = transactions.findTransaction(id);
        if (existing == null) {
            throw new RLADException("Transaction not found: " + id);
        }

        // Parse field=value pairs
        Map<String, String> updates = new HashMap<>();
        if (!updatesStr.isEmpty()) {
            String[] pairs = updatesStr.split("\\s+");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    updates.put(kv[0].toLowerCase(), kv[1]);
                } else {
                    throw new RLADException("Invalid format. Use field=value (e.g., amount=25.00)");
                }
            }
        }

        // After parsing updates, check if there are any
        if (updates.isEmpty()) {
            throw new RLADException("No fields to update. Usage: modify <hashID> field=value [field=value ...]");
        }

        // Apply updates
        String type = updates.getOrDefault("type", existing.getType());
        String category = updates.getOrDefault("category", existing.getCategory());
        String description = updates.getOrDefault("description", existing.getDescription());
        double amount = updates.containsKey("amount") ?
                parseAmount(updates.get("amount")) : existing.getAmount();
        LocalDate date = updates.containsKey("date") ?
                parseDate(updates.get("date")) : existing.getDate();

        // Validate type
        if (!type.equals("credit") && !type.equals("debit")) {
            throw new RLADException("Type must be 'credit' or 'debit'");
        }

        Transaction updated = new Transaction(type, category, amount, date, description);
        updated.setHashId(id);

        transactions.updateTransaction(id, updated);
        ui.showResult(String.format(
                "✅ Transaction updated!\n   ID: %s\n   New: %s",
                id, formatTransaction(updated)
        ));
    }

    private double parseAmount(String amountStr) throws RLADException {
        try {
            double value = Double.parseDouble(amountStr);
            if (value <= 0) throw new RLADException("Amount must be > 0");
            return value;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount");
        }
    }

    private LocalDate parseDate(String dateStr) throws RLADException {
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (Exception e) {
            throw new RLADException("Invalid date. Use YYYY-MM-DD");
        }
    }

    private String formatTransaction(Transaction t) {
        return String.format("%s | $%.2f | %s | %s | %s",
                t.getType().toUpperCase(), t.getAmount(), t.getDate(),
                t.getCategory().isEmpty() ? "(none)" : t.getCategory(),
                t.getDescription().isEmpty() ? "(none)" : t.getDescription());
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
