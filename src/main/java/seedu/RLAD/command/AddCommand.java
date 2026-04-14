package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Transaction;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AddCommand extends Command {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AddCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException(getUsageHelp());
        }

        if (hasUnclosedQuotes(rawArgs)) {
            throw new RLADException("Unclosed quote in command. Missing closing quote (\").\n"
                    + "Use quotes for multi-word descriptions or categories.\n"
                    + "Example: add debit 15.50 2026-04-12 food \"Lunch at hawker\"\n"
                    + "Type 'help add' for usage.");
        }

        List<String> parts = parseWithQuotes(rawArgs.trim());

        if (parts.size() < 3) {
            throw new RLADException(getUsageHelp());
        }

        String type = parseAndValidateType(parts.get(0));
        double amount = AmountValidator.parseAndValidate(parts.get(1));
        LocalDate date = parseAndValidateDate(parts.get(2));

        String category = null;
        String description = null;

        if (parts.size() >= 4) {
            category = parts.get(3);
            if (category != null && category.trim().matches("-?\\d+(\\.\\d+)?")) {
                throw new RLADException("Category cannot be purely numerical.\n"
                        + "Mixed alphanumerics (e.g., '1st Meeting') are acceptable.\n"
                        + "Type 'help add' for usage.");
            }
        }
        if (parts.size() >= 5) {
            description = parts.get(4);
        }

        Transaction newTransaction = new Transaction(type, category, amount, date, description);
        transactions.addTransaction(newTransaction);

        displaySuccessMessage(ui, newTransaction, category, description);
    }

    private List<String> parseWithQuotes(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            // Check for both straight quotes (") and curly quotes (“ ”)
            if (c == '"' || c == '“' || c == '”') {
                inQuotes = !inQuotes;
                // Don't add the quote character to the token
                continue;
            } else if (c == ' ' && !inQuotes) {
                if (current.length() > 0) {
                    tokens.add(removeSurroundingQuotes(current.toString()));
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            tokens.add(removeSurroundingQuotes(current.toString()));
        }
        return tokens;
    }

    private String removeSurroundingQuotes(String token) {
        if (token == null) {
            return null;
        }
        String cleaned = token.trim();
        // Remove straight quotes or curly quotes from both ends
        while (cleaned.length() >= 2 &&
                ((cleaned.startsWith("\"") && cleaned.endsWith("\"")) ||
                        (cleaned.startsWith("“") && cleaned.endsWith("”")))) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned;
    }

    private boolean hasUnclosedQuotes(String input) {
        int quoteCount = 0;
        for (char c : input.toCharArray()) {
            if (c == '"' || c == '“' || c == '”') {
                quoteCount++;
            }
        }
        return quoteCount % 2 != 0;
    }

    private String parseAndValidateType(String typeStr) throws RLADException {
        String type = typeStr.toLowerCase();
        if (!type.equals("credit") && !type.equals("debit")) {
            throw new RLADException("Invalid type: '" + typeStr + "'. Use 'credit' or 'debit'.\n"
                    + "Type 'help add' for usage.");
        }
        return type;
    }

    private LocalDate parseAndValidateDate(String dateStr) throws RLADException {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RLADException("Invalid date: '" + dateStr + "'. Use YYYY-MM-DD.\n"
                    + "Type 'help add' for usage.");
        }
    }

    private void displaySuccessMessage(Ui ui, Transaction transaction, String category, String description) {
        String cat = (category == null || category.trim().isEmpty()) ? "(none)" : category;
        String desc = (description == null || description.trim().isEmpty()) ? "(none)" : "\"" + description + "\"";

        String message = String.format("✅ Transaction added successfully!\n   ID: %s\n"
                        + "   %s: $%,.2f on %s\n   Category: %s\n   Description: %s",
                transaction.getHashId(), transaction.getType().toUpperCase(),
                transaction.getAmount(), transaction.getDate(), cat, desc);
        ui.showResult(message);
    }

    private String getUsageHelp() {
        return "Usage: add <type> <amount> <date> [category] [description]\n"
                + "Use straight double-quotes (\") for multi-word values.";
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
