package com.mlbstats.api.dto;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.stats.PlayerGameBatting;
import com.mlbstats.domain.team.Team;

import java.time.LocalDate;

public record BattingGameLogDto(
    Long gameId,
    LocalDate gameDate,
    String opponent,
    String opponentAbbreviation,
    Long opponentId,
    boolean isHome,
    String result,
    Integer teamScore,
    Integer opponentScore,
    Integer atBats,
    Integer runs,
    Integer hits,
    Integer doubles,
    Integer triples,
    Integer homeRuns,
    Integer rbi,
    Integer walks,
    Integer strikeouts,
    Integer stolenBases,
    Integer battingOrder,
    String position
) {
    public static BattingGameLogDto fromEntity(PlayerGameBatting entity) {
        Game game = entity.getGame();
        Team playerTeam = entity.getTeam();

        boolean isHome = game.getHomeTeam() != null &&
                         game.getHomeTeam().getId().equals(playerTeam.getId());

        Team opponent = isHome ? game.getAwayTeam() : game.getHomeTeam();
        Integer teamScore = isHome ? game.getHomeScore() : game.getAwayScore();
        Integer opponentScore = isHome ? game.getAwayScore() : game.getHomeScore();

        String result = null;
        if (teamScore != null && opponentScore != null) {
            if (teamScore > opponentScore) {
                result = "W " + teamScore + "-" + opponentScore;
            } else if (teamScore < opponentScore) {
                result = "L " + teamScore + "-" + opponentScore;
            } else {
                result = "T " + teamScore + "-" + opponentScore;
            }
        }

        return new BattingGameLogDto(
            game.getId(),
            game.getGameDate(),
            opponent != null ? opponent.getName() : null,
            opponent != null ? opponent.getAbbreviation() : null,
            opponent != null ? opponent.getId() : null,
            isHome,
            result,
            teamScore,
            opponentScore,
            entity.getAtBats(),
            entity.getRuns(),
            entity.getHits(),
            entity.getDoubles(),
            entity.getTriples(),
            entity.getHomeRuns(),
            entity.getRbi(),
            entity.getWalks(),
            entity.getStrikeouts(),
            entity.getStolenBases(),
            entity.getBattingOrder(),
            entity.getPositionPlayed()
        );
    }
}
