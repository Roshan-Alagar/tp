package seedu.RLAD;

import seedu.RLAD.command.AddCommand;
import seedu.RLAD.command.Command;
import seedu.RLAD.command.DeleteCommand;
import seedu.RLAD.command.HelpCommand;
import seedu.RLAD.command.ListCommand;
import seedu.RLAD.command.ModifyCommand;
import seedu.RLAD.command.SummarizeCommand;
import seedu.RLAD.exception.RLADException;

public class Parser {

    public Parser() {

    }

    // We let RLAD deal with the error catching directly
    // TODO: better input sanitization for null or empty commands
    public static String[] parseCommand(String command) throws RLADException {
        String commandToParse = command.trim();
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
        return action.matches("add|delete|modify|list|summarize|help|exit");
    }

    private static boolean requiresArguments(String action) {
        return action.matches("add|delete|modify");
    }

    public static Command parse(String input) throws RLADException {
        String[] parts = parseCommand(input); // Your existing logic to split into 2 parts
        String action = parts[0].toLowerCase();
        String arguments = parts[1];

        // The Parser acts as a "Factory"
        switch (action) {
            case "add":
                return new AddCommand(arguments);
            case "delete":
                return new DeleteCommand(arguments);
            case "list":
                return new ListCommand(arguments);
            case "help":
                return new HelpCommand(arguments);
            case "summarize":
                return new SummarizeCommand();
            case "modify":
                return new ModifyCommand(action, arguments);
            default:
                throw new RLADException("Unknown command: " + action);
        }
    }
}