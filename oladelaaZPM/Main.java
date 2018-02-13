import java.io.File;

public class Main {

    public static void main(String[] args) {
//        Gets the file name passed in through the commandLine
        String filePath = args[0];
        File fileToInterpret = new File(filePath);

//        Set the file for the interpreter class
        Helper interpreter = Helper.getInstance();
        interpreter.setZpmFile(fileToInterpret);
//        interpreter.interpret(); TODO Uncomment to just run the un-timed interpreter

        // TODO uncomment to run the timed interpreters
      /*  runTimedInterpreter(interpreter);
        System.out.println("");
        runTimedJavaInterpreter();*/
    }

    private static void runTimedInterpreter(Helper interpreter) {
        final long startTime = System.currentTimeMillis();
        interpreter.interpret();
        final long endTime = System.currentTimeMillis();
        System.out.println("ZPM Interpreter Total execution time: " + (endTime - startTime) + "ms");
    }

    private static void runTimedJavaInterpreter() {
        final long startTime = System.currentTimeMillis();
        // TODO: 2/12/2018 Uncomment to run one of these
        runProgram2();
//        runProgram5();
//        runProgram10();
        final long endTime = System.currentTimeMillis();
        System.out.println("Java Total execution time: " + (endTime - startTime) + "ms");
    }

    private static void runProgram10() {
        int A = 1;
        int a = 2;
        int numItems = 0;
        int NUMITEMS = 1;
        a += A;
        System.out.println("A = " + A);
        System.out.println("a = " + a);
        System.out.println("numItems = " + numItems);
    }

    private static void runProgram5() {
        int A = 1;
        int B = 0;
        for (int i = 0; i < 1; i++) {
            B += A;
            A *= 2;
        }

        for (int i = 0; i < 8; i++) {
            B += A;
            A *= 2;
        }

        for (int i = 0; i < 1; i++) {
            B += A;
            A *= 2;
        }

        A += 1000;

        for (int i = 0; i < 5; i++) {
            B -= A;
            A += 2;
        }

        System.out.println("A = " + A);
        System.out.println("B = " + B);
    }

    private static void runProgram2() {
        int a = 3;
        a *= 30;
        a = 3;
        a += 10;
        a -= 10;
        int b = 0;
        b += a;
        b += b;
        a = a;
        a += 4;
        b += 2;
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        String a1 = "hello";
        String b1 = "world";
        a1 += b1;
        System.out.println("a = " + a1);
        System.out.println("b = " + b1);
    }
}
