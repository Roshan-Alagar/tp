package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportCommandTest {

    @TempDir
    Path tempDir;

    private TransactionManager tm;

    private Ui createUiWithInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        return new Ui();
    }

    @BeforeEach
    void setUp() {
        tm = new TransactionManager();
    }

    private Path createValidCsv(String filename) throws IOException {
        Path file = tempDir.resolve(filename);
        String content = "HashID,Type,Category,Amount,Date,Description\n"
                + "abc123,credit,food,50.00,2026-03-15,Lunch\n"
                + "def456,debit,transport,3.50,2026-03-16,Bus fare\n";
        Files.writeString(file, content);
        return file;
    }

    @Test
    void execute_validFile_importsTransactions() throws Exception {
        Path file = createValidCsv("valid.csv");
        Ui ui = createUiWithInput("");
        ImportCommand cmd = new ImportCommand("--file " + file);
        cmd.execute(tm, ui);
        assertEquals(2, tm.getTransactionCount());
    }

    @Test
    void execute_mergeMode_addsToExisting() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 100.00,
                LocalDate.of(2026, 1, 1), "Existing"));
        assertEquals(1, tm.getTransactionCount());

        Path file = createValidCsv("merge.csv");
        Ui ui = createUiWithInput("");
        ImportCommand cmd = new ImportCommand("--file " + file + " --merge");
        cmd.execute(tm, ui);
        assertEquals(3, tm.getTransactionCount());
    }

    @Test
    void execute_replaceMode_replacesExisting() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 100.00,
                LocalDate.of(2026, 1, 1), "Existing"));

        Path file = createValidCsv("replace.csv");
        Ui ui = createUiWithInput("CONFIRM\n");
        ImportCommand cmd = new ImportCommand("--file " + file);
        cmd.execute(tm, ui);
        assertEquals(2, tm.getTransactionCount());
    }

    @Test
    void execute_replaceCancelled_dataUnchanged() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 100.00,
                LocalDate.of(2026, 1, 1), "Existing"));

        Path file = createValidCsv("cancel.csv");
        Ui ui = createUiWithInput("no\n");
        ImportCommand cmd = new ImportCommand("--file " + file);
        cmd.execute(tm, ui);
        assertEquals(1, tm.getTransactionCount());
    }

    @Test
    void execute_fileNotFound_throwsException() {
        Ui ui = createUiWithInput("");
        ImportCommand cmd = new ImportCommand("--file nonexistent.csv");
        assertThrows(RLADException.class, () -> cmd.execute(tm, ui));
    }

    @Test
    void execute_malformedCsv_importsValidRows() throws Exception {
        Path file = tempDir.resolve("malformed.csv");
        String content = "HashID,Type,Category,Amount,Date,Description\n"
                + "abc123,credit,food,50.00,2026-03-15,Good\n"
                + "def456,badtype,food,50.00,2026-03-15,Bad\n";
        Files.writeString(file, content);

        Ui ui = createUiWithInput("");
        ImportCommand cmd = new ImportCommand("--file " + file);
        cmd.execute(tm, ui);
        assertEquals(1, tm.getTransactionCount());
    }

    @Test
    void execute_allRowsInvalid_throwsException() throws Exception {
        Path file = tempDir.resolve("allinvalid.csv");
        String content = "HashID,Type,Category,Amount,Date,Description\n"
                + "abc123,badtype,food,50.00,2026-03-15,Bad\n";
        Files.writeString(file, content);

        Ui ui = createUiWithInput("");
        ImportCommand cmd = new ImportCommand("--file " + file);
        assertThrows(RLADException.class, () -> cmd.execute(tm, ui));
    }

    @Test
    void hasValidArgs_alwaysTrue() {
        assertEquals(true, new ImportCommand("--file test.csv").hasValidArgs());
        assertEquals(true, new ImportCommand("--merge").hasValidArgs());
        assertEquals(true, new ImportCommand("").hasValidArgs());
    }

    @Test
    void execute_missingFileFlag_throwsException() {
        Ui ui = createUiWithInput("");
        ImportCommand cmd = new ImportCommand("--merge");
        RLADException ex = assertThrows(RLADException.class, () -> cmd.execute(tm, ui));
        assertTrue(ex.getMessage().contains("--file is required"));
    }

    @Test
    void execute_emptyFileFlag_throwsException() {
        Ui ui = createUiWithInput("");
        ImportCommand cmd = new ImportCommand("--file");
        RLADException ex = assertThrows(RLADException.class, () -> cmd.execute(tm, ui));
        assertTrue(ex.getMessage().contains("--file is required"));
    }
}
