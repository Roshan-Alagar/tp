package seedu.RLAD.command;

import java.util.List;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

/**
 * ListCommand displays transactions, with optional filtering and sorting.
 *
 * Supported flags (all optional):
 *   --type       credit | debit
 *   --category   any string
 *   --amount     [operator] value  e.g. "-gt 50"
 *   --date       exact date
 *   --date-from  range start
 *   --date-to    range end
 *   --sort       date | amount
 *
 * Examples:
 *   list
 *   list --type credit
 *   list --category food --sort amount
 *   list --date-from 2024-01-01 --date-to 2024-03-01 --sort date
 */


public class ListCommand extends Command {

    private static final String DIVIDER = "-".repeat(75);

    public ListCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        // 1. Parse flags from rawArgs
        Map<String, String> flags = FilterCommand.parseFlags(this.rawArgs);

        // 2. Validate --sort value if provided
        String sortBy = null;
        if (flags.containsKey("sort")) {
            sortBy = flags.get("sort").toLowerCase();
            if (!sortBy.equals("date") && !sortBy.equals("amount")) {
                throw new RLADException("--sort must be 'date' or 'amount', got: '" + sortBy + "'");
            }
        }

        // 3. Build filter predicate via shared FilterCommand logic
        //    (this also validates --type, --amount operators, date formats, etc.)
        assert transactions != null : "TransactionManager should not be null";
        Predicate<Transaction> filter = FilterCommand.buildPredicate(this.rawArgs);

        // 4. Apply filter — we do NOT modify the original list in TransactionManager
        List<Transaction> results = transactions.getTransactions().stream().filter(filter).collect(Collectors.toList());

        // 5. Handle empty result gracefully
        if (results.isEmpty()) {
            ui.showResult("Empty Wallet — no transactions match your criteria.");
            return;
        }

        // 6. Apply sorting if --sort was supplied
        if (sortBy != null) {
            switch (sortBy) {
            case "date":
                results.sort(Comparator.comparing(Transaction::getDate));
                break;
            case "amount":
                results.sort(Comparator.comparingDouble(Transaction::getAmount));
                break;
            default:
                break; // already validated above
            }
        }

        // 7. Print formatted table
        ui.showResult(DIVIDER);
        ui.showResult(String.format("  %-6s %-8s %-12s %10s  %-12s  %s",
                "ID", "TYPE", "DATE", "AMOUNT", "CATEGORY", "DESCRIPTION"));
        ui.showResult(DIVIDER);
        for (Transaction t : results) {
            ui.showResult(String.format("  %-6s %-8s %-12s %10s  %-12s  %s",
                    t.getHashId(),
                    t.getType().toUpperCase(),
                    t.getDate().toString(),
                    String.format("$%.2f", t.getAmount()),
                    t.getCategory().isEmpty() ? "(none)" : t.getCategory(),
                    t.getDescription().isEmpty() ? "(none)" : t.getDescription()));
        }
        ui.showResult(DIVIDER);
        ui.showResult("  Total: " + results.size() + " transaction(s) shown.");
    }

    @Override
    public boolean hasValidArgs() {
        // No required flags for list — all are optional
        return true;
    }
}
