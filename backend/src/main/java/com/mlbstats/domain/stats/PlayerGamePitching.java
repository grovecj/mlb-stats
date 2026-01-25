package com.mlbstats.domain.stats;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "player_game_pitching")
public class PlayerGamePitching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "innings_pitched", precision = 4, scale = 1)
    private BigDecimal inningsPitched;

    @Column(name = "hits_allowed")
    private Integer hitsAllowed;

    @Column(name = "runs_allowed")
    private Integer runsAllowed;

    @Column(name = "earned_runs")
    private Integer earnedRuns;

    private Integer walks;

    private Integer strikeouts;

    @Column(name = "home_runs_allowed")
    private Integer homeRunsAllowed;

    @Column(name = "pitches_thrown")
    private Integer pitchesThrown;

    private Integer strikes;

    @Column(name = "is_starter")
    private Boolean isStarter;

    @Column(name = "is_winner")
    private Boolean isWinner;

    @Column(name = "is_loser")
    private Boolean isLoser;

    @Column(name = "is_save")
    private Boolean isSave;

    public PlayerGamePitching() {
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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

    public Integer getHomeRunsAllowed() {
        return homeRunsAllowed;
    }

    public void setHomeRunsAllowed(Integer homeRunsAllowed) {
        this.homeRunsAllowed = homeRunsAllowed;
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

    public Boolean getIsStarter() {
        return isStarter;
    }

    public void setIsStarter(Boolean isStarter) {
        this.isStarter = isStarter;
    }

    public Boolean getIsWinner() {
        return isWinner;
    }

    public void setIsWinner(Boolean isWinner) {
        this.isWinner = isWinner;
    }

    public Boolean getIsLoser() {
        return isLoser;
    }

    public void setIsLoser(Boolean isLoser) {
        this.isLoser = isLoser;
    }

    public Boolean getIsSave() {
        return isSave;
    }

    public void setIsSave(Boolean isSave) {
        this.isSave = isSave;
    }
}
