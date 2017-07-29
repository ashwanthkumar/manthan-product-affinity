package in.ashwanthkumar.manthan.service;


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
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/report")
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {
    private String dataFile;
    private ObjectMapper mapper = new ObjectMapper();
    private long totalNumberOfRecords;

    public ReportResource(String dataFile) throws IOException {
        this.dataFile = dataFile;
        this.totalNumberOfRecords = Files.lines(Paths.get(dataFile)).count();
    }

    @GET
    public GridResponse streamReport(
            @QueryParam("request") String requestFromGrid) throws IOException {
        System.out.println("Request - " + requestFromGrid);
        Map request = mapper.readValue(requestFromGrid, Map.class);
        int offset = (int) request.get("offset");
        int limit = (int) request.get("limit");
        String sortField= null;
        if(request.containsKey("sort")) {
            sortField = ((Map) ((List) request.get("sort")).get(0)).get("field").toString();
        }
        LineNumberReader reader = new LineNumberReader(new FileReader(dataFile));
        reader.setLineNumber(offset);
        String finalSortField = sortField;
        List<Map> records = Files.lines(Paths.get(dataFile))
                .skip(offset)
                .limit(limit)
                .map((line) -> {
                    try {
                        return mapper.readValue(line, Map.class);
                    } catch (IOException ignored) {
                        return ImmutableMap.of();
                    }
                })
                .sorted(new Comparator<Map>() {
                    @Override
                    public int compare(Map o1, Map o2) {
                        if(finalSortField != null) {
                            return ((Comparable) o1.get(finalSortField)).compareTo(o2.get(finalSortField));
                        } else {
                            return 0;
                        }
                    }
                })
                .collect(Collectors.toList());

        ImmutableMap<String, Object> responseMap = ImmutableMap.of("records", records, "total", totalNumberOfRecords, "status", "success");
//        String responseJson = mapper.writeValueAsString(responseMap);
//        return responseMap;
        return new GridResponse(records, totalNumberOfRecords);
    }

    @Path("/view")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String viewPage() throws IOException {
        return Joiner.on('\n')
                .join(IOUtils.readLines(getClass().getResourceAsStream("/report.html"), Charsets.UTF_8));
    }
}
