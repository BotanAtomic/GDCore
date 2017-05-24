package org.graviton.database;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by Botan on 14/01/2017. 11:20
 */
public abstract class Repository<K, V> extends ConcurrentHashMap<K,V>{

    public void add(K key, V value) {
        this.put(key, value);
    }

    public Stream<V> stream() {
        return values().stream();
    }

    public abstract V find(Object value);

}
