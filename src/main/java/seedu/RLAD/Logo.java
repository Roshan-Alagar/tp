package seedu.RLAD;

/**
 * Utility class for displaying the RLAD ASCII art logo.
 */
public class Logo {
    private static final String PADDING = "               ";
    private static final String TOP_LINE = PADDING
            + "+================================================+";
    private static final String BOTTOM_LINE = PADDING
            + "+================================================+";
    private static final String LOGO =
            PADDING + "|       ██████╗  ██╗       █████╗  ██████╗       |\n"
                    + PADDING + "|       ██╔══██╗ ██║      ██╔══██╗ ██╔══██╗      |\n"
                    + PADDING + "|       ██████╔╝ ██║      ███████║ ██║  ██║      |\n"
                    + PADDING + "|       ██╔══██╗ ██║      ██╔══██║ ██║  ██║      |\n"
                    + PADDING + "|       ██║  ██║ ███████╗ ██║  ██║ ██████╔╝      |\n"
                    + PADDING + "|       ╚═╝  ╚═╝ ╚══════╝ ╚═╝  ╚═╝ ╚═════╝       |\n"
                    + PADDING + "|              Record Losses And Debt            |";

    /**
     * Prints the RLAD ASCII art logo to standard output.
     */
    public static void printRLAD() {
        System.out.println(TOP_LINE);
        System.out.println(LOGO);
        System.out.println(BOTTOM_LINE);
        System.out.println();
    }
}