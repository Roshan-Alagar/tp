package seedu.RLAD;

import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    private final String hashId;
    private final String type; // "credit" or "debit"
    private final String category;
    private final double amount;
    private final LocalDate date;
    private final String description;

    public Transaction(String type, String category, double amount, LocalDate date, String description) {
        // Generate a short 4-character HashID for the user
        this.hashId = UUID.randomUUID().toString().substring(0, 4);
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public String getHashId() { return hashId; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | $%.2f | %s | %s",
                hashId, type.toUpperCase(), date, amount, category, description);
    }

    // TODO: implement a more robust hash handling
    public void regenerateHashId() {

    }
}
