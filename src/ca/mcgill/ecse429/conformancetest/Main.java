package ca.mcgill.ecse429.conformancetest;

import ca.mcgill.ecse429.conformancetest.statemodel.StateMachine;
import ca.mcgill.ecse429.conformancetest.statemodel.persistence.PersistenceStateMachine;

import java.io.*;


/**
 * Entry point.
 */
public class Main {
    public static void main(String[] args) {
        String stateMachineFilename = null;
        String generatedTestFileName = null;

        if (args.length > 0){
            stateMachineFilename = args[0];

            PersistenceStateMachine.loadStateMachine(stateMachineFilename);
            generatedTestFileName = generateTestFileName();
            String result = StateTestGenerator.generate(generatedTestFileName.replace(".java" , ""));

            //write to file
            generateFileWithName(generatedTestFileName, result);


        }
        else {
            System.err.print("the program requires one argument as " +
                    "the name of the state machine file name." +
                    "Example : ccoinBox.xml.\n" +
                    "The file path should be relative to the executable");

            return;
        }

    }

    /**
     * write fileContent to a java file with the fileName located in the correct path based
     * on the package name of the fileContent source
     * @param fileName file name to write to
     * @param fileContent content of the file
     */
    private static void generateFileWithName(String fileName, String fileContent) {
        //generate the path
        String s1 = "package ";
        String outPath = "src/" + fileContent.substring(fileContent.indexOf(s1) + s1.length() ,
                fileContent.indexOf(";") ) ;
        outPath = outPath.replace('.', '/') + '/' +
                fileName;

        try {
            //write to file
            writeToFile(outPath, fileContent);


            System.out.print("write to " + outPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the correct class name
     * @return
     */
    private static String generateTestFileName() {
        StateMachine sm = StateMachine.getInstance();
        if (sm != null){ //sm initialized
            return "Test" + sm.getClassName();
        }
        else {
            throw new RuntimeException("State Machine not initialized!");
        }
    }

    public static void writeToFile(String path , String content)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.print(content);
        writer.close();
    }
}
