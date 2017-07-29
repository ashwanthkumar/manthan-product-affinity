package in.ashwanthkumar.manthan;

import in.ashwanthkumar.manthan.core.ProductAffinityRecord;
import in.ashwanthkumar.manthan.index.InMemoryIndex;
import in.ashwanthkumar.manthan.index.Index;

import java.util.stream.Stream;

/**
 * Index of
 * Base:Target -> ProductAffinityRecord
 */
public class AggregateIndex {
    Index<String, ProductAffinityRecord> index = new InMemoryIndex<>();

    public void add(ProductAffinityRecord record) {
        index.put(record.aggregateKey(), record);
    }

    public ProductAffinityRecord lookup(String key) {
        return index.lookup(key, null);
    }

    public ProductAffinityRecord lookup(String key, ProductAffinityRecord defaultValue) {
        return index.lookup(key, defaultValue);
    }

    public Stream<String> allAggregates() {
        return index.allKeys();
    }

    public int size() {
        return (int) index.size();
    }
}
