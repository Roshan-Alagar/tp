package seedu.RLAD.storage;

import seedu.RLAD.Transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Handles automatic saving and loading of transactions to a local file.
 * This is for crash recovery only, not for user-facing export/import.
 * Data is stored in a simple pipe-delimited text format.
 */
public class AutoSaveManager {

    private static final Logger logger = Logger.getLogger(AutoSaveManager.class.getName());
    private static final String SAVE_DIR = "data";
    private static final String SAVE_FILE = "rlad.txt";
    private static final String SEPARATOR = "|";

    private final String filePath;

    public AutoSaveManager() {
        this.filePath = SAVE_DIR + File.separator + SAVE_FILE;
    }

    /**
     * Saves all transactions to the autosave file.
     * Overwrites the entire file each time.
     *
     * @param transactions the list of transactions to save
     */
    public void save(ArrayList<Transaction> transactions) {
        try {
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            for (Transaction t : transactions) {
                String line = t.getHashId() + SEPARATOR
                        + t.getType() + SEPARATOR
                        + (t.getCategory() != null ? t.getCategory() : "") + SEPARATOR
                        + t.getAmount() + SEPARATOR
                        + t.getDate().toString() + SEPARATOR
                        + t.getDescription();
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            logger.fine("Autosaved " + transactions.size() + " transactions.");
        } catch (IOException e) {
            logger.warning("Autosave failed: " + e.getMessage());
        }
    }

    /**
     * Loads transactions from the autosave file.
     * Returns an empty list if the file does not exist or is unreadable.
     *
     * @return list of loaded transactions
     */
    public ArrayList<Transaction> load() {
        ArrayList<Transaction> loaded = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return loaded;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                Transaction t = parseLine(line, lineNum);
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
     * Parses a single line from the autosave file into a Transaction.
     * Returns null if the line is malformed.
     */
    private Transaction parseLine(String line, int lineNum) {
        String[] parts = line.split("\\|", 6);
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
            String description = parts.length > 5 ? parts[5] : "";

            Transaction t = new Transaction(type, category, amount, date, description);
            t.setHashId(hashId);
            return t;
        } catch (Exception e) {
            logger.warning("Skipping invalid autosave line " + lineNum + ": " + e.getMessage());
            return null;
        }
    }
}
