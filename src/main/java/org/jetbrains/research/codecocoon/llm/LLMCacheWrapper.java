package org.jetbrains.research.codecocoon.llm;

import org.jetbrains.research.codecocoon.io.cache.Cache;

public class LLMCacheWrapper implements LLM {
    private final LLM llm;
    private final Cache cache;

    public LLMCacheWrapper(LLM llm, Cache cache) {
        this.llm = llm;
        this.cache = cache;
    }

    @Override
    public String query(String prompt) {
        if (cache.containsKey(prompt)) {
            return cache.get(prompt);
        }
        String response = llm.query(prompt);
        cache.put(prompt, response);
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LLMCacheWrapper otherCacheWrapper) {
            return llm.equals(otherCacheWrapper.llm);
        }
        return false;
    }
}
