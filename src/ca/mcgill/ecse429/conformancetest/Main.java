package ca.mcgill.ecse429.conformancetest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import ca.mcgill.ecse429.conformancetest.statemodel.StateMachine;
import ca.mcgill.ecse429.conformancetest.statemodel.persistence.PersistenceStateMachine;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Entry point.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length <= 0) {
            System.err.println("The program requires one argument as the name of the state machine file name. Example: ccoinbox.xml");
            return;
        }
        if ("-r".equals(args[0])) {
            // Run mode
            if (args.length <= 1) {
                System.err.println("The program requires the fully qualified test class name when in \"run\" mode." +
                        " Example: ca.mcgill.ecse429.conformancetest.ccoinbox.TestCCoinBox");
                return;
            }
            final Class<?> testClass = getTestClass(args[1]);
            if (testClass == null) {
                System.err.println("Class \"" + args[1] + "\" does not exist. Is the name fully qualified?");
                return;
            }
            final Result result = JUnitCore.runClasses(testClass);
            printJUnitResult(result);
        } else {
            // Generate mode
            final String modelPath = new File(args[0]).getAbsolutePath();
            PersistenceStateMachine.loadStateMachine(modelPath);
            final String className = generateTestClassName();
            final String code = StateTestGenerator.generate(className);
            final String classPath = generateTestClassPath(className);
            writeFile(classPath, code);
            System.out.println("Generated " + className + " at " + classPath);
        }
    }

    /**
     * Returns the class name for the generated test class by deriving it from the state machine class name.
     *
     * @return The name of the test class
     */
    private static String generateTestClassName() {
        String name = StateMachine.getInstance().getClassName();
        name = name.substring(0, name.lastIndexOf(".java"));
        return "GeneratedTest" + name;
    }

    /**
     * Returns the path for the generated test class by deriving it from the state machine package and generated class names.
     *
     * @return The name of the test class
     */
    private static String generateTestClassPath(String name) {
        return "src/" + StateMachine.getInstance().getPackageName().replace('.', '/') + '/' + name + ".java";
    }

    /**
     * Write the contents to a .java file located in the given path.
     *
     * @param path The path of the file to write
     * @param contents The contents of the file
     */
    private static void writeFile(String path, String contents) {
        try {
            final PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.print(contents);
            writer.close();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Tries to find a return the test class for the given fully qualified name. Returns null if the class doesn't exist in the class path.
     *
     * @param name The fully qualified name of the class
     * @return The class, or null if not found
     */
    private static Class<?> getTestClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException exception) {
            return null;
        }
    }

    /**
     * Prints the test results to the standard output in a nice format.
     *
     * @param result The results to print
     */
    private static void printJUnitResult(Result result) {
        System.out.printf("Ran %d test(s), ignored %d test(s), took %.2fs\n", result.getRunCount(), result.getIgnoreCount(), result.getRunTime() / 1000f);
        if (result.getFailureCount() > 0) {
            System.out.printf("Failed %d test(s)\n", result.getFailureCount());
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
                System.out.println(failure.getTrace());
            }
        }
        if (result.wasSuccessful()) {
            System.out.println("Success");
        } else {
            System.out.println("Failure");
        }
    }
}
