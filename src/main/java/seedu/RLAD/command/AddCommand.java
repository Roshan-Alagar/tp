package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Transaction;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to add a new transaction to the ledger.
 *
 * <p>This command uses position-based arguments for intuitive typing:
 * <pre>
 * add credit 3000 2026-03-01 salary "March salary"
 * add debit 15.50 2026-03-05 food "Chicken rice"
 * </pre>
 *
 * <p>The argument order is: type, amount, date, [category], [description]
 *
 * @version 2.0
 */
public class AddCommand extends Command {

    /** Formatter for strict date parsing in YYYY-MM-DD format */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    /** Maximum allowed transaction amount to prevent overflow */
    private static final double MAX_AMOUNT = 10_000_000.00;

    /**
     * Constructs an AddCommand with raw user input.
     *
     * @param rawArgs The raw argument string (everything after "add")
     */
    public AddCommand(String rawArgs) {
        super(rawArgs);
    }

    /**
     * Executes the add command by parsing arguments and creating a transaction.
     *
     * <p>The parsing strategy:
     * <ol>
     *   <li>Split input by spaces</li>
     *   <li>Token 0 = type (credit/debit)</li>
     *   <li>Token 1 = amount (positive number)</li>
     *   <li>Token 2 = date (YYYY-MM-DD)</li>
     *   <li>Remaining tokens = category (optional) + description (optional, may use quotes)</li>
     * </ol>
     *
     * @param transactions The transaction manager to add to
     * @param ui The UI component for displaying results
     * @throws RLADException If arguments are invalid or missing
     */
    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        // Validate input exists
        if (!hasValidArgs()) {
            throw new RLADException(getUsageHelp());
        }

        // Parse position-based arguments
        String[] parts = rawArgs.trim().split("\\s+");

        // Validate minimum required fields (type, amount, date)
        if (parts.length < 3) {
            throw new RLADException(getUsageHelp());
        }

        // ========== Parse required fields (positions 0, 1, 2) ==========
        String type = parseAndValidateType(parts[0]);
        double amount = parseAndValidateAmount(parts[1]);
        LocalDate date = parseAndValidateDate(parts[2]);

        // ========== Parse optional fields (positions 3+) ==========
        ParsedOptionalFields optionalFields = parseOptionalFields(parts);
        String category = optionalFields.category;
        String description = optionalFields.description;

        // Create and add the transaction
        Transaction newTransaction = new Transaction(type, category, amount, date, description);
        transactions.addTransaction(newTransaction);

        // Display success message to user
        displaySuccessMessage(ui, newTransaction, category, description);
    }

    /**
     * Validates and parses the transaction type.
     *
     * @param typeStr The type string from user input
     * @return Validated type ("credit" or "debit")
     * @throws RLADException If type is not "credit" or "debit"
     */
    private String parseAndValidateType(String typeStr) throws RLADException {
        String type = typeStr.toLowerCase();
        if (!type.equals("credit") && !type.equals("debit")) {
            throw new RLADException("Invalid type: '" + typeStr + "'. Use 'credit' or 'debit'.");
        }
        return type;
    }

    /**
     * Validates and parses the transaction amount.
     *
     * @param amountStr The amount string from user input
     * @return Validated amount as double
     * @throws RLADException If amount is not a positive number or exceeds limits
     */
    private double parseAndValidateAmount(String amountStr) throws RLADException {
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount: '" + amountStr + "'. Please enter a number (e.g., 15.50)");
        }

        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new RLADException("Invalid amount: '" + amountStr + "'. Please enter a number (e.g., 15.50)");
        }

        if (amount <= 0) {
            throw new RLADException("Amount must be greater than 0. Got: " + amount);
        }

        if (amount > MAX_AMOUNT) {
            throw new RLADException(String.format("Amount cannot exceed $%,.2f. Got: $%,.2f", MAX_AMOUNT, amount));
        }

        // Round to 2 decimal places for consistency
        return Math.round(amount * 100.0) / 100.0;
    }

    /**
     * Validates and parses the transaction date.
     *
     * @param dateStr The date string from user input
     * @return Validated date as LocalDate
     * @throws RLADException If date format is invalid
     */
    private LocalDate parseAndValidateDate(String dateStr) throws RLADException {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RLADException("Invalid date: '" + dateStr + "'. Use YYYY-MM-DD (e.g., 2026-03-01)");
        }
    }

    /**
     * Parses optional fields (category and description) from remaining arguments.
     *
     * <p>Handles quoted descriptions that may contain spaces.
     *
     * @param parts All argument parts from split input
     * @return ParsedOptionalFields containing category and description (may be null)
     */
    private ParsedOptionalFields parseOptionalFields(String[] parts) {
        if (parts.length < 4) {
            return new ParsedOptionalFields(null, null);
        }

        List<String> remainingParts = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder quotedDesc = new StringBuilder();
        String description = null;

        // Process remaining arguments (from index 3 onward)
        for (int i = 3; i < parts.length; i++) {
            String part = parts[i];

            // Handle quoted description start
            if (part.startsWith("\"") && !inQuotes) {
                inQuotes = true;
                quotedDesc.append(part.substring(1));

                // Handle case where quote ends in same token
                if (part.endsWith("\"")) {
                    description = quotedDesc.toString();
                    description = description.substring(0, description.length() - 1);
                    inQuotes = false;
                    quotedDesc.setLength(0);
                }
            } else if (inQuotes) {
                quotedDesc.append(" ").append(part);
                if (part.endsWith("\"")) {
                    description = quotedDesc.toString();
                    description = description.substring(0, description.length() - 1);
                    inQuotes = false;
                    quotedDesc.setLength(0);
                }
            } else {
                remainingParts.add(part);
            }
        }

        // First remaining part is category (if exists)
        String category = remainingParts.isEmpty() ? null : remainingParts.get(0);

        // If we have more parts and no quoted description, join them as description
        if (description == null && remainingParts.size() > 1) {
            description = String.join(" ", remainingParts.subList(1, remainingParts.size()));
        }

        return new ParsedOptionalFields(category, description);
    }

    /**
     * Displays a formatted success message to the user.
     *
     * @param ui The UI component
     * @param transaction The newly created transaction
     * @param category The category (may be null)
     * @param description The description (may be null)
     */
    private void displaySuccessMessage(Ui ui, Transaction transaction, String category, String description) {
        String categoryDisplay = (category == null || category.trim().isEmpty()) ? "(none)" : category;
        String descriptionDisplay = (description == null || description.trim().isEmpty())
                ? "(none)" : "\"" + description + "\"";

        String successMessage = String.format(
                "✅ Transaction added successfully!\n" +
                        "   ID: %s\n" +
                        "   %s: $%.2f on %s\n" +
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

    /**
     * Generates usage help text for this command.
     *
     * @return Formatted usage instructions
     */
    private String getUsageHelp() {
        return "Usage: add <type> <amount> <date> [category] [description]\n" +
                "  type: credit or debit\n" +
                "  amount: positive number (max $10,000,000)\n" +
                "  date: YYYY-MM-DD\n" +
                "  category: optional single word\n" +
                "  description: optional (use quotes for spaces)\n\n" +
                "Examples:\n" +
                "  add credit 3000 2026-03-01 salary \"March salary\"\n" +
                "  add debit 15.50 2026-03-05 food \"Chicken rice\"\n" +
                "  add debit 5.00 2026-03-06";
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

    /**
     * Container for parsed optional fields.
     */
    private static class ParsedOptionalFields {
        final String category;
        final String description;

        ParsedOptionalFields(String category, String description) {
            this.category = category;
            this.description = description;
        }
    }
}
