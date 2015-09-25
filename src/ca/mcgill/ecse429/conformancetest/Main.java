package ca.mcgill.ecse429.conformancetest;

import ca.mcgill.ecse429.conformancetest.statemodel.persistence.PersistenceStateMachine;

/**
 * Entry point.
 */
public class Main {
    public static void main(String[] args) {
        // TODO:

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
        final String stateMachineFile1 = "ccoinbox.xml";
        final String stateMachineFile2 = "legislation.xml";
        final String simpleName1 = "GeneratedTestCCoinBox";
        final String simpleName2 = "GeneratedTestLegislation";
        PersistenceStateMachine.loadStateMachine(stateMachineFile1);
        System.out.println(StateTestGenerator.generate(simpleName1));
        PersistenceStateMachine.loadStateMachine(stateMachineFile2);
        System.out.println(StateTestGenerator.generate(simpleName2));
    }
}
