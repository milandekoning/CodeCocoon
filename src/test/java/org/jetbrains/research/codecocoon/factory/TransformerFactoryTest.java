package org.jetbrains.research.codecocoon.factory;

import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.*;
import org.jetbrains.research.codecocoon.transformer.fortowhile.ForToWhileTransformer;
import org.jetbrains.research.codecocoon.transformer.identifiers.FunctionNameTransformer;
import org.jetbrains.research.codecocoon.transformer.identifiers.ParameterNameTransformer;
import org.jetbrains.research.codecocoon.transformer.identifiers.VariableNameTransformer;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


public class TransformerFactoryTest {

    SynonymGenerator mockSynonymGenerator = Mockito.mock(SynonymGenerator.class);
    TransformerFactory factory = new TransformerFactory(mockSynonymGenerator);

    @Test
    public void testNoTransformerKey() {
        Map<String, Object> config = new HashMap<>();

        assertThrows(RuntimeException.class, () -> {factory.createCompositeTransformerFrom(config);});
    }

    @Test
    public void testCreateSimpleCompositeTransformer() {
        Map<String, Object> config = createTransformerConfigs(List.of("forToWhileTransformer"));

        Transformer expected = new CompositeTransformer(List.of(new ForToWhileTransformer()));

        Transformer actual = factory.createCompositeTransformerFrom(config);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateCompositeTransformer() {
        Map<String, Object> config = createTransformerConfigs(List.of("forToWhileTransformer", "nestElseIfTransformer", "reverseIfTransformer"));

        Transformer expected = new CompositeTransformer(List.of(new ForToWhileTransformer(), new NestElseIfTransformer(), new ReverseIfTransformer()));

        Transformer actual = factory.createCompositeTransformerFrom(config);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateForToWhileTransformer() {
        Map<String, Object> config = createTransformerConfig("forToWhileTransformer");

        Transformer actual = factory.createSimpleTransformerFrom(config);

        assertEquals(new ForToWhileTransformer(), actual);
    }

    @Test
    public void testCreateNestElseIfTransformer() {
        Map<String, Object> config = createTransformerConfig("nestElseIfTransformer");

        Transformer actual = factory.createSimpleTransformerFrom(config);

        assertEquals(new NestElseIfTransformer(), actual);
    }

    @Test
    public void testCreateReverseIfTransformer() {
        Map<String, Object> config = createTransformerConfig("reverseIfTransformer");

        Transformer actual = factory.createSimpleTransformerFrom(config);

        assertEquals(new ReverseIfTransformer(), actual);
    }

    @Test
    public void testSwapRelationOperandsTransformer() {
        Map<String, Object> config = createTransformerConfig("swapRelationOperandsTransformer");

        Transformer actual = factory.createSimpleTransformerFrom(config);

        assertEquals(new SwapRelationOperandsTransformer(), actual);
    }

    @Test
    public void testExpandUnaryIncrementTransformer() {
        Map<String, Object> config = createTransformerConfig("expandUnaryIncrementTransformer");

        Transformer actual = factory.createSimpleTransformerFrom(config);

        assertEquals(new ExpandUnaryIncrementTransformer(), actual);
    }

    @Test
    public void testSwapEqualsOperandsTransformer() {
        Map<String, Object> config = createTransformerConfig("swapEqualsOperandsTransformer");

        Transformer actual = factory.createSimpleTransformerFrom(config);

        assertEquals(new SwapEqualsOperandsTransformer(), actual);
    }

    @Test
    public void testCreateVariableNameTransformer() {
        Map<String, Object> transformerConfig = new HashMap<>();
        transformerConfig.put("name", "variableNameTransformer");

        Transformer expected = new VariableNameTransformer(mockSynonymGenerator);

        Transformer actual = factory.createSimpleTransformerFrom(transformerConfig);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateFunctionNameTransformer() {
        Map<String, Object> transformerConfig = new HashMap<>();
        transformerConfig.put("name", "functionNameTransformer");

        Transformer expected = new FunctionNameTransformer(mockSynonymGenerator);

        Transformer actual = factory.createSimpleTransformerFrom(transformerConfig);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateParameterNameTransformer() {
        Map<String, Object> transformerConfig = new HashMap<>();
        transformerConfig.put("name", "parameterNameTransformer");

        Transformer expected = new ParameterNameTransformer(mockSynonymGenerator);

        Transformer actual = factory.createSimpleTransformerFrom(transformerConfig);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateVariableNameTransformerWithoutSynonymGenerator() {
        Map<String, Object> transformerConfig = new HashMap<>();
        transformerConfig.put("name", "variableNameTransformer");

        assertThrows(RuntimeException.class, () -> new TransformerFactory().createSimpleTransformerFrom(transformerConfig));
    }

    private Map<String, Object> createTransformerConfigs(List<String> names) {
        Map<String, Object> config = new HashMap<>();
        List<Map<String, Object>> transformerConfigs = new ArrayList<>();
        for (String name : names) {
            transformerConfigs.add(createTransformerConfig(name));
        }
        config.put("transformers", transformerConfigs);
        return config;
    }

    private Map<String, Object> createTransformerConfig(String name) {
        Map<String, Object> transformerConfig = new HashMap<>();
        transformerConfig.put("name", name);
        return transformerConfig;
    }

}
