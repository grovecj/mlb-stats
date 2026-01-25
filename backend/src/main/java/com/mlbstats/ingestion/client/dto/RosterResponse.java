package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RosterResponse {

    private List<RosterEntry> roster;

    public List<RosterEntry> getRoster() {
        return roster;
    }

    public void setRoster(List<RosterEntry> roster) {
        this.roster = roster;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RosterEntry {
        private PersonData person;
        private String jerseyNumber;
        private PositionData position;
        private StatusData status;

        public PersonData getPerson() {
            return person;
        }

        public void setPerson(PersonData person) {
            this.person = person;
        }

        public String getJerseyNumber() {
            return jerseyNumber;
        }

        public void setJerseyNumber(String jerseyNumber) {
            this.jerseyNumber = jerseyNumber;
        }

        public PositionData getPosition() {
            return position;
        }

        public void setPosition(PositionData position) {
            this.position = position;
        }

        public StatusData getStatus() {
            return status;
        }

        public void setStatus(StatusData status) {
            this.status = status;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusData {
        private String code;
        private String description;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonData {
        private Integer id;
        private String fullName;

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
}
