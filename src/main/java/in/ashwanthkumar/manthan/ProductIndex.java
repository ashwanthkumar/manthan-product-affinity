package in.ashwanthkumar.manthan;

import in.ashwanthkumar.manthan.core.Ticker;
import in.ashwanthkumar.manthan.index.InMemoryIndex;
import in.ashwanthkumar.manthan.index.Index;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

/**
 * Index of
 * ProductName -->> [Set of Transactions]
 */
public class ProductIndex {
    private Index<String, Set<String>> index = new InMemoryIndex<>();

    public ProductIndex add(Ticker ticker) {
        String productName = ticker.getProductDescription();
        String invoiceNumber = ticker.getInvoiceNumber();

        Set<String> invoices = index.lookup(productName, new ConcurrentSkipListSet<>());
        invoices.add(invoiceNumber);
        index.put(productName, invoices);
        return this;
    }

    public Set<String> lookup(String productName) {
        return index.lookup(productName, null);
    }

    public Set<String> lookup(String productName, Set<String> defaultValue) {
        return index.lookup(productName, defaultValue);
    }

    public Stream<String> allProducts() {
        return index.allKeys();
    }

    private static <K, V> V getOrElse(Map<K, V> map, K key, V defaultValue) {
        V value = map.get(key);
        if (value != null) return value;
        else return defaultValue;
    }

}
