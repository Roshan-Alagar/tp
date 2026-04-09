package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchCommand extends Command {

    private static final String DIVIDER = "-".repeat(75);

    public SearchCommand(String rawArgs) {
        super(rawArgs);
    }

    private String parseKeyword() {
        Map<String, String> flags = FilterCommand.parseFlags(rawArgs);
        String keyword = flags.get("keyword");
        return (keyword != null && !keyword.isEmpty()) ? keyword : null;
    }

    private boolean matchesKeyword(Transaction t, String keyword) {
        String lower = keyword.toLowerCase();
        if (t.getDescription().toLowerCase().contains(lower)) {
            return true;
        }
        if (t.getCategory() != null && t.getCategory().toLowerCase().contains(lower)) {
            return true;
        }
        if (t.getHashId().toLowerCase().contains(lower)) {
            return true;
        }
        if (String.format("%.2f", t.getAmount()).contains(lower)) {
            return true;
        }
        return String.valueOf(t.getAmount()).contains(lower);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException("Missing required field: --keyword");
        }

        String keyword = parseKeyword();
        List<Transaction> results = transactions.getTransactions().stream()
                .filter(t -> matchesKeyword(t, keyword))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            ui.showResult("No transactions found matching: \"" + keyword + "\"");
            return;
        }

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
                    (t.getCategory() == null || t.getCategory().isEmpty())
                            ? "(none)" : t.getCategory(),
                    (t.getDescription() == null || t.getDescription().isEmpty())
                            ? "(none)" : t.getDescription()));
        }
        ui.showResult(DIVIDER);
        ui.showResult("  " + results.size() + " transaction(s) found for: \"" + keyword + "\"");
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && rawArgs.contains("--keyword") && parseKeyword() != null;
    }
}
