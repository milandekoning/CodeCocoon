package org.jetbrains.research.codecocoon.transformer.identifiers;

import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.SnippetWrapper;
import org.jetbrains.research.codecocoon.transformer.Transformation;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class FunctionNameTransformerTest {

    SynonymGenerator mockSynonymGenerator = Mockito.mock(SynonymGenerator.class);
    FunctionNameTransformer transformer = new FunctionNameTransformer(mockSynonymGenerator);

    @Test
    public void testSimple() {
        String inputCode = """
                public int test() {
                  int value = 1;
                  return value;
                }""";
        String expectedCode = """
                public int test2() {
                  int value = 1;
                  return value;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("test", inputCode, "function")).thenReturn("test2");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.RenameFunction));
    }

    @Test
    public void testReserved() {
        String inputCode = """
                public int test() {
                  int value = 1;
                  return value;
                }""";
        String expectedCode = """
                public int test() {
                  int value = 1;
                  return value;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("test", inputCode, "function")).thenReturn("void");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.RenameFunction));
    }

    @Test
    public void testRecursive() {
        String inputCode = """
                public int test() {
                  int value = 1;
                  return test(value);
                }""";
        String expectedCode = """
                public int test2() {
                  int value = 1;
                  return test2(value);
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("test", inputCode, "function")).thenReturn("test2");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.RenameFunction));
    }

    @Test
    public void testOtherMethodCall() {
        String inputCode = """
                public int test() {
                  int value = chonk();
                  return test(value);
                }""";
        String expectedCode = """
                public int test2() {
                  int value = chonk();
                  return test2(value);
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("test", inputCode, "function")).thenReturn("test2");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.RenameFunction));
    }

    @Test
    public void testResultAlreadyUsed() {
        String inputCode = """
                public int test() {
                  int value = test2();
                  return test(value);
                }""";
        String expectedCode = """
                public int test() {
                  int value = test2();
                  return test(value);
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("test", inputCode, "function")).thenReturn("test2");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.RenameFunction));
    }

    @Test
    public void testRecursiveNested() {
        String inputCode = """
                public int test() {
                  int value = 1;
                  if (value == 1) {
                    return test(value);
                  }
                  return test(1);
                }""";
        String expectedCode = """
                public int test2() {
                  int value = 1;
                  if (value == 1) {
                    return test2(value);
                  }
                  return test2(1);
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("test", inputCode, "function")).thenReturn("test2");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.RenameFunction));
    }

    @Test
    public void testThis() {
        String inputCode = """
                public int test() {
                  int value = 1;
                  return this.test(value);
                }""";
        String expectedCode = """
                public int test2() {
                  int value = 1;
                  return this.test2(value);
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("test", inputCode, "function")).thenReturn("test2");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.RenameFunction));
    }

    @Test
    public void testObjectMethod() {
        String inputCode = """
                public String toString() {
                  return "Cheese";
                }""";
        String expectedCode = """
                public String toString() {
                  return "Cheese";
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.RenameFunction));
        verify(mockSynonymGenerator, times(0)).generateSynonymFor("toString", inputCode, "function");
    }
}
