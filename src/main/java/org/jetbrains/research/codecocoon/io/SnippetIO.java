package org.jetbrains.research.codecocoon.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.research.codecocoon.Snippet;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.List;


public class SnippetIO {

    public static List<Snippet> loadSnippets(String dataFilePath) throws FileNotFoundException {
        Gson gson = new Gson();

        FileReader reader = new FileReader(dataFilePath);
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();

        return Snippet.fromMap(gson.fromJson(reader, mapType));
    }

    public static void writeSnippets(String outputFilePath, List<Snippet> snippets) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Add try to make sure that the writer is closed properly.
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            gson.toJson(snippets, writer);
        }
    }
}
