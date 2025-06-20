package org.jetbrains.research.codecocoon.llm;

public class LLMUnavailableException extends RuntimeException {
    public LLMUnavailableException(String s) {
        super(s);
    }
}
