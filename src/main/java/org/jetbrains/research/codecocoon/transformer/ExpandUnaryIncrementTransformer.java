package org.jetbrains.research.codecocoon.transformer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.Snippet;

import java.util.Optional;

public class ExpandUnaryIncrementTransformer extends Transformer {

    @Override
    public Visitable visit(UnaryExpr expression, Snippet snippet) {
        if (!isInStatement(expression)) return super.visit(expression, snippet);

        AssignExpr transformedExpression = new AssignExpr();
        transformedExpression.setTarget(expression.getExpression());
        transformedExpression.setValue(new IntegerLiteralExpr("1"));
        switch (expression.getOperator()) {
            case POSTFIX_INCREMENT:
                transformedExpression.setOperator(AssignExpr.Operator.PLUS);
                break;
            case POSTFIX_DECREMENT:
                transformedExpression.setOperator(AssignExpr.Operator.MINUS);
                break;
            default:
                return super.visit(expression, snippet);
        }
        snippet.addTransformation(Transformation.ExpandUnaryIncrement);


        return super.visit(transformedExpression, snippet);
    }

    private boolean isInStatement(UnaryExpr expression) {
        Optional<Node> parent = expression.getParentNode();
        if (parent.isPresent()) {
            Node parentNode = parent.get();
            return parentNode instanceof Statement;
        }
        return true;
    }
}
