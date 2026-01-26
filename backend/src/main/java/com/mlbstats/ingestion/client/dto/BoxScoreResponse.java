package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class  BoxScoreResponse {

    private Teams teams;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Teams {
        private TeamBoxScore away;
        private TeamBoxScore home;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamBoxScore {
        private TeamInfo team;
        private List<Integer> batters;
        private List<Integer> pitchers;
        private List<Integer> battingOrder;
        private Map<String, PlayerStats> players;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamInfo {
        private Integer id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerStats {
        private PersonInfo person;
        private String jerseyNumber;
        private PositionInfo position;
        private Integer battingOrder;
        private BattingStats stats;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonInfo {
        private Integer id;
        private String fullName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PositionInfo {
        private String abbreviation;
        private String type;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BattingStats {
        private BattingStatLine batting;
        private PitchingStatLine pitching;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BattingStatLine {
        private Integer atBats;
        private Integer runs;
        private Integer hits;
        private Integer doubles;
        private Integer triples;
        private Integer homeRuns;
        private Integer rbi;
        private Integer baseOnBalls;
        private Integer strikeOuts;
        private Integer stolenBases;
        private Integer caughtStealing;
        private Integer leftOnBase;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PitchingStatLine {
        private String inningsPitched;
        private Integer hits;
        private Integer runs;
        private Integer earnedRuns;
        private Integer baseOnBalls;
        private Integer strikeOuts;
        private Integer homeRuns;
        private Integer numberOfPitches;
        private Integer strikes;
        private String note;
    }
}
