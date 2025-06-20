package org.jetbrains.research.codecocoon.transformer;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.Snippet;

public class NestElseIfTransformer extends Transformer {

    @Override
    public Visitable visit(IfStmt ifStatement, Snippet snippet) {
        if (!ifStatement.hasElseBranch()) {
            return super.visit(ifStatement, snippet);
        }
        Statement elseStatement = ifStatement.getElseStmt().get();

        BlockStmt block = new BlockStmt();
        if (elseStatement instanceof IfStmt) {
            block.addStatement(elseStatement);
            ifStatement.setElseStmt(block);
            snippet.addTransformation(Transformation.NestElseIf);
        }

        return super.visit(ifStatement, snippet);
    }

}
