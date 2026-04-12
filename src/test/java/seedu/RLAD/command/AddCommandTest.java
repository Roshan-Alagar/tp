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
            AddCommand cmd1 = new AddCommand("debit 15.50 2026-02-18 food \"Dinner at UTown\"");
            cmd1.execute(tm, ui);
            System.out.println("✓ PASS\n");
        } catch (RLADException e) {
            System.out.println("✗ FAIL: " + e.getMessage() + "\n");
        }

        // Test 2: Minimal valid input (only required fields)
        System.out.println("Test 2: Minimal valid input");
        try {
            AddCommand cmd2 = new AddCommand("credit 100.00 2026-02-19");
            cmd2.execute(tm, ui);
            System.out.println("✓ PASS\n");
        } catch (RLADException e) {
            System.out.println("✗ FAIL: " + e.getMessage() + "\n");
        }

        // Test 3: Missing required field (--type)
        System.out.println("Test 3: Missing required field");
        try {
            AddCommand cmd3 = new AddCommand("50.00 2026-02-20");
            cmd3.execute(tm, ui);
            System.out.println("✗ FAIL (Expected to throw exception but didn't)\n");
        } catch (RLADException e) {
            System.out.println("✓ PASS (Expected - Missing field): " + e.getMessage() + "\n");
        }

        // Test 4: Invalid amount format
        System.out.println("Test 4: Invalid amount format");
        try {
            AddCommand cmd4 = new AddCommand("debit fifteen 2026-02-20");
            cmd4.execute(tm, ui);
            System.out.println("✗ FAIL (Expected to throw exception but didn't)\n");
        } catch (RLADException e) {
            System.out.println("✓ PASS (Expected - Invalid amount): " + e.getMessage() + "\n");
        }

        // Test 5: Invalid date format
        System.out.println("Test 5: Invalid date format");
        try {
            AddCommand cmd5 = new AddCommand("debit 50.00 20-02-2026");
            cmd5.execute(tm, ui);
            System.out.println("✗ FAIL (Expected to throw exception but didn't)\n");
        } catch (RLADException e) {
            System.out.println("✓ PASS (Expected - Invalid date): " + e.getMessage() + "\n");
        }

        // Test 6: Invalid type
        System.out.println("Test 6: Invalid type");
        try {
            AddCommand cmd6 = new AddCommand("savings 50.00 2026-02-20");
            cmd6.execute(tm, ui);
            System.out.println("✗ FAIL (Expected to throw exception but didn't)\n");
        } catch (RLADException e) {
            System.out.println("✓ PASS (Expected - Invalid type): " + e.getMessage() + "\n");
        }

        // Test 7: Reserved category keyword "none"
        System.out.println("Test 7: Reserved category keyword 'none'");
        try {
            AddCommand cmd7 = new AddCommand("debit 10.00 2026-03-05 none \"literal none\"");
            cmd7.execute(tm, ui);
            System.out.println("✗ FAIL (Expected to throw exception but didn't)\n");
        } catch (RLADException e) {
            System.out.println("✓ PASS (Expected - Reserved keyword): " + e.getMessage() + "\n");
        }

        // Test 8: Reserved category keyword "(none)"
        System.out.println("Test 8: Reserved category keyword '(none)'");
        try {
            AddCommand cmd8 = new AddCommand("debit 10.00 2026-03-05 (none) \"literal (none)\"");
            cmd8.execute(tm, ui);
            System.out.println("✗ FAIL (Expected to throw exception but didn't)\n");
        } catch (RLADException e) {
            System.out.println("✓ PASS (Expected - Reserved keyword): " + e.getMessage() + "\n");
        }

        // Test 9: Reserved keyword case-insensitive "NONE"
        System.out.println("Test 9: Reserved keyword case-insensitive 'NONE'");
        try {
            AddCommand cmd9 = new AddCommand("debit 10.00 2026-03-05 NONE \"uppercase none\"");
            cmd9.execute(tm, ui);
            System.out.println("✗ FAIL (Expected to throw exception but didn't)\n");
        } catch (RLADException e) {
            System.out.println("✓ PASS (Expected - Reserved keyword): " + e.getMessage() + "\n");
        }

        // Test 10: Valid category not rejected
        System.out.println("Test 10: Valid category 'food' not rejected");
        try {
            AddCommand cmd10 = new AddCommand("debit 10.00 2026-03-05 food \"regular food\"");
            cmd10.execute(tm, ui);
            System.out.println("✓ PASS\n");
        } catch (RLADException e) {
            System.out.println("✗ FAIL: " + e.getMessage() + "\n");
        }

        // Test 11: Null category not rejected
        System.out.println("Test 11: Null category not rejected");
        try {
            AddCommand cmd11 = new AddCommand("debit 10.00 2026-03-05");
            cmd11.execute(tm, ui);
            System.out.println("✓ PASS\n");
        } catch (RLADException e) {
            System.out.println("✗ FAIL: " + e.getMessage() + "\n");
        }

        // Test 12: Empty quoted string should be rejected
        System.out.println("Test 12: Empty quoted string \"\"");
        try {
            AddCommand cmd12 = new AddCommand("credit 100 2026-03-01 food \"\"");
            cmd12.execute(tm, ui);
            System.out.println("✗ FAIL: Should have thrown exception\n");
        } catch (RLADException e) {
            System.out.println("✓ PASS: " + e.getMessage() + "\n");
        }

        // Test 13: Whitespace-only quoted string should be rejected
        System.out.println("Test 13: Whitespace quoted string \" \"");
        try {
            AddCommand cmd13 = new AddCommand("credit 100 2026-03-01 food \" \"");
            cmd13.execute(tm, ui);
            System.out.println("✗ FAIL: Should have thrown exception\n");
        } catch (RLADException e) {
            System.out.println("✓ PASS: " + e.getMessage() + "\n");
        }

        // Test 14: Unclosed quote should be rejected
        System.out.println("Test 14: Unclosed quote");
        try {
            AddCommand cmd14 = new AddCommand("credit 100 2026-03-01 food \"unclosed");
            cmd14.execute(tm, ui);
            System.out.println("✗ FAIL: Should have thrown exception\n");
        } catch (RLADException e) {
            System.out.println("✓ PASS: " + e.getMessage() + "\n");
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
