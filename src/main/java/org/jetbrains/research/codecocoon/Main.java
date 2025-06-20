package org.jetbrains.research.codecocoon;

import me.tongfei.progressbar.ProgressBar;
import org.jetbrains.research.codecocoon.factory.TransformerFactory;
import org.jetbrains.research.codecocoon.io.ConfigLoader;
import org.jetbrains.research.codecocoon.io.SnippetIO;
import org.jetbrains.research.codecocoon.factory.CacheFactory;
import org.jetbrains.research.codecocoon.factory.LLMFactory;
import org.jetbrains.research.codecocoon.factory.SynonymGeneratorFactory;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

    private static Map<String, Object> config;
    private static String inputFilePath;
    private static String outputFilePath;

    public static void main(String[] args) throws IOException {
        config = ConfigLoader.loadConfig(args[0]);

        setIOpaths(args, config);

        List<Snippet> snippets = SnippetIO.loadSnippets(inputFilePath);

        TransformerFactory factory = createTransformerFactory();
        Transformer compositeTransformer = factory.createCompositeTransformerFrom(config);

        transformSnippets(compositeTransformer, snippets);

        SnippetIO.writeSnippets(outputFilePath, snippets);
    }

    private static void setIOpaths(String[] args, Map<String, Object> config) {
        if (args.length > 2) {
            inputFilePath = args[1];
            outputFilePath = args[2];
        } else  {
            inputFilePath = config.get("inputFilePath").toString();
            outputFilePath = config.get("outputFilePath").toString();
        }
    }

    private static TransformerFactory createTransformerFactory() {
        Optional<SynonymGenerator> synonymGenerator = createSynonymGenerator();

        if (synonymGenerator.isPresent()) {
            return new TransformerFactory(synonymGenerator.get());
        }
        return new TransformerFactory();
    }

    private static Optional<SynonymGenerator> createSynonymGenerator() {
        CacheFactory cacheFactory = new CacheFactory();
        LLMFactory llmFactory = new LLMFactory(cacheFactory);
        SynonymGeneratorFactory synonymGeneratorFactory = new SynonymGeneratorFactory(llmFactory);
        return synonymGeneratorFactory.createSynonymGeneratorFrom(config);
    }

    private static void transformSnippets(Transformer transformer, List<Snippet> snippets) throws IOException {
        ProgressBar progressBar = new ProgressBar("Transforming:", snippets.size());
        progressBar.start();
        for (Snippet snippet : snippets) {
            transformer.transform(snippet);
            progressBar.step();
            SnippetIO.writeSnippets(outputFilePath, snippets);
        }
        progressBar.stop();
    }
}
