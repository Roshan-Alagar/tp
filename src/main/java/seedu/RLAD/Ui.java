package seedu.RLAD;

import seedu.RLAD.command.Command;

import java.util.Scanner;

public class Ui {
    private final Scanner userScanner = new Scanner(System.in);
    private static final String SEPARATOR = "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀";
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
        System.out.println("Type 'help' for the full list or '$action help' for specific argument details.");
        showLine();
    }

    public void printPossibleOptions() {
        System.out.println("Available actions:");
        System.out.println("  add       : Record a new transaction");
        System.out.println("  modify    : Edit an existing entry");
        System.out.println("  delete    : Remove an entry");
        System.out.println("  list      : View your transaction history with filters");
        System.out.println("  summarize : Get a high-level breakdown of your spending");
        System.out.println("\nFormat:");
        System.out.println("\t$action --option_0 $argument_0 ... --option_k $argument_k");
    }

    public void printAddManual() {
        System.out.println("Command: add");
        System.out.println("Description: Adds a new credit (income) or debit (expense) entry.");
        System.out.println("Parameters: --type [REQUIRED], --category, --amount [REQUIRED], --date [REQUIRED], --description");
        System.out.println("Example:");
        System.out.println("  add --type credit --category food --amount 15.00 --date 2026.02.18 --description `Hawker stall lunch set` ");
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

    public void printCommandOutput(Command command) {
        System.out.println(command.toString());
    }
}
