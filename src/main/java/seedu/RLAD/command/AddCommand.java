package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Transaction;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddCommand extends Command {
    public AddCommand(String rawArgs) {
        super(rawArgs);
    }

    /**
     * Parses the raw argument string to extract flag-value pairs.
     * Handles quoted values (like descriptions with spaces).
     *
     * @param rawArgs The raw input string (e.g., "--type debit --amount 15.50 --date 2026-02-18")
     * @return A Map with flag names as keys and their values as strings
     */
    private Map<String, String> parseArguments(String rawArgs) {
        Map<String, String> argsMap = new HashMap<>();

        //Check if the string is empty, if it's empty then the empty map is returned, nothing to parse
        if (rawArgs == null || rawArgs.trim().isEmpty()) {
            return argsMap;
        }

        //Variables to track our position while reading
        StringBuilder currentFlag = null;   //To build the flag name char by char (like "--type")
        StringBuilder currentValue = new StringBuilder();   //Builds the value char by char
        boolean insideQuotes = false;   //A flag to check if we're inside the quotes (To handle description with spaces)

        //Loop through each character of the rawArgs
        for (int i = 0; i < rawArgs.length(); i++) {
            char c = rawArgs.charAt(i); //Stores the first char in c

            //Handle quotes, checks when you reached the description part
            if (c == '"') {
                insideQuotes = !insideQuotes;
                continue;   //Moves on to the next char of description
            }

            //If we hit a space and we're not inside quotes, it's the input of a non-description flag
            if (c == ' ' && !insideQuotes) {
                //If we have a complete flag and value , store it
                if (currentFlag != null && currentValue.length() > 0) {
                    argsMap.put(currentFlag.toString(), currentValue.toString().trim());    //Add or updates the flag-string pair into the HashMap. Updates if the key is already present
                    currentFlag = null; //Reset flag
                    currentValue.setLength(0);  //Clear value
                }
                continue;
            }

            //Check for the start of the flag (--something)
            if (c == '-' && i + 1 < rawArgs.length() && rawArgs.charAt(i + 1) == '-') {
                //Stores the flag, value pair into the HashMap once it's fully found
                if (currentFlag != null && currentValue.length() > 0) {
                    argsMap.put(currentFlag.toString(), currentValue.toString().trim());
                    currentValue.setLength(0);
                }

                //Extract the flag name (--type etc.)
                int flagStart = i;
                //Finding the flag name
                while (i < rawArgs.length() && rawArgs.charAt(i) != ' ') {
                    i++;
                }
                //Flag found, finds the flag substring in the rawArgs and concatenates to currentFlag
                currentFlag = new StringBuilder(rawArgs.substring(flagStart, i));
                i--; //Adjust for loop increment
                continue;
            }

            /*
            The flag is found, the code can now go to the input string after the flag, so add character/string to currentValue.
            You'll then get a matching <flag, string> pair
             */
            if (currentFlag != null) {
                currentValue.append(c); //Appends the input string into currentValue
            }
        }

        /*
        Save the last flag-string pair
        The last flag-string pair is found but not stored yet in the previous for-loop since it onyl saves when it reaches a space
        So need this code to assign the last pair
         */
        if (currentFlag != null && currentValue.length() > 0) {
            argsMap.put(currentFlag.toString(), currentValue.toString().trim());
        }
        return argsMap;
    }

    /**
     * Validates that all required fields are present and non-empty.
     * Also validates that type is either "debit" or "credit".
     *
     * @param parsedArgs The map of parsed arguments
     * @throws RLADException if any required field is missing or invalid
     */
    private void validateRequiredFields(Map<String, String> parsedArgs) throws RLADException {
        // Only validates the necessary fields except category and description
        String[] requiredFields = {"--type", "--amount", "--date"};

        for (String field : requiredFields) {
            //Checks if the flag field is empty
            if (!parsedArgs.containsKey(field) || parsedArgs.get(field).trim().isEmpty()) {
                //Show error messages
                throw new RLADException("Missing required field: " + field);
            }
        }

        //Validate that --type is either "debit" or "credit"
        String type = parsedArgs.get("--type");
        if (!type.equals("debit") && !type.equals("credit")) {
            throw new RLADException("Invalid --type. Must be either 'debit' or 'credit'");
        }
    }

    /**
     * Converts a string amount to a double.
     * Validates that amount is positive and rounds to 2 decimal places.
     *
     * @param amountStr The amount as a string
     * @return The amount as a double
     * @throws RLADException if the amount format is invalid
     */
    private double convertAmount(String amountStr) throws RLADException {
        try {
            return Double.parseDouble(amountStr);   //Code that may cause an exception
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount format. Please enter a valid number (e.g., 15.50)");    //Code to execute if the exception is true
        }
    }

    /**
     * Converts a string date to a LocalDate object.
     * Expected format: yyyy-MM-dd
     *
     * @param dateStr The date as a string
     * @return The date as a LocalDate
     * @throws RLADException if the date format is invalid
     */
    private LocalDate convertDate(String dateStr) throws RLADException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new RLADException("Invalid date format. Please use yyyy-MM-dd (e.g., 2026-02-18)");
        }
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        // TODO: Use a tokenizer or regex to extract --type, --amount, --category, --date, and --description.
        // Step 1: Parse
        Map<String, String> parsedArgs = parseArguments(rawArgs);

        // TODO: Validate that mandatory fields (--type, --amount, --date) are present.
        // Step 2: Validate required fields
        validateRequiredFields(parsedArgs);

        // TODO: Convert the amount string to double and date string to LocalDate.
        // Step 3: Convert data types
        double amount = convertAmount(parsedArgs.get("--amount"));
        LocalDate date = convertDate(parsedArgs.get("--date"));

        // Step 4: Get all fields (optional fields may be null)
        String type = parsedArgs.get("--type");
        String category = parsedArgs.get("--category");  // May be null
        String description = parsedArgs.get("--description");  // May be null

        // TODO: Create a new Transaction object and add it via transactions.addTransaction().
        // Step 5: Create Transaction object
        // Note: category and description can be null
        Transaction newTransaction = new Transaction(type, category, amount, date, description);

        // Step 6: Add to TransactionManager
        transactions.addTransaction(newTransaction);

        // TODO: Provide success feedback to the user via ui.showResult().
        // Step 7: Show success message with the hashId
        String successMessage = String.format(
                "✅ Transaction added successfully!\n   HashID: %s\n   %s: $%.2f on %s\n   Category: %s\n   Description: %s",
                newTransaction.getHashId(),
                type.toUpperCase(),
                amount,
                date,
                category != null ? category : "(none)",
                description != null ? "\"" + description + "\"" : "(none)"
        );
        ui.showResult(successMessage);
    }

    @Override
    public boolean hasValidArgs() {
        // TODO: Check if rawArgs contains the required flags to prevent runtime RLADExceptions.
        return rawArgs != null &&
                rawArgs.contains("--type") &&
                rawArgs.contains("--amount") &&
                rawArgs.contains("--date");
    }
}
