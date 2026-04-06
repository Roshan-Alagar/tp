package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.util.Map;

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
        Map<String, String> flags = FilterCommand.parseFlags(rawArgs);
        boolean forceMode = flags.containsKey("force");

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
        return true;
    }
}
