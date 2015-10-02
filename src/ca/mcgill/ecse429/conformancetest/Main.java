package ca.mcgill.ecse429.conformancetest;

import ca.mcgill.ecse429.conformancetest.statemodel.persistence.PersistenceStateMachine;

import java.io.*;


/**
 * Entry point.
 */
public class Main {
    public static void main(String[] args) {
        String stateMachineFilename = null;
        String generatedTestFileName = "test";

        if (args.length > 0){
            stateMachineFilename = args[0];
            generatedTestFileName = generateTestFileName(stateMachineFilename);

            PersistenceStateMachine.loadStateMachine(stateMachineFilename);
            String result = StateTestGenerator.generate(generatedTestFileName);

            //writeToFile(generatedTestFileName, result);

            //generate the path
            String s1 = "package ";
            String outPath = "src/" + result.substring(result.indexOf(s1) + s1.length() ,
                    result.indexOf(";") ) ;
            outPath = outPath.replaceAll("\\.", "/") + "/" +
                    generatedTestFileName + ".java";

            try {
                String curr = new java.io.File(".").getCanonicalPath();
                String absPath = curr + "/" + outPath ;

                //write to file
                writeToFile(absPath, result);


                System.out.print("write to " + curr);
            } catch (IOException e) {
                e.printStackTrace();
            }

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
     * returns the correct class name
     * @param stateMachineFilename
     * @return
     */
    private static String generateTestFileName(String stateMachineFilename) {
        if (stateMachineFilename == null || stateMachineFilename.length() == 0){
            throw new RuntimeException("stateMachineFileName cannot be null or empty");
        }

        String content = null;
        File file = new File(stateMachineFilename);
        FileReader reader = null;

        //read into content
        try {
            reader = new FileReader(file);
            char [] chars = new char [(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();

            int beg = content.indexOf("<className>");
            int end = content.indexOf("</className>");

            String fileName = content.substring(beg + "<className>".length() , end-".java".length());
            fileName = "Test" + fileName;
            return fileName;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void writeToFile(String path , String content)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.print(content);
        writer.close();
    }
}
