// AddCommand.java - Fixed
package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Transaction;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AddCommand extends Command {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final double MAX_AMOUNT = 10_000_000.00;

    public AddCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException(getUsageHelp());
        }

        // Check for unclosed quotes first (Issue #98)
        if (hasUnclosedQuotes(rawArgs)) {
            throw new RLADException("Unclosed quote in command. Missing closing quote (\").\n" +
                    "Use quotes for multi-word descriptions or categories.\n" +
                    "Example: add debit 15.50 2026-04-12 food \"Lunch at hawker\"\n" +
                    "Type 'help add' for usage.");
        }

        // Parse with quote support for both category and description
        List<String> parts = parseWithQuotes(rawArgs.trim());

        if (parts.size() < 3) {
            throw new RLADException(getUsageHelp());
        }

        String type = parseAndValidateType(parts.get(0));
        double amount = parseAndValidateAmount(parts.get(1));
        LocalDate date = parseAndValidateDate(parts.get(2));

        String category = null;
        String description = null;

        if (parts.size() >= 4) {
            category = parts.get(3);
        }
        if (parts.size() >= 5) {
            description = parts.get(4);
        }

        Transaction newTransaction = new Transaction(type, category, amount, date, description);
        transactions.addTransaction(newTransaction);

        displaySuccessMessage(ui, newTransaction, category, description);
    }

    /**
     * Checks for unclosed quotes in the input string.
     *
     * <p>This method counts the number of double quote characters in the input.
     * If the count is odd, there is an unclosed quote. This validation prevents
     * silent data loss when users forget to close a quoted string.
     *
     * @param input The input string to check
     * @return true if there is an unclosed quote (odd number of quotes), false otherwise
     */
    private boolean hasUnclosedQuotes(String input) {
        int quoteCount = 0;
        for (char c : input.toCharArray()) {
            if (c == '"') {
                quoteCount++;
            }
        }
        return quoteCount % 2 != 0;
    }

    /**
     * Parses a string with support for quoted values (preserves spaces within quotes).
     *
     * <p>This method tokenizes the input string while respecting double quotes.
     * Text inside quotes is treated as a single token even if it contains spaces.
     * Both categories and descriptions can be quoted for multi-word values.
     *
     * <p>Example: {@code debit 15.50 2026-04-12 "health insurance" "Monthly payment"}
     * <br>Returns: {@code ["debit", "15.50", "2026-04-12", "health insurance", "Monthly payment"]}
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
                    String token = current.toString();
                    // Remove surrounding quotes if present
                    if (token.startsWith("\"") && token.endsWith("\"")) {
                        token = token.substring(1, token.length() - 1);
                    }
                    tokens.add(token);
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            String token = current.toString();
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
            }
            tokens.add(token);
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

    private String parseAndValidateType(String typeStr) throws RLADException {
        String type = typeStr.toLowerCase();
        if (!type.equals("credit") && !type.equals("debit")) {
            throw new RLADException("Invalid type: '" + typeStr +
                    "'. Use 'credit' or 'debit'.\nType 'help add' for usage.");
        }
        return type;
    }

    private double parseAndValidateAmount(String amountStr) throws RLADException {
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount: '" + amountStr +
                    "'. Please enter a number (e.g., 15.50)\nType 'help add' for usage.");
        }

        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new RLADException("Invalid amount: '" + amountStr +
                    "'. Please enter a number.\nType 'help add' for usage.");
        }

        if (amount <= 0) {
            throw new RLADException("Amount must be greater than 0. Got: " + amount + "\nType 'help add' for usage.");
        }

        if (amount > MAX_AMOUNT) {
            throw new RLADException(String.format("Amount cannot exceed $%,.2f. Got: $%,.2f\nType 'help add' for usage."
                    , MAX_AMOUNT, amount));
        }

        // Round to 2 decimal places for consistency
        double rounded = Math.round(amount * 100.0) / 100.0;
        if (rounded <= 0) {
            throw new RLADException("Amount rounds to $0.00. Minimum is $0.01.");
        }
        return rounded;
    }

    private LocalDate parseAndValidateDate(String dateStr) throws RLADException {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RLADException("Invalid date: '" + dateStr
                    + "'. Use YYYY-MM-DD (e.g., 2026-03-01)\nType 'help add' for usage.");
        }
    }

    private void displaySuccessMessage(Ui ui, Transaction transaction, String category, String description) {
        String categoryDisplay = (category == null || category.trim().isEmpty()) ? "(none)" : category;
        String descriptionDisplay = (description == null || description.trim().isEmpty())
                ? "(none)" : "\"" + description + "\"";

        String successMessage = String.format(
                "✅ Transaction added successfully!\n" +
                        "   ID: %s\n" +
                        "   %s: $%,.2f on %s\n" +
                        "   Category: %s\n" +
                        "   Description: %s",
                transaction.getHashId(),
                transaction.getType().toUpperCase(),
                transaction.getAmount(),
                transaction.getDate(),
                categoryDisplay,
                descriptionDisplay
        );
        ui.showResult(successMessage);
    }

    private String getUsageHelp() {
        return "Usage: add <type> <amount> <date> [category] [description]\n" +
                "  type: credit or debit\n" +
                "  amount: positive number (max $10,000,000)\n" +
                "  date: YYYY-MM-DD\n" +
                "  category: optional (use quotes for multi-word: \"health insurance\")\n" +
                "  description: optional (use quotes for spaces: \"Lunch at hawker\")\n\n" +
                "Examples:\n" +
                "  add credit 3000 2026-03-01 salary \"March salary\"\n" +
                "  add debit 15.50 2026-03-05 food \"Chicken rice\"\n" +
                "  add debit 5.00 2026-03-06\n" +
                "  add debit 10.50 2026-04-12 \"health insurance\" \"Monthly payment\"\n" +
                "Type 'help add' for more details.";
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
