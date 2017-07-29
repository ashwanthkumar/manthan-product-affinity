package in.ashwanthkumar.manthan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import in.ashwanthkumar.manthan.core.ProductAffinityRecord;
import in.ashwanthkumar.manthan.index.InMemoryIndex;
import in.ashwanthkumar.manthan.index.Index;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

class AffinityGraph implements Comparable<AffinityGraph>{
    public String target;
    public double affinity;

    public AffinityGraph(String target, double affinity) {
        this.target = target;
        this.affinity = affinity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AffinityGraph that = (AffinityGraph) o;

        return new EqualsBuilder()
                .append(affinity, that.affinity)
                .append(target, that.target)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(target)
                .append(affinity)
                .toHashCode();
    }

    @Override
    public int compareTo(AffinityGraph o) {
        return CompareToBuilder.reflectionCompare(this, o);
    }
}

public class DatasetConverterForGraph {
    public static void main(String[] args) throws IOException {
        String inputFile = args[0];
        String outputFile = args[1];

        ObjectMapper mapper = new ObjectMapper();
        Index<String, Set<AffinityGraph>> graphIndex = new InMemoryIndex<>();

        Files.lines(Paths.get(inputFile))
                .parallel()
                .forEach((line) -> {
                    try {
                        Map record = mapper.readValue(line, Map.class);
                        String baseProduct = (String) record.get("base_product");
                        String targetProduct = (String) record.get("target_product");
                        double affinity = (double) record.get("affinity");
                        Set<AffinityGraph> affinityGraph = graphIndex.lookup(baseProduct, new ConcurrentSkipListSet<>());
                        affinityGraph.add(new AffinityGraph(targetProduct, affinity));
                        graphIndex.put(baseProduct, affinityGraph);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


        PrintWriter writer = new PrintWriter(new File(outputFile));
        graphIndex.allKeys().forEach((base) -> {
            Set<AffinityGraph> graphs = graphIndex.lookup(base);
            ImmutableMap<String, Object> records = ImmutableMap.of("base_product", base, "graphs", graphs);
            try {
                String json = mapper.writeValueAsString(records);
                writer.println(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        writer.close();
    }
}
