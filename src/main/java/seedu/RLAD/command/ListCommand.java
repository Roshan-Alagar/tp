package seedu.RLAD.command;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

public class ListCommand extends Command {

    public ListCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        // 1. Generate the filter using the shared logic in FilterCommand
        Predicate<Transaction> filter = FilterCommand.buildPredicate(this.rawArgs);

        // 2. Filter the list using Java Streams
        List<Transaction> filteredResults = transactions.getTransactions().stream()
                .filter(filter)
                .collect(Collectors.toList());

        // 3. Display logic
        if (filteredResults.isEmpty()) {
            ui.showResult("Your wallet is empty or no transactions match your criteria.");
            return;
        }

        // TODO: Apply sorting to filteredResults here (e.g., Collections.sort)

        for (Transaction t : filteredResults) {
            ui.showResult(t.toString());
        }
    }

    @Override
    public boolean hasValidArgs() {
        // Check if rawArgs contains valid flags like --type, --category, etc.
        // Return false if invalid flags are detected to prevent execution
        return true;
    }
}
