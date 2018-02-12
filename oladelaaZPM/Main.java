import java.io.File;

public class Main {

    public static void main(String[] args) {
//        Gets the file name passed in through the commandLine
        String filePath = args[0];
        File fileToInterpret = new File(filePath);

//        Set the file for the interpreter class
        Helper interpreter = Helper.getInstance();
        interpreter.setZpmFile(fileToInterpret);
        interpreter.interpret();
    }
}
