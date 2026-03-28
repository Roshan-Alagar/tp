package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.TransactionSorter;
import seedu.RLAD.Ui;
import java.util.logging.Logger;

/**
 * Sets the global sort order for transaction display.
 * Usage:
 *   sort              -> Shows current sort setting
 *   sort amount       -> Sort by amount ascending
 *   sort date desc    -> Sort by date descending
 *   sort reset        -> Clear sort, back to insertion order
 */
public class SortCommand extends Command {
    private static final Logger logger = Logger.getLogger(SortCommand.class.getName());
    private String field;
    private String direction;

    public SortCommand(String rawArgs) {
        super(rawArgs);
        parseArgs(rawArgs);
    }

    private void parseArgs(String rawArgs) {
        if (rawArgs == null || rawArgs.trim().isEmpty()) {
            this.field = "";
            this.direction = "";
            return;
        }
        String[] tokens = rawArgs.trim().split("\\s+");
        this.field = tokens[0].toLowerCase();
        if (tokens.length > 1) {
            this.direction = tokens[1].toLowerCase();
        } else {
            this.direction = "asc";
        }
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        assert transactions != null : "TransactionManager should not be null";
        logger.info("Executing SortCommand with field: " + field + ", direction: " + direction);
        if (field.isEmpty()) {
            String currentField = transactions.getGlobalSortField();
            if (currentField.isEmpty()) {
                ui.showResult("No sort order set. Transactions are shown in insertion order.");
            } else {
                ui.showResult("Current sort: " + currentField + " ("
                        + transactions.getGlobalSortDirection() + ")");
            }
            return;
        }

        if (field.equals("reset")) {
            transactions.clearGlobalSort();
            ui.showResult("Sort order cleared. Transactions will be shown in insertion order.");
            return;
        }

        transactions.setGlobalSort(field, direction);
        ui.showResult("Sort order set: " + field + " (" + direction + ")");
    }

    @Override
    public boolean hasValidArgs() {
        if (field.isEmpty()) {
            return true;
        }
        if (field.equals("reset")) {
            return true;
        }
        if (!TransactionSorter.isValidSortField(field)) {
            return false;
        }
        if (!TransactionSorter.isValidDirection(direction)) {
            return false;
        }
        return true;
    }
}
