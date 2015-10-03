package ca.mcgill.ecse429.conformancetest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import ca.mcgill.ecse429.conformancetest.statemodel.StateMachine;
import ca.mcgill.ecse429.conformancetest.statemodel.persistence.PersistenceStateMachine;

/**
 * Entry point.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            final String modelPath = new File(args[0]).getAbsolutePath();
            PersistenceStateMachine.loadStateMachine(modelPath);
            final String className = generateTestClassName();
            final String code = StateTestGenerator.generate(className);
            final String classPath = generateTestClassPath(className);
            writeFile(classPath, code);
            System.out.println("Generated " + className + " at " + classPath);
        } else {
            System.err.print("the program requires one argument as the name of the state machine file name. Example: ccoinbox.xml.");
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
}
