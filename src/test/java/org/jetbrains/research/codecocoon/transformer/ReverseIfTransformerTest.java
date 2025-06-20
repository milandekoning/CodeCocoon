package org.jetbrains.research.codecocoon.transformer;

import org.jetbrains.research.codecocoon.Snippet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ReverseIfTransformerTest {

    Transformer transformer = new ReverseIfTransformer();

    @Test
    public void testSimple() {
        String inputCode = """
                public int test() {
                  if (true) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test() {
                  if (false) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }


    @Test
    public void testBinaryEquality() {
        String inputCode = """
                public int test(int i) {
                  if (i == 1) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (i != 1) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testBinaryInEquality() {
        String inputCode = """
                public int test(int i) {
                  if (i != 1) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (i == 1) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testBinaryAnd() {
        String inputCode = """
                public int test() {
                  if (true && false) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test() {
                  if (false || true) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testBinaryOr() {
        String inputCode = """
                public int test(int i) {
                  if (true || false) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (false && true) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testGreater() {
        String inputCode = """
                public int test(int i) {
                  if (i > 0) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (i <= 0) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testGreaterEquals() {
        String inputCode = """
                public int test(int i) {
                  if (i >= 0) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (i < 0) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testLess() {
        String inputCode = """
                public int test(int i) {
                  if (i < 0) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (i >= 0) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testLessEquals() {
        String inputCode = """
                public int test(int i) {
                  if (i <= 0) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (i > 0) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testNegation() {
        String inputCode = """
                public int test(boolean maybe) {
                  if (!maybe) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(boolean maybe) {
                  if (maybe) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testVariable() {
        String inputCode = """
                public int test(boolean maybe) {
                  if (maybe) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(boolean maybe) {
                  if (!maybe) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testMethod() {
        String inputCode = """
                public int test(Thing maybe) {
                  if (maybe.yes()) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(Thing maybe) {
                  if (!maybe.yes()) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testUnknown() {
        String inputCode = """
                public int test(Thing maybe) {
                  if (maybe ? true : false) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(Thing maybe) {
                  if (!(maybe ? true : false)) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testEnclosed() {
        String inputCode = """
                public int test(Thing maybe) {
                  if ((true)) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(Thing maybe) {
                  if (!(true)) {
                    return 2;
                  } else {
                    return 1;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

    @Test
    public void testNoElse() {
        String inputCode = """
                public int test() {
                  if (true) {
                    return 1;
                  }
                 return 2;
                }""";
        String expectedCode = """
                public int test() {
                  if (true) {
                    return 1;
                  }
                 return 2;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.ReverseIf));
    }

    @Test
    public void testElseIf() {
        String inputCode = """
                public int test() {
                  if (true) {
                    return 1;
                  } else if (false) {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test() {
                  if (true) {
                    return 1;
                  } else if (false) {
                    return 2;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testPrecedence() {
        String inputCode = """
                public int test(boolean a, boolean b, boolean c, boolean d) {
                  if (a && b || c && d) {
                    return 1;
                  } else {
                    return 2;
                  }
                }""";
        String expectedCode = """
                public int test(boolean a, boolean b, boolean c, boolean d) {
                  if ((!a || !b) && (!c || !d)) {
                    return 2;
                  } else {
                    return 1;
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
                public String getLine(int lineNumber) {
                  String js = "";
                  try {
                    // NOTE(nicksantos): Right now, this is optimized for few warnings.
                    // This is probably the right trade-off, but will be slow if there
                    // are lots of warnings in one file.
                    js = getCode();
                  } catch (IOException e) {
                    return null;
                  }
                
                  int pos = 0;
                  int startLine = 1;
                
                  // If we've saved a previous offset and it's for a line less than the
                  // one we're searching for, then start at that point.
                  if (lineNumber >= lastLine) {
                    pos = lastOffset;
                    startLine = lastLine;
                  }
                
                  for (int n = startLine; n < lineNumber; n++) {
                    int nextpos = js.indexOf('\\n', pos);
                    if (nextpos == -1) {
                      return null;
                    }
                    pos = nextpos + 1;
                  }
                
                  // Remember this offset for the next search we do.
                  lastOffset = pos;
                  lastLine = lineNumber;
                
                  if (js.indexOf('\\n', pos) == -1) {
                    // If next new line cannot be found, there are two cases
                    // 1. pos already reaches the end of file, then null should be returned
                    // 2. otherwise, return the contents between pos and the end of file.
                      return null;
                  } else {
                    return js.substring(pos, js.indexOf('\\n', pos));
                  }
                }""";
        String expectedCode = """
                public String getLine(int lineNumber) {
                  String js = "";
                  try {
                    // NOTE(nicksantos): Right now, this is optimized for few warnings.
                    // This is probably the right trade-off, but will be slow if there
                    // are lots of warnings in one file.
                    js = getCode();
                  } catch (IOException e) {
                    return null;
                  }
                
                  int pos = 0;
                  int startLine = 1;
                
                  // If we've saved a previous offset and it's for a line less than the
                  // one we're searching for, then start at that point.
                  if (lineNumber >= lastLine) {
                    pos = lastOffset;
                    startLine = lastLine;
                  }
                
                  for (int n = startLine; n < lineNumber; n++) {
                    int nextpos = js.indexOf('\\n', pos);
                    if (nextpos == -1) {
                      return null;
                    }
                    pos = nextpos + 1;
                  }
                
                  // Remember this offset for the next search we do.
                  lastOffset = pos;
                  lastLine = lineNumber;
                
                  if (js.indexOf('\\n', pos) != -1) {
                    return js.substring(pos, js.indexOf('\\n', pos));
                  } else {
                    // If next new line cannot be found, there are two cases
                    // 1. pos already reaches the end of file, then null should be returned
                    // 2. otherwise, return the contents between pos and the end of file.
                      return null;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ReverseIf));
    }

}
