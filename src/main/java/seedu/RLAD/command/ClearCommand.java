package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

/**
 * Clears all transactions from storage after user confirmation.
 */
public class ClearCommand extends Command {

    /**
     * Creates a new ClearCommand.
     *
     * @param rawArgs the raw argument string
     */
    public ClearCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException(getInvalidArgsMessage());
        }
        String trimmedArgs = rawArgs == null ? "" : rawArgs.trim();
        boolean forceMode = trimmedArgs.equalsIgnoreCase("--force");

        int count = transactions.getTransactionCount();
        if (count == 0) {
            ui.showResult("No transactions to clear.");
            return;
        }

        if (!forceMode) {
            boolean confirmed = ui.askConfirmation(
                    "WARNING: This will permanently delete all " + count + " transactions.\n"
                            + "This action cannot be undone.");
            if (!confirmed) {
                ui.showResult("Clear cancelled.");
                return;
            }
        }

        transactions.clearAllTransactions();
        ui.showResult("Cleared " + count + " transactions.");
    }

    @Override
    public boolean hasValidArgs() {
        if (rawArgs == null || rawArgs.trim().isEmpty()) {
            return true;
        }
        String trimmed = rawArgs.trim();
        return trimmed.equalsIgnoreCase("--force");
    }

    /**
     * Returns an error message for invalid arguments.
     */
    public String getInvalidArgsMessage() {
        return "Invalid argument. Usage: clear [--force]";
    }
}
