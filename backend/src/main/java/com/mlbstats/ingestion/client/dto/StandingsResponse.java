package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingsResponse {

    private List<StandingsRecord> records;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StandingsRecord {
        private String standingsType;
        private LeagueInfo league;
        private DivisionInfo division;
        private List<TeamRecord> teamRecords;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueInfo {
        private Integer id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DivisionInfo {
        private Integer id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamRecord {
        private TeamInfo team;
        private Integer wins;
        private Integer losses;
        private String winningPercentage;
        private String gamesBack;
        private String wildCardGamesBack;
        private Integer divisionRank;
        private Integer leagueRank;
        private Integer wildCardRank;
        private Integer runsScored;
        private Integer runsAllowed;
        private Integer runDifferential;
        private StreakInfo streak;
        private RecordInfo records;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamInfo {
        private Integer id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StreakInfo {
        private String streakType;
        private Integer streakNumber;
        private String streakCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RecordInfo {
        private List<SplitRecord> splitRecords;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SplitRecord {
        private Integer wins;
        private Integer losses;
        private String type;
    }
}
