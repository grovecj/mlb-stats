package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamResponse {

    private List<TeamData> teams;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamData {
        private Integer id;
        private String name;
        private String abbreviation;
        private String locationName;
        private VenueData venue;
        private LeagueData league;
        private DivisionData division;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VenueData {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueData {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DivisionData {
        private String name;
    }
}
