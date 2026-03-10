package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

public class HelpCommand extends Command {
    public HelpCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        // TODO: If rawArgs is empty, call ui.printPossibleOptions().
        // TODO: If rawArgs matches a command name (e.g., "add"),
        // call the corresponding manual method in Ui (e.g., ui.printAddManual()).
        ui.showResult("HelpCommand logic will be implemented here.");
    }

    @Override
    public boolean hasValidArgs() {
        // TODO: Validate that rawArgs is either empty or matches a valid action
        // string (add, delete, list, etc.) to prevent "help unknown_command" errors.
        return true;
    }
}
