package seedu.RLAD;

import seedu.RLAD.command.Command;

import java.util.Scanner;

/**
 * Handles all user interface interactions including input reading and output display.
 *
 * <p>The Ui class is responsible for:
 * <ul>
 *   <li>Displaying the RLAD logo and welcome message</li>
 *   <li>Reading user commands from standard input</li>
 *   <li>Formatting and displaying results, errors, and help text</li>
 *   <li>Managing confirmation dialogs for destructive operations</li>
 * </ul>
 *
 * @version 2.0
 */
public class Ui {

    /** Visual separator line for command output formatting */
    private static final String SEPARATOR =
            "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀";

    /** Scanner for reading user input from console */
    private final Scanner userScanner = new Scanner(System.in);

    /**
     * Constructs the UI and displays the RLAD logo on startup.
     */
    public Ui() {
        Logo.printRLAD();
    }

    /**
     * Reads a command from the user with a prompt indicator.
     *
     * @return The raw input string from the user
     */
    public String readCommand() {
        System.out.print("> ");
        return userScanner.nextLine();
    }

    /**
     * Displays a visual separator line.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display
     */
    public void showError(String message) {
        System.out.println("❌ ERROR: " + message);
    }

    /**
     * Displays a result message to the user.
     *
     * @param message The result message to display
     */
    public void showResult(String message) {
        System.out.println(message);
    }

    /**
     * Displays the exit message when the user quits the application.
     */
    public void showExit() {
        System.out.println("Thank you for using RLAD!\n" +
                "Your data has been saved. See you next time! 👋");
    }

    /**
     * Prints the welcome guide with available commands summary.
     */
    public void printWelcomeGuide() {
        showLine();
        System.out.println("💰 Welcome to RLAD - Record Losses And Debt!");
        System.out.println("Manage your finances from the command line - no spreadsheet headaches.");
        printQuickStart();
        System.out.println("Type 'help' for full documentation or 'help <command>' for details.");
        showLine();
    }

    /**
     * Prints quick start examples for new users.
     */
    private void printQuickStart() {
        System.out.println("\n📌 Quick Start Examples:");
        System.out.println("  add credit 3000 2026-03-01 salary \"March salary\"     # Add income");
        System.out.println("  add debit 15.50 2026-03-05 food \"Lunch\"              # Add expense");
        System.out.println("  list                                                 # View all");
        System.out.println("  list type:debit                                      # Show expenses");
        System.out.println("  budget set 2026-03 1 500                             # Set food budget");
    }

    /**
     * Prints all available commands with brief descriptions.
     */
    public void printPossibleOptions() {
        System.out.println("\n📋 Available Commands:");
        System.out.println("  add       - Record income (credit) or expense (debit)");
        System.out.println("  delete    - Remove a transaction by ID");
        System.out.println("  modify    - Edit an existing transaction");
        System.out.println("  list      - View transactions (with optional filters)");
        System.out.println("  sort      - Set global sort order (amount/date)");
        System.out.println("  summarize - Show spending summary and breakdown");
        System.out.println("  budget    - Manage monthly budgets by category");
        System.out.println("  export    - Save transactions to CSV file");
        System.out.println("  import    - Load transactions from CSV file");
        System.out.println("  search    - Find transactions by keyword");
        System.out.println("  clear     - Delete ALL transactions (with confirmation)");
        System.out.println("  help      - Show this help or command-specific help");
        System.out.println("  exit      - Quit the application");

        System.out.println("\n💡 Quick Tips:");
        System.out.println("  • Use 'list type:debit' to see only expenses");
        System.out.println("  • Use 'list cat:food' to filter by category");
        System.out.println("  • Use 'list from:2026-03-01 to:2026-03-31' for date ranges");
        System.out.println("  • Use quotes for multi-word descriptions: \"chicken rice\"");
    }

    /**
     * Prints detailed help for the 'add' command.
     */
    public void printAddManual() {
        System.out.println("\n📝 Command: add");
        System.out.println("Description: Add a new credit (income) or debit (expense).");
        System.out.println("\nFormat:");
        System.out.println("  add <type> <amount> <date> [category] [description]");
        System.out.println("\nParameters:");
        System.out.println("  type        - 'credit' (income) or 'debit' (expense)");
        System.out.println("  amount      - Positive number (max $10,000,000)");
        System.out.println("  date        - YYYY-MM-DD format");
        System.out.println("  category    - Optional single word (e.g., food, salary)");
        System.out.println("  description - Optional (use quotes for spaces)");
        System.out.println("\nExamples:");
        System.out.println("  add credit 3000 2026-03-01 salary \"March salary\"");
        System.out.println("  add debit 15.50 2026-03-05 food \"Chicken rice\"");
        System.out.println("  add debit 5.00 2026-03-06");
    }

    /**
     * Prints detailed help for the 'modify' command.
     */
    public void printModifyManual() {
        System.out.println("\n📝 Command: modify");
        System.out.println("Description: Update one or more fields of a transaction.");
        System.out.println("\nFormat:");
        System.out.println("  modify <hashID> field1=value1 [field2=value2 ...]");
        System.out.println("\nAvailable fields:");
        System.out.println("  type, amount, date, category, description");
        System.out.println("\nExamples:");
        System.out.println("  modify a7b2c3 amount=25.00");
        System.out.println("  modify a7b2c3 amount=20.00 description=\"Fancy lunch\"");
        System.out.println("  modify a7b2c3 category=transport date=2026-03-06");
    }

    /**
     * Prints detailed help for the 'delete' command.
     */
    public void printDeleteManual() {
        System.out.println("\n📝 Command: delete");
        System.out.println("Description: Permanently remove a transaction.");
        System.out.println("\nFormat:");
        System.out.println("  delete <hashID>");
        System.out.println("\nExample:");
        System.out.println("  delete a7b2c3");
        System.out.println("\nTip: Use 'list' to see all transaction IDs.");
    }

    /**
     * Prints detailed help for the 'list' command with filter options.
     */
    public void printListManual() {
        System.out.println("\n📝 Command: list");
        System.out.println("Description: View transactions with optional filtering.");
        System.out.println("\nFormat:");
        System.out.println("  list [filters...]");
        System.out.println("\nAvailable filters:");
        System.out.println("  type:credit|debit  - Filter by transaction type");
        System.out.println("  cat:category       - Filter by category (partial match)");
        System.out.println("  from:YYYY-MM-DD    - Transactions on or after this date");
        System.out.println("  to:YYYY-MM-DD      - Transactions on or before this date");
        System.out.println("  min:amount         - Minimum amount");
        System.out.println("  max:amount         - Maximum amount");
        System.out.println("\nExamples:");
        System.out.println("  list                                    # All transactions");
        System.out.println("  list type:debit                         # All expenses");
        System.out.println("  list cat:food                           # Food transactions");
        System.out.println("  list from:2026-03-01 to:2026-03-31      # March transactions");
        System.out.println("  list type:debit cat:food min:10         # Food expenses over $10");
    }

    /**
     * Prints detailed help for the 'filter' command.
     */
    public void printFilterManual() {
        System.out.println("\n📝 Command: filter");
        System.out.println("Description: Display transactions matching filter criteria.");
        System.out.println("\nFormat:");
        System.out.println("  filter [filters...]");
        System.out.println("\nAvailable filters (same as 'list'):");
        System.out.println("  type:credit|debit  - Filter by transaction type");
        System.out.println("  cat:category       - Filter by category (partial match)");
        System.out.println("  from:YYYY-MM-DD    - Transactions on or after this date");
        System.out.println("  to:YYYY-MM-DD      - Transactions on or before this date");
        System.out.println("  min:amount         - Minimum amount");
        System.out.println("  max:amount         - Maximum amount");
        System.out.println("\nExamples:");
        System.out.println("  filter type:debit");
        System.out.println("  filter cat:food");
        System.out.println("  filter from:2026-01-01 to:2026-06-30");
        System.out.println("  filter type:debit cat:food min:10");
    }

    /**
     * Prints detailed help for the 'summarize' command.
     */
    public void printSummaryManual() {
        System.out.println("\n📝 Command: summarize");
        System.out.println("Description: Show financial summary with category breakdown.");
        System.out.println("\nFormat:");
        System.out.println("  summarize [filters...]");
        System.out.println("\nFilters (same as 'list' command):");
        System.out.println("  type:credit|debit, cat:category, from:date, to:date");
        System.out.println("\nExamples:");
        System.out.println("  summarize                               # All transactions");
        System.out.println("  summarize type:debit                    # Summary of expenses only");
        System.out.println("  summarize from:2026-01-01 to:2026-03-31 # Q1 summary");
    }

    /**
     * Prints detailed help for the 'budget' command.
     */
    public void printBudgetManual() {
        System.out.println("\n📝 Command: budget");
        System.out.println("Description: Manage monthly budgets by category.");
        System.out.println("\nFormat:");
        System.out.println("  budget set <YYYY-MM> <category_code> <amount>");
        System.out.println("  budget view [YYYY-MM]");
        System.out.println("  budget edit <YYYY-MM> <category_code> <amount>");
        System.out.println("  budget delete <YYYY-MM> <category_code>");
        System.out.println("  budget yearly [YYYY]");
        System.out.println("\nCategory Codes:");
        System.out.println("  1=Food, 2=Transport, 3=Utilities, 4=Housing");
        System.out.println("  5=Health, 6=Debt, 7=Childcare, 8=Shopping");
        System.out.println("  9=Gifts, 10=Investments, 11=Emergency, 12=Savings");
        System.out.println("\nExamples:");
        System.out.println("  budget set 2026-03 1 500       # $500 food budget for March");
        System.out.println("  budget view 2026-03            # View March budget");
        System.out.println("  budget edit 2026-03 1 600      # Increase to $600");
        System.out.println("  budget delete 2026-03 1        # Remove food budget");
        System.out.println("  budget yearly 2026             # Full year summary");
    }

    /**
     * Prints detailed help for the 'export' command.
     */
    public void printExportManual() {
        System.out.println("\n📝 Command: export");
        System.out.println("Description: Export transactions to CSV file.");
        System.out.println("\nFormat:");
        System.out.println("  export [filename]");
        System.out.println("\nExamples:");
        System.out.println("  export                    # Creates transactions_YYYY-MM-DD.csv");
        System.out.println("  export backup.csv         # Creates backup.csv");
        System.out.println("\nNote: The CSV can be opened in Excel or Google Sheets.");
    }

    /**
     * Prints detailed help for the 'import' command.
     */
    public void printImportManual() {
        System.out.println("\n📝 Command: import");
        System.out.println("Description: Import transactions from CSV file.");
        System.out.println("\nFormat:");
        System.out.println("  import <filename> [merge]");
        System.out.println("\nModes:");
        System.out.println("  Without 'merge' - Replaces all existing data");
        System.out.println("  With 'merge'    - Adds to existing data");
        System.out.println("\nExamples:");
        System.out.println("  import backup.csv        # Replace all data");
        System.out.println("  import backup.csv merge  # Add to existing data");
        System.out.println("\nNote: The CSV must have headers: HashID,Type,Category,Amount,Date,Description");
    }

    /**
     * Prints detailed help for the 'clear' command.
     */
    public void printClearManual() {
        System.out.println("\n📝 Command: clear");
        System.out.println("Description: Delete ALL transactions permanently.");
        System.out.println("\nFormat:");
        System.out.println("  clear [force]");
        System.out.println("\nOptions:");
        System.out.println("  force - Skip confirmation prompt");
        System.out.println("\nExamples:");
        System.out.println("  clear           # Asks for confirmation");
        System.out.println("  clear force     # Deletes immediately");
        System.out.println("\n⚠️  Warning: This action cannot be undone!");
    }

    /**
     * Prints detailed help for the 'sort' command.
     */
    public void printSortManual() {
        System.out.println("\n📝 Command: sort");
        System.out.println("Description: Set persistent sort order for all 'list' commands.");
        System.out.println("\nFormat:");
        System.out.println("  sort [amount|date] [asc|desc]");
        System.out.println("  sort reset");
        System.out.println("  sort                     # Show current sort");
        System.out.println("\nExamples:");
        System.out.println("  sort amount              # Sort by amount (lowest first)");
        System.out.println("  sort amount desc         # Sort by amount (highest first)");
        System.out.println("  sort date desc           # Sort by date (newest first)");
        System.out.println("  sort reset               # Clear sort order");
    }

    /**
     * Prints detailed help for the 'search' command.
     */
    public void printSearchManual() {
        System.out.println("\n📝 Command: search");
        System.out.println("Description: Search transactions by keyword.");
        System.out.println("\nFormat:");
        System.out.println("  search <keyword>");
        System.out.println("\nSearches in:");
        System.out.println("  • Description");
        System.out.println("  • Category");
        System.out.println("  • HashID");
        System.out.println("  • Amount");
        System.out.println("\nExamples:");
        System.out.println("  search chicken");
        System.out.println("  search a7b2c3");
        System.out.println("  search 15.50");
    }

    /**
     * Prints the help manual for the 'help' command itself.
     */
    public void printHelpManual() {
        System.out.println("\n📝 Command: help");
        System.out.println("Description: Show usage instructions.");
        System.out.println("\nFormat:");
        System.out.println("  help [command]");
        System.out.println("\nExamples:");
        System.out.println("  help                    # Show all commands");
        System.out.println("  help add                # Show 'add' command details");
        System.out.println("  help budget             # Show budget commands");
    }

    /**
     * Prompts the user for confirmation before destructive operations.
     *
     * @param promptMessage The warning message to display
     * @return true if the user typed "CONFIRM" exactly, false otherwise
     */
    public boolean askConfirmation(String promptMessage) {
        System.out.println("\n⚠️  " + promptMessage);
        System.out.print("Type 'CONFIRM' to proceed: ");
        String input = userScanner.nextLine().trim();
        boolean confirmed = input.equals("CONFIRM");
        if (!confirmed) {
            System.out.println("Operation cancelled.");
        }
        return confirmed;
    }

    /**
     * Prints the output of a command (default implementation).
     *
     * @param command The command whose output to display
     */
    public void printCommandOutput(Command command) {
        System.out.println(command.toString());
    }
}
