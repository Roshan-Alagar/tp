// SortCommand.java - Simplified
package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.TransactionSorter;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

public class SortCommand extends Command {

    public SortCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (rawArgs == null || rawArgs.trim().isEmpty()) {
            // Show current sort
            String currentField = transactions.getGlobalSortField();
            if (currentField.isEmpty()) {
                ui.showResult("No sort order set. Use 'sort amount' or 'sort date'");
            } else {
                ui.showResult("Current sort: " + currentField + " " + transactions.getGlobalSortDirection());
            }
            return;
        }

        String[] parts = rawArgs.trim().split("\\s+");
        String field = parts[0].toLowerCase();

        if (field.equals("reset")) {
            transactions.clearGlobalSort();
            ui.showResult("Sort order cleared");
            return;
        }

        String direction = parts.length > 1 ? parts[1].toLowerCase() : "asc";

        if (!TransactionSorter.isValidSortField(field)) {
            throw new RLADException("Sort by 'amount' or 'date' only");
        }

        if (!TransactionSorter.isValidDirection(direction)) {
            throw new RLADException("Direction must be 'asc' or 'desc'");
        }

        transactions.setGlobalSort(field, direction);
        ui.showResult(String.format("Sorting by %s (%s)", field, direction));
    }

    @Override
    public boolean hasValidArgs() {
        return true;
    }
}
