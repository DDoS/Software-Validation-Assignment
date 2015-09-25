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
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
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
 * Generates the test sources by first generating the round trip path tree, then using it to generate the source of a test class.
 *
 * @see RoundTripPathTreeNode
 */
public class StateTestGenerator {
    /**
     * Generates the test cases for the state machine in {@link StateMachine#getInstance()} and writes the file to the given output path.
     *
     * @param outputClassName The simple name of the generate class containing the generated test cases
     * @return The source code for the generated test class
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

    // This generates all the test methods of the class, given a starting node
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

    // This generates generates the bodies of the test methods, given a starting node
    private static List<BlockStmt> generateTestBodies(RoundTripPathTreeNode node) {
        if (!node.isAlpha()) {
            throw new IllegalArgumentException("Expected first node to be alpha");
        }
        final BlockStmt body = new BlockStmt(new ArrayList<Statement>());
        final List<BlockStmt> bodies = new ArrayList<BlockStmt>();
        // We need to track variable names used through the test case to prevent conflicts
        final Map<String, Integer> usedVarNames = new HashMap<String, Integer>();
        // Don't pass the alpha node, but the children
        for (RoundTripPathTreeNode child : node.getChildren()) {
            generateTestBodies(child, body, bodies, usedVarNames);
        }
        return bodies;
    }

    // This method does the actual body generation: the body is forked for every children and only added to the bodies list when the leaf is reached
    private static void generateTestBodies(RoundTripPathTreeNode node, BlockStmt body, List<BlockStmt> bodies, Map<String, Integer> usedVarNames) {
        final List<Statement> statements = body.getStmts();
        final Transition transition = node.getTransition();
        // This comment will be added to the first statement in this event check
        final LineComment comment = new LineComment(" " + node.getSignature());
        boolean commentAdded = false;
        // Generate the condition reacher first if needed
        if (!transition.getCondition().isEmpty()) {
            final Statement conditionReacher = generateConditionReacher(transition.getCondition());
            conditionReacher.setComment(comment);
            commentAdded = true;
            statements.add(conditionReacher);
        }
        // Next place the pre statements of the action checks
        final List<ActionCheck> checks = generateActionChecks(transition.getAction(), usedVarNames);
        for (ActionCheck check : checks) {
            final List<Statement> pre = check.getPre();
            // Add the comment to the first pre statement, if any exist
            if (!commentAdded && !pre.isEmpty()) {
                pre.get(0).setComment(comment);
                commentAdded = true;
            }
            statements.addAll(pre);
        }
        // Now we add the actual event call
        final Statement eventStatement = eventAsStatement(transition.getEvent());
        if (!commentAdded) {
            // If this is the first statement, add the comment now
            eventStatement.setComment(comment);
        }
        statements.add(eventStatement);
        // Next we check that the event caused the right state
        statements.add(generateStateCheck(transition.getTo().getName()));
        // Finally we check the action results using the post statements
        for (ActionCheck check : checks) {
            statements.add(check.getPost());
        }
        // Generate the rest of the test case using the children
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

    // This is a while loop in which the user adds event(s) that need to be called to reach the condition, for the event being tested
    // The while loop checks the condition and stops when it is reached. This goes before the event call
    private static Statement generateConditionReacher(String condition) {
        final Expression inverseCondition = invertCondition(conditionAsExpression(condition));
        final ThrowStmt missingBody = new ThrowStmt(new ObjectCreationExpr(null, new ClassOrInterfaceType(UnsupportedOperationException.class.getSimpleName()),
                Collections.<Expression>singletonList(new StringLiteralExpr("Missing event for reaching condition: " + condition))));
        return new WhileStmt(inverseCondition, new BlockStmt(Collections.<Statement>singletonList(missingBody)));
    }

    // This transforms actions that are assignments into one or two parts: if the action depends on a previous property value,
    // it generates a variable declaration that is initialized to the value before the said action. This is the "pre" statement.
    // The second "post" statement is the action converted to an assert that checks if the results are correct.
    // Post and pre increments and decrements and compound assign operators are expanded to simple assigns
    private static List<ActionCheck> generateActionChecks(String action, Map<String, Integer> usedVarNames) {
        final List<ActionCheck> checks = new ArrayList<ActionCheck>();
        final List<Statement> statements = actionAsStatements(action);
        for (Statement statement : statements) {
            // We're looking for an action that is an assign expression statement
            if (!(statement instanceof ExpressionStmt)) {
                continue;
            }
            Expression expression = ((ExpressionStmt) statement).getExpression();
            if (!(expression instanceof AssignExpr)) {
                // Check if this is actually a decrement or increment operator
                if (expression instanceof UnaryExpr) {
                    final UnaryExpr unary = (UnaryExpr) expression;
                    final Operator operator;
                    switch (unary.getOperator()) {
                        case preDecrement:
                        case posDecrement:
                            operator = Operator.minus;
                            break;
                        case preIncrement:
                        case posIncrement:
                            operator = Operator.plus;
                            break;
                        default:
                            continue;
                    }
                    // If this is the case, expand it and keep going
                    final Expression target = unary.getExpr();
                    expression = new AssignExpr(target, new BinaryExpr(target, new IntegerLiteralExpr("1"), operator), AssignExpr.Operator.assign);
                } else {
                    continue;
                }
            }
            AssignExpr assign = (AssignExpr) expression;
            if (assign.getOperator() != AssignExpr.Operator.assign) {
                // This is a compound assign, so expand it to a simple assign
                final Expression target = assign.getTarget();
                assign = new AssignExpr(target, new BinaryExpr(target, new EnclosedExpr(assign.getValue()), getExpandedOperator(assign.getOperator())),
                        AssignExpr.Operator.assign);
            }
            // Generate the check code for the assignment
            checks.add(new ActionCheck(assign, usedVarNames));
        }
        return checks;
    }

    private static Operator getExpandedOperator(AssignExpr.Operator operator) {
        switch (operator) {
            case plus:
                return Operator.plus;
            case minus:
                return Operator.minus;
            case star:
                return Operator.times;
            case slash:
                return Operator.divide;
            case and:
                return Operator.binAnd;
            case or:
                return Operator.binOr;
            case xor:
                return Operator.xor;
            case rem:
                return Operator.remainder;
            case lShift:
                return Operator.lShift;
            case rSignedShift:
                return Operator.rSignedShift;
            case rUnsignedShift:
                return Operator.rUnsignedShift;
            default:
                return null;
        }
    }

    // This generates an assert that checks if the machine state is as expected
    private static Statement generateStateCheck(String state) {
        final MethodCallExpr stateGetter = new MethodCallExpr(VariableToGetter.SCOPE, "getState", Collections.<Expression>emptyList());
        final NameExpr stateValue = new NameExpr(getMachineClassName() + ".State." + state);
        return generateAssert(stateValue, stateGetter);
    }

    // This converts an event to a statement, which is either a constructor call and variable declaration or just a method call
    private static Statement eventAsStatement(String event) {
        final Expression expression;
        if ("@ctor".equals(event)) {
            // Constructor case is special: declarea and initialize our machine variable
            final ClassOrInterfaceType type = new ClassOrInterfaceType(getMachineClassName());
            final ObjectCreationExpr constructor = new ObjectCreationExpr(null, type, Collections.<Expression>emptyList());
            expression = new VariableDeclarationExpr(ModifierSet.FINAL, type,
                    Collections.singletonList(new VariableDeclarator(new VariableDeclaratorId(VariableToGetter.SCOPE.getName()), constructor)));
        } else {
            expression = new MethodCallExpr(VariableToGetter.SCOPE, event, Collections.<Expression>emptyList());
        }
        return new ExpressionStmt(expression);
    }

    // This parses the condition to an expression and replaces variables by equivalent getters called on a "machine" variable
    // Returns null if the condition is an empty string
    private static Expression conditionAsExpression(String condition) {
        if (condition.isEmpty()) {
            return null;
        }
        try {
            final Expression expression = JavaParser.parseExpression(condition);
            // Replace fields by getters
            expression.accept(VariableToGetter.INSTANCE, null);
            return expression;
        } catch (ParseException exception) {
            throw new RuntimeException(exception);
        }
    }

    // This parses the action as a list of statements and replaces variables by equivalent getters called on a "machine" variable
    private static List<Statement> actionAsStatements(String action) {
        if (action.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            final BlockStmt block = JavaParser.parseBlock('{' + action + '}');
            // Replace fields by getters
            block.accept(VariableToGetter.INSTANCE, null);
            return block.getStmts();
        } catch (ParseException exception) {
            throw new RuntimeException(exception);
        }
    }

    // This logically inverts a condition expression, simplifying the AST if possible
    private static Expression invertCondition(Expression expression) {
        if (expression instanceof UnaryExpr) {
            final UnaryExpr unary = (UnaryExpr) expression;
            // Unary not expression, just remove the not
            if (unary.getOperator() == UnaryExpr.Operator.not) {
                return unary.getExpr();
            } else {
                // Else wrap it in a not
                return new UnaryExpr(expression, UnaryExpr.Operator.not);
            }
        } else if (expression instanceof BinaryExpr) {
            final BinaryExpr binary = (BinaryExpr) expression;
            final Operator inverted;
            // For binary compare operators, just use the opposite operator
            // Boolean operators use De Morgan's law
            // Other operators use a not operator, but are also enclosed in ()
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
            // This expressions have lower precedence so they need to be enclosed in ()
            return new UnaryExpr(new EnclosedExpr(expression), UnaryExpr.Operator.not);
        } else {
            // These are higher precedence, no need for ()
            return new UnaryExpr(expression, UnaryExpr.Operator.not);
        }
    }

    // This generates an assert statement for expected and actual values
    private static Statement generateAssert(Expression expected, Expression actual) {
        return new ExpressionStmt(new MethodCallExpr(new NameExpr("Assert"), "assertEquals", Arrays.asList(expected, actual)));
    }

    // This generates import declaration for the given classes
    private static List<ImportDeclaration> generateImports(Class<?>... classes) {
        final List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
        for (Class<?> _class : classes) {
            imports.add(new ImportDeclaration(new NameExpr(_class.getCanonicalName()), false, false));
        }
        return imports;
    }

    // This returns the simple name of the machine currently loaded
    private static String getMachineClassName() {
        final String javaFileName = StateMachine.getInstance().getClassName();
        return javaFileName.substring(0, javaFileName.lastIndexOf(".java"));
    }

    // This transform the first character of a string to uppercase
    private static String capitalize(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    // This transform the first character of a string to lowercase
    private static String uncapitalize(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    // This class holds the pre and post action statements of an action check
    private static class ActionCheck {
        private final List<Statement> pre = new ArrayList<Statement>();
        private final Statement post;

        // Given an action that is an assign expression, create the pre and post statements
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

    // This is a transformer for the AST which generates the pre statements for an action check
    // It also replace method calls in the expected value to references to variables in pre.
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
            if (!nameExpr.getName().equals(VariableToGetter.SCOPE.getName())) {
                return super.visit(call, preStates);
            }
            // Create variable declaration in pre with the init value set to the call we found
            final String callName = call.getName();
            final int argCount = call.getArgs() == null ? 0 : call.getArgs().size();
            final String varName = methodToVarName(callName);
            final VariableDeclarationExpr preState = new VariableDeclarationExpr(ModifierSet.FINAL, getReturnType(callName, argCount),
                    Collections.singletonList(new VariableDeclarator(new VariableDeclaratorId(varName), call)));
            preStates.add(new ExpressionStmt(preState));
            // Replace the call by a reference to the variable
            return new NameExpr(varName);
        }

        // Converts a method name to an adequate unique variable name
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

        // This finds the reflection method for a given name and parameter count and returns its return type.
        // This assumes the methods have no overload on argument types, else it returns a non-existent type to be corrected manually
        // If the method returns void, an exception is thrown as this shouldn't be happening. An exception is also
        // thrown if the class isn't in the class path, which shouldn't happen either
        private static Type getReturnType(String methodName, int paramCount) {
            final Class<?> _class;
            try {
                _class = Class.forName(StateMachine.getInstance().getPackageName() + "." + getMachineClassName());
            } catch (ClassNotFoundException exception) {
                throw new RuntimeException("Expected state machine class to be in classpath", exception);
            }
            final Method[] methods = _class.getMethods();
            Type match = null;
            for (Method method : methods) {
                // Check for name match
                if (method.getName().equals(methodName)) {
                    final int count = method.getParameterTypes().length;
                    // Check for param count match, taking into account possible vararg expansion
                    if (method.isVarArgs() ? count <= paramCount : count == paramCount) {
                        if (match != null) {
                            // Got a previous match, so we have an overload conflict on param types
                            // This is hard to resolve, just give up, let the user figure it out
                            return new ClassOrInterfaceType("FixMeType");
                        }
                        // Found a (so far) unique match, get the return type
                        final Class<?> returnType = method.getReturnType();
                        if (returnType == void.class) {
                            throw new IllegalStateException("Getter shouldn't be returning void: " + methodName);
                        }
                        match = fromClass(returnType);
                    }
                }
            }
            // We only get here if we find a unique method match
            return match;
        }

        // This converts a class type to it's AST node representation
        private static Type fromClass(Class<?> _class) {
            final String name = _class.getSimpleName();
            if (_class.isPrimitive()) {
                return new PrimitiveType(Primitive.valueOf(capitalize(name)));
            }
            return new ClassOrInterfaceType(name);
        }
    }

    // This converts variables to getters called on a "machine" variable. Needed since we're testing outside the class
    private static class VariableToGetter extends ModifierVisitorAdapter<Object> {
        private static final VariableToGetter INSTANCE = new VariableToGetter();
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
