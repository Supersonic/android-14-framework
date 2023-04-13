package com.android.i18n.phonenumbers.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class RegexCache {
    private LRUCache<String, Pattern> cache;

    public RegexCache(int size) {
        this.cache = new LRUCache<>(size);
    }

    public Pattern getPatternForRegex(String regex) {
        Pattern pattern = this.cache.get(regex);
        if (pattern == null) {
            Pattern pattern2 = Pattern.compile(regex);
            this.cache.put(regex, pattern2);
            return pattern2;
        }
        return pattern;
    }

    boolean containsRegex(String regex) {
        return this.cache.containsKey(regex);
    }

    /* loaded from: classes.dex */
    private static class LRUCache<K, V> {
        private LinkedHashMap<K, V> map;
        private int size;

        public LRUCache(int size) {
            this.size = size;
            this.map = new LinkedHashMap<K, V>(((size * 4) / 3) + 1, 0.75f, true) { // from class: com.android.i18n.phonenumbers.internal.RegexCache.LRUCache.1
                @Override // java.util.LinkedHashMap
                protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return size() > LRUCache.this.size;
                }
            };
        }

        public synchronized V get(K key) {
            return this.map.get(key);
        }

        public synchronized void put(K key, V value) {
            this.map.put(key, value);
        }

        public synchronized boolean containsKey(K key) {
            return this.map.containsKey(key);
        }
    }
}
