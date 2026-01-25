package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleResponse {

    private List<DateEntry> dates;

    public List<DateEntry> getDates() {
        return dates;
    }

    public void setDates(List<DateEntry> dates) {
        this.dates = dates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DateEntry {
        private String date;
        private List<GameData> games;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<GameData> getGames() {
            return games;
        }

        public void setGames(List<GameData> games) {
            this.games = games;
        }
    }

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

        public Integer getGamePk() {
            return gamePk;
        }

        public void setGamePk(Integer gamePk) {
            this.gamePk = gamePk;
        }

        public String getGameDate() {
            return gameDate;
        }

        public void setGameDate(String gameDate) {
            this.gameDate = gameDate;
        }

        public String getGameType() {
            return gameType;
        }

        public void setGameType(String gameType) {
            this.gameType = gameType;
        }

        public StatusData getStatus() {
            return status;
        }

        public void setStatus(StatusData status) {
            this.status = status;
        }

        public TeamsData getTeams() {
            return teams;
        }

        public void setTeams(TeamsData teams) {
            this.teams = teams;
        }

        public VenueData getVenue() {
            return venue;
        }

        public void setVenue(VenueData venue) {
            this.venue = venue;
        }

        public String getDayNight() {
            return dayNight;
        }

        public void setDayNight(String dayNight) {
            this.dayNight = dayNight;
        }

        public Integer getScheduledInnings() {
            return scheduledInnings;
        }

        public void setScheduledInnings(Integer scheduledInnings) {
            this.scheduledInnings = scheduledInnings;
        }

        public Integer getSeason() {
            return season;
        }

        public void setSeason(Integer season) {
            this.season = season;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusData {
        private String abstractGameState;
        private String detailedState;

        public String getAbstractGameState() {
            return abstractGameState;
        }

        public void setAbstractGameState(String abstractGameState) {
            this.abstractGameState = abstractGameState;
        }

        public String getDetailedState() {
            return detailedState;
        }

        public void setDetailedState(String detailedState) {
            this.detailedState = detailedState;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamsData {
        private TeamGameData home;
        private TeamGameData away;

        public TeamGameData getHome() {
            return home;
        }

        public void setHome(TeamGameData home) {
            this.home = home;
        }

        public TeamGameData getAway() {
            return away;
        }

        public void setAway(TeamGameData away) {
            this.away = away;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamGameData {
        private Integer score;
        private TeamRefData team;

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public TeamRefData getTeam() {
            return team;
        }

        public void setTeam(TeamRefData team) {
            this.team = team;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamRefData {
        private Integer id;
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VenueData {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
