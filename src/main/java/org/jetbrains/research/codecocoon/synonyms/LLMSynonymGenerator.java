package org.jetbrains.research.codecocoon.synonyms;

import org.jetbrains.research.codecocoon.llm.LLM;

import java.text.MessageFormat;

public class LLMSynonymGenerator implements SynonymGenerator {
    private final String promptTemplate = "Generate a synonym for the {0} \"{1}\" in the following code snippet: \n\n{2}\nIf no good alternative is possible, return the original name.\nAnswer with only the new {0} name.";
    private final LLM llm;

    public LLMSynonymGenerator(LLM llm) {
        this.llm = llm;
    }

    @Override
    public String generateSynonymFor(String identifierName, String context, String identifierType) {
        String prompt = generatePrompt(identifierName, context, identifierType);
        try {
            String synonym = llm.query(prompt);
            return synonym;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return identifierName;
    }

    private String generatePrompt(String variableName, String context, String identifierType) {
        return MessageFormat.format(promptTemplate, identifierType, variableName, context);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LLMSynonymGenerator otherSynonymGenerator) {
            return this.llm.equals(otherSynonymGenerator.llm);
        }
        return false;
    }
}
