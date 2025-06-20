package org.jetbrains.research.codecocoon.transformer;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.Snippet;


public class SwapRelationOperandsTransformer extends Transformer {

    @Override
    public Visitable visit(BinaryExpr expression, Snippet snippet) {
        if (containsAssignment(expression.getLeft()) || containsAssignment(expression.getRight())) return super.visit(expression, snippet);


        BinaryExpr transformedExpression = new BinaryExpr();
        transformedExpression.setLeft(expression.getRight());
        transformedExpression.setRight(expression.getLeft());
        switch (expression.getOperator()) {
            case LESS:
                transformedExpression.setOperator(BinaryExpr.Operator.GREATER);
                break;
            case GREATER:
                transformedExpression.setOperator(BinaryExpr.Operator.LESS);
                break;
            case GREATER_EQUALS:
                transformedExpression.setOperator(BinaryExpr.Operator.LESS_EQUALS);
                break;
            case LESS_EQUALS:
                transformedExpression.setOperator(BinaryExpr.Operator.GREATER_EQUALS);
                break;
            default:
                return super.visit(expression, snippet);
        }
        snippet.addTransformation(Transformation.SwapRelationOperands);
        return super.visit(transformedExpression, snippet);
    }

    private boolean containsAssignment(Expression expression) {
        return !expression.findAll(AssignExpr.class).isEmpty();
    }




}
