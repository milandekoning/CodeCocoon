package org.jetbrains.research.codecocoon.transformer.fortowhile;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.transformer.exception.UnsupportedStatementException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ContinuePrependerTest {

    ContinuePrepender continuePrepender = new ContinuePrepender();
    JavaParser parser = new JavaParser();

    private NodeList<Statement> generateUpdates() {
        NodeList<Statement> updates = new NodeList<>();
        Statement IPlusPlus = new ExpressionStmt(new UnaryExpr(new NameExpr("i"), UnaryExpr.Operator.POSTFIX_INCREMENT));
        updates.add(IPlusPlus);
        return updates;
    }

    @Test
    public void testSimple() {
        ContinueStmt program = (ContinueStmt) parser.parseStatement("continue;").getResult().get();
        NodeList<Statement> updates = generateUpdates();
        Statement expected = parser.parseStatement("{i++; continue;}").getResult().get();

        Visitable actual = continuePrepender.visit(program, updates);

        assertEquals(expected, actual);
    }


    @Test
    public void testBlock() {
        BlockStmt program = (BlockStmt) parser.parseStatement("{continue;}").getResult().get();
        NodeList<Statement> updates = generateUpdates();
        Statement expected = parser.parseStatement("{i++; continue;}").getResult().get();

        Visitable actual = continuePrepender.visit(program, updates);

        assertEquals(expected, actual);
    }

    @Test
    public void testNotInBlock() {
        BlockStmt program = new BlockStmt();
        NodeList<Statement> updates = generateUpdates();
        BlockStmt expected = new BlockStmt();

        Visitable actual = continuePrepender.visit(program, updates);

        assertEquals(expected, actual);
    }

    @Test
    public void testNested() {
        BlockStmt program = (BlockStmt) parser.parseStatement("{if (true) continue;}").getResult().get();
        NodeList<Statement> updates = generateUpdates();
        Statement expected = parser.parseStatement("{if (true){i++;continue;}}").getResult().get();

        Visitable actual = continuePrepender.visit(program, updates);

        assertEquals(expected, actual);
    }

    @Test
    public void testNestedInForLoop() {
        BlockStmt program = (BlockStmt) parser.parseStatement("{for (int j = 0; j < 10; j++) continue;}").getResult().get();
        NodeList<Statement> updates = generateUpdates();
        Statement expected = parser.parseStatement("{for (int j = 0; j < 10; j++) continue;}").getResult().get();

        Visitable actual = continuePrepender.visit(program, updates);

        assertEquals(expected, actual);
    }

    @Test
    public void testNestedInWhileLoop() {
        BlockStmt program = (BlockStmt) parser.parseStatement("{while (true) continue;}").getResult().get();
        NodeList<Statement> updates = generateUpdates();
        Statement expected = parser.parseStatement("{while (true) continue;}").getResult().get();

        Visitable actual = continuePrepender.visit(program, updates);

        assertEquals(expected, actual);
    }

    @Test
    public void testNestedInDoWhileLoop() {
        BlockStmt program = (BlockStmt) parser.parseStatement("{do {continue;} while (true)}").getResult().get();
        NodeList<Statement> updates = generateUpdates();
        Statement expected = parser.parseStatement("{do {continue;} while (true)}").getResult().get();

        Visitable actual = continuePrepender.visit(program, updates);

        assertEquals(expected, actual);
    }

    @Test
    public void testLabeledContinue() {
        ContinueStmt program = (ContinueStmt) parser.parseStatement("continue label;").getResult().get();
        NodeList<Statement> updates = generateUpdates();

        assertThrows(UnsupportedStatementException.class, () -> continuePrepender.visit(program, updates));
    }

    @Test
    public void testLabeledContinueInBlock() {
        BlockStmt program = (BlockStmt) parser.parseStatement("{continue label;}").getResult().get();
        NodeList<Statement> updates = generateUpdates();

        assertThrows(UnsupportedStatementException.class, () -> continuePrepender.visit(program, updates));
    }

}
