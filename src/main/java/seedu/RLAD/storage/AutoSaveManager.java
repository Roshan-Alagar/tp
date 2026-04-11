package seedu.RLAD.storage;

import seedu.RLAD.Transaction;
import seedu.RLAD.exception.RLADException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Handles automatic saving and loading of transactions to a local file.
 * This is for crash recovery only, not for user-facing export/import.
 * Data is stored in CSV format so that pipes and commas in fields are handled safely.
 * Saving reuses CsvStorageManager.exportToCsv(); loading reads the same format but
 * preserves existing HashIDs (unlike the user-facing import which generates new ones).
 */
public class AutoSaveManager {

    private static final Logger logger = Logger.getLogger(AutoSaveManager.class.getName());
    private static final String SAVE_DIR = "data";
    private static final String SAVE_FILE = "rlad.csv";
    private static final String LEGACY_SAVE_FILE = "rlad.txt";

    private final String filePath;
    private final String legacyFilePath;

    public AutoSaveManager() {
        this.filePath = SAVE_DIR + File.separator + SAVE_FILE;
        this.legacyFilePath = SAVE_DIR + File.separator + LEGACY_SAVE_FILE;
    }

    /**
     * Saves all transactions to the autosave CSV file.
     * Delegates to CsvStorageManager.exportToCsv() which handles all escaping.
     *
     * @param transactions the list of transactions to save
     */
    public void save(ArrayList<Transaction> transactions) {
        try {
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            CsvStorageManager.exportToCsv(transactions, filePath);
            logger.fine("Autosaved " + transactions.size() + " transactions.");
        } catch (RLADException e) {
            logger.warning("Autosave failed: " + e.getMessage());
        }
    }

    /**
     * Loads transactions from the autosave file, preserving their original HashIDs.
     * If the new CSV file does not exist but the legacy pipe-delimited file does,
     * migrates the data automatically.
     * Returns an empty list if no file exists or the file is unreadable.
     *
     * @return list of loaded transactions
     */
    public ArrayList<Transaction> load() {
        File csvFile = new File(filePath);
        File legacyFile = new File(legacyFilePath);

        if (!csvFile.exists() && legacyFile.exists()) {
            ArrayList<Transaction> migrated = loadLegacy(legacyFile);
            save(migrated);
            legacyFile.delete();
            logger.fine("Migrated " + migrated.size() + " transactions from legacy rlad.txt to rlad.csv.");
            return migrated;
        }

        ArrayList<Transaction> loaded = new ArrayList<>();
        if (!csvFile.exists()) {
            return loaded;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            reader.readLine(); // skip header
            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                Transaction t = parseCsvLine(line, lineNum);
                if (t != null) {
                    loaded.add(t);
                }
            }
            reader.close();
            logger.fine("Loaded " + loaded.size() + " transactions from autosave.");
        } catch (IOException e) {
            logger.warning("Failed to load autosave: " + e.getMessage());
        }
        return loaded;
    }

    /**
     * Parses a single CSV line from the autosave file into a Transaction,
     * preserving the original HashID.
     * Returns null if the line is malformed.
     */
    private Transaction parseCsvLine(String line, int lineNum) {
        // columns: HashID(0), Type(1), Category(2), Amount(3), Date(4), Description(5)
        String[] parts = CsvStorageManager.parseCsvLine(line);
        if (parts.length < 5) {
            logger.warning("Skipping malformed autosave line " + lineNum);
            return null;
        }
        try {
            String hashId = parts[0];
            String type = parts[1];
            String category = parts[2].isEmpty() ? null : parts[2];
            double amount = Double.parseDouble(parts[3]);
            LocalDate date = LocalDate.parse(parts[4]);
            String description = parts.length > 5 && !parts[5].isEmpty() ? parts[5] : null;

            Transaction t = new Transaction(type, category, amount, date, description);
            t.setHashId(hashId);
            return t;
        } catch (Exception e) {
            logger.warning("Skipping invalid autosave line " + lineNum + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads transactions from the legacy pipe-delimited rlad.txt file.
     * Used only during one-time migration to the new CSV format.
     */
    private ArrayList<Transaction> loadLegacy(File file) {
        ArrayList<Transaction> loaded = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                String[] parts = line.split("\\|", 6);
                if (parts.length < 5) {
                    logger.warning("Skipping malformed legacy line " + lineNum);
                    continue;
                }
                try {
                    String hashId = parts[0];
                    String type = parts[1];
                    String category = parts[2].isEmpty() ? null : parts[2];
                    double amount = Double.parseDouble(parts[3]);
                    LocalDate date = LocalDate.parse(parts[4]);
                    String description = parts.length > 5 && !parts[5].isEmpty() ? parts[5] : null;

                    Transaction t = new Transaction(type, category, amount, date, description);
                    t.setHashId(hashId);
                    loaded.add(t);
                } catch (Exception e) {
                    logger.warning("Skipping invalid legacy line " + lineNum + ": " + e.getMessage());
                }
            }
            reader.close();
        } catch (IOException e) {
            logger.warning("Failed to read legacy autosave: " + e.getMessage());
        }
        return loaded;
    }
}
