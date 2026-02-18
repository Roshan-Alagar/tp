package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

public class ListCommand extends Command {
    public ListCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        // TODO: implement parsing of rawArgs
        // and interaction with TransactionManager
        ui.showResult("ListCommand logic will be implemented here.");
    }

    @Override
    public boolean hasValidArgs() {
        // For now, assume true so the app doesn't crash
        return true;
    }
}
