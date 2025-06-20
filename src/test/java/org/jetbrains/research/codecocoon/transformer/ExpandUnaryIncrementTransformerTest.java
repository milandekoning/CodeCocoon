package org.jetbrains.research.codecocoon.transformer;

import org.jetbrains.research.codecocoon.Snippet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class ExpandUnaryIncrementTransformerTest {

    Transformer transformer = new ExpandUnaryIncrementTransformer();

    @Test
    public void testIncrement() {
        String inputCode = """
                public int increment(int i) {
                  i++;
                  return i;
                }""";
        String expectedCode = """
                public int increment(int i) {
                  i += 1;
                  return i;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ExpandUnaryIncrement));
    }

    @Test
    public void testDecrement() {
        String inputCode = """
                public int increment(int i) {
                  i--;
                  return i;
                }""";
        String expectedCode = """
                public int increment(int i) {
                  i -= 1;
                  return i;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ExpandUnaryIncrement));
    }

    @Test
    public void testNotApplicable() {
        String inputCode = """
                public boolean increment(boolean b) {
                  b = !b;
                  return b;
                }""";
        String expectedCode = """
                public boolean increment(boolean b) {
                  b = !b;
                  return b;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.ExpandUnaryIncrement));
    }

    @Test
    public void testInExpression() {
        String inputCode = """
                public int increment(int i) {
                  if (i++ > 1) {
                    return i;
                  }
                  return 0;}""";
        String expectedCode = """
                public int increment(int i) {
                  if (i++ > 1) {
                    return i;
                  }
                  return 0;}""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.ExpandUnaryIncrement));
    }

    @Test
    public void testReal() {
        String inputCode = """
                public int read() throws IOException {
                    int current = super.read();
                    if (current == '\\n') {
                        lineCounter++;
                    }
                    lastChar = current;
                    return lastChar;
                }""";
        String expectedCode = """
                public int read() throws IOException {
                    int current = super.read();
                    if (current == '\\n') {
                        lineCounter += 1;
                    }
                    lastChar = current;
                    return lastChar;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ExpandUnaryIncrement));
    }

    @Test
    public void testRealProblem() {
        String inputCode = """
                public static boolean verifyCheckSum(byte[] header) {
                    long storedSum = 0;
                    long unsignedSum = 0;
                    long signedSum = 0;
                
                    int digits = 0;
                    for (int i = 0; i < header.length; i++) {
                        byte b = header[i];
                        if (CHKSUM_OFFSET  <= i && i < CHKSUM_OFFSET + CHKSUMLEN) {
                            if ('0' <= b && b <= '7' && digits++ < 6) {
                                storedSum = storedSum * 8 + b - '0';
                            } else if (digits > 0) {
                                digits = 6;
                            }
                            b = ' ';
                        }
                        unsignedSum += 0xff & b;
                        signedSum += b;
                    }
                    return storedSum == unsignedSum || storedSum == signedSum;
                }""";
        String expectedCode = """
                public static boolean verifyCheckSum(byte[] header) {
                    long storedSum = 0;
                    long unsignedSum = 0;
                    long signedSum = 0;
                
                    int digits = 0;
                    for (int i = 0; i < header.length; i += 1) {
                        byte b = header[i];
                        if (CHKSUM_OFFSET  <= i && i < CHKSUM_OFFSET + CHKSUMLEN) {
                            if ('0' <= b && b <= '7' && digits++ < 6) {
                                storedSum = storedSum * 8 + b - '0';
                            } else if (digits > 0) {
                                digits = 6;
                            }
                            b = ' ';
                        }
                        unsignedSum += 0xff & b;
                        signedSum += b;
                    }
                    return storedSum == unsignedSum || storedSum == signedSum;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.ExpandUnaryIncrement));
    }


}
