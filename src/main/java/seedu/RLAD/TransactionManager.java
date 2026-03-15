package seedu.RLAD;

import seedu.RLAD.budget.BudgetManager;

import java.time.YearMonth;
import java.util.ArrayList;

public class TransactionManager {
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    private BudgetManager budgetManager; // Add reference to BudgetManager

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

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    // Add the missing methods that the compiler is looking for:

    public Transaction findTransaction(String id) {
        for (Transaction t : transactions) {
            if (t.getHashId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public boolean deleteTransaction(String id) {
        Transaction toDelete = findTransaction(id);
        if (toDelete != null) {
            transactions.remove(toDelete);
            // Notify budget manager about deletion if needed
            return true;
        }
        return false;
    }

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
}