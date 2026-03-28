package seedu.RLAD;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TransactionSorterTest {
    private ArrayList<Transaction> createSampleTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("debit", "food", 25.00,
                LocalDate.of(2026, 2, 15), "Lunch"));
        transactions.add(new Transaction("credit", "salary", 3000.00,
                LocalDate.of(2026, 1, 1), "Monthly salary"));
        transactions.add(new Transaction("debit", "transport", 5.50,
                LocalDate.of(2026, 2, 10), "Bus fare"));
        return transactions;
    }

    @Test
    public void sortByAmount_unsortedList_sortedAscending() {
        ArrayList<Transaction> transactions = createSampleTransactions();
        ArrayList<Transaction> sorted = TransactionSorter.sort(transactions, "amount");

        assertEquals(5.50, sorted.get(0).getAmount());
        assertEquals(25.00, sorted.get(1).getAmount());
        assertEquals(3000.00, sorted.get(2).getAmount());
    }

    @Test
    public void sortByDate_unsortedList_sortedAscending() {
        ArrayList<Transaction> transactions = createSampleTransactions();
        ArrayList<Transaction> sorted = TransactionSorter.sort(transactions, "date");

        assertEquals(LocalDate.of(2026, 1, 1), sorted.get(0).getDate());
        assertEquals(LocalDate.of(2026, 2, 10), sorted.get(1).getDate());
        assertEquals(LocalDate.of(2026, 2, 15), sorted.get(2).getDate());
    }

    @Test
    public void sort_emptyList_returnsEmptyList() {
        ArrayList<Transaction> empty = new ArrayList<>();
        ArrayList<Transaction> sorted = TransactionSorter.sort(empty, "amount");

        assertTrue(sorted.isEmpty());
    }

    @Test
    public void sort_invalidField_returnsOriginalOrder() {
        ArrayList<Transaction> transactions = createSampleTransactions();
        double firstAmount = transactions.get(0).getAmount();

        ArrayList<Transaction> sorted = TransactionSorter.sort(transactions, "invalid");

        assertEquals(firstAmount, sorted.get(0).getAmount());
    }

    @Test
    public void sort_doesNotModifyOriginalList() {
        ArrayList<Transaction> transactions = createSampleTransactions();
        double originalFirstAmount = transactions.get(0).getAmount();

        TransactionSorter.sort(transactions, "amount");

        assertEquals(originalFirstAmount, transactions.get(0).getAmount());
    }

    @Test
    public void isValidSortField_validFields_returnsTrue() {
        assertTrue(TransactionSorter.isValidSortField("amount"));
        assertTrue(TransactionSorter.isValidSortField("date"));
    }

    @Test
    public void isValidSortField_invalidField_returnsFalse() {
        assertFalse(TransactionSorter.isValidSortField("name"));
        assertFalse(TransactionSorter.isValidSortField(""));
    }
}
