package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerResponse {

    private List<PlayerData> people;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerData {
        private Integer id;
        private String fullName;
        private String firstName;
        private String lastName;
        private String primaryNumber;
        private PositionData primaryPosition;
        private BatSideData batSide;
        private PitchHandData pitchHand;
        private String birthDate;
        private String height;
        private Integer weight;
        private String mlbDebutDate;
        private Boolean active;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PositionData {
        private String abbreviation;
        private String type;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BatSideData {
        private String code;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PitchHandData {
        private String code;
    }
}
