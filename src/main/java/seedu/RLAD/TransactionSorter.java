package seedu.RLAD;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Provides sorting utilities for transaction lists.
 * Supports sorting by amount or date in ascending or descending order.
 */
public class TransactionSorter {
    public static final String SORT_BY_AMOUNT = "amount";
    public static final String SORT_BY_DATE = "date";

    /**
     * Sorts the given list of transactions by the specified field in ascending order.
     *
     * @param transactions the list of transactions to sort
     * @param sortBy the field to sort by ("amount" or "date")
     * @return a new sorted ArrayList of transactions
     */
    public static ArrayList<Transaction> sort(ArrayList<Transaction> transactions, String sortBy) {
        return sort(transactions, sortBy, "asc");
    }

    /**
     * Sorts the given list of transactions by the specified field and direction.
     *
     * @param transactions the list of transactions to sort
     * @param sortBy the field to sort by ("amount" or "date")
     * @param direction the sort direction ("asc" or "desc")
     * @return a new sorted ArrayList of transactions
     */
    public static ArrayList<Transaction> sort(ArrayList<Transaction> transactions,
                                              String sortBy, String direction) {
        ArrayList<Transaction> sorted = new ArrayList<>(transactions);
        Comparator<Transaction> comparator;
        switch (sortBy) {
        case SORT_BY_AMOUNT:
            comparator = Comparator.comparingDouble(Transaction::getAmount);
            break;
        case SORT_BY_DATE:
            comparator = Comparator.comparing(Transaction::getDate);
            break;
        default:
            return sorted;
        }
        if (direction.equals("desc")) {
            comparator = comparator.reversed();
        }
        sorted.sort(comparator);
        return sorted;
    }

    /**
     * Checks if the given sort field is valid.
     *
     * @param sortBy the field name to validate
     * @return true if sortBy is "amount" or "date"
     */
    public static boolean isValidSortField(String sortBy) {
        return sortBy.equals(SORT_BY_AMOUNT) || sortBy.equals(SORT_BY_DATE);
    }

    /**
     * Checks if the given sort direction is valid.
     *
     * @param direction the direction to validate
     * @return true if direction is "asc" or "desc"
     */
    public static boolean isValidDirection(String direction) {
        return direction.equals("asc") || direction.equals("desc");
    }
}
