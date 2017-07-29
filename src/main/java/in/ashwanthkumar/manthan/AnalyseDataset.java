package in.ashwanthkumar.manthan;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import in.ashwanthkumar.manthan.core.ProductAffinityRecord;
import in.ashwanthkumar.manthan.core.Ticker;
import in.ashwanthkumar.manthan.util.SchedulerService;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class AnalyseDataset {
    static SchedulerService scheduler = SchedulerService.get();

    public static void main(String[] args) throws IOException, InterruptedException {
        String inputFile = args[0];
        String outputFile = args[1];
        File file = new File(inputFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        TreeMap<String, Set<String>> productsIndex = new TreeMap<>();
        Set<String> allTranscations = Sets.newHashSet();
        Stopwatch watch = Stopwatch.createStarted();
        while ((line = reader.readLine()) != null) {
            Ticker ticker = Ticker.parse(line);
            String productName = ticker.getProductDescription();
            String invoiceNumber = ticker.getInvoiceNumber();
            addToIndex(productsIndex, productName, invoiceNumber);
            allTranscations.add(invoiceNumber);
        }
        watch.stop();

        System.out.println("Total number of products - " + productsIndex.size());
        System.out.println("Total number of invoices - " + allTranscations.size());
        System.out.println("Time took to read the file and load the data: " + watch.toString());

        Map<String, ProductAffinityRecord> aggregateIndex = new ConcurrentHashMap<>();

        watch.reset();
        CountDownLatch latch = new CountDownLatch(productsIndex.size());
        watch.start();
        for (String baseProduct : productsIndex.keySet()) {
            Set<String> baseProductTransactions = productsIndex.get(baseProduct);
            scheduler.doLater(() -> updateAggregateIndex(productsIndex, aggregateIndex, baseProduct, baseProductTransactions, allTranscations.size(), latch));
        }
        latch.await();
        watch.stop();
        System.out.println("Time took to generate combinations for all products with every other product: " + watch.toString());
        System.out.println("Size of Aggregate Index - " + aggregateIndex.size());
        scheduler.shutdown();

        System.out.println("Starting to write the aggregate index to " + outputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        Kryo kryo = new Kryo();
        kryo.register(ProductAffinityRecord.class, new CompatibleFieldSerializer(kryo, ProductAffinityRecord.class));
        Output output = new Output(outputStream);
        kryo.writeObject(output, aggregateIndex);
        output.close();
        outputStream.close();
        System.out.println("Wrote the aggregate index in binary form at " + outputFile);
    }

    private static void updateAggregateIndex(TreeMap<String, Set<String>> productToInvoices, Map<String, ProductAffinityRecord> aggregateIndex, String baseProduct, Set<String> baseProductTransactions, int allTransactionsCount, CountDownLatch latch) {
//        System.out.println("Base=" + baseProduct + ",transactions=" + baseProductTransactions);
        for (String targetProduct : productToInvoices.keySet()) {
            if (!StringUtils.equals(targetProduct, baseProduct)) {
                Set<String> targetProductTransactions = productToInvoices.get(targetProduct);
//                System.out.println("Target=" + targetProduct + ",transactions=" + targetProductTransactions);
//                System.out.println("Intersection=" + Sets.intersection(baseProductTransactions, targetProductTransactions));
                if (Sets.intersection(baseProductTransactions, targetProductTransactions).size() > 0) {
                    String aggregateKey = baseProduct + targetProduct;
                    ProductAffinityRecord existingAggregate = getOrElse(aggregateIndex, aggregateKey, new ProductAffinityRecord(baseProduct, targetProduct, allTransactionsCount));
                    ProductAffinityRecord newAggregate = new ProductAffinityRecord(baseProduct, targetProduct, baseProductTransactions, targetProductTransactions, allTransactionsCount);
                    ProductAffinityRecord merged = existingAggregate.plus(newAggregate);
                    aggregateIndex.put(aggregateKey, merged);
                }
            }
        }
        latch.countDown();
        System.out.println("Pending items to process - " + scheduler.pendingItems());
    }

    private static void addToIndex(Map<String, Set<String>> index, String key, String value) {
        Set<String> invoices = getOrElse(index, key, new HashSet<>());
        invoices.add(value);
        index.put(key, invoices);
    }

    private static <K, V> V getOrElse(Map<K, V> map, K key, V defaultValue) {
        V value = map.get(key);
        if (value != null) return value;
        else return defaultValue;
    }
}
