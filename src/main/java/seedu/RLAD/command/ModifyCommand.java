package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * ModifyCommand handles modifying an existing transaction.
 * Allows updating fields like amount, category, description, type, and date.
 *
 * Format: modify {@code <hashID>} --field1 value1 --field2 value2 ...
 * Examples:
 *   modify a7b2 --amount 25.00
 *   modify a7b2 --amount 25.00 --description "Updated lunch"
 *   modify b3c4 --category transport --date 2026-03-15
 */
public class ModifyCommand extends Command {

    public ModifyCommand(String action, String rawArgs) {
        super(action, rawArgs);
    }

    private double parseAmount(String amountStr) throws RLADException {
        try {
            double value = Double.parseDouble(amountStr.trim());
            if (value <= 0) {
                throw new RLADException("Amount must be greater than 0");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount: '" + amountStr + "'. Must be a positive number.");
        }
    }

    private LocalDate parseDate(String dateStr) throws RLADException {
        try {
            if (dateStr.contains(".")) {
                return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            }
            return LocalDate.parse(dateStr.trim());
        } catch (Exception e) {
            throw new RLADException("Invalid date '" + dateStr + "'. Use yyyy-MM-dd or yyyy.MM.dd");
        }
    }

    private String formatTransaction(Transaction t) {
        return String.format("%s | $%.2f | %s | %s | %s",
                t.getType().toUpperCase(),
                t.getAmount(),
                t.getDate(),
                t.getCategory().isEmpty() ? "(none)" : t.getCategory(),
                t.getDescription().isEmpty() ? "(none)" : t.getDescription());
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException("Usage: modify <hashID> --field value ...");
        }

        // Split ID from the rest of the args
        String[] parts = rawArgs.trim().split("\\s+", 2);
        String id = parts[0];
        String fieldsStr = parts.length > 1 ? parts[1].trim() : "";

        if (!fieldsStr.contains("--")) {
            throw new RLADException("No fields to update. Specify at least one: "
                    + "--type, --amount, --category, --date, --description");
        }

        Transaction existing = transactions.findTransaction(id);
        if (existing == null) {
            throw new RLADException("Transaction not found: " + id);
        }

        Map<String, String> updates = FilterCommand.parseFlags(fieldsStr);

        // Merge: use updated value if provided, else keep existing
        String type = updates.containsKey("type") ? updates.get("type") : existing.getType();
        String category = updates.containsKey("category") ? updates.get("category") : existing.getCategory();
        String description = updates.containsKey("description")
                ? updates.get("description") : existing.getDescription();
        double amount = updates.containsKey("amount") ? parseAmount(updates.get("amount")) : existing.getAmount();
        LocalDate date = updates.containsKey("date") ? parseDate(updates.get("date")) : existing.getDate();

        // Validate type
        if (!type.equals("credit") && !type.equals("debit")) {
            throw new RLADException("--type must be 'credit' or 'debit', got: '" + type + "'");
        }

        Transaction updated = new Transaction(type, category, amount, date, description);
        updated.setHashId(id);

        String before = formatTransaction(existing);
        transactions.updateTransaction(id, updated);
        String after = formatTransaction(updated);

        ui.showResult(String.format(
                "Transaction updated successfully!\n   ID: %s\n   Before: %s\n   After:  %s",
                id, before, after));
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
