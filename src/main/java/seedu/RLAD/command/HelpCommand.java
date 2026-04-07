package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

public class HelpCommand extends Command {
    public HelpCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        if (rawArgs == null || rawArgs.trim().isEmpty()) {
            ui.printPossibleOptions();
            return;
        }
        String command = rawArgs.trim().toLowerCase();
        switch (command) {
        case "add":
            ui.printAddManual();
            break;
        case "modify":
            ui.printModifyManual();
            break;
        case "delete":
            ui.printDeleteManual();
            break;
        case "list":
            ui.printListManual();
            break;
        case "summarize":
            ui.printSummaryManual();
            break;
        case "export":
            ui.printExportManual();
            break;
        case "import":
            ui.printImportManual();
            break;
        case "clear":
            ui.printClearManual();
            break;
        default:
            ui.showResult("Unknown command: " + command
                    + ". Type 'help' to see available commands.");
            break;
        }
    }

    @Override
    public boolean hasValidArgs() {
        return true;
    }
}
