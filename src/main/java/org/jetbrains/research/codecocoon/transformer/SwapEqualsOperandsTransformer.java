package org.jetbrains.research.codecocoon.transformer;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.Snippet;

public class SwapEqualsOperandsTransformer extends Transformer {


    @Override
    public Visitable visit(BinaryExpr expression, Snippet snippet) {
        if (eligibleForSwap(expression)) {
            Expression left = expression.getLeft();
            expression.setLeft(expression.getRight());
            expression.setRight(left);
            snippet.addTransformation(Transformation.SwapEqualsOperands);
        }
        return super.visit(expression, snippet);
    }

    private boolean eligibleForSwap(BinaryExpr expression) {
        return (expression.getOperator() == BinaryExpr.Operator.EQUALS || expression.getOperator() == BinaryExpr.Operator.NOT_EQUALS) &&
                !containsAssignment(expression.getLeft()) && !containsAssignment(expression.getRight());
    }

    private boolean containsAssignment(Expression expression) {
        return !expression.findAll(AssignExpr.class).isEmpty();
    }
}
