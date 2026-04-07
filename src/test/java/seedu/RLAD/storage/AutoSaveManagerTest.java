package seedu.RLAD.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.RLAD.Transaction;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoSaveManagerTest {

    private AutoSaveManager autoSaveManager;
    private final String saveFile = "data" + File.separator + "rlad.txt";

    @BeforeEach
    void setUp() {
        autoSaveManager = new AutoSaveManager();
        // Clean up before each test
        File file = new File(saveFile);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() {
        File file = new File(saveFile);
        if (file.exists()) {
            file.delete();
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
    void saveAndLoad_descriptionWithPipe() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("debit", "food", 10.00,
                LocalDate.of(2026, 3, 7), "lunch|dinner"));
        transactions.get(0).setHashId("ddd444");

        autoSaveManager.save(transactions);

        ArrayList<Transaction> loaded = autoSaveManager.load();
        assertEquals(1, loaded.size());
        // Description is the last field, so pipes within it are preserved
        assertEquals("lunch|dinner", loaded.get(0).getDescription());
    }
}
