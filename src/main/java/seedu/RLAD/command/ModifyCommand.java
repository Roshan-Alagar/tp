package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

/**
 * ModifyCommand handles modifying an existing transaction.
 * Allows updating fields like amount, category, description, etc.
 */
public class ModifyCommand extends Command {

    public ModifyCommand(String action, String rawArgs) {
        super(action, rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        // TODO: 1. Parse rawArgs to extract the transaction ID and fields to update
        // Expected format: "<id> --amount <value> --category <value> ..."

        // TODO: 2. Find the existing transaction using transactions.findTransaction(id)

        // TODO: 3. If not found, show error message

        // TODO: 4. Create a new Transaction with updated fields (or clone and modify)

        // TODO: 5. Update using transactions.updateTransaction(id, updatedTransaction)

        // TODO: 6. Show success/failure message via ui.showResult()

        ui.showResult("ModifyCommand logic will be implemented here.");
    }

    @Override
    public boolean hasValidArgs() {
        // TODO: Validate that:
        // - ID is provided and valid
        // - At least one field to modify is provided (--amount, --category, etc.)
        // - Field values are valid
        return true;
    }
}
