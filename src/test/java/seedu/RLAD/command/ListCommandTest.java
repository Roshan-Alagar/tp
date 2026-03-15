package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Tests for ListCommand — covers filtering, sorting, and edge cases.
 */
public class ListCommandTest {

    private TransactionManager tm;

    // Minimal Ui stub that collects output for assertion
    private static class TestUi extends Ui {
        final List<String> lines = new ArrayList<>();

        public TestUi() {
            // suppress Logo print by not calling super()
        }

        @Override
        public void showResult(String message) {
            lines.add(message);
        }

        @Override
        public void showError(String message) {
            lines.add("ERROR: " + message);
        }

        public String allOutput() {
            return String.join("\n", lines);
        }
    }

    @BeforeEach
    void setUp() {
        tm = new TransactionManager();
        // Add sample transactions
        tm.addTransaction(new Transaction("debit",  "food",      15.00,
                LocalDate.of(2024, 1, 10), "Lunch"));
        tm.addTransaction(new Transaction("credit", "salary",   3000.00,
                LocalDate.of(2024, 2, 1),  "Monthly pay"));
        tm.addTransaction(new Transaction("debit",  "transport", 2.50,
                LocalDate.of(2024, 2, 15), "Bus ride"));
        tm.addTransaction(new Transaction("debit",  "food",      60.00,
                LocalDate.of(2024, 3, 5),  "Dinner"));
        tm.addTransaction(new Transaction("credit", "freelance", 500.00,
                LocalDate.of(2024, 3, 20), "Side project"));
    }

    // ─── Basic listing ────────────────────────────────────────────────────────

    @Test
    void listAll_showsAllTransactions() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("").execute(tm, ui);
        // Should show 5 transactions
        assertTrue(ui.allOutput().contains("5 transaction(s)"));
    }

    // ─── Filtering by --type ──────────────────────────────────────────────────

    @Test
    void filterByType_credit_showsOnlyCredits() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--type credit").execute(tm, ui);
        assertTrue(ui.allOutput().contains("2 transaction(s)"));
        assertFalse(ui.allOutput().contains("DEBIT"));
    }

    @Test
    void filterByType_debit_showsOnlyDebits() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--type debit").execute(tm, ui);
        assertTrue(ui.allOutput().contains("3 transaction(s)"));
        assertFalse(ui.allOutput().contains("CREDIT"));
    }

    @Test
    void filterByType_invalid_throwsException() {
        TestUi ui = new TestUi();
        assertThrows(RLADException.class, () ->
                new ListCommand("--type savings").execute(tm, ui));
    }

    // ─── Filtering by --category ──────────────────────────────────────────────

    @Test
    void filterByCategory_food_showsFoodOnly() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--category food").execute(tm, ui);
        assertTrue(ui.allOutput().contains("2 transaction(s)"));
    }

    // ─── Filtering by --amount ────────────────────────────────────────────────

    @Test
    void filterByAmount_gt50_showsLargeTransactions() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--amount -gt 50").execute(tm, ui);
        // 3000, 60, 500 → 3 results
        assertTrue(ui.allOutput().contains("3 transaction(s)"));
    }

    @Test
    void filterByAmount_lt10_showsSmallTransactions() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--amount -lt 10").execute(tm, ui);
        // only 2.50
        assertTrue(ui.allOutput().contains("1 transaction(s)"));
    }

    // ─── Filtering by --date range ────────────────────────────────────────────

    @Test
    void filterByDateRange_feb_showsFebruaryOnly() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--date-from 2024-02-01 --date-to 2024-02-28").execute(tm, ui);
        // salary (2024-02-01) and bus (2024-02-15) → 2 results
        assertTrue(ui.allOutput().contains("2 transaction(s)"));
    }

    @Test
    void filterByExactDate_showsCorrectTransaction() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--date 2024-01-10").execute(tm, ui);
        assertTrue(ui.allOutput().contains("1 transaction(s)"));
        assertTrue(ui.allOutput().contains("Lunch"));
    }

    // ─── Sorting ──────────────────────────────────────────────────────────────

    @Test
    void sortByAmount_resultsAscending() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--sort amount").execute(tm, ui);
        // All 5 shown — just check no exception and output present
        assertTrue(ui.allOutput().contains("5 transaction(s)"));
        // First data row should contain the smallest amount ($2.50)
        int firstDataLine = ui.lines.indexOf(ui.lines.stream()
                .filter(l -> l.contains("$2.50")).findFirst().orElse(""));
        int lastDataLine = ui.lines.indexOf(ui.lines.stream()
                .filter(l -> l.contains("$3000.00")).findFirst().orElse(""));
        assertTrue(firstDataLine < lastDataLine, "$2.50 should appear before $3000.00");
    }

    @Test
    void sortByDate_resultsChronological() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--sort date").execute(tm, ui);
        assertTrue(ui.allOutput().contains("5 transaction(s)"));
        // 2024-01-10 should appear before 2024-03-20
        int januaryLine = ui.lines.indexOf(ui.lines.stream()
                .filter(l -> l.contains("2024-01-10")).findFirst().orElse(""));
        int marchLine = ui.lines.indexOf(ui.lines.stream()
                .filter(l -> l.contains("2024-03-20")).findFirst().orElse(""));
        assertTrue(januaryLine < marchLine, "January should appear before March");
    }

    @Test
    void sortByInvalid_throwsException() {
        TestUi ui = new TestUi();
        assertThrows(RLADException.class, () ->
                new ListCommand("--sort name").execute(tm, ui));
    }

    // ─── Empty results ────────────────────────────────────────────────────────

    @Test
    void filterWithNoMatch_showsEmptyWalletMessage() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--category nonexistent").execute(tm, ui);
        assertTrue(ui.allOutput().contains("Empty Wallet"));
    }

    @Test
    void emptyTransactionManager_showsEmptyWalletMessage() throws RLADException {
        TestUi ui = new TestUi();
        TransactionManager emptyTm = new TransactionManager();
        new ListCommand("").execute(emptyTm, ui);
        assertTrue(ui.allOutput().contains("Empty Wallet"));
    }

    // ─── Combined filters ─────────────────────────────────────────────────────

    @Test
    void filterByTypeAndCategory_combined() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--type debit --category food").execute(tm, ui);
        assertTrue(ui.allOutput().contains("2 transaction(s)"));
    }

    @Test
    void filterAndSort_combined() throws RLADException {
        TestUi ui = new TestUi();
        new ListCommand("--type debit --sort amount").execute(tm, ui);
        assertTrue(ui.allOutput().contains("3 transaction(s)"));
    }

    // ─── No modification of original data ────────────────────────────────────

    @Test
    void listCommand_doesNotModifyTransactionManager() throws RLADException {
        TestUi ui = new TestUi();
        int originalSize = tm.getTransactions().size();
        new ListCommand("--type credit --sort date").execute(tm, ui);
        assertEquals(originalSize, tm.getTransactions().size(),
                "ListCommand must not modify TransactionManager data");
    }
}
