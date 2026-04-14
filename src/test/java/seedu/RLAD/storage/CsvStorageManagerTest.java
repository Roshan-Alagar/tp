package seedu.RLAD.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.RLAD.Transaction;
import seedu.RLAD.exception.RLADException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvStorageManagerTest {

    @TempDir
    Path tempDir;

    // === escapeCsvField tests ===

    @Test
    void escapeCsvField_plainField_unchanged() {
        assertEquals("hello", CsvStorageManager.escapeCsvField("hello"));
    }

    @Test
    void escapeCsvField_fieldWithComma_wrappedInQuotes() {
        assertEquals("\"hello,world\"", CsvStorageManager.escapeCsvField("hello,world"));
    }

    @Test
    void escapeCsvField_fieldWithQuote_doubledAndWrapped() {
        assertEquals("\"say \"\"hi\"\"\"", CsvStorageManager.escapeCsvField("say \"hi\""));
    }

    @Test
    void escapeCsvField_fieldWithNewline_wrappedInQuotes() {
        assertEquals("\"line1\nline2\"", CsvStorageManager.escapeCsvField("line1\nline2"));
    }

    @Test
    void escapeCsvField_nullField_returnsEmpty() {
        assertEquals("", CsvStorageManager.escapeCsvField(null));
    }

    @Test
    void escapeCsvField_formulaEquals_prefixedWithTab() {
        // Fields starting with '=' must be tab-prefixed to prevent spreadsheet injection
        assertEquals("\t=CMD|'/C calc'!A0", CsvStorageManager.escapeCsvField("=CMD|'/C calc'!A0"));
    }

    @Test
    void escapeCsvField_formulaPlus_prefixedWithTab() {
        assertEquals("\t+1+1", CsvStorageManager.escapeCsvField("+1+1"));
    }

    @Test
    void escapeCsvField_formulaMinus_prefixedWithTab() {
        assertEquals("\t-1+1", CsvStorageManager.escapeCsvField("-1+1"));
    }

    @Test
    void escapeCsvField_formulaAt_prefixedWithTab() {
        assertEquals("\t@SUM(A1)", CsvStorageManager.escapeCsvField("@SUM(A1)"));
    }

    @Test
    void exportAndImport_formulaDescription_roundTripPreservesOriginal() throws RLADException {
        // A description that starts with '=' should survive an export-then-import cycle
        // with the original value intact (the tab prefix is stripped on import).
        ArrayList<Transaction> original = new ArrayList<>();
        original.add(new Transaction("debit", "food", 9.90,
                LocalDate.of(2026, 3, 1), "=evil formula"));

        String filePath = tempDir.resolve("formula.csv").toString();
        CsvStorageManager.exportToCsv(original, filePath);

        CsvStorageManager.CsvImportResult result = CsvStorageManager.importFromCsv(filePath);
        assertEquals(1, result.getSuccessCount());
        assertEquals("=evil formula", result.getTransactions().get(0).getDescription());
    }

    // === parseCsvLine tests ===

    @Test
    void parseCsvLine_simpleLine_correctFields() {
        String[] result = CsvStorageManager.parseCsvLine("a,b,c,d,e,f");
        assertEquals(6, result.length);
        assertEquals("a", result[0]);
        assertEquals("f", result[5]);
    }

    @Test
    void parseCsvLine_quotedFieldWithComma_parsedCorrectly() {
        String[] result = CsvStorageManager.parseCsvLine("id,credit,food,50.00,2026-03-15,\"Lunch, hawker\"");
        assertEquals(6, result.length);
        assertEquals("Lunch, hawker", result[5]);
    }

    @Test
    void parseCsvLine_quotedFieldWithEscapedQuotes_parsedCorrectly() {
        String[] result = CsvStorageManager.parseCsvLine("id,credit,food,50.00,2026-03-15,\"He said \"\"hi\"\"\"");
        assertEquals(6, result.length);
        assertEquals("He said \"hi\"", result[5]);
    }

    // === export + import round-trip ===

    @Test
    void exportAndImport_roundTrip_dataPreserved() throws RLADException {
        ArrayList<Transaction> original = new ArrayList<>();
        original.add(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Lunch at hawker"));
        original.add(new Transaction("debit", "transport", 3.50,
                LocalDate.of(2026, 3, 15), "Bus fare"));

        String filePath = tempDir.resolve("test.csv").toString();
        CsvStorageManager.exportToCsv(original, filePath);

        CsvStorageManager.CsvImportResult result = CsvStorageManager.importFromCsv(filePath);
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailCount());

        ArrayList<Transaction> imported = result.getTransactions();
        assertEquals("credit", imported.get(0).getType());
        assertEquals("food", imported.get(0).getCategory());
        assertEquals(50.00, imported.get(0).getAmount(), 0.01);
        assertEquals(LocalDate.of(2026, 3, 15), imported.get(0).getDate());
        assertEquals("Lunch at hawker", imported.get(0).getDescription());

        assertEquals("debit", imported.get(1).getType());
        assertEquals(3.50, imported.get(1).getAmount(), 0.01);
    }

    @Test
    void exportAndImport_specialCharsInDescription_preserved() throws RLADException, IOException {
        ArrayList<Transaction> original = new ArrayList<>();
        original.add(new Transaction("credit", "food", 25.00,
                LocalDate.of(2026, 1, 1), "Meal, \"fancy\" place"));

        String filePath = tempDir.resolve("special.csv").toString();
        CsvStorageManager.exportToCsv(original, filePath);

        CsvStorageManager.CsvImportResult result = CsvStorageManager.importFromCsv(filePath);
        assertEquals(1, result.getSuccessCount());
        assertEquals("Meal, \"fancy\" place", result.getTransactions().get(0).getDescription());
    }

    // === import validation tests ===

    @Test
    void importFromCsv_fileNotFound_throwsException() {
        assertThrows(RLADException.class, () ->
                CsvStorageManager.importFromCsv("nonexistent.csv"));
    }

    @Test
    void importFromCsv_emptyFile_throwsException() throws IOException {
        Path file = tempDir.resolve("empty.csv");
        Files.writeString(file, "");
        assertThrows(RLADException.class, () ->
                CsvStorageManager.importFromCsv(file.toString()));
    }

    @Test
    void importFromCsv_wrongHeader_throwsException() throws IOException {
        Path file = tempDir.resolve("bad_header.csv");
        Files.writeString(file, "Wrong,Header,Format\n");
        assertThrows(RLADException.class, () ->
                CsvStorageManager.importFromCsv(file.toString()));
    }

    @Test
    void importFromCsv_malformedRows_skippedWithErrors() throws IOException {
        Path file = tempDir.resolve("malformed.csv");
        String content = "HashID,Type,Category,Amount,Date,Description\n"
                + "abc123,credit,food,50.00,2026-03-15,Good row\n"
                + "def456,invalid,food,50.00,2026-03-15,Bad type\n"
                + "ghi789,credit,food,notanumber,2026-03-15,Bad amount\n";
        Files.writeString(file, content);

        CsvStorageManager.CsvImportResult result = CsvStorageManager.importFromCsv(file.toString());
        assertEquals(1, result.getSuccessCount());
        assertEquals(2, result.getFailCount());
    }

    @Test
    void importFromCsv_invalidDate_skippedWithError() throws IOException {
        Path file = tempDir.resolve("bad_date.csv");
        String content = "HashID,Type,Category,Amount,Date,Description\n"
                + "abc123,credit,food,50.00,not-a-date,Bad date\n";
        Files.writeString(file, content);

        CsvStorageManager.CsvImportResult result = CsvStorageManager.importFromCsv(file.toString());
        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailCount());
    }

    @Test
    void importFromCsv_negativeAmount_skippedWithError() throws IOException {
        Path file = tempDir.resolve("negative.csv");
        String content = "HashID,Type,Category,Amount,Date,Description\n"
                + "abc123,credit,food,-50.00,2026-03-15,Negative\n";
        Files.writeString(file, content);

        CsvStorageManager.CsvImportResult result = CsvStorageManager.importFromCsv(file.toString());
        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailCount());
    }

    @Test
    void importFromCsv_emptyOptionalFields_parsed() throws IOException {
        Path file = tempDir.resolve("empty_fields.csv");
        String content = "HashID,Type,Category,Amount,Date,Description\n"
                + "abc123,credit,,50.00,2026-03-15,\n";
        Files.writeString(file, content);

        CsvStorageManager.CsvImportResult result = CsvStorageManager.importFromCsv(file.toString());
        assertEquals(1, result.getSuccessCount());
        Transaction t = result.getTransactions().get(0);
        assertEquals(null, t.getCategory());
        assertEquals("", t.getDescription());
    }

    // === export edge cases ===

    @Test
    void exportToCsv_emptyList_createsHeaderOnly() throws RLADException, IOException {
        String filePath = tempDir.resolve("empty.csv").toString();
        CsvStorageManager.exportToCsv(new ArrayList<>(), filePath);

        List<String> lines = Files.readAllLines(Path.of(filePath));
        assertEquals(1, lines.size());
        assertEquals("HashID,Type,Category,Amount,Date,Description", lines.get(0));
    }
}
