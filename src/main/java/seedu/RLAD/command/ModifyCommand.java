// ModifyCommand.java - Fixed with documentation
package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command to modify an existing transaction's fields.
 *
 * <p>This command supports modifying one or more fields of a transaction
 * identified by its HashID. Field values containing spaces must be enclosed
 * in double quotes.
 *
 * <p>Format: modify &lt;hashID&gt; field=value [field=value ...]
 * <br>Example: modify a7b2c3 amount=25.00 description="New description"
 *
 * <p>Supported fields: type, amount, date, category, description
 *
 * @version 2.1
 * @see Transaction
 */
public class ModifyCommand extends Command {

    /**
     * Constructs a ModifyCommand with the action and raw arguments.
     *
     * @param action The command action string ("modify")
     * @param rawArgs The raw argument string containing ID and field=value pairs
     */
    public ModifyCommand(String action, String rawArgs) {
        super(action, rawArgs);
    }

    /**
     * Executes the modify command by parsing field=value pairs and updating the transaction.
     *
     * <p>The execution flow:
     * <ol>
     *   <li>Validate that arguments are provided</li>
     *   <li>Parse arguments with quote support to preserve spaces</li>
     *   <li>Extract the HashID as the first token</li>
     *   <li>Parse remaining tokens as field=value pairs</li>
     *   <li>Validate the transaction exists</li>
     *   <li>Apply each update to the corresponding field</li>
     *   <li>Validate the updated field values</li>
     *   <li>Save the updated transaction</li>
     *   <li>Display confirmation message to the user</li>
     * </ol>
     *
     * @param transactions The transaction manager containing the data
     * @param ui The UI component for displaying results
     * @throws RLADException If arguments are invalid, transaction not found, or field values invalid
     */
    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException(getUsageHelp());
        }

        // Parse the arguments: first token is ID, rest are field=value pairs
        List<String> tokens = parseWithQuotes(rawArgs.trim());
        if (tokens.isEmpty()) {
            throw new RLADException(getUsageHelp());
        }

        String id = tokens.get(0);
        Transaction existing = transactions.findTransaction(id);
        if (existing == null) {
            throw new RLADException("Transaction not found: " + id + "\nType 'help modify' for usage.");
        }

        // Parse field=value pairs from remaining tokens
        Map<String, String> updates = parseFieldValuePairs(tokens.subList(1, tokens.size()));

        if (updates.isEmpty()) {
            throw new RLADException("No fields to update. Usage: modify <hashID> field=value [field=value ...]\n" +
                    "Type 'help modify' for usage.");
        }

        // Apply updates to create new transaction
        Transaction updated = buildUpdatedTransaction(existing, updates);

        transactions.updateTransaction(id, updated);
        ui.showResult(String.format(
                "✅ Transaction updated!\n   ID: %s\n   New: %s",
                id, formatTransaction(updated)
        ));
    }

    /**
     * Parses a string with support for quoted values (preserves spaces within quotes).
     *
     * <p>This method tokenizes the input string while respecting double quotes.
     * Text inside quotes is treated as a single token even if it contains spaces.
     *
     * <p>Example: {@code amount=25.00 description="New description"}
     * <br>Returns: {@code ["amount=25.00", "description=New description"]}
     *
     * @param input The input string to parse
     * @return A list of tokens with quotes removed from quoted sections
     */
    private List<String> parseWithQuotes(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == ' ' && !inQuotes) {
                if (current.length() > 0) {
                    tokens.add(removeSurroundingQuotes(current.toString()));
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            tokens.add(removeSurroundingQuotes(current.toString()));
        }
        return tokens;
    }

    /**
     * Removes surrounding double quotes from a string if present.
     *
     * @param token The token that may have surrounding quotes
     * @return The token without surrounding quotes, or the original if no quotes
     */
    private String removeSurroundingQuotes(String token) {
        if (token.startsWith("\"") && token.endsWith("\"")) {
            return token.substring(1, token.length() - 1);
        }
        return token;
    }

    /**
     * Parses a list of field=value strings into a map.
     *
     * <p>Validates that each token contains an equals sign and has both field and value.
     *
     * @param tokens The list of field=value tokens
     * @return A map mapping field names to their values
     * @throws RLADException If any token has invalid format
     */
    private Map<String, String> parseFieldValuePairs(List<String> tokens) throws RLADException {
        Map<String, String> updates = new HashMap<>();
        for (String pair : tokens) {
            int eqIndex = pair.indexOf('=');
            if (eqIndex <= 0 || eqIndex == pair.length() - 1) {
                throw new RLADException("Invalid format: '" + pair + "'. Use field=value (e.g., amount=25.00)\n" +
                        "Type 'help modify' for usage.");
            }
            String field = pair.substring(0, eqIndex).toLowerCase();
            String value = pair.substring(eqIndex + 1);
            updates.put(field, value);
        }
        return updates;
    }

    /**
     * Builds an updated transaction by applying field updates to an existing transaction.
     *
     * @param existing The existing transaction to update
     * @param updates Map of field names to new values
     * @return A new Transaction object with updates applied
     * @throws RLADException If any field value is invalid
     */
    private Transaction buildUpdatedTransaction(Transaction existing,Map<String,String> updates) throws RLADException {
        // Reject unknown field names
        Set<String> validFields = Set.of("type", "amount", "date", "category", "description");
        for (String key : updates.keySet()) {
            if (!validFields.contains(key)) {
                throw new RLADException("Unknown field: '" + key + "'. "
                        + "Valid fields: type, amount, date, category, description");
            }
        }

        String type = updates.getOrDefault("type", existing.getType());
        String category = updates.getOrDefault("category", existing.getCategory());
        String description = updates.getOrDefault("description", existing.getDescription());
        double amount = updates.containsKey("amount") ?
                parseAmount(updates.get("amount")) : existing.getAmount();
        LocalDate date = updates.containsKey("date") ?
                parseDate(updates.get("date")) : existing.getDate();

        // Validate type
        if (!type.equals("credit") && !type.equals("debit")) {
            throw new RLADException("Type must be 'credit' or 'debit'.\nType 'help modify' for usage.");
        }

        Transaction updated = new Transaction(type, category, amount, date, description);
        updated.setHashId(existing.getHashId());
        return updated;
    }

    /**
     * Parses and validates an amount string.
     *
     * @param amountStr The amount string to parse
     * @return The parsed amount as a double
     * @throws RLADException If amount is invalid, non-positive, or exceeds limits
     */
    private double parseAmount(String amountStr) throws RLADException {
        try {
            double value = Double.parseDouble(amountStr);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new RLADException("Invalid amount. Type 'help modify' for usage.");
            }
            if (value <= 0) {
                throw new RLADException("Amount must be > 0. Type 'help modify' for usage.");
            }
            if (value > 10_000_000.00) {
                throw new RLADException("Amount cannot exceed $10,000,000. Type 'help modify' for usage.");
            }
            double rounded = Math.round(value * 100.0) / 100.0;
            if (rounded <= 0) {
                throw new RLADException("Amount rounds to $0.00. Minimum is $0.01.");
            }
            return rounded;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount format. Type 'help modify' for usage.");
        }
    }

    /**
     * Parses and validates a date string.
     *
     * @param dateStr The date string in YYYY-MM-DD format
     * @return The parsed LocalDate
     * @throws RLADException If date format is invalid
     */
    private LocalDate parseDate(String dateStr) throws RLADException {
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            throw new RLADException("Invalid date. Use YYYY-MM-DD. Type 'help modify' for usage.");
        }
    }

    /**
     * Formats a transaction for display in the success message.
     *
     * @param t The transaction to format
     * @return A formatted string containing transaction details
     */
    private String formatTransaction(Transaction t) {
        return String.format("%s | $%,.2f | %s | %s | %s",
                t.getType().toUpperCase(), t.getAmount(), t.getDate(),
                (t.getCategory() == null || t.getCategory().isEmpty())
                        ? "(none)" : t.getCategory(),
                (t.getDescription() == null || t.getDescription().isEmpty())
                        ? "(none)" : t.getDescription());
    }

    /**
     * Generates usage help text for this command.
     *
     * @return Formatted usage instructions
     */
    private String getUsageHelp() {
        return "Usage: modify <hashID> field=value [field=value ...]\n" +
                "  Fields: type, amount, date, category, description\n" +
                "  Use quotes for values with spaces: description=\"New description\"\n" +
                "  Example: modify a7b2c3 amount=25.00 description=\"New description\"\n" +
                "Type 'help modify' for more details.";
    }

    /**
     * Validates that the command has sufficient arguments to execute.
     *
     * @return true if rawArgs is not null or empty
     */
    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
