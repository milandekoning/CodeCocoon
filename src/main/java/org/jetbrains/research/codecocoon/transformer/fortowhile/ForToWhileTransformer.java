package org.jetbrains.research.codecocoon.transformer.fortowhile;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.transformer.Transformation;
import org.jetbrains.research.codecocoon.transformer.Transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ForToWhileTransformer extends Transformer {
    ContinuePrepender continuePrepender;


    public ForToWhileTransformer() {
        continuePrepender = new ContinuePrepender();
    }


    // This transformer operates on block statements instead of for statements, because the result of this
    // transformation consists of multiple statements (the loop variable initialization and the while loop). Currently,
    // JavaParser does not support directly replacing a statement with multiple statements using the Visitor pattern.
    // Therefore, we need to modify the block statement holding the for loop instead of only replacing the for loop.
    @Override
    public Visitable visit(BlockStmt block, Snippet snippet) {
        List<ForStmt> forStatements = findForStatementsAtTopLevelOf(block);

        // Transforming a for loop to a while loop lifts the initialization of the loop variable to a higher scope. To
        // avoid duplicate initialization errors, we do not transform for loops where the loop variable shadows other
        // variables in the code block.
        filterOutShadowingForStatements(forStatements, block);

        for (ForStmt forLoop : forStatements) {
            NodeList<Statement> whileLoop = transformForToWhile(forLoop);
            replaceIn(block, forLoop, whileLoop);
            snippet.addTransformation(Transformation.ForToWhile);
        }

        return super.visit(block, snippet);
    }

    private List<ForStmt> findForStatementsAtTopLevelOf(BlockStmt block) {
        return block.getChildNodes().stream()
                .filter(child -> child instanceof ForStmt)
                .map(child -> (ForStmt) child)
                .collect(Collectors.toList());
    }

    private void filterOutShadowingForStatements(List<ForStmt> forStatements, BlockStmt block) {
        List<ForStmt> filteredForStatements = forStatements
                .stream()
                .filter(forStatement -> !initializationMatchesOtherVariableDeclaration(forStatement, block))
                .toList();
        forStatements.clear();
        forStatements.addAll(filteredForStatements);
    }

    private boolean initializationMatchesOtherVariableDeclaration(ForStmt forStatement, BlockStmt block) {
        List<VariableDeclarator> allVariableDeclarations = block.findAll(VariableDeclarator.class);

        List<VariableDeclarator> forInitializationVariableDeclarations = getInitializationVariableDeclarations(forStatement);

        for (VariableDeclarator variableDeclaration1 : allVariableDeclarations) {
            for (VariableDeclarator variableDeclaration2: forInitializationVariableDeclarations) {
                if (isDifferentDeclarationWithSameVariableName(variableDeclaration1, variableDeclaration2)) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<VariableDeclarator> getInitializationVariableDeclarations(ForStmt forStatement) {
        List<VariableDeclarator> variableDeclarations = new ArrayList<>();
        for (Expression initializationExpression : forStatement.getInitialization()) {
            variableDeclarations.addAll(initializationExpression.findAll(VariableDeclarator.class));
        }
        return variableDeclarations;
    }

    private boolean isDifferentDeclarationWithSameVariableName(VariableDeclarator declaration1, VariableDeclarator declaration2) {
        boolean isDifferentDeclaration = declaration1 != declaration2;
        boolean hasSameName = declaration1.getNameAsString().equals(declaration2.getNameAsString());
        return isDifferentDeclaration && hasSameName;
    }

    private NodeList<Statement> transformForToWhile(ForStmt forStatement) {
        NodeList<Statement> initializations = getInitializations(forStatement);
        NodeList<Statement> updates = getUpdates(forStatement);
        Optional<Expression> compare = forStatement.getCompare();
        Statement forBody = forStatement.getBody();
        BlockStmt whileBody = appendUpdates(updates, forBody);

        WhileStmt whileStatement = createWhileStmt(compare, whileBody);

        NodeList<Statement> transformedForStatement = initializations;
        transformedForStatement.add(whileStatement);

        return transformedForStatement;
    }

    private NodeList<Statement> getInitializations(ForStmt forStatement) {
        NodeList<Expression> initializationExpressions = forStatement.getInitialization();
        List<Statement> initializationStatements = initializationExpressions
                .stream()
                .map(ExpressionStmt::new)
                .collect(Collectors.toList());

        return new NodeList<>(initializationStatements);
    }

    private NodeList<Statement> getUpdates(ForStmt forStatement) {
        NodeList<Expression> updateExpressions = forStatement.getUpdate();
        List<Statement> updateStatements = updateExpressions
                .stream()
                .map(ExpressionStmt::new)
                .collect(Collectors.toList());

        return new NodeList<>(updateStatements);
    }

    private BlockStmt appendUpdates(NodeList<Statement> updates, Statement body) {
        BlockStmt whileBody;
        if (body instanceof BlockStmt) {
            whileBody = (BlockStmt) body;
        } else {
            whileBody = new BlockStmt();
            whileBody.addStatement(body);
        }

        for (Statement update : updates) {
            whileBody.addStatement(update);
        }

        // We also have to prepend the updates to every continue statement.
        continuePrepender.visit(whileBody, updates);

        return whileBody;
    }

    private WhileStmt createWhileStmt(Optional<Expression> condition, BlockStmt body) {
        if (condition.isEmpty()) {
            return new WhileStmt(new BooleanLiteralExpr(true), body);
        }
        return new WhileStmt(condition.get(), body);
    }

    private void replaceIn(BlockStmt block, Statement toReplace, NodeList<Statement> replaceBy) {
        // This could be a much nicer method if implemented in the BlockStmt class. However, for now I won't mess with
        // extending JavaParser classes.
        for (Statement statement : replaceBy) {
            block.getStatements().addBefore(statement, toReplace);
        }
        block.remove(toReplace);
    }
}
