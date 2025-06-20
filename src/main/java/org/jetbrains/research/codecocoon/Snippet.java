package org.jetbrains.research.codecocoon;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.jetbrains.research.codecocoon.transformer.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Snippet implements Cloneable {
    public String code;
    public String id;
    public Map<Transformation, Integer> transformations;
    public Map<String, String> functionMapping;
    public Map<String, String> variableMapping;
    public Map<String, String> parameterMapping;


    public Snippet(String id, String code) {
        this.code = code;
        this.id = id;
        this.transformations = new HashMap<>();
    }

    public void addTransformation(Transformation transformation) {
        if (!transformations.containsKey(transformation)) {
            transformations.put(transformation, 1);
        } else {
            transformations.put(transformation, transformations.get(transformation) + 1);
        }
    }

    public static List<Snippet> fromMap(Map<String, String> snippetMap) {
        List<Snippet> snippets = new ArrayList<>();
        for (String id : snippetMap.keySet()) {
            snippets.add(new Snippet(id, snippetMap.get(id)));
        }
        return snippets;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getId() {
        return this.id;
    }

    public Map<Transformation, Integer> getTransformations() {
        return transformations;
    }

    public void setFunctionMapping(Map<String, String> functionMapping) {
        this.functionMapping = functionMapping;
    }

    public void setVariableMapping(Map<String, String> variableMapping) {
        this.variableMapping = variableMapping;
    }

    public void setParameterMapping(Map<String, String> parameterMapping) {
        this.parameterMapping = parameterMapping;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Snippet other)) return false;
        if (!this.id.equals(other.id)) return false;
        try {
            return this.formattedCode().equals(other.formattedCode());
        } catch (FormatterException e) {
            return false;
        }
    }

    private String formattedCode() throws FormatterException {
        // Wrapping in a class is required for the google java code formatter.
        String wrappedSnippet = "class Wrapper {\n" + this.code + "\n}";
        String removedComments = wrappedSnippet.replaceAll("//.*|/\\*.*?\\*/", "");
        String removedEmptyLines = removedComments.replaceAll("(?m)^[ \t]*\r?\n", "");

        return new Formatter().formatSource(removedEmptyLines);
    }
    
    @Override
    public Snippet clone() {
        try {
            return (Snippet) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }


}
