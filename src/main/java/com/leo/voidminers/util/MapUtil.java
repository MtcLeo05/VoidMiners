package com.leo.voidminers.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {
    /**
     * Creates a MUTABLE map of type K, V
     */
    @SafeVarargs
    public static <K, V> Map<K, V> of(Map.Entry<K, V>... entries) {
        return Map.ofEntries(entries);
    }

    /**
     * Converts a map to a map that is surely mutable
     */
    public static <K, V> Map<K, V> mutable(Map<K, V> map) {
        return new HashMap<>(map);
    }

    /**
     * Creates a map entry based on a pair of values
     */
    public static <K, V> Map.Entry<K, V> createEntry(K key, V value) {
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map.entrySet().stream().toList().get(0);
    }
}
