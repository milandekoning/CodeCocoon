package org.jetbrains.research.codecocoon.transformer.fortowhile;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.transformer.exception.UnsupportedStatementException;

public class ContinuePrepender extends ModifierVisitor<NodeList<Statement>> {

    @Override
    public Visitable visit(ContinueStmt continueStmt, NodeList<Statement> updates) {
        if (continueStmt.getLabel().isPresent()) throw new UnsupportedStatementException("Labeled continue is not supported.");

        BlockStmt block = new BlockStmt();
        addUpdates(block, updates);
        block.addStatement(continueStmt);
        return block;
    }

    @Override
    public Visitable visit(BlockStmt block, NodeList<Statement> updates) {
        if (endsWithContinue(block)) {
            block.getStatements().removeLast();

            addUpdates(block, updates);
            block.addStatement(new ContinueStmt());
        }
        propagateTroughBlock(block, updates);

        return block;
    }

    private void addUpdates(BlockStmt block, NodeList<Statement> updates) {
        for (Statement update : updates) {
            block.addStatement(update);
        }
    }

    private boolean endsWithContinue(BlockStmt block) {
        boolean hasLastStatement = block.getStatements().getLast().isPresent();
        if (!hasLastStatement) return false;

        Statement lastStatement = block.getStatements().getLast().get();
        if (!(lastStatement instanceof ContinueStmt continueStmt)) return false;

        if (continueStmt.getLabel().isPresent()) throw new UnsupportedStatementException("Labeled continue is not supported");

        return true;
    }

    private void propagateTroughBlock(BlockStmt block, NodeList<Statement> updates) {
        for (Statement statement : block.getStatements()) {
            if (!(statement instanceof ContinueStmt) &&
                    !(statement instanceof ForStmt) &&
                    !(statement instanceof WhileStmt) &&
                    !(statement instanceof DoStmt)) {
                statement.accept(this, updates);
            }
        }
    }
}
