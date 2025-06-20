package org.jetbrains.research.codecocoon.synonyms;

public interface SynonymGenerator {

    String generateSynonymFor(String identifierName, String context, String identifierType);
}
