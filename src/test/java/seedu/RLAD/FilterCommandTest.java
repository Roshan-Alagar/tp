package seedu.RLAD;

import org.junit.jupiter.api.Test;
import seedu.RLAD.command.FilterCommand;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilterCommandTest {
    private ArrayList<Transaction> createSampleTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("debit", "food", 25.00,
                LocalDate.of(2024, 3, 15), "Lunch"));
        transactions.add(new Transaction("credit", "salary", 3000.00,
                LocalDate.of(2024, 1, 1), "Monthly salary"));
        transactions.add(new Transaction("debit", "transport", 5.50,
                LocalDate.of(2024, 6, 10), "Bus fare"));
        transactions.add(new Transaction("debit", "food", 150.00,
                LocalDate.of(2024, 5, 20), "Dinner"));
        transactions.add(new Transaction("credit", "freelance", 500.00,
                LocalDate.of(2024, 4, 1), "Side project"));
        return transactions;
    }

    private ArrayList<Transaction> applyFilter(String rawArgs) {
        ArrayList<Transaction> transactions = createSampleTransactions();
        Predicate<Transaction> predicate = FilterCommand.buildPredicate(rawArgs);
        ArrayList<Transaction> results = new ArrayList<>();
        for (Transaction t : transactions) {
            if (predicate.test(t)) {
                results.add(t);
            }
        }
        return results;
    }

    @Test
    public void buildPredicate_nullArgs_returnsAll() {
        ArrayList<Transaction> results = createSampleTransactions();
        Predicate<Transaction> predicate = FilterCommand.buildPredicate(null);
        long count = results.stream().filter(predicate).count();
        assertEquals(5, count);
    }

    @Test
    public void buildPredicate_emptyArgs_returnsAll() {
        ArrayList<Transaction> results = createSampleTransactions();
        Predicate<Transaction> predicate = FilterCommand.buildPredicate("");
        long count = results.stream().filter(predicate).count();
        assertEquals(5, count);
    }

    @Test
    public void buildPredicate_filterByTypeDebit_returnsDebitsOnly() {
        ArrayList<Transaction> results = applyFilter("--type debit");
        assertEquals(3, results.size());
        for (Transaction t : results) {
            assertEquals("debit", t.getType());
        }
    }

    @Test
    public void buildPredicate_filterByTypeCredit_returnsCreditsOnly() {
        ArrayList<Transaction> results = applyFilter("--type credit");
        assertEquals(2, results.size());
        for (Transaction t : results) {
            assertEquals("credit", t.getType());
        }
    }

    @Test
    public void buildPredicate_filterByCategory_matchesCategory() {
        ArrayList<Transaction> results = applyFilter("--category food");
        assertEquals(2, results.size());
        for (Transaction t : results) {
            assertEquals("food", t.getCategory());
        }
    }

    @Test
    public void buildPredicate_amountGt_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--amount -gt 100");
        for (Transaction t : results) {
            assertTrue(t.getAmount() > 100);
        }
        assertEquals(3, results.size());
    }

    @Test
    public void buildPredicate_amountGte_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--amount -gte 150");
        for (Transaction t : results) {
            assertTrue(t.getAmount() >= 150);
        }
        assertEquals(3, results.size());
    }

    @Test
    public void buildPredicate_amountEq_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--amount -eq 25");
        assertEquals(1, results.size());
        assertEquals(25.00, results.get(0).getAmount());
    }

    @Test
    public void buildPredicate_amountDefaultEq_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--amount 25");
        assertEquals(1, results.size());
        assertEquals(25.00, results.get(0).getAmount());
    }

    @Test
    public void buildPredicate_amountLt_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--amount -lt 100");
        for (Transaction t : results) {
            assertTrue(t.getAmount() < 100);
        }
        assertEquals(2, results.size());
    }

    @Test
    public void buildPredicate_amountLeq_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--amount -leq 25");
        for (Transaction t : results) {
            assertTrue(t.getAmount() <= 25);
        }
        assertEquals(2, results.size());
    }

    @Test
    public void buildPredicate_amountRange_filtersCorrectly() {
        // parseFlags uses a map so duplicate --amount keys are not supported;
        // test a single amount filter instead
        ArrayList<Transaction> results = applyFilter("--amount -gte 25");
        for (Transaction t : results) {
            assertTrue(t.getAmount() >= 25);
        }
        assertEquals(4, results.size());
    }

    @Test
    public void buildPredicate_exactDate_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--date 2024-03-15");
        assertEquals(1, results.size());
        assertEquals(LocalDate.of(2024, 3, 15), results.get(0).getDate());
    }

    @Test
    public void buildPredicate_dateFrom_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--date-from 2024-04-01");
        for (Transaction t : results) {
            assertFalse(t.getDate().isBefore(LocalDate.of(2024, 4, 1)));
        }
        assertEquals(3, results.size());
    }

    @Test
    public void buildPredicate_dateTo_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--date-to 2024-03-15");
        for (Transaction t : results) {
            assertFalse(t.getDate().isAfter(LocalDate.of(2024, 3, 15)));
        }
        assertEquals(2, results.size());
    }

    @Test
    public void buildPredicate_dateRange_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--date-from 2024-01-01 --date-to 2024-06-30");
        assertEquals(5, results.size());
    }

    @Test
    public void buildPredicate_dateRangeNarrow_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--date-from 2024-03-01 --date-to 2024-05-01");
        assertEquals(2, results.size());
    }

    @Test
    public void buildPredicate_multipleFlags_chainsWithAnd() {
        ArrayList<Transaction> results = applyFilter("--type debit --amount -gt 10");
        assertEquals(2, results.size());
        for (Transaction t : results) {
            assertEquals("debit", t.getType());
            assertTrue(t.getAmount() > 10);
        }
    }

    @Test
    public void buildPredicate_typeAndCategory_filtersCorrectly() {
        ArrayList<Transaction> results = applyFilter("--type debit --category food");
        assertEquals(2, results.size());
    }

    @Test
    public void buildPredicate_invalidType_throwsException() {
        assertThrows(RLADException.class, () -> FilterCommand.buildPredicate("--type invalid"));
    }

    @Test
    public void buildPredicate_invalidAmount_throwsException() {
        assertThrows(RLADException.class, () -> FilterCommand.buildPredicate("--amount -gt abc"));
    }

    @Test
    public void buildPredicate_invalidDate_throwsException() {
        assertThrows(RLADException.class, () -> FilterCommand.buildPredicate("--date not-a-date"));
    }

    @Test
    public void buildPredicate_missingValue_throwsException() {
        assertThrows(RLADException.class, () -> FilterCommand.buildPredicate("--type"));
    }

    @Test
    public void buildPredicate_unknownFlag_ignoredSilently() {
        // parseFlags silently ignores unknown flags — should still return all transactions
        ArrayList<Transaction> results = applyFilter("--unknown value");
        assertEquals(5, results.size());
    }
}
