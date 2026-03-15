package seedu.RLAD.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

/**
 * FilterCommand handles filtering transactions based on various criteria.
 * Provides shared filtering logic that can be used by other commands
 * (e.g., ListCommand, DeleteCommand, SummarizeCommand).
 * Supported flags for buildPredicate():
 *   --type       credit | debit
 *   --category   any string (case-insensitive)
 *   --amount     [operator] value  e.g. "-gt 50", "-lt 20", "100"
 *                operators: -gt, -gte, -eq, -lt, -leq
 *   --date       exact date  yyyy-MM-dd or yyyy.MM.dd
 *   --date-from  range start yyyy-MM-dd or yyyy.MM.dd
 *   --date-to    range end   yyyy-MM-dd or yyyy.MM.dd
 * NOTE: --sort is intentionally NOT handled here; ListCommand deals with it.
 */

public class FilterCommand extends Command {

    public FilterCommand(String rawArgs) {
        super(rawArgs);
    }

    public static Map<String, String> parseFlags(String rawArgs) {
        Map<String, String> flags = new LinkedHashMap<>();
        if (rawArgs == null || rawArgs.isBlank()) {
            return flags;
        }

        // Split on every "--" boundary (keep the delimiter via lookahead)
        String[] parts = rawArgs.trim().split("(?=--[\\w-])");
        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) {
                continue;
            }
            // Strip leading "--"
            if (part.startsWith("--")) {
                part = part.substring(2);
            }
            int spaceIdx = part.indexOf(' ');
            if (spaceIdx == -1) {
                // Flag with no value
                flags.put(part.trim(), "");
            } else {
                flags.put(part.substring(0, spaceIdx).trim(),
                        part.substring(spaceIdx + 1).trim());
            }
        }
        return flags;
    }

    /**
     * Parses a date string in yyyy-MM-dd or yyyy.MM.dd format.
     */
    private static LocalDate parseDate(String dateStr) throws RLADException {
        try {
            if (dateStr.contains(".")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            }
            return LocalDate.parse(dateStr); // ISO format yyyy-MM-dd
        } catch (Exception e) {
            throw new RLADException("Invalid date '" + dateStr + "'. Use yyyy-MM-dd or yyyy.MM.dd");
        }
    }

    /**
     * Builds an amount Predicate from an argument like "-gt 50" or "100".
     * Supported operators: -gt, -gte, -eq, -lt, -leq
     */
    private static Predicate<Transaction> buildAmountPredicate(String amountArg) throws RLADException {
        String[] parts = amountArg.trim().split("\\s+");
        try {
            if (parts.length == 1) {
                // No operator — exact match
                double value = Double.parseDouble(parts[0]);
                return t -> t.getAmount() == value;
            }
            if (parts.length == 2) {
                String op = parts[0];
                double value = Double.parseDouble(parts[1]);
                switch (op) {
                case "-gt":  return t -> t.getAmount() > value;
                case "-gte": return t -> t.getAmount() >= value;
                case "-eq":  return t -> t.getAmount() == value;
                case "-lt":  return t -> t.getAmount() < value;
                case "-leq": return t -> t.getAmount() <= value;
                default:
                    throw new RLADException("Unknown amount operator '" + op
                            + "'. Use: -gt, -gte, -eq, -lt, -leq");
                }
            }
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount value in: '" + amountArg + "'. Must be a number.");
        }
        throw new RLADException("Invalid --amount format. Example: --amount -gt 50");
    }

    /**
     * Builds a composite Predicate from rawArgs flags.
     * Each active flag adds an AND condition to the predicate.
     *
     * @param rawArgs the raw argument string from the user's input
     * @return a Predicate that returns true for transactions matching ALL supplied filters
     * @throws RLADException if any flag value is invalid
     */

    public static Predicate<Transaction> buildPredicate(String rawArgs) throws RLADException {
        Predicate<Transaction> predicate = t -> true; // start with "match all"
        Map<String, String> flags = parseFlags(rawArgs);

        if (flags.containsKey("type")) {
            String type = flags.get("type").toLowerCase();
            if (!type.equals("credit") && !type.equals("debit")) {
                throw new RLADException("--type must be 'credit' or 'debit', got: '" + type + "'");
            }
            predicate = predicate.and(t -> t.getType().equalsIgnoreCase(type));
        }

        if (flags.containsKey("category")) {
            String category = flags.get("category").toLowerCase();
            predicate = predicate.and(t -> t.getCategory().equalsIgnoreCase(category));
        }

        if (flags.containsKey("amount")) {
            predicate = predicate.and(buildAmountPredicate(flags.get("amount")));
        }

        if (flags.containsKey("date")) {
            LocalDate date = parseDate(flags.get("date"));
            predicate = predicate.and(t -> t.getDate().equals(date));
        }

        if (flags.containsKey("date-from")) {
            LocalDate from = parseDate(flags.get("date-from"));
            predicate = predicate.and(t -> !t.getDate().isBefore(from));
        }

        if (flags.containsKey("date-to")) {
            LocalDate to = parseDate(flags.get("date-to"));
            predicate = predicate.and(t -> !t.getDate().isAfter(to));
        }

        return predicate;
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        ui.showResult("FilterCommand is a shared helper. Use 'list' to view transactions.");
    }

    @Override
    public boolean hasValidArgs() {
        return true;
    }
}
