package org.jetbrains.research.codecocoon.transformer.identifiers;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.Transformation;

import java.util.*;

public class ParameterNameTransformer extends IdentifierTransformer {

    public ParameterNameTransformer(SynonymGenerator synonymGenerator) {
        super(synonymGenerator);
    }

    @Override
    public Visitable visit(CallableDeclaration<?> functionDeclaration, Snippet snippet) {
        Map<String, String> parameterMapping = createParameterMapping(functionDeclaration, snippet);

        renameParameters(functionDeclaration, parameterMapping);

        updateSnippet(parameterMapping, snippet);

        return functionDeclaration;
    }

    public Map<String, String> createParameterMapping(CallableDeclaration<?> functionDeclaration, Snippet snippet) {
        NodeList<Parameter> parameters = functionDeclaration.getParameters();

        Map<String, String> parameterMapping = new HashMap<>();
        for (Parameter parameter : parameters) {
            String originalName = parameter.getNameAsString();
            String newName = synonymGenerator.generateSynonymFor(originalName, snippet.getCode(), "parameter");

            if (isValidSynonym(newName, functionDeclaration, parameterMapping)) {
                parameterMapping.put(originalName, newName);
            }

        }
        return parameterMapping;
    }

    private void renameParameters(CallableDeclaration<?> functionDeclaration, Map<String, String> parameterMapping) {
        List<NodeWithSimpleName<?>> parameterOccurrences = findAllParameterOccurrences(functionDeclaration);

        for (NodeWithSimpleName<?> occurence : parameterOccurrences) {
            if (parameterMapping.containsKey(occurence.getNameAsString())) {
                occurence.setName(parameterMapping.get(occurence.getNameAsString()));
            }
        }
    }

    private List<NodeWithSimpleName<?>> findAllParameterOccurrences(CallableDeclaration<?> functionDeclaration) {
        // We have to merge the parameters and accesses because JavaParser represents them as different objects.
        List<? extends NodeWithSimpleName<?>> parameters = functionDeclaration.findAll(Parameter.class);
        List<? extends NodeWithSimpleName<?>> accesses = functionDeclaration.findAll(NameExpr.class);
        List<NodeWithSimpleName<?>> allParameterOccurrences = new ArrayList<>();
        allParameterOccurrences.addAll(parameters);
        allParameterOccurrences.addAll(accesses);

        return allParameterOccurrences;
    }

    public void updateSnippet(Map<String, String> parameterMapping, Snippet snippet) {
        int transformationsApplied = parameterMapping.size();
        for (int i = 0; i < transformationsApplied; i++) snippet.addTransformation(Transformation.RenameParameters);
        snippet.setParameterMapping(parameterMapping);
    }


}
