package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerResponse {

    private List<PlayerData> people;

    public List<PlayerData> getPeople() {
        return people;
    }

    public void setPeople(List<PlayerData> people) {
        this.people = people;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerData {
        private Integer id;
        private String fullName;
        private String firstName;
        private String lastName;
        private String primaryNumber;
        private PositionData primaryPosition;
        private String batSide;
        private String pitchHand;
        private String birthDate;
        private String height;
        private Integer weight;
        private String mlbDebutDate;
        private Boolean active;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPrimaryNumber() {
            return primaryNumber;
        }

        public void setPrimaryNumber(String primaryNumber) {
            this.primaryNumber = primaryNumber;
        }

        public PositionData getPrimaryPosition() {
            return primaryPosition;
        }

        public void setPrimaryPosition(PositionData primaryPosition) {
            this.primaryPosition = primaryPosition;
        }

        public String getBatSide() {
            return batSide;
        }

        public void setBatSide(String batSide) {
            this.batSide = batSide;
        }

        public String getPitchHand() {
            return pitchHand;
        }

        public void setPitchHand(String pitchHand) {
            this.pitchHand = pitchHand;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }

        public String getMlbDebutDate() {
            return mlbDebutDate;
        }

        public void setMlbDebutDate(String mlbDebutDate) {
            this.mlbDebutDate = mlbDebutDate;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PositionData {
        private String abbreviation;
        private String type;

        public String getAbbreviation() {
            return abbreviation;
        }

        public void setAbbreviation(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BatSideData {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PitchHandData {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
