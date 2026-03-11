package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SummarizeCommandTest {

    private TransactionManager manager;
    private List<String> output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        manager = new TransactionManager();
        output = new ArrayList<>();
        // Stub Ui to capture showResult calls
        ui = new Ui() {
            @Override
            public void showResult(String message) {
                output.add(message);
            }
        };
    }

    private void addTransaction(String type, String category, double amount, String date) {
        manager.addTransaction(new Transaction(type, category, amount,
                LocalDate.parse(date), null));
    }

    @Test
    void execute_noTransactions_showsEmptyMessage() throws Exception {
        new SummarizeCommand("").execute(manager, ui);
        assertTrue(output.get(0).contains("No transactions found"));
    }

    @Test
    void execute_creditAndDebit_correctTotals() throws Exception {
        addTransaction("credit", "salary", 3000.00, "2026-01-15");
        addTransaction("debit", "food", 50.00, "2026-01-16");
        addTransaction("debit", "food", 25.50, "2026-01-17");

        new SummarizeCommand("").execute(manager, ui);
        String result = output.get(0);

        assertTrue(result.contains("3000.00"), "Total credit should be 3000.00");
        assertTrue(result.contains("75.50"), "Total debit should be 75.50");
        assertTrue(result.contains("2924.50"), "Net balance should be 2924.50");
    }

    @Test
    void execute_categoryBreakdown_groupsCorrectly() throws Exception {
        addTransaction("debit", "food", 20.00, "2026-01-01");
        addTransaction("debit", "food", 30.00, "2026-01-02");
        addTransaction("debit", "transport", 10.00, "2026-01-03");

        new SummarizeCommand("").execute(manager, ui);
        String result = output.get(0);

        assertTrue(result.contains("food"), "Category 'food' should appear");
        assertTrue(result.contains("50.00"), "Food total should be 50.00");
        assertTrue(result.contains("transport"), "Category 'transport' should appear");
        assertTrue(result.contains("10.00"), "Transport total should be 10.00");
    }

    @Test
    void execute_nullCategory_groupedAsUncategorized() throws Exception {
        manager.addTransaction(new Transaction("debit", null, 15.00,
                LocalDate.parse("2026-01-01"), null));

        new SummarizeCommand("").execute(manager, ui);
        String result = output.get(0);

        assertTrue(result.contains("(uncategorized)"), "Null category should appear as (uncategorized)");
    }

    @Test
    void execute_floatingPointPrecision_displaysCorrectly() throws Exception {
        addTransaction("debit", "misc", 0.10, "2026-01-01");
        addTransaction("debit", "misc", 0.20, "2026-01-02");

        new SummarizeCommand("").execute(manager, ui);
        String result = output.get(0);

        assertTrue(result.contains("0.30"), "0.10 + 0.20 should display as 0.30, not 0.30000000000000004");
    }

    @Test
    void hasValidArgs_alwaysTrue() {
        assertTrue(new SummarizeCommand("").hasValidArgs());
        assertTrue(new SummarizeCommand("--type debit").hasValidArgs());
    }
}
