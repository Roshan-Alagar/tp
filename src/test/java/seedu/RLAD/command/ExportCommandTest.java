package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportCommandTest {

    @TempDir
    Path tempDir;

    private TransactionManager tm;
    private Ui ui;

    @BeforeEach
    void setUp() {
        tm = new TransactionManager();
        System.setIn(new ByteArrayInputStream("".getBytes()));
        ui = new Ui();
    }

    @Test
    void execute_withTransactions_createsFile() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Lunch"));
        tm.addTransaction(new Transaction("debit", "transport", 3.50,
                LocalDate.of(2026, 3, 16), "Bus"));

        String file = tempDir.resolve("out.csv").toString();
        ExportCommand cmd = new ExportCommand("--file out.csv --path " + tempDir);
        cmd.execute(tm, ui);

        Path filePath = tempDir.resolve("out.csv");
        assertTrue(Files.exists(filePath));
        List<String> lines = Files.readAllLines(filePath);
        assertEquals(3, lines.size());
        assertEquals("HashID,Type,Category,Amount,Date,Description", lines.get(0));
    }

    @Test
    void execute_emptyTransactions_noFileCreated() throws RLADException {
        ExportCommand cmd = new ExportCommand("--file out.csv --path " + tempDir);
        cmd.execute(tm, ui);
        assertTrue(!Files.exists(tempDir.resolve("out.csv")));
    }

    @Test
    void execute_invalidPath_throwsException() {
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Test"));
        ExportCommand cmd = new ExportCommand("--path /nonexistent/dir/xyz");
        assertThrows(RLADException.class, () -> cmd.execute(tm, ui));
    }

    @Test
    void execute_specialCharsInDescription_exportedCorrectly() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 25.00,
                LocalDate.of(2026, 1, 1), "Meal, \"fancy\" place"));

        ExportCommand cmd = new ExportCommand("--file special.csv --path " + tempDir);
        cmd.execute(tm, ui);

        List<String> lines = Files.readAllLines(tempDir.resolve("special.csv"));
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("\"Meal, \"\"fancy\"\" place\""));
    }

    @Test
    void hasValidArgs_always_returnsTrue() {
        assertEquals(true, new ExportCommand("").hasValidArgs());
        assertEquals(true, new ExportCommand("--file test.csv").hasValidArgs());
    }

    @Test
    void execute_emptyFileFlag_throwsException() {
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Test"));
        ExportCommand cmd = new ExportCommand("--file");
        assertThrows(RLADException.class, () -> cmd.execute(tm, ui));
    }

    @Test
    void execute_emptyPathFlag_throwsException() {
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Test"));
        ExportCommand cmd = new ExportCommand("--file out.csv --path");
        assertThrows(RLADException.class, () -> cmd.execute(tm, ui));
    }
}
