package ca.mcgill.ecse429.conformancetest;

import ca.mcgill.ecse429.conformancetest.statemodel.persistence.PersistenceStateMachine;

/**
 *
 */
public class Main {
    public static void main(String[] args) {
        final String stateMachineFile = "ccoinbox.xml";
        final String generatedTestFile = "src/ca/mcgill/ecse429/conformancetest/ccoinbox/GeneratedTestCCoinBox.java";

        PersistenceStateMachine.loadStateMachine(stateMachineFile);
        StateTestGenerator.generate(generatedTestFile);
    }
}
