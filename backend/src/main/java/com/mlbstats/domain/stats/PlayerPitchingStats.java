package com.mlbstats.domain.stats;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_pitching_stats")
public class PlayerPitchingStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private Integer season;

    @Column(name = "game_type")
    private String gameType = "R";

    @Column(name = "games_played")
    private Integer gamesPlayed;

    @Column(name = "games_started")
    private Integer gamesStarted;

    private Integer wins;

    private Integer losses;

    private Integer saves;

    private Integer holds;

    @Column(name = "innings_pitched", precision = 5, scale = 1)
    private BigDecimal inningsPitched;

    @Column(name = "hits_allowed")
    private Integer hitsAllowed;

    @Column(name = "runs_allowed")
    private Integer runsAllowed;

    @Column(name = "earned_runs")
    private Integer earnedRuns;

    @Column(name = "home_runs_allowed")
    private Integer homeRunsAllowed;

    private Integer walks;

    private Integer strikeouts;

    @Column(precision = 5, scale = 2)
    private BigDecimal era;

    @Column(precision = 4, scale = 2)
    private BigDecimal whip;

    @Column(name = "k_per_9", precision = 4, scale = 2)
    private BigDecimal kPer9;

    @Column(name = "bb_per_9", precision = 4, scale = 2)
    private BigDecimal bbPer9;

    @Column(name = "h_per_9", precision = 4, scale = 2)
    private BigDecimal hPer9;

    @Column(name = "pitches_thrown")
    private Integer pitchesThrown;

    private Integer strikes;

    private Integer balls;

    @Column(name = "complete_games")
    private Integer completeGames;

    private Integer shutouts;

    @Column(name = "quality_starts")
    private Integer qualityStarts;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PlayerPitchingStats() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public Integer getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Integer gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

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

    public BigDecimal getInningsPitched() {
        return inningsPitched;
    }

    public void setInningsPitched(BigDecimal inningsPitched) {
        this.inningsPitched = inningsPitched;
    }

    public Integer getHitsAllowed() {
        return hitsAllowed;
    }

    public void setHitsAllowed(Integer hitsAllowed) {
        this.hitsAllowed = hitsAllowed;
    }

    public Integer getRunsAllowed() {
        return runsAllowed;
    }

    public void setRunsAllowed(Integer runsAllowed) {
        this.runsAllowed = runsAllowed;
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

    public Integer getWalks() {
        return walks;
    }

    public void setWalks(Integer walks) {
        this.walks = walks;
    }

    public Integer getStrikeouts() {
        return strikeouts;
    }

    public void setStrikeouts(Integer strikeouts) {
        this.strikeouts = strikeouts;
    }

    public BigDecimal getEra() {
        return era;
    }

    public void setEra(BigDecimal era) {
        this.era = era;
    }

    public BigDecimal getWhip() {
        return whip;
    }

    public void setWhip(BigDecimal whip) {
        this.whip = whip;
    }

    public BigDecimal getkPer9() {
        return kPer9;
    }

    public void setkPer9(BigDecimal kPer9) {
        this.kPer9 = kPer9;
    }

    public BigDecimal getBbPer9() {
        return bbPer9;
    }

    public void setBbPer9(BigDecimal bbPer9) {
        this.bbPer9 = bbPer9;
    }

    public BigDecimal gethPer9() {
        return hPer9;
    }

    public void sethPer9(BigDecimal hPer9) {
        this.hPer9 = hPer9;
    }

    public Integer getPitchesThrown() {
        return pitchesThrown;
    }

    public void setPitchesThrown(Integer pitchesThrown) {
        this.pitchesThrown = pitchesThrown;
    }

    public Integer getStrikes() {
        return strikes;
    }

    public void setStrikes(Integer strikes) {
        this.strikes = strikes;
    }

    public Integer getBalls() {
        return balls;
    }

    public void setBalls(Integer balls) {
        this.balls = balls;
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

    public Integer getQualityStarts() {
        return qualityStarts;
    }

    public void setQualityStarts(Integer qualityStarts) {
        this.qualityStarts = qualityStarts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
