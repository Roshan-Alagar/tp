package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

public class ModifyCommand extends Command {
    public ModifyCommand(String action, String rawArgs) {
        super(action, rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        // TODO: implement parsing of rawArgs
        // and interaction with TransactionManager
        ui.showResult("ModifyCommand logic will be implemented here.");
    }

    @Override
    public boolean hasValidArgs() {
        // Implement args check format
        return true;
    }
}
