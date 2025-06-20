package org.jetbrains.research.codecocoon.factory;

import org.jetbrains.research.codecocoon.io.cache.Cache;
import org.jetbrains.research.codecocoon.io.cache.PersistentCache;

public class CacheFactory {

    public Cache create(String fileName) {
        return new PersistentCache(fileName);
    }
}
