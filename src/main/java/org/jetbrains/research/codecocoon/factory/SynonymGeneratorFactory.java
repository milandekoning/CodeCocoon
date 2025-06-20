package org.jetbrains.research.codecocoon.factory;

import org.jetbrains.research.codecocoon.llm.LLM;
import org.jetbrains.research.codecocoon.synonyms.LLMSynonymGenerator;
import org.jetbrains.research.codecocoon.synonyms.LexicalSynonymGenerator;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;

import java.util.Map;
import java.util.Optional;

public class SynonymGeneratorFactory {
    private final LLMFactory llmFactory;

    public SynonymGeneratorFactory(LLMFactory llmFactory) {
        this.llmFactory = llmFactory;
    }

    public Optional<SynonymGenerator> createSynonymGeneratorFrom(Map<String, Object> config) {
        if (!config.containsKey("synonymGenerator")) return Optional.empty();

        Map<String, Object> synonymGeneratorConfig = (Map<String, Object>) config.get("synonymGenerator");

        return Optional.of(createFrom(synonymGeneratorConfig));
    }

    private SynonymGenerator createFrom(Map<String, Object> config) {
        String synonymGeneratorName = (String) config.get("name");

        switch (synonymGeneratorName) {
            case "llmSynonymGenerator":
                return createLLMSynonymGeneratorFrom(config);
            case "lexicalSynonymGenerator":
                return createLexicalSynonymGenerator();
        }
        throw new IllegalArgumentException("Unknown Synonym Generator: " + synonymGeneratorName);
    }

    private LLMSynonymGenerator createLLMSynonymGeneratorFrom(Map<String, Object> config) {
        Map<String, Object> llmConfig = (Map<String, Object>) config.get("llm");
        LLM llm = llmFactory.createLLMFrom(llmConfig);
        return new LLMSynonymGenerator(llm);
    }


    private LexicalSynonymGenerator createLexicalSynonymGenerator() {
        try {
            return new LexicalSynonymGenerator();
        } catch (Exception e) {
            throw new IllegalArgumentException("Problem with Synonym Generator: " + e);
        }
    }
}
