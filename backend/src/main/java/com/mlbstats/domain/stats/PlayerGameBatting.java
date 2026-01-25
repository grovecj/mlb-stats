package com.mlbstats.domain.stats;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "player_game_batting")
@Getter
@Setter
@NoArgsConstructor
public class PlayerGameBatting {

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

    @Column(name = "batting_order")
    private Integer battingOrder;

    @Column(name = "position_played")
    private String positionPlayed;
}
