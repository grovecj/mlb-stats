package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RosterResponse {

    private List<RosterEntry> roster;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RosterEntry {
        private PersonData person;
        private String jerseyNumber;
        private PositionData position;
        private StatusData status;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusData {
        private String code;
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonData {
        private Integer id;
        private String fullName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PositionData {
        private String abbreviation;
        private String type;
    }
}
