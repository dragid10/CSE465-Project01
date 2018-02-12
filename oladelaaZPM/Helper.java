import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class Helper {
    //    ========================= VARIABLES =========================
    private static Helper ourInstance = new Helper();
    private final HashSet<String> RESERVED_WORDS = new HashSet<>();
    private final HashSet<String> RESERVED_SYMBOLS = new HashSet<>();
    private final HashMap<String, Integer> INTEGER_VARIABLE_TABLE = new HashMap<>();
    private final HashMap<String, String> STRING_VARIABLE_TABLE = new HashMap<>();
    private File zpmFile;
    private int lineNumber;

    //    ========================= CONSTRUCTOR =========================
    private Helper() {
        RESERVED_WORDS.add("PRINT");
        RESERVED_WORDS.add("FOR");
        RESERVED_WORDS.add("ENDFOR");
        RESERVED_SYMBOLS.add("+=");
        RESERVED_SYMBOLS.add("*=");
        RESERVED_SYMBOLS.add("-=");
        lineNumber = 1;
    }

    //    ========================= GETTERS / SETTERS =========================
    private File getZpmFile() {
        return zpmFile;
    }

    public void setZpmFile(File zpmFile) {
        this.zpmFile = zpmFile;
    }

    //    ========================= METHODS =========================

    /**
     * Interprets the program by parsing each line and running a corresponding method
     */
    public void interpret() {
//        Variables used in this method
        String line;
        try {
//            Create buffered reader to read in file
            BufferedReader in = new BufferedReader(new FileReader(getZpmFile()));

//            Runs each line through a parsing method
            while ((line = in.readLine()) != null) {
                parseProgramLine(line.trim());
                lineNumber++;
            }

//            Closes buffered reader because we're done with it
            in.close();
        } catch (FileNotFoundException e) { // Thrown if the file doesn't actually exist
            System.err.println("The file passed in does not exist!");
            e.printStackTrace();
            System.exit(404);
        } catch (IOException e) { // Thrown if there was a problem reading a line for some reason
            System.err.println("Something went wrong while trying to read the file!");
            e.printStackTrace();
            System.exit(420);
        }
    }

    /**
     * Parses the line given by enacting a certain function on it,
     * depending on what the line consists of
     *
     * @param programLine The current line of the program to parse through
     */
    private void parseProgramLine(String programLine) {
//        Checks to see if the line ends with either a "ENDFOR" keyword or a semi-colon
        if (!endsWithSemicolon(programLine) || !endsForLoop(programLine)) {
            System.err.println("Missing Semi-colon on line: " + lineNumber);
            System.exit(400);
        }

//        Replaces all semi-colons with a space before the spplit, b/c it's uneeded
        programLine = programLine.replace(";", "").trim();

//        Sends the line to a specified method depending on what its action is
        String[] lineParts = programLine.split(" ");
        String actionToTake = lineParts[0];

        if (RESERVED_WORDS.contains(actionToTake)) {
            // TODO: 2/11/2018 Actually Implement this
            switch (actionToTake) {
                case "PRINT":
                    double hello = 0;
                    break;
                case "FOR":

                    break;
            }
        } else {
//            Send new array of just the variable components to a parsing method
            String[] variableParseList = new String[lineParts.length - 1];
            System.arraycopy(lineParts, 1, variableParseList, 0, lineParts.length - 1);
            parseVariable(variableParseList);
        }
    }

    /**
     * Analyzes each part of an assignment / declaration and handles its execution
     *
     * @param variableList Array of parts of a variable declaration or assignment
     */
    private void parseVariable(String[] variableList) {
        String operator = variableList[1];
        String variableName = variableList[0];
        String variableValue = variableList[2];

        if (RESERVED_SYMBOLS.contains(operator)) { // Checks if the operator is a reserved symbol for special parsing
            assignVariable(variableName, variableValue, operator);

        } else { // More likely a variable deceleration or type change
            assignVariable(variableName, variableValue);
        }
    }

    /**
     * @param key      Name of the variable
     * @param value    Value of the variable
     * @param operator Determines how value is set
     */
    private void assignVariable(String key, String value, String operator) {
        try {
//        Try to parse value to see if its a number, then see if it has already been declared
//        If it hasn't been declared already, throw a runtime error
            if (!INTEGER_VARIABLE_TABLE.containsKey(key)) {
                System.err.println("Cannot find symbol: " + key);
                System.exit(400);
            }

//          Gets the number we want to operate with and the number already stored for the variable
            int parsedValue = Integer.parseInt(value);
            int currentMapValue = INTEGER_VARIABLE_TABLE.get(key);

//            Sends the two numbers through the method to apply the operator and return for storage in variables HashMap
            int valueToStore = applyOperator(currentMapValue, parsedValue, operator);
            INTEGER_VARIABLE_TABLE.put(key, valueToStore);
        } catch (NumberFormatException e) {
            if (value.contains("\"")) { // Hit this if the value is actually a string literal

//          Checks to see if it has already been declared in order to apply the operation
                if (!STRING_VARIABLE_TABLE.containsKey(key)) {
                    System.err.println("Cannot find symbol: " + key);
                    System.exit(400);
                }

//          Gets the String that's currently saved in the variables HashMap
//          Cleans current value to get rid of quotation marks applies operator, and stores new value
                String currentMapValue = STRING_VARIABLE_TABLE.get(key);
                value = value.replace("\"", "").trim();
                String valueToStore = applyOperator(currentMapValue, value, operator);
                STRING_VARIABLE_TABLE.put(key, valueToStore);
            } else { // We know that the value is instead another variable

//                Checks to see if the variable is a string or an int
                if (STRING_VARIABLE_TABLE.containsKey(value)) { // It's a String
//                    Applies operator and stores in string HashMap
                    String currentlyStoredKeyString = STRING_VARIABLE_TABLE.get(key);
                    String currentlyStoredValueString = STRING_VARIABLE_TABLE.get(value);
                    String valToStore = applyOperator(currentlyStoredKeyString, currentlyStoredValueString, operator);
                    STRING_VARIABLE_TABLE.put(key, valToStore);
                } else if (INTEGER_VARIABLE_TABLE.containsKey(value)) { //It's an Int

//                    Applies operator and stores in int HashMap
                    int currentlyStoredInt = INTEGER_VARIABLE_TABLE.get(key);
                    int currentlyStoredValueInt = INTEGER_VARIABLE_TABLE.get(value);
                    int valToStore = applyOperator(currentlyStoredInt, currentlyStoredValueInt, operator);
                    INTEGER_VARIABLE_TABLE.put(key, valToStore);
                } else { // It hasn't been declared before

                    System.err.println("Cannot find symbol: " + value);
                    System.exit(400);
                }
            }
        }
    }

    /**
     * Adds the key value pair to the variable list as a variable
     *
     * @param key   Name of the variable
     * @param value Value of the variable
     */
    private void assignVariable(String key, String value) {
        try {
//        Try to parse value to see if its a number, then see if it was a string before.
//        If it was a string before, then we remove it from the string HashMap because it's now an int
            int actualVal = Integer.parseInt(value);
            if (STRING_VARIABLE_TABLE.containsKey(key)) {
                STRING_VARIABLE_TABLE.remove(key);
            }
            INTEGER_VARIABLE_TABLE.put(key, actualVal);
        } catch (NumberFormatException e) {
//          Means that the value is not a number, but instead a string
//          Checks to see if it was in the int HashMap before, and if so then delete it b/c its now a string
            if (INTEGER_VARIABLE_TABLE.containsKey(key)) {
                INTEGER_VARIABLE_TABLE.remove(key);
            }

            if (value.contains("\"")) { // If the value is a string literal, then clean it up & add to the HashMap
                value = value.replace("\"", "").trim();
                STRING_VARIABLE_TABLE.put(key, value);
            } else { // If not a string literal, then it refers to another variable
                if (INTEGER_VARIABLE_TABLE.containsKey(value)) {
                    INTEGER_VARIABLE_TABLE.put(key, INTEGER_VARIABLE_TABLE.get(value));
                } else if (STRING_VARIABLE_TABLE.containsKey(value)) {
                    STRING_VARIABLE_TABLE.put(key, STRING_VARIABLE_TABLE.get(value));
                } else { // If the variable hasn't been  declared, then throw a runtime error
                    System.err.println("Cannot find symbol: " + value);
                    System.exit(400);
                }
            }
        }
    }

    /**
     * @param newValue    Number to be applied to the value already stored in HashMap
     * @param storedValue Number currently stored in HashMap
     * @param operator    Operator to apply to storedValue and newValue (e.g. storedValue *= newValue)
     * @return int New value to store in the variables HashMap
     */
    private int applyOperator(int storedValue, int newValue, String operator) {
        switch (operator) {
            case "+=":
                storedValue += newValue;
                break;
            case "-=":
                storedValue -= newValue;
                break;
            case "*=":
                storedValue *= newValue;
                break;
            default:
                System.err.println("Illegal character: " + operator);
                System.exit(400);
                break;
        }
        return storedValue;
    }

    /**
     * @param newValue    String to be applied to the value already stored in HashMap
     * @param storedValue String currently stored in HashMap
     * @param operator    Operator to apply to storedValue and newValue (e.g. storedValue *= newValue)
     * @return int New value to store in the variables HashMap
     */
    private String applyOperator(String storedValue, String newValue, String operator) {
        switch (operator) {
            case "+=":
                storedValue += newValue;
                break;
            default:
                System.err.println("Illegal character: " + operator);
                System.exit(400);
                break;
        }
        return storedValue;
    }

    /**
     * @param programLine Program line passed in to check if it ends with a ENDFOR keyword
     * @return boolean This returns true if the last word in the line is ENDFOR,
     * and returns false otherwise
     */
    private boolean endsForLoop(String programLine) {
        return (programLine.contains("ENDFOR"));
    }

    /**
     * @param programLine Program line passed in to check if it ends with a semi-colon
     * @return boolean This returns true if the last character in the line is a semi-colon,
     * and returns false otherwise
     */
    private boolean endsWithSemicolon(String programLine) {
        return programLine.charAt(programLine.length() - 1) == ';';
    }

    //    ========================= NEW INSTANCE =========================

    /**
     * Returns a new Instance of the class because it's a singleton
     *
     * @return Helper Returns new instance of Helper class
     */
    public static Helper getInstance() {
        return ourInstance;
    }
}
