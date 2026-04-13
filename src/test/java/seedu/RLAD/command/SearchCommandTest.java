package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchCommandTest {

    private TransactionManager manager;
    private List<String> output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        manager = new TransactionManager();
        output = new ArrayList<>();
        ui = new Ui() {
            @Override
            public void showResult(String message) {
                output.add(message);
            }
        };

        manager.addTransaction(new Transaction("debit", "food", 50.00,
                LocalDate.parse("2026-01-10"), "Hawker lunch"));
        manager.addTransaction(new Transaction("credit", "salary", 3000.00,
                LocalDate.parse("2026-01-15"), "Monthly pay"));
        manager.addTransaction(new Transaction("debit", "transport", 2.50,
                LocalDate.parse("2026-01-20"), "MRT ride"));
    }

    @Test
    void execute_searchByDescription_findsMatch() throws Exception {
        new SearchCommand("lunch").execute(manager, ui);
        assertTrue(output.stream().anyMatch(s -> s.contains("Hawker lunch")));
    }

    @Test
    void execute_searchByCategory_findsMatch() throws Exception {
        new SearchCommand("food").execute(manager, ui);
        assertTrue(output.stream().anyMatch(s -> s.contains("food") || s.contains("FOOD")));
    }

    @Test
    void execute_searchByAmount_findsMatch() throws Exception {
        new SearchCommand("50.00").execute(manager, ui);
        assertTrue(output.stream().anyMatch(s -> s.contains("$50.00")));
    }

    @Test
    void execute_noMatches_showsEmptyMessage() throws Exception {
        new SearchCommand("xyz999").execute(manager, ui);
        assertTrue(output.get(0).contains("No transactions found matching"));
    }

    @Test
    void execute_searchByPartialDescription_findsMatch() throws Exception {
        new SearchCommand("MRT").execute(manager, ui);
        assertTrue(output.stream().anyMatch(s -> s.contains("MRT")));
    }

    @Test
    void execute_caseInsensitive_findsMatch() throws Exception {
        new SearchCommand("SALARY").execute(manager, ui);
        assertTrue(output.stream().anyMatch(s -> s.contains("salary") || s.contains("SALARY")));
    }

    @Test
    void hasValidArgs_withKeyword_returnsTrue() {
        assertTrue(new SearchCommand("food").hasValidArgs());
    }

    @Test
    void hasValidArgs_missingKeyword_returnsFalse() {
        assertFalse(new SearchCommand("").hasValidArgs());
        assertFalse(new SearchCommand(null).hasValidArgs());
    }

    @Test
    void execute_showsResultCount() throws Exception {
        new SearchCommand("food").execute(manager, ui);
        assertTrue(output.stream().anyMatch(s -> s.contains("transaction(s) found")));
    }

}
