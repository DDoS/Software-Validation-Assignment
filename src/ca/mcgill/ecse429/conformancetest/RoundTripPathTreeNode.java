package ca.mcgill.ecse429.conformancetest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.mcgill.ecse429.conformancetest.statemodel.State;
import ca.mcgill.ecse429.conformancetest.statemodel.StateMachine;
import ca.mcgill.ecse429.conformancetest.statemodel.Transition;

/**
 * Represents a node in a round trip path tree for a state machine.
 */
public class RoundTripPathTreeNode {
    private final Transition transition;
    private final List<RoundTripPathTreeNode> children;

    // No transition, this is the alpha state
    private RoundTripPathTreeNode(List<RoundTripPathTreeNode> children) {
        this(null, children);
    }

    // Just a transition, this is a leaf node
    private RoundTripPathTreeNode(Transition transition) {
        this(transition, Collections.<RoundTripPathTreeNode>emptyList());
    }

    // Regular node with transition and children
    private RoundTripPathTreeNode(Transition transition, List<RoundTripPathTreeNode> children) {
        this.transition = transition;
        this.children = children;
    }

    /**
     * Returns true if this is an alpha node. Such a node has a null transition and state.
     *
     * @return Whether this is an alpha node
     */
    public boolean isAlpha() {
        return transition == null;
    }

    /**
     * Returns the state for this node. A null state represents the alpha state.
     *
     * @return The state
     */
    public State getState() {
        return isAlpha() ? null : transition.getTo();
    }

    /**
     * Returns the transition into the state of this node. A null transition represents the transition into alpha.
     *
     * @return The transition into
     */
    public Transition getTransition() {
        return transition;
    }

    /**
     * Returns the children of the node as subtrees.
     *
     * @return The children
     */
    public List<RoundTripPathTreeNode> getChildren() {
        return children;
    }

    /**
     * Returns a string that describes concisely the state and transition of this node, including events and conditions.
     *
     * @return The signature of the node
     */
    public String getSignature() {
        final StringBuilder builder = new StringBuilder();
        if (isAlpha()) {
            builder.append("alpha [] / ; -> @ctor");
        } else {
            builder.append(transition.getEvent())
                    .append(" [").append(transition.getCondition()).append(']')
                    .append(" / ").append(transition.getAction().isEmpty() ? ";" : transition.getAction())
                    .append(" -> ").append(transition.getTo().getName());
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }

    // A toString with an indentation level for pretty printing. Used for debug.
    private String toString(int level) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("    ");
        }
        builder.append(getSignature()).append('\n');
        for (RoundTripPathTreeNode child : children) {
            builder.append(child.toString(level + 1));
        }
        return builder.toString();
    }

    /**
     * Generates the round trip path tree for an initial state.
     *
     * @param from The initial state
     * @return The tree
     */
    public static RoundTripPathTreeNode build(State from) {
        // Create a set of visited states and add the first state as visited
        final HashSet<State> visited = new HashSet<State>();
        visited.add(from);
        // Create the subtree of the first state, which are the children of alpha
        return new RoundTripPathTreeNode(buildChildren(from, visited));
    }

    /*
     * builds the children for a particular state.
     * The visited states should contain all states in the parent tree.
     */
    private static List<RoundTripPathTreeNode> buildChildren(State from, Set<State> visited) {
        final List<RoundTripPathTreeNode> children = new ArrayList<RoundTripPathTreeNode>();
        final List<Transition> transitions = getTransitionsFrom(from);
        for (final Transition transition : transitions) {
            final State to = transition.getTo();
            if (visited.contains(to)) {
                // Already visited, this is a leaf
                children.add(new RoundTripPathTreeNode(transition));
            } else {
                // Not visited, mark as so and generate the children
                visited.add(to);
                // Clone the set so paths don't affect each other
                children.add(new RoundTripPathTreeNode(transition, buildChildren(to, new HashSet<State>(visited))));
            }
        }
        return children;
    }

    /*
     * Gets all the transitions that have this state as the from state.
     */
    private static List<Transition> getTransitionsFrom(State state) {
        final List<Transition> from = new ArrayList<Transition>();
        for (Transition transition : StateMachine.getInstance().getTransitions()) {
            if (transition.getFrom().equals(state)) {
                from.add(transition);
            }
        }
        return from;
    }
}
