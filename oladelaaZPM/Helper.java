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
            System.err.println("RUNTIME ERROR: The file passed in does not exist!");
            e.printStackTrace();
            System.exit(404);
        } catch (IOException e) { // Thrown if there was a problem reading a line for some reason
            System.err.println("RUNTIME ERROR: Something went wrong while trying to read the file!");
            e.printStackTrace();
            System.exit(500);
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
        if (!endsWithSemicolon(programLine)) {
            if (!endsForLoop(programLine)) {
                System.err.println("RUNTIME ERROR: Missing Semi-colon on line: " + lineNumber);
                System.exit(400);
            }
        }

//        Replaces all semi-colons with a space before the split, b/c it's unneeded
        programLine = programLine.replace(";", "").trim();
        String[] lineParts = programLine.trim().split("\\s+");
        String actionToTake = lineParts[0];

        if (RESERVED_WORDS.contains(actionToTake)) {
            String variableName = lineParts[1];
            switch (actionToTake) {
                case "PRINT":
                    runPrintVariableValue(variableName);
                    break;
                case "FOR":
                    String[] minifiedLineParts = new String[lineParts.length - 3];
                    int numOfTimesToLoop = 0;
                    try {
                        numOfTimesToLoop = Integer.parseInt(lineParts[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("RUNTIME ERROR: Invalid FOR-LOOP syntax on line: " + lineNumber);
                        System.exit(400);
                    }
                    System.arraycopy(lineParts, 2, minifiedLineParts, 0, lineParts.length - 3);
                    runForLoop(minifiedLineParts, numOfTimesToLoop);
                    break;
            }
        } else {
//            Send new array of just the variable components to a parsing method
            parseVariable(lineParts);
        }
    }

    /**
     * Method for parsing and executing the a FOR-loop statement
     *
     * @param lineParts        The statements to execute for the for-loop
     * @param numOfTimesToLoop The number of times to execute the given set of statements
     */
    private void runForLoop(String[] lineParts, int numOfTimesToLoop) {
//           Checks to see if we have a nested for-loop
        String startOfFirstCommand = lineParts[0];
        if (startOfFirstCommand.equals("FOR")) { // Is a nested FOR-loop
//           Recursively call the lines in the nested loop
            String[] minifiedLineParts = new String[lineParts.length - 3];
            int numLoops = 0;
            try {
                numLoops = Integer.parseInt(lineParts[1]);
            } catch (NumberFormatException e) {
                System.err.println("RUNTIME ERROR: Invalid FOR-LOOP syntax on line: " + lineNumber);
                System.exit(400);
            }
            System.arraycopy(lineParts, 2, minifiedLineParts, 0, lineParts.length - 3);
            runForLoop(minifiedLineParts, numLoops);
        } else { // It's a command instead of a for-loop

//            Each command is going to be an assignment of some way so we store the line to execute in a string array
            int numOfCommands = lineParts.length / 3;
            String[] commandsToExecute = new String[numOfCommands];

            int j = 0;
            String variableToWriteTo, assignmentOperator, valueToWriteWith;
            for (int i = 0; i < lineParts.length; i += 3) {
                variableToWriteTo = lineParts[i];
                assignmentOperator = lineParts[i + 1];
                valueToWriteWith = lineParts[i + 2];
                commandsToExecute[j] = String.format("%s %s %s", variableToWriteTo.trim(), assignmentOperator.trim(),
                        valueToWriteWith.trim());
                j++;
            }
//            Pass each line through the line parser to be parsed
            for (int i = 0; i < numOfTimesToLoop; i++) {
                for (String line : commandsToExecute) {
                    parseProgramLine(line + " ;");
                }
            }
        }
    }

    /**
     * Prints value of passed in variable to the console
     *
     * @param variableName Variable whose value will be printed to the console
     */
    private void runPrintVariableValue(String variableName) {
        if (INTEGER_VARIABLE_TABLE.containsKey(variableName)) { // Variable is an int
            System.out.println(variableName + " = " + INTEGER_VARIABLE_TABLE.get(variableName));
        } else if (STRING_VARIABLE_TABLE.containsKey(variableName)) { // Variable is an int
            System.out.println(variableName + " = " + STRING_VARIABLE_TABLE.get(variableName));
        } else { // Variable has not been previously declared
            System.err.println("RUNTIME ERROR: Cannot find symbol '" + variableName + "' on line " + lineNumber);
            System.exit(404);
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
                    System.err.println("RUNTIME ERROR: RUNTIME ERROR: Cannot find symbol '" + key + "' on line " + lineNumber);
                    System.exit(404);
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

                    System.err.println("RUNTIME ERROR: Cannot find symbol '" + value + "' on line " + lineNumber);
                    System.exit(404);
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
            if (value.contains("\"")) { // If the value is a string literal, then clean it up & add to the HashMap
                value = value.replace("\"", "").trim();
                STRING_VARIABLE_TABLE.put(key, value);
                INTEGER_VARIABLE_TABLE.remove(key);
            } else { // If not a string literal, then it refers to another variable
                if (INTEGER_VARIABLE_TABLE.containsKey(value) && !STRING_VARIABLE_TABLE.containsKey(value)) {
                    INTEGER_VARIABLE_TABLE.put(key, INTEGER_VARIABLE_TABLE.get(value));
                } else if (STRING_VARIABLE_TABLE.containsKey(value) && !INTEGER_VARIABLE_TABLE.containsKey(value)) {
                    STRING_VARIABLE_TABLE.put(key, STRING_VARIABLE_TABLE.get(value));
                } else { // If the variable hasn't been  declared, then throw a runtime error
                    System.err.println("RUNTIME ERROR: Cannot find symbol '" + value + "' on line " + lineNumber);
                    System.exit(404);
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
                System.err.println("RUNTIME ERROR: Illegal character: " + operator);
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
                System.err.println("RUNTIME ERROR: Illegal character: " + operator);
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
