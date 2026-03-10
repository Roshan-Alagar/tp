package seedu.RLAD;

public class Logo {
    private static final String P = "               ";
    private static final String BORDER =
            P + "+================================================+";
    private static final String LOGO =
            P + "|        ____   _        _     ____              |\n"
            + P + "|       |  _ \\ | |      / \\   |  _ \\             |\n"
            + P + "|       | |_) || |     / _ \\  | | | |            |\n"
            + P + "|       |  _ < | |___ / ___ \\ | |_| |            |\n"
            + P + "|       |_| \\_\\|_____/_/   \\_\\|____/             |\n"
            + P + "|          Record Losses And Debt                |";

    public static void printRLAD() {
        System.out.println(BORDER);
        System.out.println(LOGO);
        System.out.println(BORDER);
        System.out.println();
    }
}
