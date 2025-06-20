package org.jetbrains.research.codecocoon.io.cache;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.Map;

public class PersistentCache implements Cache{
    private final DB db;
    private final Map<String, String> cache;

    public PersistentCache(String cachePath) {
        db = DBMaker.fileDB(cachePath).transactionEnable().make();

        cache = db.hashMap("cache")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.STRING)
                .createOrOpen();
    }

    public boolean containsKey(String prompt) {
        return cache.containsKey(prompt);
    }

    public String get(String prompt) {
        return cache.get(prompt);
    }

    public void put(String prompt, String value) {
        cache.put(prompt, value);
        db.commit();
    }
}
