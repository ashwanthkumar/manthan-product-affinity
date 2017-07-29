package in.ashwanthkumar.manthan;

import in.ashwanthkumar.manthan.core.Ticker;
import in.ashwanthkumar.manthan.index.InMemoryIndex;
import in.ashwanthkumar.manthan.index.Index;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

/**
 * Index of
 * ProductName -->> [Set of Transactions]
 */
public class ProductIndex {
    private Index<String, Set<String>> index = new InMemoryIndex<>();
    private Set<String> allTransactions = new ConcurrentSkipListSet<>();

    public void add(Ticker ticker) {
        String productName = ticker.getProductDescription();
        String invoiceNumber = ticker.getInvoiceNumber();

        Set<String> invoices = index.lookup(productName, new ConcurrentSkipListSet<>());
        invoices.add(invoiceNumber);
        index.put(productName, invoices);
        allTransactions.add(invoiceNumber);
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

    public int totalTransactionsCount() {
        return allTransactions.size();
    }

    public int size() {
        return (int) index.size();
    }
}
