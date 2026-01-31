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
@Table(name = "player_pitching_splits")
@Getter
@Setter
@NoArgsConstructor
public class PlayerPitchingSplit {

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

    @Column(name = "games_started")
    private Integer gamesStarted;

    @Column(name = "innings_pitched", precision = 5, scale = 1)
    private BigDecimal inningsPitched;

    private Integer wins;

    private Integer losses;

    private Integer saves;

    private Integer holds;

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

    // Rate stats
    @Column(precision = 5, scale = 2)
    private BigDecimal era;

    @Column(precision = 4, scale = 2)
    private BigDecimal whip;

    @Column(name = "k_per_9", precision = 4, scale = 2)
    private BigDecimal kPer9;

    @Column(name = "bb_per_9", precision = 4, scale = 2)
    private BigDecimal bbPer9;

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
