package org.jetbrains.research.codecocoon.transformer.identifiers;

import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.SnippetWrapper;
import org.jetbrains.research.codecocoon.transformer.Transformation;
import org.jetbrains.research.codecocoon.transformer.Transformer;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class ParameterNameTransformerTest {

    SynonymGenerator mockSynonymGenerator = Mockito.mock(SynonymGenerator.class);
    Transformer transformer = new ParameterNameTransformer(mockSynonymGenerator);

    @Test
    public void testSimple() {
        String inputCode = """
                public int test(int a) {
                  return a;
                }""";
        String expectedCode = """
                public int test(int b) {
                  return b;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("a", inputCode, "parameter")).thenReturn("b");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.RenameParameters));
    }

    @Test
    public void testConflictWithOtherParameter1() {
        String inputCode = """
                public int test(int a, int b) {
                  return a;
                }""";
        String expectedCode = """
                public int test(int a, int c) {
                  return a;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("a", inputCode, "parameter")).thenReturn("b");
        when(mockSynonymGenerator.generateSynonymFor("b", inputCode, "parameter")).thenReturn("c");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.RenameParameters));
    }

    @Test
    public void testConflictWithOtherParameter2() {
        String inputCode = """
                public int test(int a, int b) {
                  return a;
                }""";
        String expectedCode = """
                public int test(int c, int b) {
                  return c;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("a", inputCode, "parameter")).thenReturn("c");
        when(mockSynonymGenerator.generateSynonymFor("b", inputCode, "parameter")).thenReturn("c");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.RenameParameters));
    }

    @Test
    public void testConflictWithOtherVariable1() {
        String inputCode = """
                public int test(int a) {
                  int b = 0;
                  return a;
                }""";
        String expectedCode = """
                public int test(int a) {
                  int b = 0;
                  return a;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("a", inputCode, "parameter")).thenReturn("b");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.RenameParameters));
    }

    @Test
    public void testReserved() {
        String inputCode = """
                public int test(int a) {
                  return a;
                }""";
        String expectedCode = """
                public int test(int a) {
                  return a;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("a", inputCode, "parameter")).thenReturn("new");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.RenameParameters));
    }
}
