package seedu.RLAD;

import java.util.ArrayList;

public class TransactionManager {
    private final ArrayList<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        // TODO: Implement a loop to regenerate ID if idExists(t.getHashId()) is true
        transactions.add(t);
    }

    /**
     * Checks if a Transaction HashID is already in use.
     * TODO: Replace O(N) list search with a HashSet for O(1) lookups to improve scaling.
     */

    private boolean idExists(String hashId) {
        return false;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Finds a transaction by its ID.
     * @param id the transaction ID to search for
     * @return the Transaction if found, null otherwise
     */
    public Transaction findTransaction(String id) {
        for (Transaction t : transactions) {
            if (t.getHashId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Deletes a transaction by its ID.
     * @param id the transaction ID to delete
     * @return true if deletion was successful, false if ID not found
     */
    public boolean deleteTransaction(String id) {
        Transaction toDelete = findTransaction(id);
        if (toDelete != null) {
            transactions.remove(toDelete);
            return true;
        }
        return false;
    }

    /**
     * Updates an existing transaction with new data.
     * @param id the transaction ID to update
     * @param updated the new transaction data
     * @return true if update was successful, false if ID not found
     */
    public boolean updateTransaction(String id, Transaction updated) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getHashId().equals(id)) {
                transactions.set(i, updated);
                return true;
            }
        }
        return false;
    }
}
