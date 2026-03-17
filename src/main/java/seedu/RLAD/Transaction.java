package seedu.RLAD;

import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    // TODO: Consider using a full UUID or a counter-based ID if collisions become too frequent in testing.
    private String hashId;
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

    public String getHashId() {
        return hashId;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | $%.2f | %s | %s",
                hashId, type.toUpperCase(), date, amount, category, description);
    }

    // TODO: Generate a new 4-character substring of a UUID.
    // Ensure this remains consistent with the initial ID generation logic in the constructor.
    public void regenerateHashId() {
        // this.hashId = ... (Note: hashId needs to be non-final for this to work)
    }
}
