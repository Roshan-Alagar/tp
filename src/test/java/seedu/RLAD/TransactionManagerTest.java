package seedu.RLAD;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionManagerTest {
    private TransactionManager tm;

    @BeforeEach
    void setUp() {
        tm = new TransactionManager(); // Re-initializes for every test
    }

    @Test
    void testAddTransaction() {
        Transaction t = new Transaction("debit", "food", 10.0, LocalDate.now(),
                "Lunch");
        tm.addTransaction(t);

        assertEquals(1, tm.getTransactions().size());
        assertNotNull(tm.findTransaction(t.getHashId()));

        assertThrows(NullPointerException.class, () -> {
            tm.addTransaction(null);
        });

        Transaction t2 = new Transaction("debit", "food", 10.0, LocalDate.now(),
                "Lunch");
        t2.setHashId(t.getHashId());
        tm.addTransaction(t2);

        // verifies whether the hash regeneration works as expected
        assertNotEquals(t.getHashId(), tm.getTransactions().get(1).getHashId());
    }

    @Test
    void findTransaction_nonExistentId_returnsNull() {
        assertNull(tm.findTransaction("9999"));
    }

    @Test
    void deleteTransaction_existingId_removesFromStorage() {
        Transaction t = new Transaction("debit", "food", 10.0, LocalDate.now(), "Lunch");
        Transaction t2 = new Transaction("debit", "food", 10.0, LocalDate.now(),
                "Lunch");

        tm.addTransaction(t);
        tm.addTransaction(t2);

        assertTrue(tm.deleteTransaction(t.getHashId()));
        assertEquals(1, tm.getTransactions().size());
        assertNull(tm.findTransaction(t.getHashId()));
    }

    @Test
    void deleteTransaction_nonExistentId_returnsFalse() {
        Transaction t = new Transaction("debit", "food", 10.0, LocalDate.now(), "Lunch");
        tm.addTransaction(t);
        tm.deleteTransaction(UUID.randomUUID().toString().substring(0, 6));
    }

    @Test
    void findTransaction_existing_returnsNull() {
        Transaction t = new Transaction("debit", "food", 10.0, LocalDate.now(), "Lunch");
        Transaction t2 = new Transaction("debit", "food", 10.0, LocalDate.now(),
                "Lunch");
        Transaction t3 = new Transaction("debit", "food", 10.0, LocalDate.now(),
                "Lunch");

        tm.addTransaction(t);
        tm.addTransaction(t2);
        tm.addTransaction(t3);

        // Checks whether it finds the right transactions
        assertEquals(tm.findTransaction(t.getHashId()), t);
        // Able to deal gracefully with `clones`
        assertNotEquals(tm.findTransaction(t.getHashId()), t2);
    }
    @Test
    void updateTransaction_validId_updatesBothStorageStructures() {
        Transaction oldT = new Transaction("debit", "food", 10.0, LocalDate.now(), "Original");
        tm.addTransaction(oldT);
        String targetId = oldT.getHashId();

        Transaction newT = new Transaction("debit", "food", 50.0, LocalDate.now(), "Updated");
        newT.setHashId(targetId);

        boolean isSuccessful = tm.updateTransaction(targetId, newT);

        assertTrue(isSuccessful, "Update should return true for existing ID");

        assertEquals(50.0, tm.findTransaction(targetId).getAmount());
        assertEquals("Updated", tm.findTransaction(targetId).getDescription());

        assertEquals(1, tm.getTransactions().size());
        assertEquals(50.0, tm.getTransactions().get(0).getAmount());
    }

    @Test
    void updateTransaction_nonExistentId_returnsFalse() {
        Transaction newT = new Transaction("debit", "travel", 100.0, LocalDate.now(), "Flight");

        boolean isSuccessful = tm.updateTransaction("fakeID", newT);

        assertFalse(isSuccessful, "Update should return false if ID is not found");
        assertEquals(0, tm.getTransactions().size(), "List should remain empty");
    }

    @Test
    void addTransaction_notifiesBudgetManager() {
        // TODO: Implement mock BudgetManager to verify notification signal
    }

    // ===== Case-insensitive HashID Tests =====

    @Test
    void findTransaction_uppercaseInput_matchesLowercaseStoredId() {
        Transaction t = new Transaction("debit", "food", 10.0, LocalDate.now(), "Lunch");
        tm.addTransaction(t);

        String storedId = t.getHashId(); // stored as lowercase
        String uppercaseInput = storedId.toUpperCase();

        assertNotNull(tm.findTransaction(uppercaseInput),
                "Should find transaction using uppercase hashID");
    }

    @Test
    void findTransaction_mixedCaseInput_matchesLowercaseStoredId() {
        Transaction t = new Transaction("debit", "food", 10.0, LocalDate.now(), "Lunch");
        tm.addTransaction(t);

        String storedId = t.getHashId(); // stored as lowercase
        String mixedCaseInput = storedId.substring(0, 3).toUpperCase()
                + storedId.substring(3).toLowerCase();

        assertNotNull(tm.findTransaction(mixedCaseInput),
                "Should find transaction using mixed-case hashID");
    }

    @Test
    void findTransaction_lowercaseInput_stillMatches() {
        Transaction t = new Transaction("debit", "food", 10.0, LocalDate.now(), "Lunch");
        tm.addTransaction(t);

        String storedId = t.getHashId(); // stored as lowercase

        assertNotNull(tm.findTransaction(storedId),
                "Should find transaction using exact lowercase hashID");
    }

    @Test
    void deleteTransaction_uppercaseInput_deletesCorrectTransaction() {
        Transaction t = new Transaction("debit", "food", 10.0, LocalDate.now(), "Lunch");
        tm.addTransaction(t);

        String storedId = t.getHashId();
        String uppercaseInput = storedId.toUpperCase();

        assertTrue(tm.deleteTransaction(uppercaseInput),
                "Delete should succeed with uppercase hashID");
        assertNull(tm.findTransaction(storedId),
                "Transaction should no longer exist after deletion");
        assertEquals(0, tm.getTransactions().size(),
                "All transactions should be deleted");
    }

    @Test
    void deleteTransaction_mixedCaseInput_deletesCorrectTransaction() {
        Transaction t = new Transaction("debit", "food", 10.0, LocalDate.now(), "Lunch");
        tm.addTransaction(t);

        String storedId = t.getHashId();
        String mixedCaseInput = storedId.substring(0, 2).toUpperCase()
                + storedId.substring(2);

        assertTrue(tm.deleteTransaction(mixedCaseInput),
                "Delete should succeed with mixed-case hashID");
        assertNull(tm.findTransaction(storedId),
                "Transaction should no longer exist after deletion");
    }

    @Test
    void updateTransaction_uppercaseInput_updatesCorrectTransaction() {
        Transaction oldT = new Transaction("debit", "food", 10.0, LocalDate.now(), "Original");
        tm.addTransaction(oldT);

        String storedId = oldT.getHashId();
        String uppercaseInput = storedId.toUpperCase();

        Transaction updated = new Transaction("credit", "salary", 50.0, LocalDate.now(), "Updated");
        updated.setHashId(storedId);

        assertTrue(tm.updateTransaction(uppercaseInput, updated),
                "Update should succeed with uppercase hashID");
        assertEquals("Updated", tm.findTransaction(storedId).getDescription(),
                "Transaction description should be updated");
    }

    @Test
    void updateTransaction_mixedCaseInput_updatesCorrectTransaction() {
        Transaction oldT = new Transaction("debit", "food", 10.0, LocalDate.now(), "Original");
        tm.addTransaction(oldT);

        String storedId = oldT.getHashId();
        String mixedCaseInput = storedId.substring(0, 4).toUpperCase()
                + storedId.substring(4).toLowerCase();

        Transaction updated = new Transaction("credit", "salary", 50.0, LocalDate.now(), "Updated");
        updated.setHashId(storedId);

        assertTrue(tm.updateTransaction(mixedCaseInput, updated),
                "Update should succeed with mixed-case hashID");
        assertEquals("Updated", tm.findTransaction(storedId).getDescription(),
                "Transaction description should be updated");
    }

    @Test
    void findTransaction_allCaseVariations_findSameTransaction() {
        Transaction t = new Transaction("debit", "food", 10.0, LocalDate.now(), "Lunch");
        tm.addTransaction(t);

        String storedId = t.getHashId();
        String uppercase = storedId.toUpperCase();
        String lowercase = storedId.toLowerCase();
        String mixedCase = storedId.substring(0, 2).toUpperCase()
                + storedId.substring(2, 4).toLowerCase()
                + storedId.substring(4).toUpperCase();

        Transaction foundUpper = tm.findTransaction(uppercase);
        Transaction foundLower = tm.findTransaction(lowercase);
        Transaction foundMixed = tm.findTransaction(mixedCase);

        assertNotNull(foundUpper, "Uppercase should match");
        assertNotNull(foundLower, "Lowercase should match");
        assertNotNull(foundMixed, "Mixed case should match");

        assertEquals(t.getHashId(), foundUpper.getHashId(), "All variants should return same transaction");
        assertEquals(t.getHashId(), foundLower.getHashId(), "All variants should return same transaction");
        assertEquals(t.getHashId(), foundMixed.getHashId(), "All variants should return same transaction");
    }
}
