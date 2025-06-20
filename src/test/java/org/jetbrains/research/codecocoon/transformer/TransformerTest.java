package org.jetbrains.research.codecocoon.transformer;

import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.io.SnippetIO;
import org.jetbrains.research.codecocoon.transformer.fortowhile.ForToWhileTransformer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;


import static org.junit.Assert.*;


public class TransformerTest {

    Transformer transformer = new MockTransformer();

    /**
     * Tests whether the inputs are parsed and reconstructed by JavaParser correctly.
     * The testing data is single function defects4j dataset. The following functions were removed because they resulted
     * in semantically equivalent outputs, but could not be easily compared.
     * - Codec-6 and Math-16 contain an array declaration formatted as int arr[], while JavaParser reconstructs this as int[] arr.
     * - Math-44 and Math-7 contain an inherited javadoc comment that is reconstructed slightly differently but is still equivalent.
     * - Mockito-32 contains a comment within a string, which is messed up by the comment remover.
     */
    @Test
    public void testParsingAndReconstructing() throws FileNotFoundException, URISyntaxException {
        String datasetPath = getClass().getClassLoader().getResource("defects4j-sf-updated.json").toURI().getPath();
        List<Snippet> snippets = SnippetIO.loadSnippets(datasetPath);

        for (Snippet snippet : snippets) {
            Snippet clone = snippet.clone();
            transformer.transform(clone);

            assertEquals(snippet, clone);
        }
    }

    @Test
    public void testEmpty() {
        assertThrows(IllegalArgumentException.class, () -> transformer.transform(SnippetWrapper.wrap("")));
    }

    @Test
    public void testIllegal() {
        assertThrows(IllegalArgumentException.class, () -> transformer.transform(SnippetWrapper.wrap("void")));
    }

    @Test
    public void testEquals() {
        assertEquals(new ForToWhileTransformer(), new ForToWhileTransformer());
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(new ForToWhileTransformer(), new NestElseIfTransformerTest());
    }

}
