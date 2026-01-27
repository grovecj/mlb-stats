package com.mlbstats.domain.game;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_innings")
@Getter
@Setter
@NoArgsConstructor
public class GameInning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "inning_number", nullable = false)
    private Integer inningNumber;

    @Column(name = "away_runs")
    private Integer awayRuns = 0;

    @Column(name = "home_runs")
    private Integer homeRuns = 0;

    @Column(name = "away_hits")
    private Integer awayHits;

    @Column(name = "home_hits")
    private Integer homeHits;

    @Column(name = "away_errors")
    private Integer awayErrors;

    @Column(name = "home_errors")
    private Integer homeErrors;

    @Column(name = "away_left_on_base")
    private Integer awayLeftOnBase;

    @Column(name = "home_left_on_base")
    private Integer homeLeftOnBase;
}
