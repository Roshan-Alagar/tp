package seedu.RLAD;

import seedu.RLAD.command.Command;
import seedu.RLAD.exception.RLADException;

public class RLAD {
   private final Ui ui;
   private final TransactionManager transactions;

    public RLAD() {
        this.ui = new Ui();
        this.transactions = new TransactionManager();
    }

    public void run() {
        ui.printWelcomeGuide();
        boolean isExit = false;

        while (!isExit) {
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
                    cmd.execute(transactions, ui);
                } else {
                    ui.showError("Invalid arguments for " + input.split(" ")[0]);
                    ui.printPossibleOptions();
                }

            } catch (RLADException e) {
                ui.showError(e.getMessage());
                ui.printPossibleOptions();
            } catch (Exception e) {
                    ui.showError("An unexpected error occurred: " + e.getMessage());
                    ui.printPossibleOptions();
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
