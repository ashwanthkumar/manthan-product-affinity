package in.ashwanthkumar.manthan.index;

import java.util.stream.Stream;

public interface Index<K, V> {
    V lookup(K key);

    V lookup(K key, V defaultValue);

    void put(K key, V value);

    Stream<K> allKeys();

    long size();
}
