package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.TransactionSorter;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Command to list transactions with optional filtering.
 *
 * <p>Format: list [filters...]
 *
 * <p>Supported filters (all optional):
 * <ul>
 *   <li>type:credit|debit - Filter by transaction type</li>
 *   <li>cat:category - Filter by category (partial match)</li>
 *   <li>from:YYYY-MM-DD - Show transactions on or after this date</li>
 *   <li>to:YYYY-MM-DD - Show transactions on or before this date</li>
 *   <li>min:amount - Show transactions with amount >= value</li>
 *   <li>max:amount - Show transactions with amount <= value</li>
 * </ul>
 *
 * <p>Examples:
 * <pre>
 * list                          # Show all transactions
 * list type:debit               # Show only expenses
 * list cat:food                 # Show food-related transactions
 * list from:2026-03-01 to:2026-03-31  # March transactions
 * list type:debit cat:food min:10  # Expenses on food over $10
 * </pre>
 *
 * @version 2.0
 */
public class ListCommand extends Command {

    /** Separator line for table formatting (75 dashes) */
    private static final String DIVIDER = "-".repeat(75);

    /** Formatter for date parsing */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Constructs a ListCommand with optional filter arguments.
     *
     * @param rawArgs The filter string (may be empty for all transactions)
     */
    public ListCommand(String rawArgs) {
        super(rawArgs);
    }

    /**
     * Executes the list command by filtering, sorting, and displaying transactions.
     *
     * <p>The execution flow:
     * <ol>
     *   <li>Retrieve all transactions from TransactionManager</li>
     *   <li>Apply user-specified filters (if any)</li>
     *   <li>Apply global sort order (if set)</li>
     *   <li>Format and display results in a table</li>
     * </ol>
     *
     * @param transactions The transaction manager containing the data
     * @param ui The UI component for displaying results
     * @throws RLADException If filter syntax is invalid
     */
    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        // Get all transactions
        List<Transaction> allTransactions = transactions.getTransactions();

        // Apply filters if provided
        List<Transaction> filtered = allTransactions;
        if (hasFilters()) {
            filtered = applyFilters(allTransactions, rawArgs.trim());
        }

        // Handle empty result case
        if (filtered.isEmpty()) {
            ui.showResult("📭 No transactions found." +
                    (hasFilters() ? " Try removing some filters." : ""));
            return;
        }

        // Apply global sort order if set
        String sortField = transactions.getGlobalSortField();
        if (!sortField.isEmpty()) {
            filtered = TransactionSorter.sort(
                    new ArrayList<>(filtered),
                    sortField,
                    transactions.getGlobalSortDirection()
            );
        }

        // Display formatted table
        displayTransactionTable(ui, filtered);
    }

    /**
     * Checks if filter arguments were provided.
     *
     * @return true if rawArgs is not null/empty and contains filter syntax
     */
    private boolean hasFilters() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }

    /**
     * Applies filters to the transaction list based on filter string.
     *
     * <p>Filter syntax: key:value pairs separated by spaces
     * <br>Example: "type:debit cat:food min:10"
     *
     * @param transactions The original transaction list
     * @param filterStr The filter string
     * @return Filtered list of transactions
     * @throws RLADException If filter syntax is invalid
     */
    private List<Transaction> applyFilters(List<Transaction> transactions, String filterStr)
            throws RLADException {

        // Split by spaces but preserve filter:value pairs
        String[] parts = filterStr.split("\\s+");
        List<Transaction> result = new ArrayList<>(transactions);

        // Process each filter in sequence
        for (int i = 0; i < parts.length; i++) {
            String filter = parts[i].toLowerCase();

            // Check if this part contains a colon (it's a filter)
            if (filter.contains(":")) {
                String[] filterParts = filter.split(":", 2);
                String filterType = filterParts[0];
                String filterValue = filterParts[1];

                switch (filterType) {
                case "type":
                    result = filterByType(result, filterValue);
                    break;

                case "cat":
                case "category":
                    result = filterByCategory(result, filterValue);
                    break;

                case "from":
                    LocalDate fromDate = parseDate(filterValue);
                    result = filterByDateFrom(result, fromDate);
                    break;

                case "to":
                    LocalDate toDate = parseDate(filterValue);
                    result = filterByDateTo(result, toDate);
                    break;

                case "min":
                    double minAmount = parseAmount(filterValue);
                    result = filterByMinAmount(result, minAmount);
                    break;

                case "max":
                    double maxAmount = parseAmount(filterValue);
                    result = filterByMaxAmount(result, maxAmount);
                    break;

                default:
                    throw new RLADException("Unknown filter: '" + filterType +
                            "'. Available: type, cat, from, to, min, max");
                }
            } else if (filter.startsWith("--sort")) {
                // Handle sort - this is handled elsewhere, skip in filters
                i++; // Skip the next part which is the sort field
            }
            // Ignore other non-filter tokens
        }

        return result;
    }

    /**
     * Filters transactions by type (credit/debit).
     *
     * @param transactions The list to filter
     * @param type The type to filter by ("credit" or "debit")
     * @return Filtered list
     */
    private List<Transaction> filterByType(List<Transaction> transactions, String type) {
        String normalizedType = type.toLowerCase();
        return transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase(normalizedType))
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions by category (case-insensitive partial match).
     *
     * @param transactions The list to filter
     * @param category The category substring to match
     * @return Filtered list
     */
    private List<Transaction> filterByCategory(List<Transaction> transactions, String category) {
        String lowerCategory = category.toLowerCase();
        return transactions.stream()
                .filter(t -> t.getCategory() != null &&
                        t.getCategory().toLowerCase().contains(lowerCategory))
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions on or after a specific date.
     *
     * @param transactions The list to filter
     * @param fromDate The start date (inclusive)
     * @return Filtered list
     */
    private List<Transaction> filterByDateFrom(List<Transaction> transactions, LocalDate fromDate) {
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(fromDate))
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions on or before a specific date.
     *
     * @param transactions The list to filter
     * @param toDate The end date (inclusive)
     * @return Filtered list
     */
    private List<Transaction> filterByDateTo(List<Transaction> transactions, LocalDate toDate) {
        return transactions.stream()
                .filter(t -> !t.getDate().isAfter(toDate))
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions with amount greater than or equal to minimum.
     *
     * @param transactions The list to filter
     * @param minAmount The minimum amount (inclusive)
     * @return Filtered list
     */
    private List<Transaction> filterByMinAmount(List<Transaction> transactions, double minAmount) {
        return transactions.stream()
                .filter(t -> t.getAmount() >= minAmount)
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions with amount less than or equal to maximum.
     *
     * @param transactions The list to filter
     * @param maxAmount The maximum amount (inclusive)
     * @return Filtered list
     */
    private List<Transaction> filterByMaxAmount(List<Transaction> transactions, double maxAmount) {
        return transactions.stream()
                .filter(t -> t.getAmount() <= maxAmount)
                .collect(Collectors.toList());
    }

    /**
     * Parses a date string with validation.
     *
     * @param dateStr The date string in YYYY-MM-DD format
     * @return Parsed LocalDate
     * @throws RLADException If date format is invalid
     */
    private LocalDate parseDate(String dateStr) throws RLADException {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RLADException("Invalid date: '" + dateStr + "'. Use YYYY-MM-DD");
        }
    }

    /**
     * Parses an amount string with validation.
     *
     * @param amountStr The amount string
     * @return Parsed double
     * @throws RLADException If amount format is invalid
     */
    private double parseAmount(String amountStr) throws RLADException {
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount < 0) {
                throw new RLADException("Amount cannot be negative: " + amountStr);
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount: '" + amountStr + "'");
        }
    }

    /**
     * Displays transactions in a formatted table.
     *
     * @param ui The UI component
     * @param transactions The transactions to display
     */
    private void displayTransactionTable(Ui ui, List<Transaction> transactions) {
        // Table header
        ui.showResult(DIVIDER);
        ui.showResult(String.format("  %-6s %-8s %-12s %10s  %-12s  %s",
                "ID", "TYPE", "DATE", "AMOUNT", "CATEGORY", "DESCRIPTION"));
        ui.showResult(DIVIDER);

        // Table rows
        for (Transaction t : transactions) {
            ui.showResult(String.format("  %-6s %-8s %-12s %10s  %-12s  %s",
                    t.getHashId(),
                    t.getType().toUpperCase(),
                    t.getDate().toString(),
                    formatAmount(t.getAmount()),
                    formatCategory(t.getCategory()),
                    formatDescription(t.getDescription())
            ));
        }

        // Table footer with count
        ui.showResult(DIVIDER);
        ui.showResult(String.format("  📊 Total: %d transaction(s) shown", transactions.size()));
    }

    /**
     * Formats amount with currency symbol and 2 decimal places.
     *
     * @param amount The amount to format
     * @return Formatted string (e.g., "$15.50")
     */
    private String formatAmount(double amount) {
        return String.format("$%.2f", amount);
    }

    /**
     * Formats category for display, replacing null/empty with "(none)".
     *
     * @param category The category string
     * @return Formatted category
     */
    private String formatCategory(String category) {
        return (category == null || category.isEmpty()) ? "(none)" : category;
    }

    /**
     * Formats description for display, replacing null/empty with "(none)".
     *
     * @param description The description string
     * @return Formatted description
     */
    private String formatDescription(String description) {
        return (description == null || description.isEmpty()) ? "(none)" : description;
    }

    /**
     * Validates that the command can execute (always true for list).
     *
     * @return true (list command always has valid arguments)
     */
    @Override
    public boolean hasValidArgs() {
        return true;
    }
}