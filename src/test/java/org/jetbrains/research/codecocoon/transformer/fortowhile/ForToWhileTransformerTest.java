package org.jetbrains.research.codecocoon.transformer.fortowhile;

import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.transformer.SnippetWrapper;
import org.jetbrains.research.codecocoon.transformer.Transformation;
import org.jetbrains.research.codecocoon.transformer.Transformer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ForToWhileTransformerTest {

    Transformer transformer = new ForToWhileTransformer();

    @Test
    public void testSimple() {
        String inputCode = """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++) {
                    total += values[i];
                  }
                  return total;
                }""";
        String expectedCode = """
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

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ForToWhile));
    }

    @Test
    public void testMultipleForStatements() {
        String inputCode = """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++) {
                    total += values[i];
                  }
                  for (int j = 0; j < values.length; j++) {
                    total += values[j];
                  }
                  return total;
                }""";
        String expectedCode = """
                public int sum(int[] values) {
                  int total = 0;
                  int i = 0;
                  while (i < values.length) {
                    total += values[i];
                    i++;
                  }
                  int j = 0;
                  while (j < values.length) {
                    total += values[j];
                    j++;
                  }
                  return total;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(2), snippet.getTransformations().get(Transformation.ForToWhile));
    }

    @Test
    public void testShadowing() {
        String inputCode = """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++) {
                    total += values[i];
                  }
                  for (int i = 0; i < values.length; i++) {
                    total += values[i];
                  }
                  return total;
                }""";
        String expectedCode = """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++) {
                    total += values[i];
                  }
                  for (int i = 0; i < values.length; i++) {
                    total += values[i];
                  }
                  return total;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.ForToWhile));
    }

    @Test
    public void testMultipleInitializations() {
        String inputCode = """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0, j = 0; i < values.length; i++) {
                    total += values[i];
                  }
                  return total;
                }""";
        String expectedCode = """
                public int sum(int[] values) {
                  int total = 0;
                  int i = 0, j = 0;
                  while (i < values.length) {
                    total += values[i];
                    i++;
                  }
                  return total;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testMultipleUpdates() {
        String inputCode = """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++, i--) {
                    total += values[i];
                  }
                  return total;
                }""";
        String expectedCode = """
                public int sum(int[] values) {
                  int total = 0;
                  int i = 0;
                  while (i < values.length) {
                    total += values[i];
                    i++;
                    i--;
                  }
                  return total;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testEmptyCompare() {
        String inputCode = """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0;; i++) {
                    total += values[i];
                  }
                  return total;
                }""";
        String expectedCode = """
                public int sum(int[] values) {
                  int total = 0;
                  int i = 0;
                  while (true) {
                    total += values[i];
                    i++;
                  }
                  return total;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testNonBlockBody() {
        String inputCode = """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++) total += values[i];
                  return total;
                }""";
        String expectedCode = """
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

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testNested() {
        String inputCode = """
                public int sum(int[] values) {
                  int total = 0;
                  for (int i = 0; i < values.length; i++) {
                    for (int j = 0; j < values.length; j++) {
                      total += values[j];
                    }
                  }
                  return total;
                }""";
        String expectedCode = """
                public int sum(int[] values) {
                  int total = 0;
                  int i = 0;
                  while (i < values.length) {
                    int j = 0;
                    while (j < values.length) {
                      total += values[j];
                      j++;
                    }
                    i++;
                  }
                  return total;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(2), snippet.getTransformations().get(Transformation.ForToWhile));
    }

    @Test
    public void testContinue() {
        String inputCode = """
                void test() {
                    for (int i = 0; i < 10; i++) {
                        if (i == 3) {
                            continue;
                        }
                    }
                }""";
        String expectedCode = """
                void test() {
                    int i = 0;
                    while (i < 10) {
                       if (i == 3) {
                            i++;
                            continue;
                        }
                        i++;
                    }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testReal() {
        String inputCode = """
                private String format(JSError error, boolean warning) {
                  // extract source excerpt
                  SourceExcerptProvider source = getSource();
                  String sourceExcerpt = source == null ? null :
                      excerpt.get(
                          source, error.sourceName, error.lineNumber, excerptFormatter);
                
                  // formatting the message
                  StringBuilder b = new StringBuilder();
                  if (error.sourceName != null) {
                    b.append(error.sourceName);
                    if (error.lineNumber > 0) {
                      b.append(':');
                      b.append(error.lineNumber);
                    }
                    b.append(": ");
                  }
                
                  b.append(getLevelName(warning ? CheckLevel.WARNING : CheckLevel.ERROR));
                  b.append(" - ");
                
                  b.append(error.description);
                  b.append('\\n');
                  if (sourceExcerpt != null) {
                    b.append(sourceExcerpt);
                    b.append('\\n');
                    int charno = error.getCharno();
                
                    // padding equal to the excerpt and arrow at the end
                    // charno == sourceExpert.length() means something is missing
                    // at the end of the line
                    if (excerpt.equals(LINE)
                        && 0 <= charno && charno < sourceExcerpt.length()) {
                      for (int i = 0; i < charno; i++) {
                        char c = sourceExcerpt.charAt(i);
                        if (Character.isWhitespace(c)) {
                          b.append(c);
                        } else {
                          b.append(' ');
                        }
                      }
                      b.append("^\\n");
                    }
                  }
                  return b.toString();
                }""";
        String expectedCode = """
                private String format(JSError error, boolean warning) {
                  // extract source excerpt
                  SourceExcerptProvider source = getSource();
                  String sourceExcerpt = source == null ? null :
                      excerpt.get(
                          source, error.sourceName, error.lineNumber, excerptFormatter);
                
                  // formatting the message
                  StringBuilder b = new StringBuilder();
                  if (error.sourceName != null) {
                    b.append(error.sourceName);
                    if (error.lineNumber > 0) {
                      b.append(':');
                      b.append(error.lineNumber);
                    }
                    b.append(": ");
                  }
                
                  b.append(getLevelName(warning ? CheckLevel.WARNING : CheckLevel.ERROR));
                  b.append(" - ");
                
                  b.append(error.description);
                  b.append('\\n');
                  if (sourceExcerpt != null) {
                    b.append(sourceExcerpt);
                    b.append('\\n');
                    int charno = error.getCharno();
                
                    // padding equal to the excerpt and arrow at the end
                    // charno == sourceExpert.length() means something is missing
                    // at the end of the line
                    if (excerpt.equals(LINE)
                        && 0 <= charno && charno < sourceExcerpt.length()) {
                      int i = 0;
                      while (i < charno) {
                        char c = sourceExcerpt.charAt(i);
                        if (Character.isWhitespace(c)) {
                          b.append(c);
                        } else {
                          b.append(' ');
                        }
                      i++;
                      }
                      b.append("^\\n");
                    }
                  }
                  return b.toString();
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ForToWhile));
    }

}
