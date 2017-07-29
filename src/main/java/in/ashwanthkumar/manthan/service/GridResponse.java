package in.ashwanthkumar.manthan.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class GridResponse {
    @JsonProperty
    public List<Map> records;
    @JsonProperty
    public long total;

    @JsonProperty
    public String status;

    public GridResponse(List<Map> records, long total) {
        this.records = records;
        this.total = total;
        this.status = "success";
    }
}
