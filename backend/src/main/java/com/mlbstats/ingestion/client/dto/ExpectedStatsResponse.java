package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpectedStatsResponse {

    private List<StatGroup> stats;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatGroup {
        private TypeData type;
        private GroupData group;
        private List<StatSplit> splits;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TypeData {
        private String displayName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GroupData {
        private String displayName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatSplit {
        private String season;
        private ExpectedStatData stat;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExpectedStatData {
        // Expected batting/pitching stats (xBA, xSLG, xwOBA)
        private String avg;   // xBA
        private String slg;   // xSLG
        private String woba;  // xwOBA
        private String wobaCon;
    }
}
