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
            specialAssignVariable(variableName, variableValue, operator);

        } else { // More likely a variable deceleration or type change
            assignVariable(variableName, variableValue);
        }
    }

    /**
     * @param key      Name of the variable
     * @param value    Value of the variable
     * @param operator Determines how value is set
     */
    private void specialAssignVariable(String key, String value, String operator) {

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
//        If it was a string before, then we remove it from the string hashmap because it's now an int
            int actualVal = Integer.parseInt(value);
            if (STRING_VARIABLE_TABLE.containsKey(key)) {
                STRING_VARIABLE_TABLE.remove(key);
            }
            INTEGER_VARIABLE_TABLE.put(key, actualVal);
        } catch (NumberFormatException e) {
//          Means that the value is not a number, but instead a string
//          Checks to see if it was in the int hashmap before, and if so then delete it b/c its now a string
            if (INTEGER_VARIABLE_TABLE.containsKey(key)) {
                INTEGER_VARIABLE_TABLE.remove(key);
            }

            if (value.contains("\"")) { // If the value is a string literal, then clean it up & add to the hashmap
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
