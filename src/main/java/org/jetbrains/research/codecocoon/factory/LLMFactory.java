package org.jetbrains.research.codecocoon.factory;

import org.jetbrains.research.codecocoon.io.cache.Cache;
import org.jetbrains.research.codecocoon.llm.LLM;
import org.jetbrains.research.codecocoon.llm.LLMCacheWrapper;
import org.jetbrains.research.codecocoon.llm.OpenAI;

import java.util.Map;

public class LLMFactory {
    CacheFactory cacheFactory;

    public LLMFactory(CacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    public LLM createLLMFrom(Map<String, Object> config) {
        String provider = (String) config.get("provider");
        String model = (String) config.get("model");
        String cachePath = (String) config.get("cachePath");

        LLM llm = createLLMFrom(provider, model);

        if(cachePath != null) {
            Cache cache = cacheFactory.create(cachePath);
            return new LLMCacheWrapper(llm, cache);
        }

        return llm;
    }

    private LLM createLLMFrom(String provider, String model) {
        switch (provider) {
            case "openai":
                return new OpenAI(model);
        }
        throw new IllegalArgumentException("Unknown LLM provider: " + model);
    }
}
