package seedu.RLAD;

import seedu.RLAD.command.Command;

import java.util.Scanner;

public class Ui {
    private static final String SEPARATOR =
            "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀";
    private final Scanner userScanner = new Scanner(System.in);

    public Ui() {
        Logo.printRLAD();
    }

    // User input
    public String readCommand() {
        System.out.print("> "); // A simple prompt character
        return userScanner.nextLine();
    }

    // Space it
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    // no throwable error for now
    public void showError (String message) {
        System.out.println("ERROR: " + message);
    }

    public void showResult(String message) {
        System.out.println(message);
    }

    public void showExit() {
        System.out.println("Thank you for abusing me!\n See you next time...");
    }

    public void printWelcomeGuide() {
        showLine();
        System.out.println("Hello and welcome to RLAD!");
        System.out.println("Handle your financial life from one spot without the spreadsheet headaches");
        printPossibleOptions();
        System.out.println("Type 'help' for the full list or 'help <command>' for specific argument details.");
        showLine();
    }

    public void printPossibleOptions() {
        System.out.println("Available actions:");
        System.out.println("  add       : Record a new transaction");
        System.out.println("  modify    : Edit an existing entry");
        System.out.println("  budget    : Setting budget goals for the month");
        System.out.println("  delete    : Remove an entry");
        System.out.println("  sort      : Set or view the global sort order (amount/date, asc/desc)");
        System.out.println("  list      : View your transaction history (with filtering and sorting)");
        System.out.println("  summarize : Get a high-level breakdown of your spending");
        System.out.println("  export    : Export transactions to a CSV file");
        System.out.println("  import    : Import transactions from a CSV file");
        System.out.println("  filter    : Display transactions matching filter criteria");
        System.out.println("  search    : Search transactions by keyword");
        System.out.println("  clear     : Delete all transactions permanently");
        System.out.println("  help      : Show usage instructions (try 'help <command>')");
        System.out.println("  exit      : Exit the application");
        System.out.println("\nFormat:");
        System.out.println("\t$action --option_0 $argument_0 ... --option_k $argument_k");
    }

    public void printAddManual() {
        System.out.println("Command: add");
        System.out.println("Description: Adds a new credit (income) or debit (expense) entry.");
        System.out.println(
                "Parameters: --type [REQUIRED], --category, --amount [REQUIRED], --date [REQUIRED], --description");
        System.out.println("Example:");
        System.out.println(
                "  add --type credit --category food --amount 15.00 --date 2026.02.18 --description " +
                "`Hawker stall lunch set` ");
    }

    public void printModifyManual() {
        System.out.println("Command: modify");
        System.out.println("Description: Updates specific fields of an existing entry via its index.");
        System.out.println("Parameters: --hashID [REQUIRED], --type, --category, --amount, --date, --description");
        System.out.println("Example:");
        System.out.println("  modify --hashID a7b2 --amount 20.00 --description `Fancy hawker lunch` ");
    }

    public void printDeleteManual() {
        System.out.println("Command: delete");
        System.out.println("Description: Removes a transaction from the records permanently.");
        System.out.println("Parameters: --hashID [REQUIRED]");
        System.out.println("Example:");
        System.out.println("  delete --hashID a7b2");
    }

    public void printListManual() {
        System.out.println("Command: list");
        System.out.println("Description: Displays transactions. Use options to filter the results.");
        System.out.println("Options: --category, --type, --from (date), --to (date), --sort [date|amount]");
        System.out.println("Example:");
        System.out.println("  list --type debit --sort amount --from 2026.01.01 --to 2026.02.01");
    }

    public void printSummaryManual() {
        System.out.println("Command: summarize");
        System.out.println("Description: Provides a statistical overview of your finances.");
        System.out.println("Options: --by [category|month|type]");
        System.out.println("Example:");
        System.out.println("  summarize --by category");
    }

    /**
     * Prompts the user for confirmation and returns true if they type CONFIRM.
     * @param promptMessage the warning message to display before asking
     * @return true if user typed CONFIRM
     */
    public boolean askConfirmation(String promptMessage) {
        System.out.println(promptMessage);
        System.out.print("Type CONFIRM to proceed: ");
        String input = userScanner.nextLine().trim();
        return input.equals("CONFIRM");
    }

    public void printExportManual() {
        System.out.println("Command: export");
        System.out.println("Description: Exports all transactions to a CSV file.");
        System.out.println("Options: --file [filename], --path [directory]");
        System.out.println("Example:");
        System.out.println("  export --file backup.csv");
        System.out.println("  export --path /Users/name/Documents/");
    }

    public void printImportManual() {
        System.out.println("Command: import");
        System.out.println("Description: Imports transactions from a CSV file.");
        System.out.println("Options: --file [filename] (required), --merge");
        System.out.println("Example:");
        System.out.println("  import --file backup.csv");
        System.out.println("  import --file backup.csv --merge");
    }

    public void printClearManual() {
        System.out.println("Command: clear");
        System.out.println("Description: Deletes ALL transactions permanently.");
        System.out.println("Options: --force (skip confirmation)");
        System.out.println("Example:");
        System.out.println("  clear");
        System.out.println("  clear --force");
    }

    public void printSortManual() {
        System.out.println("Command: sort");
        System.out.println("Description: Set or view the global sort order for transactions.");
        System.out.println("Parameters: FIELD [DIRECTION]");
        System.out.println("  FIELD: amount or date");
        System.out.println("  DIRECTION: asc or desc (default: asc)");
        System.out.println("  Use 'sort reset' to clear global sort.");
        System.out.println("  Use 'sort' with no arguments to view current sort.");
        System.out.println("Example:");
        System.out.println("  sort amount desc");
        System.out.println("  sort date");
        System.out.println("  sort reset");
    }

    public void printBudgetManual() {
        System.out.println("Command: budget");
        System.out.println("Description: Manage monthly budget allocations by category.");
        System.out.println("Sub-commands: set, view, edit, delete");
        System.out.println("Parameters: --month YYYY-MM --category CODE --amount AMOUNT");
        System.out.println("  Category codes: 1=Food, 2=Transport, 3=Utilities, 4=Housing,");
        System.out.println("    5=Health/Insurance, 6=Debt, 7=Childcare, 8=Shopping,");
        System.out.println("    9=Gifts, 10=Investments, 11=Emergency, 12=Savings");
        System.out.println("Example:");
        System.out.println("  budget set --month 2026-03 --category 1 --amount 500.00");
        System.out.println("  budget view --month 2026-03");
        System.out.println("  budget edit --month 2026-03 --category 1 --amount 600.00");
        System.out.println("  budget delete --month 2026-03 --category 1");
    }

    public void printFilterManual() {
        System.out.println("Command: filter");
        System.out.println("Description: Display transactions matching filter criteria.");
        System.out.println("Options: --type, --category, --amount, --date, --date-from, --date-to, --sort");
        System.out.println("  --amount supports operators: -gt, -gte, -eq, -lt, -leq");
        System.out.println("  --date supports: today, yesterday, this-week, this-month, last-month, last-year");
        System.out.println("Example:");
        System.out.println("  filter --type debit --category food");
        System.out.println("  filter --amount -gt 50 --date-from 2026-01-01 --date-to 2026-06-30");
    }

    public void printSearchManual() {
        System.out.println("Command: search");
        System.out.println("Description: Search transactions by keyword.");
        System.out.println("Searches description, category, HashID, and amount.");
        System.out.println("Example:");
        System.out.println("  search chicken");
        System.out.println("  search a7b2");
    }

    public void printHelpManual() {
        System.out.println("Command: help");
        System.out.println("Description: Show usage instructions for commands.");
        System.out.println("Format: help [COMMAND]");
        System.out.println("  With no argument, lists all available commands.");
        System.out.println("  With a command name, shows detailed usage for that command.");
        System.out.println("Example:");
        System.out.println("  help");
        System.out.println("  help add");
    }

    public void printCommandOutput(Command command) {
        System.out.println(command.toString());
    }
}
