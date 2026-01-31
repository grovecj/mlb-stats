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
@Table(name = "player_batting_splits")
@Getter
@Setter
@NoArgsConstructor
public class PlayerBattingSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private Integer season;

    @Enumerated(EnumType.STRING)
    @Column(name = "split_type", nullable = false, length = 30)
    private SplitType splitType;

    // Counting stats
    @Column(name = "games_played")
    private Integer gamesPlayed;

    @Column(name = "plate_appearances")
    private Integer plateAppearances;

    @Column(name = "at_bats")
    private Integer atBats;

    private Integer runs;

    private Integer hits;

    private Integer doubles;

    private Integer triples;

    @Column(name = "home_runs")
    private Integer homeRuns;

    private Integer rbi;

    private Integer walks;

    private Integer strikeouts;

    @Column(name = "stolen_bases")
    private Integer stolenBases;

    @Column(name = "caught_stealing")
    private Integer caughtStealing;

    @Column(name = "hit_by_pitch")
    private Integer hitByPitch;

    @Column(name = "sac_flies")
    private Integer sacFlies;

    @Column(name = "ground_into_dp")
    private Integer groundIntoDp;

    // Rate stats
    @Column(name = "batting_avg", precision = 4, scale = 3)
    private BigDecimal battingAvg;

    @Column(precision = 4, scale = 3)
    private BigDecimal obp;

    @Column(precision = 4, scale = 3)
    private BigDecimal slg;

    @Column(precision = 4, scale = 3)
    private BigDecimal ops;

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
