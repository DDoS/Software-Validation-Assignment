package ca.mcgill.ecse429.conformancetest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.ModifierVisitorAdapter;

import ca.mcgill.ecse429.conformancetest.statemodel.StateMachine;
import ca.mcgill.ecse429.conformancetest.statemodel.Transition;
import org.junit.Assert;
import org.junit.Test;

/**
 * Generates the test sources by first generating the round trip path tree, then using it to generate test cases.
 *
 * @see RoundTripPathTreeNode
 */
public class StateTestGenerator {
    /**
     * Generates the test cases for the state machine in {@link StateMachine#getInstance()} and writes the file to the given output path.
     *
     * @param outputClassName The simple name of the generate class containing the generated test cases
     */
    public static String generate(String outputClassName) {
        final StateMachine machine = StateMachine.getInstance();
        final RoundTripPathTreeNode tree = RoundTripPathTreeNode.build(machine.getStartState());
        final List<BodyDeclaration> methods = generateTestMethods(tree);
        final ClassOrInterfaceDeclaration testClass = new ClassOrInterfaceDeclaration(ModifierSet.PUBLIC, false, outputClassName);
        testClass.setMembers(methods);
        final CompilationUnit file = new CompilationUnit(new PackageDeclaration(new NameExpr(machine.getPackageName())),
                generateImports(Assert.class, Test.class), Collections.<TypeDeclaration>singletonList(testClass));
        return file.toString();
    }

    private static List<BodyDeclaration> generateTestMethods(RoundTripPathTreeNode node) {
        final List<BlockStmt> bodies = generateTestBodies(node);
        final List<BodyDeclaration> methods = new ArrayList<BodyDeclaration>();
        int i = 0;
        for (BlockStmt body : bodies) {
            final MethodDeclaration method = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(),
                    "conformanceTest" + i++, Collections.<Parameter>emptyList());
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
        final Map<String, Integer> usedVarNames = new HashMap<String, Integer>();
        // Don't pass the alpha node, but the children
        for (RoundTripPathTreeNode child : node.getChildren()) {
            generateTestBodies(child, body, bodies, usedVarNames);
        }
        return bodies;
    }

    private static void generateTestBodies(RoundTripPathTreeNode node, BlockStmt body, List<BlockStmt> bodies, Map<String, Integer> usedVarNames) {
        final List<Statement> statements = body.getStmts();
        final Transition transition = node.getTransition();
        final LineComment comment = new LineComment(" " + node.getSignature());
        boolean commentAdded = false;
        if (!transition.getCondition().isEmpty()) {
            final Statement conditionReacher = generateConditionReacher(transition.getCondition());
            conditionReacher.setComment(comment);
            commentAdded = true;
            statements.add(conditionReacher);
        }
        final List<ActionCheck> checks = generateActionChecks(transition.getAction(), usedVarNames);
        for (ActionCheck check : checks) {
            statements.addAll(check.getPre());
        }
        statements.add(eventAsStatement(transition.getEvent()));
        statements.add(generateStateCheck(transition.getTo().getName()));
        for (ActionCheck check : checks) {
            final Statement post = check.getPost();
            if (!commentAdded) {
                post.setComment(comment);
                commentAdded = true;
            }
            statements.add(post);
        }
        final List<RoundTripPathTreeNode> children = node.getChildren();
        if (children.isEmpty()) {
            // Reached leaf, test case is complete
            bodies.add(body);
        } else {
            // Generate the test statements for the next state transitions
            for (RoundTripPathTreeNode child : children) {
                // Clone the body and used variable names to ensure each test case has its own
                generateTestBodies(child, (BlockStmt) body.clone(), bodies, new HashMap<String, Integer>(usedVarNames));
            }
        }
    }

    private static Statement generateConditionReacher(String condition) {
        final Expression inverseCondition = invertCondition(conditionAsExpression(condition));
        final ThrowStmt missingBody = new ThrowStmt(new ObjectCreationExpr(null, new ClassOrInterfaceType(UnsupportedOperationException.class.getSimpleName()),
                Collections.<Expression>singletonList(new StringLiteralExpr("Missing event for reaching condition: " + condition))));
        return new WhileStmt(inverseCondition, new BlockStmt(Collections.<Statement>singletonList(missingBody)));
    }

    private static List<ActionCheck> generateActionChecks(String action, Map<String, Integer> usedVarNames) {
        final List<ActionCheck> checks = new ArrayList<ActionCheck>();
        final List<Statement> statements = actionAsStatements(action);
        for (Statement statement : statements) {
            if (!(statement instanceof ExpressionStmt)) {
                continue;
            }
            final Expression expression = ((ExpressionStmt) statement).getExpression();
            if (!(expression instanceof AssignExpr)) {
                continue;
            }
            checks.add(new ActionCheck((AssignExpr) expression, usedVarNames));
        }
        return checks;
    }

    private static Statement generateStateCheck(String state) {
        final MethodCallExpr stateGetter = new MethodCallExpr(FieldToAccessor.SCOPE, "getState", Collections.<Expression>emptyList());
        final NameExpr stateValue = new NameExpr(getMachineClassName() + ".State." + state);
        return generateAssert(stateValue, stateGetter);
    }

    private static Statement eventAsStatement(String event) {
        final Expression expression;
        if ("@ctor".equals(event)) {
            String className = getMachineClassName();
            final ClassOrInterfaceType type = new ClassOrInterfaceType(className);
            final ObjectCreationExpr constructor = new ObjectCreationExpr(null, type, Collections.<Expression>emptyList());
            expression = new VariableDeclarationExpr(ModifierSet.FINAL, type,
                    Collections.singletonList(new VariableDeclarator(new VariableDeclaratorId(FieldToAccessor.SCOPE.getName()), constructor)));
        } else {
            expression = new MethodCallExpr(FieldToAccessor.SCOPE, event, Collections.<Expression>emptyList());
        }
        return new ExpressionStmt(expression);
    }

    private static Expression conditionAsExpression(String condition) {
        if (condition.isEmpty()) {
            return null;
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

    private static Expression invertCondition(Expression expression) {
        if (expression instanceof UnaryExpr) {
            final UnaryExpr unary = (UnaryExpr) expression;
            if (unary.getOperator() == UnaryExpr.Operator.not) {
                expression = unary.getExpr();
            } else {
                expression = new UnaryExpr(expression, UnaryExpr.Operator.not);
            }
        } else if (expression instanceof BinaryExpr) {
            final BinaryExpr binary = (BinaryExpr) expression;
            final Operator inverted;
            switch (binary.getOperator()) {
                case equals:
                    inverted = Operator.notEquals;
                    break;
                case notEquals:
                    inverted = Operator.equals;
                    break;
                case less:
                    inverted = Operator.greaterEquals;
                    break;
                case greater:
                    inverted = Operator.lessEquals;
                    break;
                case lessEquals:
                    inverted = Operator.greater;
                    break;
                case greaterEquals:
                    inverted = Operator.less;
                    break;
                case or:
                    return new BinaryExpr(invertCondition(binary.getLeft()), invertCondition(binary.getRight()), Operator.and);
                case and:
                    return new BinaryExpr(invertCondition(binary.getLeft()), invertCondition(binary.getRight()), Operator.or);
                default:
                    return new UnaryExpr(new EnclosedExpr(binary), UnaryExpr.Operator.not);
            }
            return new BinaryExpr(binary.getLeft(), binary.getRight(), inverted);
        } else if (expression instanceof AssignExpr || expression instanceof ConditionalExpr || expression instanceof InstanceOfExpr) {
            return new UnaryExpr(new EnclosedExpr(expression), UnaryExpr.Operator.not);
        } else {
            return new UnaryExpr(expression, UnaryExpr.Operator.not);
        }
        return expression;
    }

    private static Statement generateAssert(Expression expected, Expression actual) {
        return new ExpressionStmt(new MethodCallExpr(new NameExpr("Assert"), "assertEquals", Arrays.asList(expected, actual)));
    }

    private static List<ImportDeclaration> generateImports(Class<?>... classes) {
        final List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
        for (Class<?> _class : classes) {
            imports.add(new ImportDeclaration(new NameExpr(_class.getCanonicalName()), false, false));
        }
        return imports;
    }

    private static String getMachineClassName() {
        return getClassName(StateMachine.getInstance().getClassName());
    }

    private static String getClassName(String javaFileName) {
        return javaFileName.substring(0, javaFileName.lastIndexOf(".java"));
    }

    private static String capitalize(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    private static String uncapitalize(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    private static class ActionCheck {
        private final List<Statement> pre = new ArrayList<Statement>();
        private final Statement post;

        private ActionCheck(AssignExpr action, Map<String, Integer> usedVarNames) {
            final Expression value = (Expression) action.getValue().accept(new PreStateExtractor(usedVarNames), pre);
            post = generateAssert(value, action.getTarget());
        }

        private List<Statement> getPre() {
            return pre;
        }

        private Statement getPost() {
            return post;
        }
    }

    private static class PreStateExtractor extends ModifierVisitorAdapter<List<Statement>> {
        private final Map<String, Integer> usedVarNames;

        private PreStateExtractor(Map<String, Integer> usedVarNames) {
            this.usedVarNames = usedVarNames;
        }

        @Override
        public Node visit(MethodCallExpr call, List<Statement> preStates) {
            // Look for a call to a machine property getter
            final Expression scope = call.getScope();
            if (!(scope instanceof NameExpr)) {
                return super.visit(call, preStates);
            }
            final NameExpr nameExpr = (NameExpr) scope;
            if (!nameExpr.getName().equals(FieldToAccessor.SCOPE.getName())) {
                return super.visit(call, preStates);
            }
            // Create variable declaration in pre-state with value set to the call
            final String callName = call.getName();
            final int argCount = call.getArgs() == null ? 0 : call.getArgs().size();
            final String varName = methodToVarName(callName);
            final VariableDeclarationExpr preState = new VariableDeclarationExpr(ModifierSet.FINAL, getReturnType(callName, argCount),
                    Collections.singletonList(new VariableDeclarator(new VariableDeclaratorId(varName), call)));
            preStates.add(new ExpressionStmt(preState));
            // Replace call by reference to the variable
            return new NameExpr(varName);
        }

        private String methodToVarName(String name) {
            if (name.startsWith("get")) {
                name = uncapitalize(name.substring(3));
            }
            // Make variable names unique when reusing
            Integer count = usedVarNames.get(name);
            if (count == null) {
                usedVarNames.put(name, 0);
            } else {
                usedVarNames.put(name, ++count);
                name = name + count;
            }
            return name;
        }

        private static Type getReturnType(String methodName, int paramCount) {
            try {
                final Class<?> _class = Class.forName(StateMachine.getInstance().getPackageName() + "." + getMachineClassName());
                final Method[] methods = _class.getMethods();
                Type match = null;
                for (Method method : methods) {
                    if (method.getName().equals(methodName)) {
                        final int count = method.getParameterTypes().length;
                        if (method.isVarArgs() ? count <= paramCount : count == paramCount) {
                            if (match != null) {
                                // Overload conflict, would be too hard to resolve, just give up
                                return new ClassOrInterfaceType("FixMeType");
                            }
                            final Class<?> returnType = method.getReturnType();
                            if (returnType == void.class) {
                                throw new IllegalStateException("Getter shouldn't be returning void: " + methodName);
                            }
                            match = fromClass(returnType);
                        }
                    }
                }
                return match;
            } catch (ClassNotFoundException exception) {
                throw new RuntimeException("Expected state machine class to be in classpath", exception);
            }
        }

        private static Type fromClass(Class<?> _class) {
            final String name = _class.getSimpleName();
            if (_class.isPrimitive()) {
                return new PrimitiveType(Primitive.valueOf(capitalize(name)));
            }
            return new ClassOrInterfaceType(name);
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
            return new MethodCallExpr(SCOPE, "get" + capitalize(name.getName()));
        }
    }
}
