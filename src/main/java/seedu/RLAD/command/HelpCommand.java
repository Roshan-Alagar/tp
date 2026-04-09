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
        case "sort":
            ui.printSortManual();
            break;
        case "budget":
            ui.printBudgetManual();
            break;
        case "search":
            ui.printSearchManual();
            break;
        case "help":
            ui.printHelpManual();
            break;
        case "exit":
            ui.showResult("Command: exit\nDescription: Exits the application.\nExample:\n  exit");
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
