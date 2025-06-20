package org.jetbrains.research.codecocoon.synonyms;

import org.jetbrains.research.codecocoon.llm.LLM;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

public class LLMSynonymGeneratorTest {

    @Test
    public void testSimple() {
        LLM mockLLM = Mockito.mock(LLM.class);
        SynonymGenerator generator = new LLMSynonymGenerator(mockLLM);

        String snippet = "public int test() {\n  int value = 1;\n  return value;\n}";
        String variable = "value";
        String synonym = "result";

        String prompt = "Generate a synonym for the variable \"value\" in the following code snippet: \n\npublic int test() {\n  int value = 1;\n  return value;\n}\nIf no good alternative is possible, return the original name.\nAnswer with only the new variable name.";

        when(mockLLM.query(prompt)).thenReturn(synonym);

        String result = generator.generateSynonymFor(variable, snippet, "variable");

        verify(mockLLM).query(prompt);
        assertEquals(synonym, result);
    }

    @Test
    public void testParameter() {
        LLM mockLLM = Mockito.mock(LLM.class);
        SynonymGenerator generator = new LLMSynonymGenerator(mockLLM);

        String snippet = "public int test() {\n  int value = 1;\n  return value;\n}";
        String variable = "value";
        String synonym = "result";

        String prompt = "Generate a synonym for the parameter \"value\" in the following code snippet: \n\npublic int test() {\n  int value = 1;\n  return value;\n}\nIf no good alternative is possible, return the original name.\nAnswer with only the new parameter name.";

        when(mockLLM.query(prompt)).thenReturn(synonym);

        String result = generator.generateSynonymFor(variable, snippet, "parameter");

        verify(mockLLM).query(prompt);
        assertEquals(synonym, result);
    }

    @Test
    public void testException() {
        LLM mockLLM = Mockito.mock(LLM.class);
        SynonymGenerator generator = new LLMSynonymGenerator(mockLLM);

        String snippet = "public int test() {\n  int value = 1;\n  return value;\n}";
        String variable = "value";

        String prompt = "Generate a synonym for the variable \"value\" in the following code snippet: \n\npublic int test() {\n  int value = 1;\n  return value;\n}\nIf no good alternative is possible, return the original name.\nAnswer with only the new variable name.";

        when(mockLLM.query(prompt)).thenThrow(new RuntimeException());

        String result = generator.generateSynonymFor(variable, snippet, "variable");

        verify(mockLLM).query(prompt);
        assertEquals(variable, result);
    }

    @Test
    public void testEquals() {
        LLM mockLLM = Mockito.mock(LLM.class);
        SynonymGenerator generator1 = new LLMSynonymGenerator(mockLLM);
        SynonymGenerator generator2 = new LLMSynonymGenerator(mockLLM);

        assertEquals(generator1, generator2);
    }

    @Test
    public void testNotEquals() {
        LLM mockLLM1 = Mockito.mock(LLM.class);
        LLM mockLLM2 = Mockito.mock(LLM.class);
        SynonymGenerator generator1 = new LLMSynonymGenerator(mockLLM1);
        SynonymGenerator generator2 = new LLMSynonymGenerator(mockLLM2);

        assertNotEquals(generator1, generator2);
    }

}
