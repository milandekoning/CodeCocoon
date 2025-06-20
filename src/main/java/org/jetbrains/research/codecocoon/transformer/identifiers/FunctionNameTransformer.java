package org.jetbrains.research.codecocoon.transformer.identifiers;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.Transformation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FunctionNameTransformer extends IdentifierTransformer {

    public FunctionNameTransformer(SynonymGenerator synonymGenerator) {
        super(synonymGenerator);
    }

    @Override
    public Visitable visit(MethodDeclaration methodDeclaration, Snippet snippet) {
        String oldName = methodDeclaration.getNameAsString();

        if (isObjectMethod(oldName)) return super.visit(methodDeclaration, snippet);

        String newName = synonymGenerator.generateSynonymFor(oldName, snippet.code, "function");

        if (isValidSynonym(newName, methodDeclaration)) {
            methodDeclaration.setName(newName);

            replaceFunctionCalls(methodDeclaration, oldName, newName);

            snippet.addTransformation(Transformation.RenameFunction);
            snippet.setFunctionMapping(Map.of(oldName, newName));
        }

        return super.visit(methodDeclaration, snippet);
    }

    private boolean isObjectMethod(String name) {
        Set<String> objectMethods = Set.of(
                "getClass", "hashCode", "equals", "clone", "toString", "notify", "notifyAll", "wait", "finalize"
        );
        return objectMethods.contains(name);
    }

    private boolean isValidSynonym(String newName, MethodDeclaration methodDeclaration) {
        List<MethodCallExpr> methodCalls = methodDeclaration.findAll(MethodCallExpr.class);
        for (MethodCallExpr methodCall : methodCalls) {
            if (methodCall.getNameAsString().equals(newName)) return false;
        }

        return isLegalIdentifierName(newName);
    }

    private void replaceFunctionCalls(MethodDeclaration methodDeclaration, String oldName, String newName) {
        List<MethodCallExpr> methodCalls = methodDeclaration.findAll(MethodCallExpr.class);
        for (MethodCallExpr methodCall : methodCalls) {
            boolean nameMatches = methodCall.getNameAsString().equals(oldName);
            if (nameMatches) {
                methodCall.setName(newName);
            }
        }
    }


}
