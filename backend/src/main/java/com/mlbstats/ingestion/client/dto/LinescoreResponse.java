package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Response DTO for MLB API linescore endpoint: /game/{gamePk}/linescore
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinescoreResponse {

    private String currentInning;
    private String currentInningOrdinal;
    private String inningState;
    private String inningHalf;
    private Boolean isTopInning;
    private Integer scheduledInnings;
    private List<InningData> innings;
    private TeamLinescore teams;
    private DefenseData defense;
    private OffenseData offense;
    private Integer balls;
    private Integer strikes;
    private Integer outs;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InningData {
        private Integer num;
        private String ordinalNum;
        private TeamInningScore home;
        private TeamInningScore away;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamInningScore {
        private Integer runs;
        private Integer hits;
        private Integer errors;
        private Integer leftOnBase;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamLinescore {
        private TeamTotals home;
        private TeamTotals away;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamTotals {
        private Integer runs;
        private Integer hits;
        private Integer errors;
        private Integer leftOnBase;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DefenseData {
        private PlayerRef pitcher;
        private PlayerRef catcher;
        private PlayerRef first;
        private PlayerRef second;
        private PlayerRef third;
        private PlayerRef shortstop;
        private PlayerRef left;
        private PlayerRef center;
        private PlayerRef right;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OffenseData {
        private PlayerRef batter;
        private PlayerRef onDeck;
        private PlayerRef inHole;
        private PlayerRef first;
        private PlayerRef second;
        private PlayerRef third;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerRef {
        private Integer id;
        private String fullName;
        private String link;
    }
}
