package ca.mcgill.ecse429.conformancetest;

import ca.mcgill.ecse429.conformancetest.statemodel.StateMachine;

/**
 *
 */
public class StateTestGenerator {
    public static void generate(String outputPath) {
        final StateMachine machine = StateMachine.getInstance();
        System.out.println(machine);
    }
}
