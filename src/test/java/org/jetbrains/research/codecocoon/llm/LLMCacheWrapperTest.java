package org.jetbrains.research.codecocoon.llm;

import org.jetbrains.research.codecocoon.io.cache.Cache;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

public class LLMCacheWrapperTest {

    @Test
    public void testNotInCache() {
        LLM mockLLM = Mockito.mock(LLM.class);
        Cache mockCache = Mockito.mock(Cache.class);
        LLM llmCacheWrapper = new LLMCacheWrapper(mockLLM, mockCache);

        String prompt = "Generate a synonym for variable \"value\" in the following code snippet: \n\npublic int test() {\n  int value = 1;\n  return value;\n}\nIf no good alternative is possible, return the original name.\nAnswer with only the new variable name.";
        String synonym = "result";

        when(mockLLM.query(prompt)).thenReturn(synonym);

        String result = llmCacheWrapper.query(prompt);

        verify(mockLLM).query(prompt);
        verify(mockCache).containsKey(prompt);
        verify(mockCache).put(prompt, synonym);
        assertEquals(synonym, result);
    }

    @Test
    public void testInCache() {
        LLM mockLLM = Mockito.mock(LLM.class);
        Cache mockCache = Mockito.mock(Cache.class);
        LLM llmCacheWrapper = new LLMCacheWrapper(mockLLM, mockCache);

        String prompt = "Generate a synonym for variable \"value\" in the following code snippet: \n\npublic int test() {\n  int value = 1;\n  return value;\n}\nIf no good alternative is possible, return the original name.\nAnswer with only the new variable name.";
        String synonym = "result";

        when(mockCache.containsKey(prompt)).thenReturn(true);
        when(mockCache.get(prompt)).thenReturn(synonym);

        String result = llmCacheWrapper.query(prompt);

        verify(mockLLM, times(0)).query(prompt);
        verify(mockCache).containsKey(prompt);
        verify(mockCache, times(0)).put(prompt, synonym);
        assertEquals(synonym, result);
    }

    @Test
    public void testEquals() {
        LLM mockLLM = Mockito.mock(LLM.class);
        Cache mockCache = Mockito.mock(Cache.class);
        LLM llmCacheWrapper1 = new LLMCacheWrapper(mockLLM, mockCache);
        LLM llmCacheWrapper2 = new LLMCacheWrapper(mockLLM, mockCache);

        assertEquals(llmCacheWrapper1, llmCacheWrapper2);
    }

    @Test
    public void testNotEquals() {
        LLM mockLLM1 = Mockito.mock(LLM.class);
        LLM mockLLM2 = Mockito.mock(LLM.class);
        Cache mockCache = Mockito.mock(Cache.class);
        LLM llmCacheWrapper1 = new LLMCacheWrapper(mockLLM1, mockCache);
        LLM llmCacheWrapper2 = new LLMCacheWrapper(mockLLM2, mockCache);

        assertNotEquals(llmCacheWrapper1, llmCacheWrapper2);
    }
}
