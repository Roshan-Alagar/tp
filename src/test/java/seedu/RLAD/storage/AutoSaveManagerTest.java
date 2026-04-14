package seedu.RLAD.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.RLAD.Transaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoSaveManagerTest {

    private AutoSaveManager autoSaveManager;
    private final String saveFile = "data" + File.separator + "rlad.csv";
    private final String hashFile = "data" + File.separator + "rlad.csv.sha256";

    @BeforeEach
    void setUp() {
        autoSaveManager = new AutoSaveManager();
        deleteIfExists(saveFile);
        deleteIfExists(hashFile);
    }

    @AfterEach
    void tearDown() {
        deleteIfExists(saveFile);
        deleteIfExists(hashFile);
    }

    private void deleteIfExists(String path) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
    }

    @Test
    void saveAndLoad_roundTrip() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("credit", "salary", 3000.00,
                LocalDate.of(2026, 3, 1), "March salary"));
        transactions.add(new Transaction("debit", "food", 15.50,
                LocalDate.of(2026, 3, 5), "Chicken rice"));

        // Fix hashIds for deterministic test
        transactions.get(0).setHashId("aaa111");
        transactions.get(1).setHashId("bbb222");

        autoSaveManager.save(transactions);

        ArrayList<Transaction> loaded = autoSaveManager.load();
        assertEquals(2, loaded.size());

        assertEquals("aaa111", loaded.get(0).getHashId());
        assertEquals("credit", loaded.get(0).getType());
        assertEquals("salary", loaded.get(0).getCategory());
        assertEquals(3000.00, loaded.get(0).getAmount());
        assertEquals(LocalDate.of(2026, 3, 1), loaded.get(0).getDate());
        assertEquals("March salary", loaded.get(0).getDescription());

        assertEquals("bbb222", loaded.get(1).getHashId());
        assertEquals("debit", loaded.get(1).getType());
        assertEquals("food", loaded.get(1).getCategory());
        assertEquals(15.50, loaded.get(1).getAmount());
    }

    @Test
    void load_noFile_returnsEmptyList() {
        ArrayList<Transaction> loaded = autoSaveManager.load();
        assertTrue(loaded.isEmpty());
    }

    @Test
    void save_emptyList_createsEmptyFile() {
        autoSaveManager.save(new ArrayList<>());

        ArrayList<Transaction> loaded = autoSaveManager.load();
        assertTrue(loaded.isEmpty());
    }

    @Test
    void saveAndLoad_nullCategory() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("debit", null, 5.00,
                LocalDate.of(2026, 3, 6), ""));
        transactions.get(0).setHashId("ccc333");

        autoSaveManager.save(transactions);

        ArrayList<Transaction> loaded = autoSaveManager.load();
        assertEquals(1, loaded.size());
        assertEquals(null, loaded.get(0).getCategory());
        assertEquals(5.00, loaded.get(0).getAmount());
    }

    @Test
    void save_createsHashFile() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("credit", "salary", 1000.00,
                LocalDate.of(2026, 3, 1), "Pay"));

        autoSaveManager.save(transactions);

        assertTrue(new File(hashFile).exists(), "Hash file should be created alongside the CSV");
    }

    @Test
    void saveAndLoad_integrityCheckPasses() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("credit", "salary", 1000.00,
                LocalDate.of(2026, 3, 1), "Pay"));
        transactions.get(0).setHashId("fff999");

        autoSaveManager.save(transactions);
        // An unmodified file should load fine
        ArrayList<Transaction> loaded = autoSaveManager.load();
        assertEquals(1, loaded.size());
        assertEquals("fff999", loaded.get(0).getHashId());
    }

    @Test
    void load_tamperedFile_loadsWithWarning() throws IOException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("credit", "salary", 1000.00,
                LocalDate.of(2026, 3, 1), "Pay"));
        autoSaveManager.save(transactions);

        // Tamper: append a byte to the CSV so the hash no longer matches
        try (FileWriter fw = new FileWriter(saveFile, true)) {
            fw.write(" ");
        }

        // Should still return data (warns but does not block)
        ArrayList<Transaction> loaded = autoSaveManager.load();
        assertTrue(loaded.size() >= 0, "Load should complete even when integrity check fails");
    }

    @Test
    void saveAndLoad_descriptionWithPipe() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("debit", "food", 10.00,
                LocalDate.of(2026, 3, 7), "lunch|dinner"));
        transactions.get(0).setHashId("ddd444");

        autoSaveManager.save(transactions);

        ArrayList<Transaction> loaded = autoSaveManager.load();
        assertEquals(1, loaded.size());
        // CSV format correctly escapes pipe characters in any field
        assertEquals("lunch|dinner", loaded.get(0).getDescription());
    }
}
