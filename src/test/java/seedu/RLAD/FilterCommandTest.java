package seedu.RLAD;

import org.junit.jupiter.api.Test;
import seedu.RLAD.command.FilterCommand;
import seedu.RLAD.exception.RLADException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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
    public void buildPredicate_nullArgs_assertionError() {
        assertThrows(AssertionError.class, () -> FilterCommand.buildPredicate(null));
    }

    @Test
    public void buildPredicate_emptyArgs_returnsAll() {
        ArrayList<Transaction> results = createSampleTransactions();
        Predicate<Transaction> predicate = FilterCommand.buildPredicate("");
        long count = results.stream().filter(predicate).count();
        assertEquals(5, count);
    }

    // Fix: Use --type flag syntax
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

    // Fix: Use --category flag syntax
    @Test
    public void buildPredicate_filterByCategory_matchesCategory() {
        ArrayList<Transaction> results = applyFilter("--category food");
        assertEquals(2, results.size());
        for (Transaction t : results) {
            assertEquals("food", t.getCategory());
        }
    }

    // Fix: Use --amount with operators
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
    public void buildPredicate_invalidCalendarDate_throwsException() {
        assertThrows(RLADException.class, () -> FilterCommand.buildPredicate("--date 2024-02-30"));
        assertThrows(RLADException.class, () -> FilterCommand.buildPredicate("--date 2024.02.30"));
        assertThrows(RLADException.class, () -> FilterCommand.buildPredicate("--date 2024.06.31"));
    }

    // --- Enhanced category filter tests ---

    private ArrayList<Transaction> createCategoryTestTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("debit", "Food", 10.00,
                LocalDate.of(2024, 3, 1), "Lunch"));
        transactions.add(new Transaction("debit", "fast food", 8.00,
                LocalDate.of(2024, 3, 2), "McDonalds"));
        transactions.add(new Transaction("debit", "food delivery", 15.00,
                LocalDate.of(2024, 3, 3), "GrabFood"));
        transactions.add(new Transaction("debit", "Transport", 5.00,
                LocalDate.of(2024, 3, 4), "Bus"));
        transactions.add(new Transaction("credit", "Savings", 100.00,
                LocalDate.of(2024, 3, 5), "Monthly savings"));
        return transactions;
    }

    private ArrayList<Transaction> applyCategoryFilter(String rawArgs) {
        ArrayList<Transaction> transactions = createCategoryTestTransactions();
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
    public void buildPredicate_categoryPartialMatch_matchesSubstring() {
        ArrayList<Transaction> results = applyCategoryFilter("--category food");
        assertEquals(3, results.size());
    }

    @Test
    public void buildPredicate_categoryPartialMatchCaseInsensitive_matches() {
        ArrayList<Transaction> results = applyCategoryFilter("--category FOOD");
        assertEquals(3, results.size());
    }

    @Test
    public void buildPredicate_categoryMultiple_matchesEither() {
        ArrayList<Transaction> results = applyCategoryFilter("--category food,transport");
        assertEquals(4, results.size());
    }

    @Test
    public void buildPredicate_categoryMultipleWithSpaces_matchesEither() {
        ArrayList<Transaction> results = applyCategoryFilter("--category food, transport");
        assertEquals(4, results.size());
    }

    @Test
    public void buildPredicate_categoryCode1_matchesFood() {
        ArrayList<Transaction> results = applyCategoryFilter("--category 1");
        assertEquals(1, results.size());
        assertEquals("Food", results.get(0).getCategory());
    }

    @Test
    public void buildPredicate_categoryCode12_matchesSavings() {
        ArrayList<Transaction> results = applyCategoryFilter("--category 12");
        assertEquals(1, results.size());
        assertEquals("Savings", results.get(0).getCategory());
    }

    @Test
    public void buildPredicate_categoryInvalidCode_throwsException() {
        assertThrows(RLADException.class, () -> FilterCommand.buildPredicate("--category 99"));
    }

    // --- Enhanced date filter tests ---

    private ArrayList<Transaction> createDateTestTransactions() {
        LocalDate today = LocalDate.now();
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("debit", "food", 10.00, today, "Today"));
        transactions.add(new Transaction("debit", "food", 10.00,
                today.minusDays(1), "Yesterday"));
        transactions.add(new Transaction("debit", "food", 10.00,
                today.minusDays(7), "Last week"));
        transactions.add(new Transaction("debit", "food", 10.00,
                today.minusMonths(2), "Two months ago"));
        return transactions;
    }

    private ArrayList<Transaction> applyDateFilter(String rawArgs) {
        ArrayList<Transaction> transactions = createDateTestTransactions();
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
    public void buildPredicate_dateFromToday_matchesTodayOnly() {
        ArrayList<Transaction> results = applyDateFilter("--date-from today --date-to today");
        assertEquals(1, results.size());
        assertEquals(LocalDate.now(), results.get(0).getDate());
    }

    @Test
    public void buildPredicate_dateFromYesterday_matchesTwoDays() {
        ArrayList<Transaction> results = applyDateFilter("--date-from yesterday");
        assertEquals(2, results.size());
    }

    @Test
    public void buildPredicate_dateFromThisWeek_filtersCorrectly() {
        LocalDate mondayThisWeek = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ArrayList<Transaction> results = applyDateFilter("--date-from this-week");
        for (Transaction t : results) {
            assertFalse(t.getDate().isBefore(mondayThisWeek));
        }
    }

    @Test
    public void buildPredicate_dateFromThisMonth_filtersCorrectly() {
        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);
        ArrayList<Transaction> results = applyDateFilter("--date-from this-month");
        for (Transaction t : results) {
            assertFalse(t.getDate().isBefore(firstOfMonth));
        }
    }

    @Test
    public void buildPredicate_dateFromLastMonth_filtersCorrectly() {
        LocalDate firstOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        ArrayList<Transaction> results = applyDateFilter("--date-from last-month");
        for (Transaction t : results) {
            assertFalse(t.getDate().isBefore(firstOfLastMonth));
        }
    }

    @Test
    public void buildPredicate_dateRangeInvalid_throwsException() {
        assertThrows(RLADException.class,
                () -> FilterCommand.buildPredicate("--date-from 2024-12-31 --date-to 2024-01-01"));
    }

    @Test
    public void buildPredicate_dateFromTomorrow_matchesNone() {
        ArrayList<Transaction> results = applyDateFilter("--date-from tomorrow");
        assertEquals(0, results.size());
    }

    @Test
    public void buildPredicate_combinedCategoryAndDate_filtersCorrectly() {
        ArrayList<Transaction> results = applyDateFilter("--category food --date-from yesterday");
        assertEquals(2, results.size());
    }

    @Test
    public void buildPredicate_dateFromLastYear_filtersCorrectly() {
        ArrayList<Transaction> results = applyDateFilter("--date-from last-year");
        assertEquals(4, results.size());
    }

    @Test
    public void buildPredicate_dateToRelativeKeyword_filtersCorrectly() {
        ArrayList<Transaction> results = applyDateFilter("--date-to yesterday");
        for (Transaction t : results) {
            assertFalse(t.getDate().isAfter(LocalDate.now().minusDays(1)));
        }
    }

    @Test
    public void buildPredicate_categoryMultipleWithEmptyEntries_matchesValidOnes() {
        ArrayList<Transaction> results = applyCategoryFilter("--category food,,transport");
        assertEquals(4, results.size());
    }

    @Test
    public void buildPredicate_categoryNoMatch_returnsEmpty() {
        ArrayList<Transaction> results = applyCategoryFilter("--category nonexistent");
        assertEquals(0, results.size());
    }

    @Test
    public void applyColonFilters_minGreaterThanMax_throwsException() {
        ArrayList<Transaction> transactions = createSampleTransactions();
        assertThrows(RLADException.class,
                () -> FilterCommand.applyColonFilters(transactions, "min:100 max:50"));
    }

    @Test
    public void applyColonFilters_validMinMax_filtersCorrectly() throws RLADException {
        ArrayList<Transaction> transactions = createSampleTransactions();
        java.util.List<Transaction> results =
                FilterCommand.applyColonFilters(transactions, "min:10 max:200");
        for (Transaction t : results) {
            assertTrue(t.getAmount() >= 10 && t.getAmount() <= 200);
        }
        assertEquals(2, results.size());
    }
}
