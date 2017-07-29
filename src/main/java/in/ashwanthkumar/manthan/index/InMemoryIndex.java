package in.ashwanthkumar.manthan.index;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

/**
 * An {@link Index} implementation that's backed by ConcurrentSkipListMap
 *
 * @param <K>
 * @param <V>
 */
public class InMemoryIndex<K, V> implements Index<K, V> {

    private Map<K, V> index = new ConcurrentSkipListMap<>();

    public InMemoryIndex(Map<K, V> index) {
        this.index = index;
    }

    public InMemoryIndex() {
    }

    @Override
    public V lookup(K key) {
        return getOrElse(index, key, null);
    }

    @Override
    public V lookup(K key, V defaultValue) {
        return getOrElse(index, key, defaultValue);
    }

    @Override
    public void put(K key, V value) {
        index.put(key, value);
    }

    @Override
    public Stream<K> allKeys() {
        return index.keySet().stream();
    }

    private V getOrElse(Map<K, V> map, K key, V defaultValue) {
        V value = map.get(key);
        if (value != null) return value;
        else return defaultValue;
    }

}
