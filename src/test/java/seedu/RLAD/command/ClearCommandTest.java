package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClearCommandTest {

    private TransactionManager tm;

    @BeforeEach
    void setUp() {
        tm = new TransactionManager();
    }

    private Ui createUiWithInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        return new Ui();
    }

    @Test
    void execute_emptyTransactions_showsMessage() throws RLADException {
        Ui ui = createUiWithInput("");
        ClearCommand cmd = new ClearCommand("");
        cmd.execute(tm, ui);
        assertEquals(0, tm.getTransactionCount());
    }

    @Test
    void execute_forceFlag_clearsWithoutConfirmation() throws RLADException {
        Ui ui = createUiWithInput("");
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Test"));
        tm.addTransaction(new Transaction("debit", "transport", 3.50,
                LocalDate.of(2026, 3, 15), "Test2"));
        assertEquals(2, tm.getTransactionCount());

        ClearCommand cmd = new ClearCommand("--force");
        cmd.execute(tm, ui);
        assertEquals(0, tm.getTransactionCount());
    }

    @Test
    void execute_confirmationAccepted_clearsData() throws RLADException {
        Ui ui = createUiWithInput("CONFIRM\n");
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Test"));
        assertEquals(1, tm.getTransactionCount());

        ClearCommand cmd = new ClearCommand("");
        cmd.execute(tm, ui);
        assertEquals(0, tm.getTransactionCount());
    }

    @Test
    void execute_confirmationDenied_dataUnchanged() throws RLADException {
        Ui ui = createUiWithInput("no\n");
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Test"));
        assertEquals(1, tm.getTransactionCount());

        ClearCommand cmd = new ClearCommand("");
        cmd.execute(tm, ui);
        assertEquals(1, tm.getTransactionCount());
    }

    @Test
    void hasValidArgs_always_returnsTrue() {
        ClearCommand cmd = new ClearCommand("--force");
        assertEquals(true, cmd.hasValidArgs());

        ClearCommand cmd2 = new ClearCommand("");
        assertEquals(true, cmd2.hasValidArgs());
    }
}
