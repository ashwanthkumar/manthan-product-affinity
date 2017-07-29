package in.ashwanthkumar.manthan;

import com.google.common.collect.Sets;
import org.junit.Test;

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

}