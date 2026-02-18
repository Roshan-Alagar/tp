package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

public class SummarizeCommand extends Command {
    public SummarizeCommand() {
        super();
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        // TODO: implement parsing of rawArgs
        // and interaction with TransactionManager
        ui.showResult("SummarizeCommand logic will be implemented here.");
    }

    @Override
    public boolean hasValidArgs() {
        // Implement args check format
        return true;
    }
}
