package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;
import seedu.RLAD.storage.CsvStorageManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Imports transactions from a CSV file, either replacing or merging with existing data.
 */
public class ImportCommand extends Command {

    /**
     * Creates a new ImportCommand.
     *
     * @param rawArgs the raw argument string
     */
    public ImportCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        Map<String, String> flags = FilterCommand.parseFlags(rawArgs);

        String filePath = flags.get("file");
        if (filePath == null || filePath.isBlank()) {
            throw new RLADException("--file is required for import. "
                    + "Usage: import --file FILENAME [--merge]");
        }

        boolean mergeMode = flags.containsKey("merge");

        if (!Files.exists(Paths.get(filePath))) {
            throw new RLADException("File not found: " + filePath);
        }

        if (!mergeMode && transactions.getTransactionCount() > 0) {
            boolean confirmed = ui.askConfirmation(
                    "WARNING: Replace mode will delete all "
                            + transactions.getTransactionCount() + " existing transactions.\n"
                            + "Use --merge to add to existing data instead.");
            if (!confirmed) {
                ui.showResult("Import cancelled.");
                return;
            }
        }

        CsvStorageManager.CsvImportResult result = CsvStorageManager.importFromCsv(filePath);

        for (String error : result.getErrors()) {
            ui.showError(error);
        }

        if (result.getSuccessCount() == 0) {
            throw new RLADException("No valid transactions found in file.");
        }

        if (!mergeMode) {
            transactions.clearAllTransactions();
        }

        for (Transaction t : result.getTransactions()) {
            transactions.addTransaction(t);
        }

        ui.showResult("Import complete: " + result.getSuccessCount() + " succeeded, "
                + result.getFailCount() + " failed.");
    }

    @Override
    public boolean hasValidArgs() {
        return true;
    }
}
