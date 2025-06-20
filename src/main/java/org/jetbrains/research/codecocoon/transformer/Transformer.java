package org.jetbrains.research.codecocoon.transformer;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import org.jetbrains.research.codecocoon.Snippet;

/**
 * Refer to Design.md for visitor pattern.
 */
public abstract class Transformer extends ModifierVisitor<Snippet> {

    public void transform(Snippet snippet) {
        CallableDeclaration<?> function = parse(snippet.getCode());
        try {
            function.accept(this, snippet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        snippet.setCode(function.toString());
    }

    private CallableDeclaration<?> parse(String snippet) {
        JavaParser parser = new JavaParser();

        ParseResult<MethodDeclaration> parseMethodResult = parser.parseMethodDeclaration(snippet);

        if (parseMethodResult.isSuccessful() && parseMethodResult.getResult().isPresent()) {
            return parseMethodResult.getResult().get();
        } else {
            return parseConstructor(snippet);
        }
    }

    private ConstructorDeclaration parseConstructor(String snippet) {
        JavaParser parser = new JavaParser();
        // Wrap the method snippet in a class, since a constructor can only be parsed in a class context.
        String wrappedSnippet = "class Wrapper {" + snippet + "}";
        ParseResult<CompilationUnit> parseResult = parser.parse(wrappedSnippet);

        if (!parseResult.isSuccessful() || parseResult.getResult().isEmpty()) {
            throw new IllegalArgumentException("Could not parse function: " + snippet);
        }

        CompilationUnit parsedClass = parseResult.getResult().get();
        ConstructorDeclaration constructor = parsedClass
                .findFirst(ConstructorDeclaration.class)
                .orElseThrow(() -> new IllegalArgumentException("Could not parse function: " + snippet));

        return constructor;
    }

    @Override
    public boolean equals(Object o) {
        return this.getClass() == o.getClass();
    }
}
