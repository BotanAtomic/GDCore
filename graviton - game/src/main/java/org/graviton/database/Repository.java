package org.graviton.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by Botan on 14/01/2017. 11:20
 */
public abstract class Repository<K, V> {
    protected Map<K, V> objects = new ConcurrentHashMap<>();

    public void add(K key, V value) {
        this.objects.put(key, value);
    }

    public V remove(K key) {
        return objects.remove(key);
    }

    public V get(K key) {
        return objects.get(key);
    }

    public Stream<V> stream() {
        return objects.values().stream();
    }

    public abstract V find(Object value);

}
