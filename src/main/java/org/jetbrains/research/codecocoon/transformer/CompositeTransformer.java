package org.jetbrains.research.codecocoon.transformer;

import org.jetbrains.research.codecocoon.Snippet;

import java.util.List;

public class CompositeTransformer extends Transformer {
    private final List<Transformer> transformers;

    public CompositeTransformer(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    @Override
    public void transform(Snippet snippet) {
        for (Transformer transformer : transformers) {
            transformer.transform(snippet);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof CompositeTransformer otherCompositeTransformer) {
            return transformers.equals(otherCompositeTransformer.transformers);
        }
        return false;
    }

    @Override
    public String toString() {
        return "CompositeTransformer(" + transformers.toString() + ")";
    }
}
