package org.jetbrains.research.codecocoon.transformer.identifiers;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.Transformation;

import java.util.*;


public class VariableNameTransformer extends IdentifierTransformer {

    public VariableNameTransformer(SynonymGenerator synonymGenerator) {
        super(synonymGenerator);
    }

    @Override
    public Visitable visit(CallableDeclaration<?> functionDeclaration, Snippet snippet) {
        Map<String, String> variableSynonymMapping = createVariableSynonymMapping(functionDeclaration, snippet);

        renameVariables(functionDeclaration, variableSynonymMapping);

        updateSnippet(snippet, variableSynonymMapping);

        // We don't want to continue to lower levels, as this implementation already finds and replaces variables in all
        // scopes below.
        return functionDeclaration;
    }

    private Map<String, String> createVariableSynonymMapping(CallableDeclaration<?> functionDeclaration, Snippet snippet) {
        Map<String, String> variableMapping = new HashMap<>();

        List<VariableDeclarator> originalVariableDeclarations = functionDeclaration.findAll(VariableDeclarator.class);
        for (VariableDeclarator variable : originalVariableDeclarations) {
            String originalName = variable.getNameAsString();
            String synonym = synonymGenerator.generateSynonymFor(originalName, snippet.getCode(), "variable");

            if (isValidSynonym(synonym, functionDeclaration, variableMapping)) {
                variableMapping.put(originalName, synonym);
            }
        }

        return variableMapping;
    }

    private void renameVariables(CallableDeclaration<?> functionDeclaration, Map<String, String> variableSynonymMapping) {
        List<NodeWithSimpleName<?>> allVariableOccurrences = findAllVariableOccurrences(functionDeclaration);

        for (NodeWithSimpleName<?> variableOccurrence : allVariableOccurrences) {
            String variableName = variableOccurrence.getNameAsString();
            if (variableSynonymMapping.containsKey(variableName)) {
                String synonym = variableSynonymMapping.get(variableName);
                variableOccurrence.setName(synonym);
            }
        }
    }

    private List<NodeWithSimpleName<?>> findAllVariableOccurrences(CallableDeclaration<?> functionDeclaration) {
        // We have to merge the variable declarations and accesses because JavaParser represents them as different objects.
        List<? extends NodeWithSimpleName<?>> declarations = functionDeclaration.findAll(VariableDeclarator.class);
        List<? extends NodeWithSimpleName<?>> accesses = functionDeclaration.findAll(NameExpr.class);
        List<NodeWithSimpleName<?>> allVariableOccurrences = new ArrayList<>();
        allVariableOccurrences.addAll(declarations);
        allVariableOccurrences.addAll(accesses);

        return allVariableOccurrences;
    }

    private void updateSnippet(Snippet snippet, Map<String, String> variableSynonymMapping) {
        int transformationsApplied = variableSynonymMapping.size();
        for (int i = 0; i < transformationsApplied; i++) snippet.addTransformation(Transformation.RenameVariables);
        snippet.setVariableMapping(variableSynonymMapping);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof VariableNameTransformer otherVariableNameTransformer) {
            return this.synonymGenerator.equals(otherVariableNameTransformer.synonymGenerator);
        }
        return false;
    }

    @Override
    public String toString() {
        return "VariableNameTransformer(" + synonymGenerator + ")";
    }


}


