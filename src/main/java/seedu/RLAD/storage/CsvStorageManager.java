package seedu.RLAD.storage;

import seedu.RLAD.Transaction;
import seedu.RLAD.exception.RLADException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Handles CSV import and export operations for transactions.
 */
public class CsvStorageManager {

    private static final String CSV_HEADER = "HashID,Type,Category,Amount,Date,Description";
    private static final int EXPECTED_COLUMN_COUNT = 6;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Exports transactions to a CSV file.
     *
     * @param transactions the list of transactions to export
     * @param filePath the full path to the output file
     * @throws RLADException if file writing fails
     */
    public static void exportToCsv(ArrayList<Transaction> transactions, String filePath)
            throws RLADException {
        Path path = Paths.get(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(CSV_HEADER);
            writer.newLine();
            for (Transaction t : transactions) {
                writer.write(formatTransactionRow(t));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RLADException("Failed to write CSV file: " + e.getMessage());
        }
    }

    /**
     * Imports transactions from a CSV file.
     *
     * @param filePath the full path to the CSV file
     * @return CsvImportResult containing parsed transactions and any error messages
     * @throws RLADException if file cannot be read or header is invalid
     */
    public static CsvImportResult importFromCsv(String filePath) throws RLADException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new RLADException("File not found: " + filePath);
        }

        ArrayList<Transaction> imported = new ArrayList<>();
        ArrayList<String> errors = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new RLADException("CSV file is empty.");
            }
            validateHeader(headerLine);

            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    String[] columns = parseCsvLine(line);
                    if (columns.length != EXPECTED_COLUMN_COUNT) {
                        errors.add("Row " + lineNum + ": expected " + EXPECTED_COLUMN_COUNT
                                + " columns, got " + columns.length);
                        continue;
                    }
                    Transaction t = parseRow(columns, lineNum);
                    imported.add(t);
                } catch (RLADException e) {
                    errors.add("Row " + lineNum + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RLADException("Failed to read CSV file: " + e.getMessage());
        }

        return new CsvImportResult(imported, errors);
    }

    /**
     * Formats a single transaction as a CSV row.
     *
     * @param t the transaction to format
     * @return CSV-formatted row string
     */
    private static String formatTransactionRow(Transaction t) {
        return escapeCsvField(t.getHashId()) + ","
                + escapeCsvField(t.getType()) + ","
                + escapeCsvField(t.getCategory() != null ? t.getCategory() : "") + ","
                + String.format("%.2f", t.getAmount()) + ","
                + t.getDate().format(DATE_FORMATTER) + ","
                + escapeCsvField(t.getDescription());
    }

    /**
     * Validates that the CSV header matches expected format.
     *
     * @param headerLine the first line of the CSV file
     * @throws RLADException if header does not match expected schema
     */
    private static void validateHeader(String headerLine) throws RLADException {
        String[] headers = parseCsvLine(headerLine.trim());
        String[] expected = CSV_HEADER.split(",");
        if (headers.length != expected.length) {
            throw new RLADException("Invalid CSV header. Expected: " + CSV_HEADER);
        }
        for (int i = 0; i < expected.length; i++) {
            if (!expected[i].equalsIgnoreCase(headers[i].trim())) {
                throw new RLADException("Invalid CSV header. Expected: " + CSV_HEADER);
            }
        }
    }

    /**
     * Parses a CSV row into a Transaction object.
     * HashID from CSV is ignored; a new one is generated.
     *
     * @param columns the parsed CSV columns
     * @param lineNum the line number for error reporting
     * @return a new Transaction
     * @throws RLADException if data validation fails
     */
    private static Transaction parseRow(String[] columns, int lineNum) throws RLADException {
        // columns: HashID(0), Type(1), Category(2), Amount(3), Date(4), Description(5)
        String type = columns[1].trim().toLowerCase();
        if (!type.equals("credit") && !type.equals("debit")) {
            throw new RLADException("invalid type '" + columns[1].trim() + "' (must be credit or debit)");
        }

        String category = columns[2].trim();
        if (category.isEmpty()) {
            category = null;
        }

        double amount;
        try {
            amount = Double.parseDouble(columns[3].trim());
        } catch (NumberFormatException e) {
            throw new RLADException("invalid amount '" + columns[3].trim() + "'");
        }
        if (amount < 0) {
            throw new RLADException("amount must not be negative: " + columns[3].trim());
        }

        LocalDate date;
        try {
            date = LocalDate.parse(columns[4].trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RLADException("invalid date '" + columns[4].trim() + "' (expected yyyy-MM-dd)");
        }

        String description = columns[5].trim();
        if (description.isEmpty()) {
            description = null;
        }

        return new Transaction(type, category, amount, date, description);
    }

    /**
     * Escapes a field value for CSV output following RFC 4180.
     * Wraps in double quotes if the value contains commas, quotes, or newlines.
     * Doubles any existing double-quote characters.
     *
     * @param field the field value to escape
     * @return the escaped field value
     */
    static String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Parses a single CSV line respecting quoted fields.
     * Handles escaped quotes (doubled double-quotes) per RFC 4180.
     *
     * @param line the CSV line to parse
     * @return array of field values
     */
    static String[] parseCsvLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current = new StringBuilder();
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());

        return fields.toArray(new String[0]);
    }

    /**
     * Result container for import operations.
     */
    public static class CsvImportResult {
        private final ArrayList<Transaction> transactions;
        private final ArrayList<String> errors;

        /**
         * Creates a new CsvImportResult.
         *
         * @param transactions successfully parsed transactions
         * @param errors list of error messages for failed rows
         */
        public CsvImportResult(ArrayList<Transaction> transactions, ArrayList<String> errors) {
            this.transactions = transactions;
            this.errors = errors;
        }

        /**
         * Returns the successfully parsed transactions.
         *
         * @return list of transactions
         */
        public ArrayList<Transaction> getTransactions() {
            return transactions;
        }

        /**
         * Returns the error messages for failed rows.
         *
         * @return list of error messages
         */
        public ArrayList<String> getErrors() {
            return errors;
        }

        /**
         * Returns the number of successfully parsed transactions.
         *
         * @return success count
         */
        public int getSuccessCount() {
            return transactions.size();
        }

        /**
         * Returns the number of failed rows.
         *
         * @return failure count
         */
        public int getFailCount() {
            return errors.size();
        }
    }
}
