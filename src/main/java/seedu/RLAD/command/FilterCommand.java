package seedu.RLAD.command;

import java.util.function.Predicate;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;
/**
 * FilterCommand handles filtering transactions based on various criteria.
 * Provides shared filtering logic that can be used by other commands
 * (e.g., ListCommand, DeleteCommand).
 */

public class FilterCommand extends Command {

    public FilterCommand(String rawArgs) {
        super(rawArgs);
    }

    /**
     * Builds a Predicate based on the provided filter criteria.
     * TODO: Implement parsing of rawArgs to support:
     * - --category (exact match)
     * - --type (credit/debit)
     * - --amount with operators (-gt, -gte, -eq, -lt, -leq)
     * - --date (exact match) day.month.year
     * - --date-from (range start) day.month.year
     * - --date-to (range end) format day.month.year
     *
     * @return Predicate that can be used to filter transactions
     */
    public static Predicate<Transaction> buildPredicate(String rawArgs) {
        // TODO: Implement predicate building logic
        return transaction -> true; // Returns all transactions for now
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        // TODO: Implement filtering logic using buildPredicate()
        ui.showResult("FilterCommand logic will be implemented here.");
    }

    @Override
    public boolean hasValidArgs() {
        // TODO: Validate filter arguments
        return true;
    }
}
