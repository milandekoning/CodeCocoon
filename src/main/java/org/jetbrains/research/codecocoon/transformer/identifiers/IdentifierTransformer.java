package org.jetbrains.research.codecocoon.transformer.identifiers;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.Visitable;
import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.Transformer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class IdentifierTransformer extends Transformer {
    protected final SynonymGenerator synonymGenerator;

    public IdentifierTransformer(SynonymGenerator synonymGenerator) {
        this.synonymGenerator = synonymGenerator;
    }

    @Override
    public Visitable visit(MethodDeclaration method, Snippet snippet) {
        return this.visit((CallableDeclaration<?>) method, snippet);
    }

    @Override
    public Visitable visit(ConstructorDeclaration constructor, Snippet snippet) {
        return this.visit((CallableDeclaration<?>) constructor, snippet);
    }

    public Visitable visit(CallableDeclaration<?> callableDeclaration, Snippet snippet) {
        return callableDeclaration;
    }

    protected boolean isValidSynonym(String synonym, CallableDeclaration<?> functionDeclaration, Map<String, String> identifierMapping) {
        Collection<? extends NodeWithSimpleName<?>> originalParameters = functionDeclaration.getParameters();
        Collection<? extends NodeWithSimpleName<?>> originalVariables = functionDeclaration.findAll(VariableDeclarator.class);

        boolean collidesWithOriginalIdentifier = collides(synonym, originalParameters) || collides(synonym, originalVariables);
        boolean collidesWithOtherSynonym = identifierMapping.containsValue(synonym);

        return isLegalIdentifierName(synonym) && !collidesWithOriginalIdentifier && !collidesWithOtherSynonym;
    }

    protected boolean collides(String synonym, Collection<? extends NodeWithSimpleName<?>> declarations) {
        return declarations
                .stream()
                .map(NodeWithSimpleName::getNameAsString)
                .anyMatch(x -> x.equals(synonym));
    }

    protected boolean isLegalIdentifierName(String identifier) {
        // There is probably a library for this, but I could not find one. ChatGPT made this for me.
        if (identifier == null || identifier.isEmpty()) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(identifier.charAt(0))) {
            return false;
        }
        for (int i = 1; i < identifier.length(); i++) {
            if (!Character.isJavaIdentifierPart(identifier.charAt(i))) {
                return false;
            }
        }
        return !isReservedKeyword(identifier);
    }

    private boolean isReservedKeyword(String identifier) {
        Set<String> keywords = Set.of(
                "abstract", "assert", "boolean", "break", "byte", "case", "catch",
                "char", "class", "const", "continue", "default", "do", "double", "else",
                "enum", "extends", "final", "finally", "float", "for", "goto", "if",
                "implements", "import", "instanceof", "int", "interface", "long", "native",
                "new", "package", "private", "protected", "public", "return", "short",
                "static", "strictfp", "super", "switch", "synchronized", "this", "throw",
                "throws", "transient", "try", "void", "volatile", "while"
        );
        return keywords.contains(identifier);
    }
}
