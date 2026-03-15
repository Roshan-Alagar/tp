package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Transaction;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

public class AddCommandTest {
    public static void main(String[] args) {
        System.out.println("=== ADD COMMAND TESTS ===\n");

        TransactionManager tm = new TransactionManager();
        Ui ui = new Ui();

        // Test 1: Valid input with all fields
        System.out.println("Test 1: Valid input with all fields");
        try {
            AddCommand cmd1 = new AddCommand("--type debit --amount 15.50 " +
                    "--date 2026-02-18 --category food --description \"Dinner at UTown\"");
            cmd1.execute(tm, ui);
            System.out.println("✓ PASS\n");
        } catch (RLADException e) {
            System.out.println("✗ FAIL: " + e.getMessage() + "\n");
        }

        // Test 2: Minimal valid input (only required fields)
        System.out.println("Test 2: Minimal valid input");
        try {
            AddCommand cmd2 = new AddCommand("--type credit --amount 100.00 --date 2026-02-19");
            cmd2.execute(tm, ui);
            System.out.println("✓ PASS\n");
        } catch (RLADException e) {
            System.out.println("✗ FAIL: " + e.getMessage() + "\n");
        }

        // Test 3: Missing required field (--type)
        System.out.println("Test 3: Missing --type");
        try {
            AddCommand cmd3 = new AddCommand("--amount 50.00 --date 2026-02-20");
            cmd3.execute(tm, ui);
            System.out.println("✓ PASS\n");
        } catch (RLADException e) {
            System.out.println("✗ FAIL (Expected - Missing field): " + e.getMessage() + "\n");
        }

        // Test 4: Invalid amount format
        System.out.println("Test 4: Invalid amount format");
        try {
            AddCommand cmd4 = new AddCommand("--type debit --amount fifteen --date 2026-02-20");
            cmd4.execute(tm, ui);
            System.out.println("✓ PASS\n");
        } catch (RLADException e) {
            System.out.println("✗ FAIL (Expected - Invalid amount): " + e.getMessage() + "\n");
        }

        // Test 5: Invalid date format
        System.out.println("Test 5: Invalid date format");
        try {
            AddCommand cmd5 = new AddCommand("--type debit --amount 50.00 --date 20-02-2026");
            cmd5.execute(tm, ui);
            System.out.println("✓ PASS\n");
        } catch (RLADException e) {
            System.out.println("✗ FAIL (Expected - Invalid date): " + e.getMessage() + "\n");
        }

        // Test 6: Invalid type
        System.out.println("Test 6: Invalid type");
        try {
            AddCommand cmd6 = new AddCommand("--type savings --amount 50.00 --date 2026-02-20");
            cmd6.execute(tm, ui);
            System.out.println("✓ PASS\n");
        } catch (RLADException e) {
            System.out.println("✗ FAIL (Expected - Invalid type): " + e.getMessage() + "\n");
        }

        // Show final transactions
        System.out.println("\n=== FINAL TRANSACTIONS ===");
        if (tm.getTransactions().isEmpty()) {
            System.out.println("No transactions added.");
        } else {
            for (Transaction t : tm.getTransactions()) {
                System.out.println(t);
            }
        }
    }
}
