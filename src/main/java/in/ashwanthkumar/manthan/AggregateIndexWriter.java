package in.ashwanthkumar.manthan;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.ashwanthkumar.manthan.core.ProductAffinityRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class AggregateIndexWriter implements AutoCloseable {
    private ObjectMapper mapper = new ObjectMapper();
    private PrintWriter writer;

    public AggregateIndexWriter(String outputFile) throws FileNotFoundException {
        this.writer = new PrintWriter(new File(outputFile));
    }

    public void write(ProductAffinityRecord record) throws IOException {
        String jsonLine = mapper.writeValueAsString(record);
        writer.println(jsonLine);
    }

    public void close() {
        writer.close();
    }
}
