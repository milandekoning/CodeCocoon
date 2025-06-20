package org.jetbrains.research.codecocoon.factory;

import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.*;
import org.jetbrains.research.codecocoon.transformer.fortowhile.ForToWhileTransformer;
import org.jetbrains.research.codecocoon.transformer.identifiers.FunctionNameTransformer;
import org.jetbrains.research.codecocoon.transformer.identifiers.ParameterNameTransformer;
import org.jetbrains.research.codecocoon.transformer.identifiers.VariableNameTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransformerFactory {
    private SynonymGenerator synonymGenerator;

    public TransformerFactory() {}

    public TransformerFactory(SynonymGenerator synonymGenerator) {
        this.synonymGenerator = synonymGenerator;
    }

    public Transformer createCompositeTransformerFrom(Map<String, Object> config) {
        List<Map<String, Object>> transformerConfigs = (List<Map<String, Object>>) config.get("transformers");

        if(transformerConfigs == null) {
            throw new RuntimeException("No transformers configured");
        }

        List<Transformer> transformers = new ArrayList<>();
        for (Map<String, Object> transformerConfig : transformerConfigs) {
            Transformer newTransformer = createSimpleTransformerFrom(transformerConfig);
            transformers.add(newTransformer);
        }

        return new CompositeTransformer(transformers);
    }

    public Transformer createSimpleTransformerFrom(Map<String, Object> config) {
        String transformerName = (String) config.get("name");

        return switch (transformerName) {
            case "forToWhileTransformer" -> new ForToWhileTransformer();
            case "nestElseIfTransformer" -> new NestElseIfTransformer();
            case "reverseIfTransformer" -> new ReverseIfTransformer();
            case "swapRelationOperandsTransformer" -> new SwapRelationOperandsTransformer();
            case "expandUnaryIncrementTransformer" -> new ExpandUnaryIncrementTransformer();
            case "swapEqualsOperandsTransformer" -> new SwapEqualsOperandsTransformer();
            case "variableNameTransformer" -> createVariableNameTransformer();
            case "functionNameTransformer" -> createFunctionNameTransformer();
            case "parameterNameTransformer" -> createParameterNameTransformer();
            default -> throw new RuntimeException("Unknown transformer: " + transformerName);
        };
    }

    private VariableNameTransformer createVariableNameTransformer() {
        if (this.synonymGenerator == null) throw new RuntimeException("No synonym generator configured");
        return new VariableNameTransformer(synonymGenerator);
    }

    private FunctionNameTransformer createFunctionNameTransformer() {
        if (this.synonymGenerator == null) throw new RuntimeException("No synonym generator configured");
        return new FunctionNameTransformer(synonymGenerator);
    }

    private ParameterNameTransformer createParameterNameTransformer() {
        if (this.synonymGenerator == null) throw new RuntimeException("No synonym generator configured");
        return new ParameterNameTransformer(synonymGenerator);
    }

}
