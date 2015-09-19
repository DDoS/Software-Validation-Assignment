package ca.mcgill.ecse429.conformancetest;

import ca.mcgill.ecse429.conformancetest.statemodel.persistence.PersistenceStateMachine;

/**
 * Entry point.
 */
public class Main {
    public static void main(String[] args) {
        final String stateMachineFile1 = "ccoinbox.xml";
        final String stateMachineFile2 = "legislation.xml";
        final String generatedTestFile1 = "src/ca/mcgill/ecse429/conformancetest/ccoinbox/GeneratedTestCCoinBox.java";
        final String generatedTestFile2 = "src/ca/mcgill/ecse429/conformancetest/legislation/GeneratedTestLegislation.java";

        PersistenceStateMachine.loadStateMachine(stateMachineFile1);
        StateTestGenerator.generate(generatedTestFile1);

        PersistenceStateMachine.loadStateMachine(stateMachineFile2);
        StateTestGenerator.generate(generatedTestFile2);
    }
}
