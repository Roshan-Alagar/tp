package seedu.RLAD;

import seedu.RLAD.command.AddCommand;
import seedu.RLAD.command.Command;
import seedu.RLAD.command.DeleteCommand;
import seedu.RLAD.command.HelpCommand;
import seedu.RLAD.command.ListCommand;
import seedu.RLAD.command.ModifyCommand;
import seedu.RLAD.command.SearchCommand;
import seedu.RLAD.command.SortCommand;
import seedu.RLAD.command.SummarizeCommand;
import seedu.RLAD.command.ExportCommand;
import seedu.RLAD.command.ImportCommand;
import seedu.RLAD.command.ClearCommand;
import seedu.RLAD.exception.RLADException;
import seedu.RLAD.budget.BudgetCommand;

public class Parser {

    public Parser() {

    }

    // We let RLAD deal with the error catching directly
    // TODO: better input sanitization for null or empty commands
    public static String[] parseCommand(String command) throws RLADException {
        String commandToParse = command.trim();

        // silently prevents the overflow
        if (commandToParse.isEmpty() ||
                commandToParse.startsWith("▀") ||
                commandToParse.startsWith("Type 'help'") ||
                commandToParse.startsWith("Hello and welcome")) {

            // Return a special internal action that the switch handles as "do nothing"
            return new String[]{"noop", ""};
        }

        if (commandToParse.isEmpty()) {
            throw new RLADException("Empty commands are invalid");
        }

        String[] parts = commandToParse.split("\\s+", 2);
        String action = parts[0].toLowerCase();

        // allow the command where arguments are not required to exit gracefully
        String arguments = (parts.length > 1) ? parts[1].trim() : "";

        if (!isValidAction(action)) {
            throw new RLADException("Unknown command: " + action + ". Type 'help' for options.");
        }

        // forces argument invocation
        if (requiresArguments(action) && arguments.isEmpty()) {
            throw new RLADException("The '" + action + "' command requires arguments (e.g., --command).");
        }

        return new String[]{action, arguments};
    }

    private static boolean isValidAction(String action) {
        return action.matches(
                "add|delete|modify|list|sort|summarize|help|exit|budget|search|find|export|import|clear|noop");
    }

    private static boolean requiresArguments(String action) {
        return action.matches("add|delete|modify|budget|search|find");
    }

    public static Command parse(String input) throws RLADException {
        String[] parts = parseCommand(input); // Your existing logic to split into 2 parts
        String action = parts[0].toLowerCase();
        String arguments = parts[1];

        // The Parser acts as a "Factory"
        switch (action) {
        case "noop": // ignore trash that overflows into the scanner
            return new Command("") {
                @Override
                public void execute(TransactionManager t, Ui u) {
                    
                }
                @Override
                public boolean hasValidArgs() {
                    return true;
                }
            };
        case "add":
            return new AddCommand(arguments);
        case "delete":
            return new DeleteCommand(arguments);
        case "list":
            return new ListCommand(arguments);
        case "sort":
            return new SortCommand(arguments);
        case "help":
            return new HelpCommand(arguments);
        case "summarize":
            return new SummarizeCommand(arguments);
        case "modify":
            return new ModifyCommand(action, arguments);
        case "budget":
            return new BudgetCommand(arguments);
        case "search":
        case "find":
            return new SearchCommand(arguments);
        case "export":
            return new ExportCommand(arguments);
        case "import":
            return new ImportCommand(arguments);
        case "clear":
            return new ClearCommand(arguments);
        default:
            throw new RLADException("Unknown command: " + action);
        }
    }
}
