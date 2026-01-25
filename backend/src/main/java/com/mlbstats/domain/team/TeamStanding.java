package com.mlbstats.domain.team;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_standings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"team_id", "season"})
})
@Getter
@Setter
@NoArgsConstructor
public class TeamStanding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Integer season;

    @Column(nullable = false)
    private Integer wins;

    @Column(nullable = false)
    private Integer losses;

    @Column(name = "winning_percentage", precision = 4, scale = 3)
    private BigDecimal winningPercentage;

    @Column(name = "games_back")
    private String gamesBack;

    @Column(name = "wild_card_games_back")
    private String wildCardGamesBack;

    @Column(name = "division_rank")
    private Integer divisionRank;

    @Column(name = "league_rank")
    private Integer leagueRank;

    @Column(name = "wild_card_rank")
    private Integer wildCardRank;

    @Column(name = "runs_scored")
    private Integer runsScored;

    @Column(name = "runs_allowed")
    private Integer runsAllowed;

    @Column(name = "run_differential")
    private Integer runDifferential;

    @Column(name = "streak_code")
    private String streakCode;

    @Column(name = "home_wins")
    private Integer homeWins;

    @Column(name = "home_losses")
    private Integer homeLosses;

    @Column(name = "away_wins")
    private Integer awayWins;

    @Column(name = "away_losses")
    private Integer awayLosses;

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
