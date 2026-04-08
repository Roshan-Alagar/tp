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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for DeleteCommand — covers valid deletion, missing ID, invalid ID, and edge cases.
 */
public class DeleteCommandTest {

    private TransactionManager tm;
    private Transaction sampleTransaction;
    private String sampleHashId;

    private static class TestUi extends Ui {
        final List<String> lines = new ArrayList<>();

        public TestUi() {
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
        sampleTransaction = new Transaction("debit", "food", 15.00,
                LocalDate.of(2026, 1, 10), "Lunch");
        tm.addTransaction(sampleTransaction);
        sampleHashId = sampleTransaction.getHashId();
    }

    @Test
    void execute_validHashId_deletesTransaction() throws RLADException {
        TestUi ui = new TestUi();
        // Fix: Pass just the hashId, not "--hashID " + hashId
        new DeleteCommand(sampleHashId).execute(tm, ui);
        assertEquals(0, tm.getTransactions().size(),
                "Transaction should be removed from TransactionManager");
    }

    @Test
    void execute_validHashId_showsSuccessMessage() throws RLADException {
        TestUi ui = new TestUi();
        new DeleteCommand(sampleHashId).execute(tm, ui);
        assertTrue(ui.allOutput().contains("deleted successfully"),
                "Success message should be shown");
    }

    @Test
    void execute_validHashId_showsHashIdInMessage() throws RLADException {
        TestUi ui = new TestUi();
        new DeleteCommand(sampleHashId).execute(tm, ui);
        assertTrue(ui.allOutput().contains(sampleHashId),
                "Output should contain the deleted transaction's hash ID");
    }

    @Test
    void execute_missingHashIdFlag_throwsException() {
        TestUi ui = new TestUi();
        assertThrows(RLADException.class, () ->
                        new DeleteCommand("").execute(tm, ui),
                "Should throw when hash ID is missing");
    }

    @Test
    void execute_nullArgs_throwsException() {
        TestUi ui = new TestUi();
        assertThrows(RLADException.class, () ->
                        new DeleteCommand(null).execute(tm, ui),
                "Should throw when rawArgs is null");
    }

    @Test
    void execute_nonExistentHashId_throwsException() {
        TestUi ui = new TestUi();
        assertThrows(RLADException.class, () ->
                        new DeleteCommand("xxxxxx").execute(tm, ui),
                "Should throw when transaction ID does not exist");
    }

    @Test
    void execute_nonExistentHashId_doesNotModifyManager() {
        TestUi ui = new TestUi();
        int sizeBefore = tm.getTransactions().size();
        try {
            new DeleteCommand("xxxxxx").execute(tm, ui);
        } catch (RLADException e) {
            // expected
        }
        assertEquals(sizeBefore, tm.getTransactions().size(),
                "TransactionManager should not be modified when ID not found");
    }

    @Test
    void hasValidArgs_withValidHashId_returnsTrue() {
        assertTrue(new DeleteCommand(sampleHashId).hasValidArgs());
    }

    @Test
    void hasValidArgs_missingFlag_returnsFalse() {
        assertFalse(new DeleteCommand("").hasValidArgs());
    }

    @Test
    void hasValidArgs_nullArgs_returnsFalse() {
        assertFalse(new DeleteCommand(null).hasValidArgs());
    }

    @Test
    void execute_deletesCorrectTransaction_othersRemain() throws RLADException {
        TestUi ui = new TestUi();
        Transaction other = new Transaction("credit", "salary", 3000.00,
                LocalDate.of(2026, 2, 1), "Pay");
        tm.addTransaction(other);
        String otherHashId = other.getHashId();

        new DeleteCommand(sampleHashId).execute(tm, ui);

        assertEquals(1, tm.getTransactions().size(),
                "Only one transaction should remain");
        assertEquals(otherHashId, tm.getTransactions().get(0).getHashId(),
                "The remaining transaction should be the other one");
    }

    @Test
    void execute_emptyManager_throwsException() {
        TestUi ui = new TestUi();
        TransactionManager emptyTm = new TransactionManager();
        assertThrows(RLADException.class, () ->
                        new DeleteCommand("abcd").execute(emptyTm, ui),
                "Should throw when TransactionManager is empty");
    }
}
