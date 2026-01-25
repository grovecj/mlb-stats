package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatsResponse {

    private List<StatGroup> stats;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatGroup {
        private String type;
        private String group;
        private List<StatSplit> splits;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatSplit {
        private String season;
        private TeamData team;
        private StatData stat;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamData {
        private Integer id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatData {
        // Batting stats
        private Integer gamesPlayed;
        private Integer atBats;
        private Integer runs;
        private Integer hits;
        private Integer doubles;
        private Integer triples;
        private Integer homeRuns;
        private Integer rbi;
        private Integer stolenBases;
        private Integer caughtStealing;
        private Integer baseOnBalls;
        private Integer strikeOuts;
        private String avg;
        private String obp;
        private String slg;
        private String ops;
        private Integer plateAppearances;
        private Integer totalBases;
        private Integer intentionalWalks;
        private Integer hitByPitch;
        private Integer sacFlies;
        private Integer groundIntoDoublePlay;

        // Pitching stats
        private Integer gamesStarted;
        private Integer wins;
        private Integer losses;
        private Integer saves;
        private Integer holds;
        private String inningsPitched;
        private Integer hitBatsmen;
        private Integer earnedRuns;
        private Integer homeRunsAllowed;
        private String era;
        private String whip;
        private Integer numberOfPitches;
        private Integer strikes;
        private Integer completeGames;
        private Integer shutouts;
    }
}
