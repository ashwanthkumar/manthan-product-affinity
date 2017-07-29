package in.ashwanthkumar.manthan;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import in.ashwanthkumar.manthan.core.ProductAffinityRecord;
import in.ashwanthkumar.manthan.core.Ticker;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class DatasetConverter {
    public static void main(String[] args) throws IOException, InterruptedException {
        String inputFile = args[0];
        String outputFile = args[1];

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(2 * Runtime.getRuntime().availableProcessors()));

        final ProductIndex productsIndex = new ProductIndex();
        final Stopwatch watch = Stopwatch.createStarted();
        Files.lines(Paths.get(inputFile)).parallel().forEach(line -> {
            Ticker ticker = Ticker.parse(line);
            productsIndex.add(ticker);
        });
        watch.stop();

        System.out.println("Total number of products - " + productsIndex.size());
        System.out.println("Time took to read the file and load the data: " + watch.toString());
        watch.reset();

        final AggregateIndex aggregateIndex = new AggregateIndex();
        final CountDownLatch latch = new CountDownLatch(productsIndex.size());
        final long totalTransactionsCount = productsIndex.totalTransactionsCount();
        watch.start();
        productsIndex.allProducts().parallel().forEach((baseProduct) -> {
            Set<String> baseProductTransactions = productsIndex.lookup(baseProduct, Sets.newHashSet());

            productsIndex.allProducts().parallel().forEach((targetProduct) -> {
                if (!StringUtils.equals(targetProduct, baseProduct)) {
                    Set<String> targetProductTransactions = productsIndex.lookup(targetProduct, Sets.newHashSet());
                    if (Sets.intersection(baseProductTransactions, targetProductTransactions).size() > 0) {
                        ProductAffinityRecord defaultAffinityRecord = new ProductAffinityRecord(baseProduct, targetProduct, totalTransactionsCount);
                        ProductAffinityRecord merged = aggregateIndex
                                .lookup(defaultAffinityRecord.aggregateKey(), defaultAffinityRecord)
                                .plus(new ProductAffinityRecord(baseProduct, targetProduct, baseProductTransactions, targetProductTransactions, totalTransactionsCount));
                        aggregateIndex.add(merged);
                    }
                }
            });
            latch.countDown();
            System.out.println("Pending items to process - " + latch.getCount());
        });
        latch.await();
        watch.stop();
        System.out.println("Time took to generate combinations for all products with every other product: " + watch.toString());
        System.out.println("Size of Aggregate Index - " + aggregateIndex.size());

        System.out.println("Starting to write the aggregate index to " + outputFile);
        try (AggregateIndexWriter writer = new AggregateIndexWriter(outputFile)) {

            aggregateIndex.allAggregates().forEach((key) -> {
                try {
                    writer.write(aggregateIndex.lookup(key));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        System.out.println("Wrote the aggregate index as json lines to " + outputFile);
    }
}
