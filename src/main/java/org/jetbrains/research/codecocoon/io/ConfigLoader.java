package org.jetbrains.research.codecocoon.io;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ConfigLoader {

    public static Map<String, Object> loadConfig(String filePath) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            return yaml.load(inputStream);
        }
    }

}
