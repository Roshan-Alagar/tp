package seedu.RLAD;

import java.util.ArrayList;

public class TransactionManager {
    private final ArrayList<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        while (idExists(t.getHashId())) {
            t.regenerateHashId();
        }
        transactions.add(t);
    }

    // TODO: implement a more robust collision handling
    private boolean idExists(String hashId) {
        return false;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}
