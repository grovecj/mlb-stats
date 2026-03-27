package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeasonAdvancedResponse {

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
        private AdvancedStatData stat;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamData {
        private Integer id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AdvancedStatData {
        // Common advanced stats
        private String babip;
        private String iso;
        private Integer qualityStarts;

        // Batting advanced
        private String walksPerPlateAppearance;       // BB%
        private String strikeoutsPerPlateAppearance;  // K%
        private String walksPerStrikeout;

        // Pitching advanced
        private String strikeoutsPer9;
        private String baseOnBallsPer9;
        private String hitsPer9;
        private String homeRunsPer9;
        private String strikesoutsToWalks;
        private String whiffPercentage;
        private String flyBallPercentage;  // FB%
        private String strikeoutsMinusWalksPercentage;

        // Batted ball data (for calculating GB%)
        private Integer groundOuts;
        private Integer flyOuts;
        private Integer popOuts;
        private Integer lineOuts;
        private Integer ballsInPlay;
    }
}
