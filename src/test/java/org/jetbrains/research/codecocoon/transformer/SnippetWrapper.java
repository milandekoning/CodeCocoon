package org.jetbrains.research.codecocoon.transformer;

import org.jetbrains.research.codecocoon.Snippet;

public class SnippetWrapper {

    public static Snippet wrap(String code) {
        return new Snippet("id", code);
    }
}
