package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

/**
 * Command to delete a transaction by its HashID.
 *
 * <p>Format: delete &lt;hashID&gt;
 * <br>Example: delete a7b2c3
 *
 * <p>The HashID is the 6-character unique identifier shown in list views.
 *
 * @version 2.0
 */
public class DeleteCommand extends Command {

    /**
     * Constructs a DeleteCommand with the raw HashID argument.
     *
     * @param rawArgs The HashID of the transaction to delete
     */
    public DeleteCommand(String rawArgs) {
        super(rawArgs);
    }

    /**
     * Executes the delete command by finding and removing the transaction.
     *
     * <p>Steps:
     * <ol>
     *   <li>Validate that a HashID was provided</li>
     *   <li>Look up the transaction by HashID</li>
     *   <li>If found, delete it and notify the user</li>
     *   <li>If not found, show an error message</li>
     * </ol>
     *
     * @param transactions The transaction manager containing the data
     * @param ui The UI component for displaying results
     * @throws RLADException If no HashID is provided or transaction not found
     */
    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        // Validate input
        if (!hasValidArgs()) {
            throw new RLADException(getUsageHelp());
        }

        // Clean and extract HashID case (remove any extra whitespace and make case insensitive)
        String hashId = rawArgs.trim().toLowerCase();

        // Find the transaction
        Transaction toDelete = transactions.findTransaction(hashId);

        if (toDelete == null) {
            throw new RLADException("Transaction not found: " + hashId +
                    "\nUse 'list' to see all transaction IDs.");
        }

        // Perform deletion
        transactions.deleteTransaction(hashId);

        // Confirm deletion to user
        ui.showResult(String.format(
                "✅ Transaction deleted successfully!\n" +
                        "   ID: %s\n" +
                        "   Deleted: %s",
                hashId, toDelete.toString()
        ));
    }

    /**
     * Generates usage help text for this command.
     *
     * @return Formatted usage instructions
     */
    private String getUsageHelp() {
        return "Usage: delete <hashID>\n" +
                "  hashID: The 6-character transaction ID (e.g., a7b2c3)\n\n" +
                "Example:\n" +
                "  delete a7b2c3\n\n" +
                "Tip: Use 'list' to see all transaction IDs.";
    }

    /**
     * Validates that a HashID was provided.
     *
     * @return true if rawArgs is not null or empty
     */
    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
