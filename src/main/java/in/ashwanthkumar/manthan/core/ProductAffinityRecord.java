package in.ashwanthkumar.manthan.core;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
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

    public ProductAffinityRecord(String baseProduct, String targetProduct, long allTransactionsCount) {
        this(baseProduct, targetProduct, Sets.newHashSet(), Sets.newHashSet(), allTransactionsCount);
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

    @JsonValue
    public Map<String, Object> forOutput() {
        return ImmutableMap.<String, Object>builder()
                .put("base_product", getBaseProduct())
                .put("base_support_count", baseSupportCount())
                .put("base_support_percentage", baseSupportPercentage())

                .put("target_product", getTargetProduct())
                .put("target_support_count", targetSupportCount())
                .put("target_support_percentage", targetSupportPercentage())

                .put("join_support_count", jointSupportCount())
                .put("join_support_percentage", joinSupportPercentage())

                .put("confidence_base_given_target_percentage", confidenceForBaseGivenTargetPercentage())
                .put("confidence_target_given_base_percentage", confidenceForTargetGivenBasePercentage())

                .put("lift", lift())
                .put("affinity", affinity())
                .build();
    }

    public ProductAffinityRecord plus(ProductAffinityRecord another) {
        Preconditions.checkArgument(StringUtils.equals(this.getBaseProduct(), another.getBaseProduct()) && StringUtils.equals(this.getTargetProduct(), another.getTargetProduct()), "Can't plus 2 different ProductAffinityRecord. Both base and target should match.");
        long maxOfTransactions = (this.allTransactionsCount > another.allTransactionsCount) ? this.allTransactionsCount : another.allTransactionsCount;
        return new ProductAffinityRecord(
                this.getBaseProduct(),
                this.getTargetProduct(),
                Sets.union(this.transactionsOfBaseProduct, another.transactionsOfBaseProduct),
                Sets.union(this.transactionsOfTargetProduct, another.transactionsOfTargetProduct),
                maxOfTransactions);
    }

    public String getBaseProduct() {
        return baseProduct;
    }

    public String getTargetProduct() {
        return targetProduct;
    }

    public Set<String> getTransactionsOfBaseProduct() {
        return transactionsOfBaseProduct;
    }

    public Set<String> getTransactionsOfTargetProduct() {
        return transactionsOfTargetProduct;
    }

    public long getAllTransactionsCount() {
        return allTransactionsCount;
    }

    public String aggregateKey() {
        return baseProduct + targetProduct;
    }
}
