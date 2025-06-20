package org.jetbrains.research.codecocoon.factory;

import net.sf.extjwnl.JWNLException;
import org.jetbrains.research.codecocoon.llm.LLM;
import org.jetbrains.research.codecocoon.synonyms.LLMSynonymGenerator;
import org.jetbrains.research.codecocoon.synonyms.LexicalSynonymGenerator;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SynonymGeneratorFactoryTest {

    LLMFactory mockLLMFactory = Mockito.mock(LLMFactory.class);
    SynonymGeneratorFactory factory = new SynonymGeneratorFactory(mockLLMFactory);


    @Test
    public void testCreateLLMSynonymGenerator() {
        Map<String, Object> parentConfig = new HashMap<>();
        Map<String, Object> llmConfig = new HashMap<>();
        Map<String, Object> synonymGeneratorConfig = new HashMap<>();
        parentConfig.put("synonymGenerator", synonymGeneratorConfig);
        synonymGeneratorConfig.put("name", "llmSynonymGenerator");
        synonymGeneratorConfig.put("llm", llmConfig);

        LLM mockLLM = Mockito.mock(LLM.class);
        Optional<SynonymGenerator> expected = Optional.of(new LLMSynonymGenerator(mockLLM));

        when(mockLLMFactory.createLLMFrom(llmConfig)).thenReturn(mockLLM);

        Optional<SynonymGenerator> actual = factory.createSynonymGeneratorFrom(parentConfig);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateLexicalSynonymGenerator() throws JWNLException {
        Map<String, Object> parentConfig = new HashMap<>();
        Map<String, Object> synonymGeneratorConfig = new HashMap<>();
        parentConfig.put("synonymGenerator", synonymGeneratorConfig);
        synonymGeneratorConfig.put("name", "lexicalSynonymGenerator");

        Optional<SynonymGenerator> expected = Optional.of(new LexicalSynonymGenerator());

        Optional<SynonymGenerator> actual = factory.createSynonymGeneratorFrom(parentConfig);

        assertEquals(expected, actual);
    }

    @Test
    public void testNoSynonymGenerator() {
        Map<String, Object> synonymGeneratorConfig = new HashMap<>();

        Optional<SynonymGenerator> expected = Optional.empty();

        Optional<SynonymGenerator> actual = factory.createSynonymGeneratorFrom(synonymGeneratorConfig);

        assertEquals(expected, actual);
    }
}
