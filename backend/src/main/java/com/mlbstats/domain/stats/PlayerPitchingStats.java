package com.mlbstats.domain.stats;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_pitching_stats")
@Getter
@Setter
@NoArgsConstructor
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

    @Column(nullable = false)
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

    // Advanced Sabermetric Stats

    @Column(precision = 4, scale = 1)
    private BigDecimal war;

    @Column(precision = 5, scale = 2)
    private BigDecimal fip;

    @Column(precision = 5, scale = 2)
    private BigDecimal xfip;

    @Column(precision = 5, scale = 2)
    private BigDecimal siera;

    @Column(name = "k_pct", precision = 4, scale = 1)
    private BigDecimal kPct;

    @Column(name = "bb_pct", precision = 4, scale = 1)
    private BigDecimal bbPct;

    @Column(name = "gb_pct", precision = 4, scale = 1)
    private BigDecimal gbPct;

    @Column(name = "fb_pct", precision = 4, scale = 1)
    private BigDecimal fbPct;

    @Column(name = "hard_hit_pct_against", precision = 4, scale = 1)
    private BigDecimal hardHitPctAgainst;

    @Column(name = "avg_exit_velocity_against", precision = 4, scale = 1)
    private BigDecimal avgExitVelocityAgainst;

    @Column(precision = 5, scale = 2)
    private BigDecimal xera;

    @Column(name = "avg_spin_rate")
    private Integer avgSpinRate;

    @Column(name = "whiff_pct", precision = 4, scale = 1)
    private BigDecimal whiffPct;

    @Column(name = "chase_pct", precision = 4, scale = 1)
    private BigDecimal chasePct;

    // gWAR (Grove WAR) components
    @Column(precision = 4, scale = 1)
    private BigDecimal gwar;

    @Column(name = "gwar_pitching", precision = 5, scale = 1)
    private BigDecimal gwarPitching;

    @Column(name = "gwar_replacement", precision = 5, scale = 1)
    private BigDecimal gwarReplacement;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
