package seedu.RLAD.storage;

import seedu.RLAD.Transaction;
import seedu.RLAD.exception.RLADException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private static final String HASH_FILE_NAME = "rlad.csv.sha256";

    private final String filePath;
    private final String legacyFilePath;
    private final String hashFilePath;

    public AutoSaveManager() {
        this.filePath = SAVE_DIR + File.separator + SAVE_FILE;
        this.legacyFilePath = SAVE_DIR + File.separator + LEGACY_SAVE_FILE;
        this.hashFilePath = SAVE_DIR + File.separator + HASH_FILE_NAME;
    }

    /**
     * Saves all transactions to the autosave CSV file, then writes a SHA-256 hash of
     * the file alongside it so that accidental or malicious edits can be detected on
     * the next load.
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
            saveHash();
            logger.fine("Autosaved " + transactions.size() + " transactions.");
        } catch (RLADException e) {
            logger.warning("Autosave failed: " + e.getMessage());
        }
    }

    /**
     * Computes the SHA-256 digest of a file and writes it as a hex string to the
     * companion hash file ({@code rlad.csv.sha256}).
     */
    private void saveHash() {
        try {
            String hash = computeSha256(filePath);
            Files.write(Paths.get(hashFilePath), hash.getBytes());
            logger.fine("Integrity hash saved.");
        } catch (IOException e) {
            logger.warning("Could not save integrity hash: " + e.getMessage());
        }
    }

    /**
     * Verifies the autosave file against the stored SHA-256 hash.
     *
     * @return {@code true} if the file matches the stored hash (or no hash file exists
     *         yet), {@code false} if the file has been modified since the last save
     */
    private boolean verifyIntegrity() {
        File hashFile = new File(hashFilePath);
        if (!hashFile.exists()) {
            return true; // No hash yet (e.g. first run or pre-feature install) — skip check
        }
        try {
            String stored = new String(Files.readAllBytes(Paths.get(hashFilePath))).trim();
            String computed = computeSha256(filePath);
            return stored.equals(computed);
        } catch (IOException e) {
            logger.warning("Integrity verification failed: " + e.getMessage());
            return true; // Cannot verify — proceed rather than block the user
        }
    }

    /**
     * Returns the SHA-256 hex digest of the file at {@code path}.
     *
     * @param path path of the file to hash
     * @return lowercase hex SHA-256 string
     * @throws IOException if the file cannot be read or SHA-256 is unavailable
     */
    private String computeSha256(String path) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(Paths.get(path));
            byte[] hashBytes = digest.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandated by the Java SE spec; this branch is unreachable in practice
            throw new IOException("SHA-256 algorithm unavailable: " + e.getMessage());
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

        if (!verifyIntegrity()) {
            System.out.println("⚠️  WARNING: Autosave integrity check failed.");
            System.out.println("   The data file may have been modified outside of RLAD.");
            System.out.println("   Loading data anyway — please verify your transactions.");
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
            if (amount < 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
                logger.warning("Skipping autosave line " + lineNum + ": invalid amount " + parts[3]);
                return null;
            }
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
                    if (amount < 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
                        logger.warning("Skipping legacy line " + lineNum
                                + ": invalid amount " + parts[3]);
                        continue;
                    }
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
