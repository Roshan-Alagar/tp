package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HelpCommandTest {

    private TransactionManager tm;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        tm = new TransactionManager();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    private Ui createUi() {
        System.setIn(new ByteArrayInputStream("".getBytes()));
        return new Ui();
    }

    private String getOutput() {
        return outputStream.toString();
    }

    @Test
    void execute_noArgs_showsAllCommands() {
        HelpCommand cmd = new HelpCommand("");
        cmd.execute(tm, createUi());
        String output = getOutput();
        // Check for common commands in the output
        assertTrue(output.contains("add") || output.contains("Available Commands"),
                "Output should show available commands");
    }

    @Test
    void execute_nullArgs_showsAllCommands() {
        HelpCommand cmd = new HelpCommand(null);
        cmd.execute(tm, createUi());
        String output = getOutput();
        assertTrue(output.contains("add") || output.contains("Available Commands"),
                "Output should show available commands");
    }

    @Test
    void execute_addArg_showsAddManual() {
        HelpCommand cmd = new HelpCommand("add");
        cmd.execute(tm, createUi());
        String output = getOutput();
        assertTrue(output.contains("add") || output.contains("Command: add"),
                "Output should show add command help");
    }

    @Test
    void execute_modifyArg_showsModifyManual() {
        HelpCommand cmd = new HelpCommand("modify");
        cmd.execute(tm, createUi());
        String output = getOutput();
        assertTrue(output.contains("modify") || output.contains("Command: modify"),
                "Output should show modify command help");
    }

    @Test
    void execute_deleteArg_showsDeleteManual() {
        HelpCommand cmd = new HelpCommand("delete");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("delete"), "Output should show delete command help");
    }

    @Test
    void execute_listArg_showsListManual() {
        HelpCommand cmd = new HelpCommand("list");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("list"), "Output should show list command help");
    }

    @Test
    void execute_summarizeArg_showsSummarizeManual() {
        HelpCommand cmd = new HelpCommand("summarize");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("summarize"), "Output should show summarize command help");
    }

    @Test
    void execute_exportArg_showsExportManual() {
        HelpCommand cmd = new HelpCommand("export");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("export"), "Output should show export command help");
    }

    @Test
    void execute_importArg_showsImportManual() {
        HelpCommand cmd = new HelpCommand("import");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("import"), "Output should show import command help");
    }

    @Test
    void execute_clearArg_showsClearManual() {
        HelpCommand cmd = new HelpCommand("clear");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("clear"), "Output should show clear command help");
    }

    @Test
    void execute_sortArg_showsSortManual() {
        HelpCommand cmd = new HelpCommand("sort");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("sort"), "Output should show sort command help");
    }

    @Test
    void execute_budgetArg_showsBudgetManual() {
        HelpCommand cmd = new HelpCommand("budget");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("budget"), "Output should show budget command help");
    }

    @Test
    void execute_filterArg_showsUnknownCommand() {
        HelpCommand cmd = new HelpCommand("filter");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("Unknown command"),
                "Output should show unknown command for filter");
    }

    @Test
    void execute_searchArg_showsSearchManual() {
        HelpCommand cmd = new HelpCommand("search");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("search"), "Output should show search command help");
    }

    @Test
    void execute_helpArg_showsHelpManual() {
        HelpCommand cmd = new HelpCommand("help");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("help"), "Output should show help command help");
    }

    @Test
    void execute_exitArg_showsExitManual() {
        HelpCommand cmd = new HelpCommand("exit");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("exit"), "Output should show exit command help");
    }

    @Test
    void execute_unknownCommand_showsError() {
        HelpCommand cmd = new HelpCommand("foobar");
        cmd.execute(tm, createUi());
        String output = getOutput();
        assertTrue(output.contains("Unknown command") || output.contains("foobar"),
                "Output should show unknown command error");
    }

    @Test
    void execute_caseInsensitive_showsManual() {
        HelpCommand cmd = new HelpCommand("ADD");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("add") || getOutput().contains("Command: add"),
                "Output should show add command help");
    }

    @Test
    void execute_extraWhitespace_showsManual() {
        HelpCommand cmd = new HelpCommand("  list  ");
        cmd.execute(tm, createUi());
        assertTrue(getOutput().contains("list"), "Output should show list command help");
    }

    @Test
    void hasValidArgs_alwaysTrue() {
        HelpCommand cmd = new HelpCommand("anything");
        assertTrue(cmd.hasValidArgs());
    }
}
