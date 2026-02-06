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
@Table(name = "player_batting_stats")
@Getter
@Setter
@NoArgsConstructor
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

    @Column(nullable = false)
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

    // Advanced Sabermetric Stats

    @Column(precision = 4, scale = 1)
    private BigDecimal war;

    @Column(precision = 4, scale = 3)
    private BigDecimal woba;

    @Column(name = "wrc_plus")
    private Integer wrcPlus;

    @Column(name = "hard_hit_pct", precision = 4, scale = 1)
    private BigDecimal hardHitPct;

    @Column(name = "barrel_pct", precision = 4, scale = 1)
    private BigDecimal barrelPct;

    @Column(name = "avg_exit_velocity", precision = 4, scale = 1)
    private BigDecimal avgExitVelocity;

    @Column(name = "avg_launch_angle", precision = 4, scale = 1)
    private BigDecimal avgLaunchAngle;

    @Column(name = "sprint_speed", precision = 4, scale = 1)
    private BigDecimal sprintSpeed;

    @Column(precision = 4, scale = 3)
    private BigDecimal xba;

    @Column(precision = 4, scale = 3)
    private BigDecimal xslg;

    @Column(precision = 4, scale = 3)
    private BigDecimal xwoba;

    @Column(name = "k_pct", precision = 4, scale = 1)
    private BigDecimal kPct;

    @Column(name = "bb_pct", precision = 4, scale = 1)
    private BigDecimal bbPct;

    // gWAR (Grove WAR) components
    @Column(precision = 4, scale = 1)
    private BigDecimal gwar;

    @Column(name = "gwar_batting", precision = 5, scale = 1)
    private BigDecimal gwarBatting;

    @Column(name = "gwar_baserunning", precision = 5, scale = 1)
    private BigDecimal gwarBaserunning;

    @Column(name = "gwar_fielding", precision = 5, scale = 1)
    private BigDecimal gwarFielding;

    @Column(name = "gwar_positional", precision = 5, scale = 1)
    private BigDecimal gwarPositional;

    @Column(name = "gwar_replacement", precision = 5, scale = 1)
    private BigDecimal gwarReplacement;

    /**
     * Outs Above Average from Baseball Savant - used for fielding component of gWAR
     */
    @Column
    private Integer oaa;

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
