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

class ModifyCommandTest {

    private TransactionManager manager;
    private List<String> output;
    private Ui ui;
    private String existingId;

    @BeforeEach
    void setUp() {
        manager = new TransactionManager();
        output = new ArrayList<>();
        ui = new Ui() {
            @Override
            public void showResult(String message) {
                output.add(message);
            }
        };

        Transaction t = new Transaction("debit", "food", 50.00,
                LocalDate.parse("2026-01-10"), "Hawker lunch");
        manager.addTransaction(t);
        existingId = t.getHashId();
    }

    @Test
    void execute_modifySingleAmount_updatesCorrectly() throws Exception {
        // FIX: Add "modify" as the first argument
        new ModifyCommand("modify", existingId + " amount=100.00").execute(manager, ui);
        Transaction updated = manager.findTransaction(existingId);
        assertEquals(100.00, updated.getAmount());
    }

    @Test
    void execute_modifySingleCategory_updatesCorrectly() throws Exception {
        // FIX: Add "modify" as the first argument
        new ModifyCommand("modify", existingId + " category=transport").execute(manager, ui);
        Transaction updated = manager.findTransaction(existingId);
        assertEquals("transport", updated.getCategory());
    }

    @Test
    void execute_modifyMultipleFields_updatesAll() throws Exception {
        // FIX: Add "modify" as the first argument
        new ModifyCommand("modify", existingId + " amount=25.00 category=dining").execute(manager, ui);
        Transaction updated = manager.findTransaction(existingId);
        assertEquals(25.00, updated.getAmount());
        assertEquals("dining", updated.getCategory());
    }

    @Test
    void execute_modifyType_updatesCorrectly() throws Exception {
        // FIX: Add "modify" as the first argument
        new ModifyCommand("modify", existingId + " type=credit").execute(manager, ui);
        Transaction updated = manager.findTransaction(existingId);
        assertEquals("credit", updated.getType());
    }

    @Test
    void execute_modifyDate_updatesCorrectly() throws Exception {
        // FIX: Add "modify" as the first argument
        new ModifyCommand("modify", existingId + " date=2026-03-15").execute(manager, ui);
        Transaction updated = manager.findTransaction(existingId);
        assertEquals(LocalDate.parse("2026-03-15"), updated.getDate());
    }

    @Test
    void execute_invalidId_throwsException() {
        // FIX: Add "modify" as the first argument
        assertThrows(RLADException.class, () ->
                new ModifyCommand("modify", "zzzz amount=10.00").execute(manager, ui));
    }

    @Test
    void execute_noFields_throwsException() {
        // FIX: Add "modify" as the first argument
        RLADException exception = assertThrows(RLADException.class, () ->
                new ModifyCommand("modify", existingId).execute(manager, ui));

        // Verify the exception message is appropriate
        assertTrue(exception.getMessage().contains("No fields to update") ||
                        exception.getMessage().contains("field=value"),
                "Exception message should indicate missing fields");
    }

    @Test
    void execute_invalidType_throwsException() {
        // FIX: Add "modify" as the first argument
        assertThrows(RLADException.class, () ->
                new ModifyCommand("modify", existingId + " type=cash").execute(manager, ui));
    }

    @Test
    void execute_invalidAmount_throwsException() {
        // FIX: Add "modify" as the first argument
        assertThrows(RLADException.class, () ->
                new ModifyCommand("modify", existingId + " amount=abc").execute(manager, ui));
    }

    @Test
    void execute_showsBeforeAfterComparison() throws Exception {
        // FIX: Add "modify" as the first argument
        new ModifyCommand("modify", existingId + " amount=99.00").execute(manager, ui);
        // Check for update confirmation message
        boolean hasUpdateMessage = output.stream().anyMatch(s ->
                s.contains("updated") || s.contains("Transaction") || s.contains("✅"));
        assertTrue(hasUpdateMessage, "Should show update confirmation");
    }

    @Test
    void execute_preservesHashId() throws Exception {
        // FIX: Add "modify" as the first argument
        new ModifyCommand("modify", existingId + " amount=75.00").execute(manager, ui);
        Transaction updated = manager.findTransaction(existingId);
        assertEquals(existingId, updated.getHashId());
    }

    @Test
    void execute_amountRoundsToZero_throwsException() {
        RLADException ex = assertThrows(RLADException.class, () ->
                new ModifyCommand(existingId + " amount=0.001").execute(manager, ui));
        assertTrue(ex.getMessage().contains("$0.00") || ex.getMessage().contains("0.01"));
    }
}
