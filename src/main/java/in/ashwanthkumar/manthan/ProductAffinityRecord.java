package in.ashwanthkumar.manthan;

import com.google.common.collect.Sets;

import java.util.Set;

public class ProductAffinityRecord {
    private String baseProduct;
    private String targetProduct;

    private Set<String> transactionsOfBaseProduct;
    private Set<String> transactionsOfTargetProduct;

    private long allTransactionsCount;
    private final Sets.SetView<String> invoicesIntersection;

    public ProductAffinityRecord(String baseProduct, String targetProduct, Set<String> transactionsOfBaseProduct, Set<String> transactionsOfTargetProduct, long allTransactionsCount) {
        this.baseProduct = baseProduct;
        this.targetProduct = targetProduct;
        this.transactionsOfBaseProduct = transactionsOfBaseProduct;
        this.transactionsOfTargetProduct = transactionsOfTargetProduct;
        this.allTransactionsCount = allTransactionsCount;
        invoicesIntersection = Sets.intersection(transactionsOfBaseProduct, transactionsOfTargetProduct);
    }

    public double supportForBaseProduct() {
        return (double) transactionsOfBaseProduct.size() / allTransactionsCount;
    }

    public double supportForTargetProduct() {
        return (double) transactionsOfTargetProduct.size() / allTransactionsCount;
    }

    public double confidenceForBaseGivenTarget() {
        return (double) invoicesIntersection.size() / transactionsOfBaseProduct.size();
    }

    public double confidenceForTargetGivenBase() {
        return (double) invoicesIntersection.size() / transactionsOfTargetProduct.size();
    }

    public long baseSupportCount() {
        return transactionsOfBaseProduct.size();
    }

    public double baseSupportPercentage() {
        return 100 * supportForBaseProduct();
    }

    public long targetSupportCount() {
        return transactionsOfTargetProduct.size();
    }

    public double targetSupportPercentage() {
        return 100 * supportForTargetProduct();
    }

    public long jointSupportCount() {
        return invoicesIntersection.size();
    }

    public double joinSupportPercentage() {
        return 100 * ((double) jointSupportCount() / allTransactionsCount);
    }

    public double confidenceForBaseGivenTargetPercentage() {
        return 100 * confidenceForBaseGivenTarget();
    }

    public double confidenceForTargetGivenBasePercentage() {
        return 100 * confidenceForTargetGivenBase();
    }

    public double lift() {
        return (double) (allTransactionsCount * jointSupportCount()) / (baseSupportCount() * targetSupportCount());
    }

    public double affinity() {
        return 100 * ((double) jointSupportCount()) / (baseSupportCount() + targetSupportCount() - jointSupportCount());
    }

    public String getBaseProduct() {
        return baseProduct;
    }

    public String getTargetProduct() {
        return targetProduct;
    }
}
