package seedu.RLAD;

import seedu.RLAD.command.Command;
import seedu.RLAD.exception.RLADException;
import seedu.RLAD.budget.BudgetManager;
import seedu.RLAD.budget.BudgetCommand;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RLAD {
    private final Ui ui;
    private final TransactionManager transactions;
    private Parser parser;
    private BudgetManager budgetManager;

    public RLAD() {
        this.ui = new Ui();
        this.transactions = new TransactionManager();
        this.parser = new Parser();
        this.budgetManager = new BudgetManager(transactions);
        this.budgetManager.setUi(this.ui);
        this.transactions.setBudgetManager(budgetManager);
        this.transactions.loadFromAutoSave();
        this.budgetManager.load();

        // daddy logger takes care of all the loggers from here
        Logger packageLogger = Logger.getLogger("seedu.RLAD");
        // hides the logs from the user unless something crashes
        packageLogger.setLevel(Level.WARNING);
    }

    public void run() {
        ui.printWelcomeGuide();
        boolean isExit = false;

        while (!isExit) {
            System.out.flush();
            System.err.flush();

            try {
                String input = ui.readCommand();

                if (input.trim().equalsIgnoreCase("exit")) {
                    isExit = true;
                    ui.showExit();
                    continue;
                }

                ui.showLine();

                // The parser returns the exact command type to execute
                Command cmd = Parser.parse(input);

                // Custom error check for each Command class
                if (cmd.hasValidArgs()) {
                    // Check if this command needs BudgetManager
                    if (cmd instanceof BudgetCommand) {
                        // Pass the shared budgetManager instance
                        cmd.execute(transactions, ui, budgetManager);
                    } else {
                        cmd.execute(transactions, ui);
                    }

                    // Optional: Update budgets after transaction changes
                    // This ensures budget data is always fresh
                    String cmdName = cmd.getClass().getSimpleName();
                    if (cmdName.equals("AddCommand") ||
                            cmdName.equals("DeleteCommand") ||
                            cmdName.equals("ModifyCommand")) {
                        // Transaction data changed - you could trigger a budget refresh here if needed
                    }

                } else {
                    ui.showError("Invalid arguments for " + input.split(" ")[0]);
                    ui.printPossibleOptions();
                }

            } catch (NoSuchElementException e) {
                // EOF reached (e.g., piped input ended) — exit gracefully
                isExit = true;
                ui.showExit();
                continue;
            } catch (RLADException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("An unexpected error occurred: " + e.getMessage());
            }

            if (!isExit) {
                ui.showLine();
            }
        }
    }

    public static void main(String[] args) {
        new RLAD().run();
    }
}
