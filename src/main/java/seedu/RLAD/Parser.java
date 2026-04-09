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

/**
 * Parses user input into executable Command objects.
 *
 * <p>RLAD uses a simple two-part parsing strategy:
 * <ol>
 *   <li>Extract the command action (first word)</li>
 *   <li>Pass the remaining arguments to the appropriate Command constructor</li>
 * </ol>
 *
 * <p>Unlike flag-based parsers, RLAD uses position-based arguments
 * for most commands, making the CLI more intuitive to use.
 *
 * <p>Example parsing:
 * <pre>
 * Input: "add credit 3000 2026-03-01 salary"
 *   → action = "add"
 *   → arguments = "credit 3000 2026-03-01 salary"
 *   → Command = new AddCommand("credit 3000 2026-03-01 salary")
 * </pre>
 *
 * @version 2.0
 */
public class Parser {

    /**
     * Set of all valid command actions recognized by RLAD.
     */
    private static final String VALID_ACTIONS =
            "add|delete|modify|list|sort|summarize|help|exit|budget|search|find|export|import|clear|noop";

    /**
     * Commands that require at least one argument to be meaningful.
     */
    private static final String ARGUMENT_REQUIRED_ACTIONS =
            "add|delete|modify|budget|search|find";

    /**
     * Constructs a new Parser (no initialization needed).
     */
    public Parser() {
        // Parser is stateless; no initialization required
    }

    /**
     * Parses a raw input string into action and argument parts.
     *
     * <p>This method performs basic validation:
     * <ul>
     *   <li>Rejects empty commands</li>
     *   <li>Rejects unknown command actions</li>
     *   <li>Ensures argument-required commands have arguments</li>
     * </ul>
     *
     * @param command The raw user input string
     * @return String array with [action, arguments] (arguments may be empty)
     * @throws RLADException If command is invalid or empty
     */
    public static String[] parseCommand(String command) throws RLADException {
        String commandToParse = command.trim();

        // Handle special cases that might appear from UI artifacts
        if (commandToParse.isEmpty() ||
                commandToParse.startsWith("▀") ||
                commandToParse.startsWith("Type 'help'") ||
                commandToParse.startsWith("Hello and welcome")) {
            // Return a "noop" (no operation) action that does nothing
            return new String[]{"noop", ""};
        }

        // Split into action (first word) and the rest (arguments)
        String[] parts = commandToParse.split("\\s+", 2);
        String action = parts[0].toLowerCase();
        String arguments = (parts.length > 1) ? parts[1].trim() : "";

        // Validate action is recognized
        if (!isValidAction(action)) {
            throw new RLADException("Unknown command: '" + action + "'. Type 'help' for options.");
        }

        // Validate that commands requiring arguments have them
        if (requiresArguments(action) && arguments.isEmpty()) {
            throw new RLADException("The '" + action + "' command requires arguments.\n" +
                    "Type 'help " + action + "' for usage.");
        }

        return new String[]{action, arguments};
    }

    /**
     * Checks if the action is a valid RLAD command.
     *
     * @param action The command action string
     * @return true if action is recognized
     */
    private static boolean isValidAction(String action) {
        return action.matches(VALID_ACTIONS);
    }

    /**
     * Checks if the command requires arguments to be meaningful.
     *
     * @param action The command action string
     * @return true if arguments are required
     */
    private static boolean requiresArguments(String action) {
        return action.matches(ARGUMENT_REQUIRED_ACTIONS);
    }

    /**
     * Parses input and creates the appropriate Command object.
     *
     * <p>Acts as a factory, returning concrete Command subclasses based on the action.
     *
     * @param input The raw user input string
     * @return A Command object ready for execution
     * @throws RLADException If parsing fails or command is unknown
     */
    public static Command parse(String input) throws RLADException {
        String[] parts = parseCommand(input);
        String action = parts[0].toLowerCase();
        String arguments = parts[1];

        // Factory pattern: create command based on action
        switch (action) {
        case "noop":
            // No operation command - does nothing (for UI artifacts)
            return new Command("") {
                @Override
                public void execute(TransactionManager t, Ui u) {
                    // Intentionally empty - no operation needed
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
            return new ModifyCommand(arguments);

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
