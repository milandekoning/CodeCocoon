package org.jetbrains.research.codecocoon.transformer;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.Snippet;


public class ReverseIfTransformer extends Transformer {

    @Override
    public Visitable visit(IfStmt ifStatement, Snippet snippet) {
        if (!ifStatement.hasElseBranch()) return super.visit(ifStatement, snippet);

        Statement thenStatement = ifStatement.getThenStmt();
        Statement elseStatement = ifStatement.getElseStmt().get();
        if (!(elseStatement instanceof BlockStmt)) return super.visit(ifStatement, snippet);

        ifStatement.setThenStmt(elseStatement);
        ifStatement.setElseStmt(thenStatement);

        Expression condition = ifStatement.getCondition();
        Expression negatedCondition = negate(condition);
        ifStatement.setCondition(negatedCondition);

        snippet.addTransformation(Transformation.ReverseIf);
        return super.visit(ifStatement, snippet);
    }

    private Expression negate(Expression condition) {
        if (condition instanceof BooleanLiteralExpr booleanLiteralExpression) return new BooleanLiteralExpr(!booleanLiteralExpression.getValue());
        if (condition instanceof BinaryExpr binaryCondition) return negate(binaryCondition);
        if (condition instanceof NameExpr) return new UnaryExpr(condition, UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        if (condition instanceof MethodCallExpr) return new UnaryExpr(condition, UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        if (condition instanceof EnclosedExpr) return new UnaryExpr(condition, UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        if (condition instanceof UnaryExpr unaryExpression && unaryExpression.getOperator() == UnaryExpr.Operator.LOGICAL_COMPLEMENT) return unaryExpression.getExpression();

        return new UnaryExpr(new EnclosedExpr(condition), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
    }

    private Expression negate(BinaryExpr condition) {
        switch (condition.getOperator()) {
            case NOT_EQUALS -> {
                condition.setOperator(Operator.EQUALS);
                return condition;
            }
            case EQUALS -> {
                condition.setOperator(Operator.NOT_EQUALS);
                return condition;
            }
            case GREATER -> {
                condition.setOperator(Operator.LESS_EQUALS);
                return condition;
            }
            case GREATER_EQUALS -> {
                condition.setOperator(Operator.LESS);
                return condition;
            }
            case LESS -> {
                condition.setOperator(Operator.GREATER_EQUALS);
                return condition;
            }
            case LESS_EQUALS -> {
                condition.setOperator(Operator.GREATER);
                return condition;
            }
            case AND -> {
                condition.setOperator(Operator.OR);
                condition.setLeft(negate(condition.getLeft()));
                condition.setRight(negate(condition.getRight()));
                return condition;
            }
            case OR -> {
                condition.setOperator(Operator.AND);
                // This lowers the precedence of the operator (|| is evaluated before &&). If the children contain &&,
                // we need to wrap them in parentheses to preserve semantic behaviour.
                Expression left = condition.getLeft();
                Expression right = condition.getRight();
                condition.setLeft(safeNegate(left));
                condition.setRight(safeNegate(right));
                return condition;
            }
        }

        return new UnaryExpr(new EnclosedExpr(condition), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
    }

    private Expression safeNegate(Expression expression) {
        if (isAndExpression(expression))  {
            return new EnclosedExpr(negate(expression));
        } else {
            return negate(expression);
        }
    }


    private boolean isAndExpression(Expression expression) {
        if (!(expression instanceof BinaryExpr)) return false;
        return ((BinaryExpr) expression).getOperator() == Operator.AND;
    }

}
