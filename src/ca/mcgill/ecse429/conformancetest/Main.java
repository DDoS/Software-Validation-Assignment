package ca.mcgill.ecse429.conformancetest;

import ca.mcgill.ecse429.conformancetest.statemodel.persistence.PersistenceStateMachine;

import java.io.*;


/**
 * Entry point.
 */
public class Main {
    public static void main(String[] args) {
        // TODO:

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



        // accept a single argument: the xml file path
        // This path may be relative, so File.getAbsolutePath() needs to be used to make sure it's absolute
        // example of arguments: ccoinbox.xml

        // Next this file gets loaded into the state machine using
        // PersistenceStateMachine.loadStateMachine(stateMachineFile);

        // Then the simple name of the output class is generated from StateMachine.getClassName() by adding a prefix
        // This also needs to remove the .java suffix; check out StateTestGenerator.getClassName(String)
        // example simple name: GeneratedTestCCoinBox

        // Now we can generate the source test code using
        // StateTestGenerator.generate(simpleName);
        // This returns a string which is the source code

        // Then we need the output file. This should be "src/" followed by the state machine package name with '.' replaced by '/'
        // followed by the generated simple named followed by ".java"
        // example output file: src/ca/mcgill/ecse429/conformancetest/ccoinbox/GeneratedTestCCoinBox.java

        // Finally we can write out the source code to the output file path
        // Just use whatever is recommended

        // This is just test code, remove it when done

        /*
        final String stateMachineFile1 = "ccoinbox.xml";
        final String stateMachineFile2 = "legislation.xml";
        final String simpleName1 = "TestCCoinBox";
        final String simpleName2 = "GeneratedTestLegislation";
        PersistenceStateMachine.loadStateMachine(stateMachineFile1);
        System.out.println(StateTestGenerator.generate(simpleName1));
        PersistenceStateMachine.loadStateMachine(stateMachineFile2);
        System.out.println(StateTestGenerator.generate(simpleName2));
        **/
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
