package ca.mcgill.ecse429.conformancetest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.ModifierVisitorAdapter;

import ca.mcgill.ecse429.conformancetest.statemodel.StateMachine;

/**
 * Generates the test sources by first generating the round trip path tree, then using it to generate test cases.
 *
 * @see RoundTripPathTreeNode
 */
public class StateTestGenerator {
    /**
     * Generates the test cases for the state machine in {@link StateMachine#getInstance()} and writes the file to the given output path.
     *
     * @param outputPath Where to write the test cases
     */
    public static void generate(String outputPath) {
        final StateMachine machine = StateMachine.getInstance();
        final RoundTripPathTreeNode tree = RoundTripPathTreeNode.build(machine.getStartState());
        final List<MethodDeclaration> methods = generateTestMethods(tree);
        for (MethodDeclaration method : methods) {
            System.out.println(method);
        }
    }

    private static List<MethodDeclaration> generateTestMethods(RoundTripPathTreeNode node) {
        final List<BlockStmt> bodies = generateTestBodies(node);
        final List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
        int i = 0;
        for (BlockStmt body : bodies) {
            final MethodDeclaration method = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "conformanceTest" + i++, Collections.<Parameter>emptyList());
            method.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Test")));
            method.setBody(body);
            methods.add(method);
        }
        return methods;
    }

    private static List<BlockStmt> generateTestBodies(RoundTripPathTreeNode node) {
        if (!node.isAlpha()) {
            throw new IllegalArgumentException("Expected first node to be alpha");
        }
        final BlockStmt body = new BlockStmt(new ArrayList<Statement>());
        final List<BlockStmt> bodies = new ArrayList<BlockStmt>();
        // Don't pass the alpha node, but the children
        for (RoundTripPathTreeNode child : node.getChildren()) {
            generateTestBodies(child, body, bodies);
        }
        return bodies;
    }

    private static void generateTestBodies(RoundTripPathTreeNode node, BlockStmt body, List<BlockStmt> bodies) {
        // TODO: generate test statements for the node here and store them in the body. This is just a test:
        // body.getStmts().add(new ExpressionStmt(new MethodCallExpr(null, node.getState().getName())));
        final List<RoundTripPathTreeNode> children = node.getChildren();
        if (children.isEmpty()) {
            // Reached leaf, test case is complete
            bodies.add(body);
        } else {
            // Generate the test statements for the next state transitions
            for (RoundTripPathTreeNode child : children) {
                // Clone the body to ensure each test case has its own
                generateTestBodies(child, (BlockStmt) body.clone(), bodies);
            }
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
