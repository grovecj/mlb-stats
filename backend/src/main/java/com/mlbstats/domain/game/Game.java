package com.mlbstats.domain.game;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mlb_id", unique = true, nullable = false)
    private Integer mlbId;

    @Column(nullable = false)
    private Integer season;

    @Column(name = "game_date", nullable = false)
    private LocalDate gameDate;

    @Column(name = "game_type")
    private String gameType;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "day_night")
    private String dayNight;

    @Column(name = "scheduled_innings")
    private Integer scheduledInnings = 9;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_probable_pitcher_id")
    private Player homeProbablePitcher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_probable_pitcher_id")
    private Player awayProbablePitcher;

    // Live game state fields
    @Column(name = "current_inning")
    private Integer currentInning;

    @Column(name = "inning_half")
    private String inningHalf;

    private Integer outs;

    private Integer balls;

    private Integer strikes;

    @Column(name = "runner_on_first")
    private Boolean runnerOnFirst = false;

    @Column(name = "runner_on_second")
    private Boolean runnerOnSecond = false;

    @Column(name = "runner_on_third")
    private Boolean runnerOnThird = false;

    @Column(name = "home_hits")
    private Integer homeHits;

    @Column(name = "away_hits")
    private Integer awayHits;

    @Column(name = "home_errors")
    private Integer homeErrors;

    @Column(name = "away_errors")
    private Integer awayErrors;

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
