package com.mlbstats.domain.stats;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "player_game_pitching")
@Getter
@Setter
@NoArgsConstructor
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
}
