package in.ashwanthkumar.manthan.service.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/graph")
@Produces(MediaType.APPLICATION_JSON)
public class GraphResource {
    private String dataFile;
    private ObjectMapper mapper = new ObjectMapper();

    public GraphResource(String dataFile) {
        this.dataFile = dataFile;
    }

    @GET
    public List<Map> streamGraph(
            @QueryParam("limit") int limit,
            @QueryParam("offset") int offset) throws IOException {
        return Files.lines(Paths.get(dataFile))
                .skip(offset)
                .limit(limit)
                .map((line) -> {
                    try {
                        return mapper.readValue(line, Map.class);
                    } catch (IOException ignored) {
                        return ImmutableMap.of();
                    }
                })
                .collect(Collectors.toList());

    }

    @Path("/view")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String viewPage() throws IOException {
        return Joiner.on('\n')
                .join(IOUtils.readLines(getClass().getResourceAsStream("/graph.html"), Charsets.UTF_8));
    }
}
