package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SabermetricsResponse {

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
        private TeamData team;
        private SabermetricData stat;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamData {
        private Integer id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SabermetricData {
        // Batting sabermetrics
        private BigDecimal woba;
        private BigDecimal wRaa;
        private BigDecimal wRc;
        private BigDecimal wRcPlus;
        private BigDecimal rar;
        private BigDecimal war;
        private BigDecimal batting;
        private BigDecimal fielding;
        private BigDecimal baseRunning;
        private BigDecimal positional;
        private BigDecimal wLeague;
        private BigDecimal replacement;
        private BigDecimal spd;
        private BigDecimal ubr;
        private BigDecimal wGdp;
        private BigDecimal wSb;

        // Pitching sabermetrics
        private BigDecimal fip;
        private BigDecimal xfip;
        private BigDecimal fipMinus;
        private BigDecimal ra9War;
        private BigDecimal eraMinus;
        private BigDecimal pli;
        private BigDecimal inli;
        private BigDecimal gmli;
        private BigDecimal exli;
        private BigDecimal sd;
        private BigDecimal md;
    }
}
