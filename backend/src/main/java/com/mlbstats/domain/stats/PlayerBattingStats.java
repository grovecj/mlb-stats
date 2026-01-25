package com.mlbstats.domain.stats;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_batting_stats")
public class PlayerBattingStats {

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

    @Column(name = "at_bats")
    private Integer atBats;

    private Integer runs;

    private Integer hits;

    private Integer doubles;

    private Integer triples;

    @Column(name = "home_runs")
    private Integer homeRuns;

    private Integer rbi;

    @Column(name = "stolen_bases")
    private Integer stolenBases;

    @Column(name = "caught_stealing")
    private Integer caughtStealing;

    private Integer walks;

    private Integer strikeouts;

    @Column(name = "batting_avg", precision = 4, scale = 3)
    private BigDecimal battingAvg;

    @Column(precision = 4, scale = 3)
    private BigDecimal obp;

    @Column(precision = 4, scale = 3)
    private BigDecimal slg;

    @Column(precision = 4, scale = 3)
    private BigDecimal ops;

    @Column(precision = 4, scale = 3)
    private BigDecimal babip;

    @Column(precision = 4, scale = 3)
    private BigDecimal iso;

    @Column(name = "plate_appearances")
    private Integer plateAppearances;

    @Column(name = "total_bases")
    private Integer totalBases;

    @Column(name = "extra_base_hits")
    private Integer extraBaseHits;

    @Column(name = "intentional_walks")
    private Integer intentionalWalks;

    @Column(name = "hit_by_pitch")
    private Integer hitByPitch;

    @Column(name = "sac_flies")
    private Integer sacFlies;

    @Column(name = "ground_into_dp")
    private Integer groundIntoDp;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PlayerBattingStats() {
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

    public BigDecimal getBattingAvg() {
        return battingAvg;
    }

    public void setBattingAvg(BigDecimal battingAvg) {
        this.battingAvg = battingAvg;
    }

    public BigDecimal getObp() {
        return obp;
    }

    public void setObp(BigDecimal obp) {
        this.obp = obp;
    }

    public BigDecimal getSlg() {
        return slg;
    }

    public void setSlg(BigDecimal slg) {
        this.slg = slg;
    }

    public BigDecimal getOps() {
        return ops;
    }

    public void setOps(BigDecimal ops) {
        this.ops = ops;
    }

    public BigDecimal getBabip() {
        return babip;
    }

    public void setBabip(BigDecimal babip) {
        this.babip = babip;
    }

    public BigDecimal getIso() {
        return iso;
    }

    public void setIso(BigDecimal iso) {
        this.iso = iso;
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

    public Integer getExtraBaseHits() {
        return extraBaseHits;
    }

    public void setExtraBaseHits(Integer extraBaseHits) {
        this.extraBaseHits = extraBaseHits;
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

    public Integer getGroundIntoDp() {
        return groundIntoDp;
    }

    public void setGroundIntoDp(Integer groundIntoDp) {
        this.groundIntoDp = groundIntoDp;
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
