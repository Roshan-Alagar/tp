package seedu.RLAD.command;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.budget.BudgetCategory;
import seedu.RLAD.exception.RLADException;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(FilterCommand.class.getName());
    private static final DateTimeFormatter DOT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu.MM.dd").withResolverStyle(ResolverStyle.STRICT);
    public FilterCommand(String rawArgs) {
        super(rawArgs);
    }

    public static Map<String, String> parseFlags(String rawArgs) {
        Map<String, String> flags = new LinkedHashMap<>();
        if (rawArgs == null || rawArgs.isBlank()) {
            return flags;
        }

        // Split on every "--" boundary (keep the delimiter via lookahead)
        String[] parts = rawArgs.trim().split("(?=\\s+--[a-zA-Z])");
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
                return LocalDate.parse(dateStr, DOT_DATE_FORMAT);
            }
            return LocalDate.parse(dateStr); // ISO format yyyy-MM-dd
        } catch (Exception e) {
            throw new RLADException("Invalid date '" + dateStr
                    + "'. Use a valid date in yyyy-MM-dd or yyyy.MM.dd format.");
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
        assert rawArgs != null : "rawArgs should not be null";
        logger.info("Building predicate with args: " + rawArgs);
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
            predicate = predicate.and(buildCategoryPredicate(flags.get("category")));
        }

        if (flags.containsKey("amount")) {
            predicate = predicate.and(buildAmountPredicate(flags.get("amount")));
        }

        if (flags.containsKey("date")) {
            LocalDate date = parseDate(flags.get("date"));
            predicate = predicate.and(t -> t.getDate().equals(date));
        }

        predicate = predicate.and(buildDateRangePredicate(flags));

        return predicate;
    }

    /**
     * Builds a category predicate with partial matching, multiple categories, and code support.
     */
    private static Predicate<Transaction> buildCategoryPredicate(String categoryValue)
            throws RLADException {
        assert categoryValue != null : "categoryValue should not be null";
        logger.info("Building category predicate with value: " + categoryValue);
        if (categoryValue.isBlank()) {
            throw new RLADException("--category requires a value.");
        }

        // Check if it's a category code (integer 1-12)
        try {
            int code = Integer.parseInt(categoryValue.trim());
            String categoryName = BudgetCategory.fromCode(code).getDisplayName();
            return t -> t.getCategory() != null && t.getCategory().equalsIgnoreCase(categoryName);
        } catch (NumberFormatException ignored) {
            // Not a number, continue to other checks
        } catch (RLADException e) {
            throw new RLADException("Invalid category code: " + categoryValue.trim()
                    + ". Use 1-12 or a category name. Type 'help' to see category list.");
        }

        // Check if it's multiple categories (comma-separated)
        if (categoryValue.contains(",")) {
            String[] categories = categoryValue.split(",");
            Predicate<Transaction> combined = t -> false;
            for (String cat : categories) {
                String trimmed = cat.trim().toLowerCase();
                if (trimmed.isEmpty()) {
                    continue;
                }
                combined = combined.or(t -> t.getCategory() != null
                        && t.getCategory().toLowerCase().contains(trimmed));
            }
            return combined;
        }

        // Default: partial match (case-insensitive contains)
        String lowerCategory = categoryValue.trim().toLowerCase();
        if (lowerCategory.equals("(none)") || lowerCategory.equals("none")) {
            return t -> t.getCategory() == null || t.getCategory().isBlank();
        }

        return t -> t.getCategory() != null && t.getCategory().toLowerCase().contains(lowerCategory);
    }

    /**
     * Builds a date range predicate with support for relative dates and range validation.
     */
    private static Predicate<Transaction> buildDateRangePredicate(Map<String, String> flags)
            throws RLADException {
        assert flags != null : "flags should not be null";
        logger.info("Building date range predicate");
        Predicate<Transaction> predicate = t -> true;
        LocalDate from = null;
        LocalDate to = null;

        if (flags.containsKey("date-from")) {
            from = parseFlexibleDate(flags.get("date-from"));
            LocalDate finalFrom = from;
            predicate = predicate.and(t -> !t.getDate().isBefore(finalFrom));
        }

        if (flags.containsKey("date-to")) {
            to = parseFlexibleDate(flags.get("date-to"));
            LocalDate finalTo = to;
            predicate = predicate.and(t -> !t.getDate().isAfter(finalTo));
        }

        if (from != null && to != null && from.isAfter(to)) {
            throw new RLADException("--date-from (" + from + ") cannot be after --date-to (" + to + ").");
        }

        return predicate;
    }

    /**
     * Parses flexible date strings including relative keywords and absolute dates.
     */
    private static LocalDate parseFlexibleDate(String dateStr) throws RLADException {
        assert dateStr != null : "dateStr should not be null";
        logger.info("Parsing flexible date: " + dateStr);
        if (dateStr.isBlank()) {
            throw new RLADException("Date value cannot be empty.");
        }
        String trimmed = dateStr.trim().toLowerCase();
        LocalDate today = LocalDate.now();

        switch (trimmed) {
        case "today":
            return today;
        case "yesterday":
            return today.minusDays(1);
        case "tomorrow":
            return today.plusDays(1);
        case "this-week":
            return today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        case "this-month":
            return today.withDayOfMonth(1);
        case "last-month":
            return today.minusMonths(1).withDayOfMonth(1);
        case "last-year":
            return today.minusYears(1).withDayOfYear(1);
        default:
            return parseDate(trimmed);
        }
    }

    /**
     * Applies colon-style filters to a transaction list.
     * Supported filters: type:, cat:/category:, from:, to:, min:, max:
     *
     * @param transactions the full list to filter
     * @param filterStr    space-separated key:value pairs (e.g. "type:debit cat:food min:10")
     * @return filtered list
     * @throws RLADException if any filter value is invalid
     */
    public static List<Transaction> applyColonFilters(List<Transaction> transactions, String filterStr)
            throws RLADException {
        String[] parts = filterStr.split("\\s+");
        List<Transaction> result = new ArrayList<>(transactions);

        for (String part : parts) {
            String token = part.toLowerCase();
            if (!token.contains(":")) {
                continue;
            }
            String[] kv = token.split(":", 2);
            String key = kv[0];
            String value = kv[1];

            switch (key) {
            case "type":
                result = result.stream()
                        .filter(t -> t.getType().equalsIgnoreCase(value))
                        .collect(Collectors.toList());
                break;
            case "cat":
            case "category":
                if (value.trim().equalsIgnoreCase("none") || value.trim().equalsIgnoreCase("(none)")) {
                    result = result.stream()
                            .filter(t -> t.getCategory() == null || t.getCategory().isBlank())
                            .collect(Collectors.toList());
                } else {
                    result = result.stream()
                            .filter(t -> t.getCategory() != null
                                    && t.getCategory().toLowerCase().contains(value))
                            .collect(Collectors.toList());
                }
                break;
            case "from":
                LocalDate fromDate = parseColonDate(value);
                result = result.stream()
                        .filter(t -> !t.getDate().isBefore(fromDate))
                        .collect(Collectors.toList());
                break;
            case "to":
                LocalDate toDate = parseColonDate(value);
                result = result.stream()
                        .filter(t -> !t.getDate().isAfter(toDate))
                        .collect(Collectors.toList());
                break;
            case "min":
                double minAmt = parseColonAmount(value);
                result = result.stream()
                        .filter(t -> t.getAmount() >= minAmt)
                        .collect(Collectors.toList());
                break;
            case "max":
                double maxAmt = parseColonAmount(value);
                result = result.stream()
                        .filter(t -> t.getAmount() <= maxAmt)
                        .collect(Collectors.toList());
                break;
            default:
                throw new RLADException("Unknown filter: '" + key
                        + "'. Available: type, cat, from, to, min, max");
            }
        }
        return result;
    }

    private static LocalDate parseColonDate(String dateStr) throws RLADException {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            throw new RLADException("Invalid date: '" + dateStr + "'. Use YYYY-MM-DD");
        }
    }

    private static double parseColonAmount(String amountStr) throws RLADException {
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount < 0) {
                throw new RLADException("Amount cannot be negative: " + amountStr);
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount: '" + amountStr + "'");
        }
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
