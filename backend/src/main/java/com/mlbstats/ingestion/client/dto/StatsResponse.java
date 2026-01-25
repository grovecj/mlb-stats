package com.mlbstats.ingestion.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatsResponse {

    private List<StatGroup> stats;

    public List<StatGroup> getStats() {
        return stats;
    }

    public void setStats(List<StatGroup> stats) {
        this.stats = stats;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatGroup {
        private String type;
        private String group;
        private List<StatSplit> splits;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public List<StatSplit> getSplits() {
            return splits;
        }

        public void setSplits(List<StatSplit> splits) {
            this.splits = splits;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatSplit {
        private String season;
        private TeamData team;
        private StatData stat;

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }

        public TeamData getTeam() {
            return team;
        }

        public void setTeam(TeamData team) {
            this.team = team;
        }

        public StatData getStat() {
            return stat;
        }

        public void setStat(StatData stat) {
            this.stat = stat;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamData {
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

        // Getters and setters for batting stats
        public Integer getGamesPlayed() {
            return gamesPlayed;
        }

        public void setGamesPlayed(Integer gamesPlayed) {
            this.gamesPlayed = gamesPlayed;
        }

        public Integer getAtBats() {
            return atBats;
        }

        public void setAtBats(Integer atBats) {
            this.atBats = atBats;
        }

        public Integer getRuns() {
            return runs;
        }

        public void setRuns(Integer runs) {
            this.runs = runs;
        }

        public Integer getHits() {
            return hits;
        }

        public void setHits(Integer hits) {
            this.hits = hits;
        }

        public Integer getDoubles() {
            return doubles;
        }

        public void setDoubles(Integer doubles) {
            this.doubles = doubles;
        }

        public Integer getTriples() {
            return triples;
        }

        public void setTriples(Integer triples) {
            this.triples = triples;
        }

        public Integer getHomeRuns() {
            return homeRuns;
        }

        public void setHomeRuns(Integer homeRuns) {
            this.homeRuns = homeRuns;
        }

        public Integer getRbi() {
            return rbi;
        }

        public void setRbi(Integer rbi) {
            this.rbi = rbi;
        }

        public Integer getStolenBases() {
            return stolenBases;
        }

        public void setStolenBases(Integer stolenBases) {
            this.stolenBases = stolenBases;
        }

        public Integer getCaughtStealing() {
            return caughtStealing;
        }

        public void setCaughtStealing(Integer caughtStealing) {
            this.caughtStealing = caughtStealing;
        }

        public Integer getBaseOnBalls() {
            return baseOnBalls;
        }

        public void setBaseOnBalls(Integer baseOnBalls) {
            this.baseOnBalls = baseOnBalls;
        }

        public Integer getStrikeOuts() {
            return strikeOuts;
        }

        public void setStrikeOuts(Integer strikeOuts) {
            this.strikeOuts = strikeOuts;
        }

        public String getAvg() {
            return avg;
        }

        public void setAvg(String avg) {
            this.avg = avg;
        }

        public String getObp() {
            return obp;
        }

        public void setObp(String obp) {
            this.obp = obp;
        }

        public String getSlg() {
            return slg;
        }

        public void setSlg(String slg) {
            this.slg = slg;
        }

        public String getOps() {
            return ops;
        }

        public void setOps(String ops) {
            this.ops = ops;
        }

        public Integer getPlateAppearances() {
            return plateAppearances;
        }

        public void setPlateAppearances(Integer plateAppearances) {
            this.plateAppearances = plateAppearances;
        }

        public Integer getTotalBases() {
            return totalBases;
        }

        public void setTotalBases(Integer totalBases) {
            this.totalBases = totalBases;
        }

        public Integer getIntentionalWalks() {
            return intentionalWalks;
        }

        public void setIntentionalWalks(Integer intentionalWalks) {
            this.intentionalWalks = intentionalWalks;
        }

        public Integer getHitByPitch() {
            return hitByPitch;
        }

        public void setHitByPitch(Integer hitByPitch) {
            this.hitByPitch = hitByPitch;
        }

        public Integer getSacFlies() {
            return sacFlies;
        }

        public void setSacFlies(Integer sacFlies) {
            this.sacFlies = sacFlies;
        }

        public Integer getGroundIntoDoublePlay() {
            return groundIntoDoublePlay;
        }

        public void setGroundIntoDoublePlay(Integer groundIntoDoublePlay) {
            this.groundIntoDoublePlay = groundIntoDoublePlay;
        }

        // Getters and setters for pitching stats
        public Integer getGamesStarted() {
            return gamesStarted;
        }

        public void setGamesStarted(Integer gamesStarted) {
            this.gamesStarted = gamesStarted;
        }

        public Integer getWins() {
            return wins;
        }

        public void setWins(Integer wins) {
            this.wins = wins;
        }

        public Integer getLosses() {
            return losses;
        }

        public void setLosses(Integer losses) {
            this.losses = losses;
        }

        public Integer getSaves() {
            return saves;
        }

        public void setSaves(Integer saves) {
            this.saves = saves;
        }

        public Integer getHolds() {
            return holds;
        }

        public void setHolds(Integer holds) {
            this.holds = holds;
        }

        public String getInningsPitched() {
            return inningsPitched;
        }

        public void setInningsPitched(String inningsPitched) {
            this.inningsPitched = inningsPitched;
        }

        public Integer getHitBatsmen() {
            return hitBatsmen;
        }

        public void setHitBatsmen(Integer hitBatsmen) {
            this.hitBatsmen = hitBatsmen;
        }

        public Integer getEarnedRuns() {
            return earnedRuns;
        }

        public void setEarnedRuns(Integer earnedRuns) {
            this.earnedRuns = earnedRuns;
        }

        public Integer getHomeRunsAllowed() {
            return homeRunsAllowed;
        }

        public void setHomeRunsAllowed(Integer homeRunsAllowed) {
            this.homeRunsAllowed = homeRunsAllowed;
        }

        public String getEra() {
            return era;
        }

        public void setEra(String era) {
            this.era = era;
        }

        public String getWhip() {
            return whip;
        }

        public void setWhip(String whip) {
            this.whip = whip;
        }

        public Integer getNumberOfPitches() {
            return numberOfPitches;
        }

        public void setNumberOfPitches(Integer numberOfPitches) {
            this.numberOfPitches = numberOfPitches;
        }

        public Integer getStrikes() {
            return strikes;
        }

        public void setStrikes(Integer strikes) {
            this.strikes = strikes;
        }

        public Integer getCompleteGames() {
            return completeGames;
        }

        public void setCompleteGames(Integer completeGames) {
            this.completeGames = completeGames;
        }

        public Integer getShutouts() {
            return shutouts;
        }

        public void setShutouts(Integer shutouts) {
            this.shutouts = shutouts;
        }
    }
}
