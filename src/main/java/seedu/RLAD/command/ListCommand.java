package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.TransactionSorter;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.util.ArrayList;
import java.util.List;

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
            filtered = FilterCommand.applyColonFilters(allTransactions, rawArgs.trim());
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
        return String.format("$%,.2f", amount);
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
