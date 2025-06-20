package org.jetbrains.research.codecocoon.transformer;

import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.fortowhile.ForToWhileTransformer;
import org.jetbrains.research.codecocoon.transformer.identifiers.VariableNameTransformer;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

public class CompositeTransformerTest {

    @Test
    public void testNoTransformers() {
        Transformer compositeTransformer = new CompositeTransformer(new ArrayList<>());

        String inputCode =  """
                public int test() {
                  int value = 1;
                  return value;
                }""";
        String expectedCode =  """
                public int test() {
                  int value = 1;
                  return value;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        compositeTransformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testOneTransformer() {
        List<Transformer> transformers = List.of(
                new ForToWhileTransformer()
        );
        Transformer compositeTransformer = new CompositeTransformer(transformers);

        String inputCode =  """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++) {
                    total += values[i];
                  }
                  return total;
                }""";
        String expectedCode =  """
                public int sum(int[] values) {
                  int total = 0;
                  int i = 0;
                  while (i < values.length) {
                    total += values[i];
                    i++;
                  }
                  return total;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        compositeTransformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testTwoTransformers() {
        List<Transformer> transformers = List.of(
                new ForToWhileTransformer(),
                new NestElseIfTransformer()
        );
        Transformer compositeTransformer = new CompositeTransformer(transformers);

        String inputCode =  """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++) {
                    if (i < 6) {
                      total += values[i];
                    } else if (i > 9) {
                      total -= values[i];
                    }
                  }
                  return total;
                }""";
        String expectedCode =  """
                public int sum(int[] values) {
                  int total = 0;
                  int i = 0;
                  while (i < values.length) {
                    if (i < 6) {
                      total += values[i];
                    } else {
                      if (i > 9) {
                        total -= values[i];
                      }
                    }
                    i++;
                  }
                  return total;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        compositeTransformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testThreeTransformers() {
        List<Transformer> transformers = List.of(
                new ForToWhileTransformer(),
                new NestElseIfTransformer(),
                new ReverseIfTransformer()
        );
        Transformer compositeTransformer = new CompositeTransformer(transformers);

        String inputCode =  """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++) {
                    if (i < 6) {
                      total += values[i];
                    } else if (i > 9) {
                      total -= values[i];
                    }
                  }
                  return total;
                }""";
        String expectedCode =  """
                public int sum(int[] values) {
                  int total = 0;
                  int i = 0;
                  while (i < values.length) {
                    if (i >= 6) {
                      if (i > 9) {
                        total -= values[i];
                      }
                    } else {
                      total += values[i];
                    }
                    i++;
                  }
                  return total;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        compositeTransformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testFourTransformers() {
        SynonymGenerator mockSynonymGenerator = Mockito.mock(SynonymGenerator.class);
        List<Transformer> transformers = List.of(
                new VariableNameTransformer(mockSynonymGenerator),
                new ForToWhileTransformer(),
                new NestElseIfTransformer(),
                new ReverseIfTransformer()
        );
        Transformer compositeTransformer = new CompositeTransformer(transformers);

        String inputCode =  """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++) {
                    if (i < 6) {
                      total += values[i];
                    } else if (i > 9) {
                      total -= values[i];
                    }
                  }
                  return total;
                }""";
        String expectedCode =  """
                public int sum(int[] values) {
                  int aggregate = 0;
                  int i = 0;
                  while (i < values.length) {
                    if (i >= 6) {
                      if (i > 9) {
                        aggregate -= values[i];
                      }
                    } else {
                      aggregate += values[i];
                    }
                    i++;
                  }
                  return aggregate;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("total", inputCode, "variable")).thenReturn("aggregate");

        compositeTransformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testEquals() {
        Transformer compositeTransformer1 = new CompositeTransformer(List.of(new ForToWhileTransformer()));
        Transformer compositeTransformer2 = new CompositeTransformer(List.of(new ForToWhileTransformer()));

        assertEquals(compositeTransformer1, compositeTransformer2);
    }

    @Test
    public void testNotEquals1() {
        Transformer compositeTransformer1 = new CompositeTransformer(List.of(new ForToWhileTransformer()));
        Transformer compositeTransformer2 = new CompositeTransformer(List.of(new NestElseIfTransformer()));

        assertNotEquals(compositeTransformer1, compositeTransformer2);
    }

    @Test
    public void testNotEquals2() {
        Transformer compositeTransformer1 = new CompositeTransformer(List.of(new ForToWhileTransformer()));
        Transformer compositeTransformer2 = new CompositeTransformer(new ArrayList<>());

        assertNotEquals(compositeTransformer1, compositeTransformer2);
    }
}
