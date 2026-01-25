package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleResponse {

    private List<DateEntry> dates;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DateEntry {
        private String date;
        private List<GameData> games;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GameData {
        private Integer gamePk;
        private String gameDate;
        private String gameType;
        private StatusData status;
        private TeamsData teams;
        private VenueData venue;
        private String dayNight;
        private Integer scheduledInnings;
        private Integer season;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusData {
        private String abstractGameState;
        private String detailedState;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamsData {
        private TeamGameData home;
        private TeamGameData away;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamGameData {
        private Integer score;
        private TeamRefData team;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamRefData {
        private Integer id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VenueData {
        private String name;
    }
}
