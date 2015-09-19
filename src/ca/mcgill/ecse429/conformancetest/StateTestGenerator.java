package ca.mcgill.ecse429.conformancetest;

import ca.mcgill.ecse429.conformancetest.statemodel.StateMachine;

/**
 * Generates the test sources by first generating the round trip path tree, then using it to generate test cases.
 *
 * @see RoundTripPathTree
 */
public class StateTestGenerator {
    /**
     * Generates the test cases for the state machine in {@link StateMachine#getInstance()} and writes the file to the given output path.
     *
     * @param outputPath Where to write the test cases
     */
    public static void generate(String outputPath) {
        final StateMachine machine = StateMachine.getInstance();
        final RoundTripPathTree tree = RoundTripPathTree.build(machine.getStartState());
        System.out.println(tree.toString());
    }
}
