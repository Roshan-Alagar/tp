package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

public class DeleteCommand extends Command {
    public DeleteCommand(String rawArgs) {
        super(rawArgs);
    }

    private String parseHashId() {
        if (rawArgs == null) {
            return null;
        }
        int idx = rawArgs.indexOf("--hashID");
        if (idx == -1) {
            return null;
        }
        String afterFlag = rawArgs.substring(idx + "--hashID".length()).trim();
        // Take only the first token (the ID value)
        String[] tokens = afterFlag.split("\\s+", 2);
        return tokens.length > 0 && !tokens[0].isEmpty() ? tokens[0] : null;
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException("Missing required field: --hashID");
        }

        String hashId = parseHashId();
        Transaction toDelete = transactions.findTransaction(hashId);
        if (toDelete == null) {
            throw new RLADException("Transaction not found: " + hashId);
        }

        transactions.deleteTransaction(hashId);
        ui.showResult(String.format(
                "Transaction deleted successfully!\n   HashID: %s\n   %s",
                hashId, toDelete.toString()));
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && rawArgs.contains("--hashID") && parseHashId() != null;
    }
}
