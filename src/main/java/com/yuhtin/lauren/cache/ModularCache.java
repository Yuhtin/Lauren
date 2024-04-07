package com.yuhtin.lauren.cache;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class ModularCache<K, V> {

    private final AsyncLoadingCache<K, V> cache;

    public static <K, V> ModularCache<K, V> create(AsyncLoadingCache<K, V> cache) {
        return new ModularCache<>(cache);
    }

    public CompletableFuture<V> get(K key) {
        return cache.get(key);
    }

    public CompletableFuture<V> getIfPresent(K key) {
        return cache.getIfPresent(key);
    }

    public Collection<CompletableFuture<V>> values() {
        return cache.asMap().values();
    }

    public void invalidate(K key) {
        cache.synchronous().invalidate(key);
    }

    public void put(K key, V value) {
        cache.synchronous().put(key, value);
    }

    public void invalidateAll() {
        cache.synchronous().invalidateAll();
    }

    public int size() {
        return cache.synchronous().asMap().size();
    }
}
