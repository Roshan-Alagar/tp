package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

public abstract class Command {
    protected final String action;
    protected final String rawArgs;

    // For commands with no action or args (e.g., exit/summarize default)
    protected Command() {
        this.action = null;
        this.rawArgs = null;
    }

    // For commands that only need rawArgs
    protected Command(String rawArgs) {
        this.action = null;
        this.rawArgs = rawArgs;
    }

    // For commands needing both
    protected Command(String action, String rawArgs) {
        this.action = action;
        this.rawArgs = rawArgs;
    }

    /**
     * The "Bridge": Uses data from 'transactions' and
     * sends output to 'ui'.
     */
    public abstract void execute(TransactionManager transactions, Ui ui) throws RLADException;
    /**
     * Validates if the internal state of the command (parsed args)
     * is actually runnable.
     */
    public abstract boolean hasValidArgs();
}
