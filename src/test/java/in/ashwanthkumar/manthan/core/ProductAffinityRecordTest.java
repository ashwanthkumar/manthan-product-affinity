package in.ashwanthkumar.manthan.core;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProductAffinityRecordTest {
    ProductAffinityRecord record = new ProductAffinityRecord(
            "BaseProduct",
            "TargetProduct",
            Sets.newHashSet("T1", "T2", "T3"),
            Sets.newHashSet("T1", "T2", "T4", "T5"),
            5);

    @Test
    public void shouldComputeBaseSupportCount() {
        assertThat(record.baseSupportCount(), is(3L));
    }

    @Test
    public void shouldComputeBaseSupportPercentage() {
        assertThat(record.baseSupportPercentage(), is(60.0));
    }

    @Test
    public void shouldComputeTargetSupportCount() {
        assertThat(record.targetSupportCount(), is(4L));
    }

    @Test
    public void shouldComputeTargetSupportPercentage() {
        assertThat(record.targetSupportPercentage(), is(80.0));
    }

    @Test
    public void shouldComputeJointSupportCount() {
        assertThat(record.jointSupportCount(), is(2L));
    }

    @Test
    public void shouldComputeJointSupportPercentage() {
        assertThat(record.joinSupportPercentage(), is(40.0));
    }

    @Test
    public void shouldComputeConfidenceForBaseGivenTargetPercentage() {
        assertThat(record.confidenceForBaseGivenTargetPercentage(), is(66.66666666666666));
    }

    @Test
    public void shouldComputeConfidenceForTargetGivenBasePercentage() {
        assertThat(record.confidenceForTargetGivenBasePercentage(), is(50.0));
    }

    @Test
    public void shouldComputeLift() {
        assertThat(record.lift(), is(0.8333333333333334));
    }

    @Test
    public void shouldComputeAffinity() {
        assertThat(record.affinity(), is(40.0));
    }

    @Test
    public void shouldPlusTwoInstances() {
        ProductAffinityRecord one = new ProductAffinityRecord(
                "BaseProduct",
                "TargetProduct",
                Sets.newHashSet("T1", "T2", "T3"),
                Sets.newHashSet("T1", "T2", "T4", "T5"),
                5);

        ProductAffinityRecord two = new ProductAffinityRecord(
                "BaseProduct",
                "TargetProduct",
                Sets.newHashSet("T3", "T5", "T6"),
                Sets.newHashSet("T1", "T2", "T4", "T5"),
                6);

        ProductAffinityRecord merged = one.plus(two);
        assertThat(merged.getBaseProduct(), is(one.getBaseProduct()));
        assertThat(merged.getTargetProduct(), is(one.getTargetProduct()));
        assertThat(merged.getTransactionsOfBaseProduct(), is(Sets.newHashSet("T1", "T2", "T3", "T5", "T6")));
        assertThat(merged.getTransactionsOfTargetProduct(), is(Sets.newHashSet("T1", "T2", "T4", "T5")));
        assertThat(merged.getAllTransactionsCount(), is(6L));
    }

    @Test
    public void shouldComputeAggregateKey() {
        assertThat(record.aggregateKey(), is("BaseProductTargetProduct"));
    }

    @Test
    public void shouldReturnAllNecessaryKeysInMap() {
        ProductAffinityRecord record = new ProductAffinityRecord(
                "BaseProduct",
                "TargetProduct",
                Sets.newHashSet("T3", "T5", "T6"),
                Sets.newHashSet("T1", "T2", "T4", "T5"),
                6);
        Map<String, Object> output = record.forOutput();
        assertThat(output.get("base_product"), is(record.getBaseProduct()));
        assertThat(output.get("base_support_count"), is(record.baseSupportCount()));
        assertThat(output.get("base_support_percentage"), is(record.baseSupportPercentage()));
        assertThat(output.get("target_product"), is(record.getTargetProduct()));
        assertThat(output.get("target_support_count"), is(record.targetSupportCount()));
        assertThat(output.get("target_support_percentage"), is(record.targetSupportPercentage()));
        assertThat(output.get("join_support_count"), is(record.jointSupportCount()));
        assertThat(output.get("join_support_percentage"), is(record.joinSupportPercentage()));
        assertThat(output.get("confidence_base_given_target_percentage"), is(record.confidenceForBaseGivenTargetPercentage()));
        assertThat(output.get("confidence_target_given_base_percentage"), is(record.confidenceForTargetGivenBasePercentage()));
        assertThat(output.get("lift"), is(record.lift()));
        assertThat(output.get("affinity"), is(record.affinity()));
    }

}
