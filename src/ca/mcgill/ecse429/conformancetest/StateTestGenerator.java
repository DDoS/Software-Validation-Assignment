package ca.mcgill.ecse429.conformancetest;

import java.util.Collections;
import java.util.List;

import ca.mcgill.ecse429.conformancetest.statemodel.StateMachine;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitorAdapter;

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
        visitTree(tree);
    }

    private static void visitTree(RoundTripPathTree tree) {
        if (!tree.isAlpha()) {
            System.out.println(conditionAsExpression(tree.getTransition().getCondition()));
            System.out.println(actionAsStatements(tree.getTransition().getAction()));
        }
        for (RoundTripPathTree subTree : tree.getChildren()) {
            visitTree(subTree);
        }
    }

    private static Expression conditionAsExpression(String condition) {
        if (condition.isEmpty()) {
            return new BooleanLiteralExpr(true);
        }
        try {
            final Expression expression = JavaParser.parseExpression(condition);
            expression.accept(FieldToAccessor.INSTANCE, null);
            return expression;
        } catch (ParseException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static List<Statement> actionAsStatements(String action) {
        if (action.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            final BlockStmt block = JavaParser.parseBlock('{' + action + '}');
            block.accept(FieldToAccessor.INSTANCE, null);
            return block.getStmts();
        } catch (ParseException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static class FieldToAccessor extends ModifierVisitorAdapter<Object> {
        private static final FieldToAccessor INSTANCE = new FieldToAccessor();
        private static final NameExpr SCOPE = new NameExpr("machine");

        @Override
        public Node visit(MethodCallExpr call, Object arg) {
            call.setScope(SCOPE);
            return call;
        }

        @Override
        public Node visit(NameExpr name, Object arg) {
            if (!(name.getParentNode() instanceof AssignExpr)) {
                return new MethodCallExpr(SCOPE, "get" + capitalize(name.getName()));
            }
            return name;
        }

        @Override
        public Node visit(AssignExpr assign, Object arg) {
            final Expression target = assign.getTarget();
            if (target instanceof NameExpr) {
                final NameExpr name = (NameExpr) target;
                final Expression value = (Expression) assign.getValue().accept(this, arg);
                return new MethodCallExpr(SCOPE, "set" + capitalize(name.getName()), Collections.singletonList(value));
            }
            return super.visit(assign, arg);
        }

        private static String capitalize(String string) {
            return Character.toUpperCase(string.charAt(0)) + string.substring(1);
        }
    }
}
