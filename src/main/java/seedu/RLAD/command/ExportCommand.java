package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;
import seedu.RLAD.storage.CsvStorageManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

/**
 * Exports all transactions to a CSV file.
 */
public class ExportCommand extends Command {

    /**
     * Creates a new ExportCommand.
     *
     * @param rawArgs the raw argument string
     */
    public ExportCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        Map<String, String> flags = FilterCommand.parseFlags(rawArgs);

        String filename = flags.getOrDefault("file",
                "transactions_" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv");
        if (filename.isBlank()) {
            throw new RLADException("--file requires a filename. "
                    + "Example: export --file transactions.csv");
        }

        String directory = flags.getOrDefault("path", ".");
        if (directory.isBlank()) {
            throw new RLADException("--path requires a directory. "
                    + "Example: export --path ./data");
        }

        Path dirPath = Paths.get(directory);
        if (!Files.isDirectory(dirPath)) {
            throw new RLADException("Directory does not exist: " + directory);
        }

        ArrayList<Transaction> txns = transactions.getTransactions();
        if (txns.isEmpty()) {
            ui.showResult("No transactions to export.");
            return;
        }

        String fullPath = dirPath.resolve(filename).toString();
        CsvStorageManager.exportToCsv(txns, fullPath);

        ui.showResult("Exported " + txns.size() + " transactions to: " + fullPath);
    }

    @Override
    public boolean hasValidArgs() {
        return true;
    }
}
