package seedu.RLAD;

import java.util.Scanner;
import java.util.ArrayList;

public class RLAD {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ArrayList<Double> income = new ArrayList<>();
        ArrayList<Double> expenses = new ArrayList<>();
        ArrayList<String> itemType = new ArrayList<>();
        Logo.printRLAD();
        addTransaction userTransaction = new addTransaction(income, expenses, itemType, in);
    }
}
