package seedu.RLAD;

import seedu.RLAD.budget.BudgetManager;

import java.util.ArrayList;

/**
 * TransactionManager - The Model layer of the application.
 * Handles storage and retrieval of Transaction data.
 *
 * === How Commands Interact with TransactionManager ===
 *
 * PARSER (Parser.java)
 *   └─> Creates Command objects (does NOT interact with TransactionManager)
 *
 * COMMANDS
 *   ├─ AddCommand
 *   │     └─> addTransaction(t) : Adds new transaction to storage
 *   │
 *   ├─ DeleteCommand
 *   │     ├─> findTransaction(id) : Locates transaction by ID
 *   │     └─> deleteTransaction(id) : Removes transaction from storage
 *   │
 *   ├─ ListCommand
 *   │     └─> getTransactions() : Retrieves all transactions, applies sorting
 *   │
 *   ├─ FilterCommand
 *   │     └─> getTransactions() : Retrieves all, applies buildPredicate() + sorting
 *   │
 *   ├─ SortCommand
 *   │     └─> setGlobalSort() / clearGlobalSort() : Manages global sort state
 *   │
 *   ├─ ModifyCommand
 *   │     ├─> findTransaction(id) : Locates transaction to modify
 *   │     └─> updateTransaction(id, updated) : Replaces old transaction
 *   │
 *   └─ SummarizeCommand
 *         └─> getTransactions() : Retrieves all for summaries
 */

public class TransactionManager {
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    private String globalSortField = "";
    private String globalSortDirection = "asc";
    private BudgetManager budgetManager;

    /**
     * Creates a new transaction and adds it to storage.
     * Used by: AddCommand
     * @param t the Transaction to add
     */

    public TransactionManager() {
        // Default constructor
    }

    public void setBudgetManager(BudgetManager budgetManager) {
        this.budgetManager = budgetManager;
    }

    public void addTransaction(Transaction t) {
        // TODO: Implement a loop to regenerate ID if idExists(t.getHashId()) is true
        transactions.add(t);

        // Notify budget manager about the new transaction
        if (budgetManager != null) {
            budgetManager.onTransactionAdded(t);
        }
    }

    /**
     * Checks if a Transaction HashID is already in use.
     * TODO: Replace O(N) list search with a HashSet for O(1) lookups to improve scaling.
     */
    private boolean idExists(String hashId) {
        return false;
    }

    /**
     * Retrieves all transactions from storage.
     * Used by: ListCommand, SummarizeCommand (with FilterCommand applied afterwards)
     * @return ArrayList of all transactions
     */
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Finds a transaction by its ID.
     * Used by: DeleteCommand, ModifyCommand
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
     * Used by: DeleteCommand
     * @param id the transaction ID to delete
     * @return true if deletion was successful, false if ID not found
     */
    public boolean deleteTransaction(String id) {
        Transaction toDelete = findTransaction(id);
        if (toDelete != null) {
            transactions.remove(toDelete);
            // Notify budget manager about deletion if needed
            return true;
        }
        return false;
    }

    /**
     * Updates an existing transaction with new data.
     * Used by: ModifyCommand
     * @param id the transaction ID to update
     * @param updated the new transaction data
     * @return true if update was successful, false if ID not found
     */
    public boolean updateTransaction(String id, Transaction updated) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getHashId().equals(id)) {
                transactions.set(i, updated);
                // Notify budget manager about update if needed
                return true;
            }
        }
        return false;
    }

    public String getGlobalSortField() {
        return globalSortField;
    }

    public String getGlobalSortDirection() {
        return globalSortDirection;
    }

    public void setGlobalSort(String field, String direction) {
        this.globalSortField = field;
        this.globalSortDirection = direction;
    }

    public void clearGlobalSort() {
        this.globalSortField = "";
        this.globalSortDirection = "asc";
    }
}
