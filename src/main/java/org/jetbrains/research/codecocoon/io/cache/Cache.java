package org.jetbrains.research.codecocoon.io.cache;

public interface Cache {

    boolean containsKey(String prompt);

    String get(String prompt);

    void put(String prompt, String value);
}
