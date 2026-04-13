// DeleteCommand.java - Fixed
package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

public class DeleteCommand extends Command {

    public DeleteCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException(getUsageHelp());
        }

        // Validate single argument — reject extra parameters
        String trimmed = rawArgs.trim();
        if (trimmed.contains(" ")) {
            throw new RLADException("Too many arguments. Usage: delete <hashID>\n"
                    + "  Example: delete a7b2c3");
        }

        // Clean and extract HashID (make case insensitive)
        String hashId = trimmed.toLowerCase();
        Transaction toDelete = transactions.findTransaction(hashId);

        if (toDelete == null) {
            throw new RLADException("Transaction not found: " + hashId +
                    "\nUse 'list' to see all transaction IDs.\nType 'help delete' for usage.");
        }

        transactions.deleteTransaction(hashId);

        ui.showResult(String.format(
                "✅ Transaction deleted successfully!\n" +
                        "   ID: %s\n" +
                        "   Deleted: %s",
                hashId, toDelete.toString()
        ));
    }

    private String getUsageHelp() {
        return "Usage: delete <hashID>\n" +
                "  hashID: The 6-character transaction ID (e.g., a7b2c3)\n\n" +
                "Example:\n" +
                "  delete a7b2c3\n\n" +
                "Tip: Use 'list' to see all transaction IDs.\n" +
                "Type 'help delete' for more details.";
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
