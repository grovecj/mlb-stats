package com.mlbstats.api.dto;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.stats.PlayerGamePitching;
import com.mlbstats.domain.team.Team;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PitchingGameLogDto(
    Long gameId,
    LocalDate gameDate,
    String opponent,
    String opponentAbbreviation,
    Long opponentId,
    boolean isHome,
    String result,
    Integer teamScore,
    Integer opponentScore,
    String decision,
    BigDecimal inningsPitched,
    Integer hitsAllowed,
    Integer runsAllowed,
    Integer earnedRuns,
    Integer walks,
    Integer strikeouts,
    Integer homeRunsAllowed,
    Integer pitchesThrown,
    Integer strikes,
    Boolean isStarter
) {
    public static PitchingGameLogDto fromEntity(PlayerGamePitching entity) {
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

        // Determine decision (W/L/S/H or blank)
        String decision = null;
        if (Boolean.TRUE.equals(entity.getIsWinner())) {
            decision = "W";
        } else if (Boolean.TRUE.equals(entity.getIsLoser())) {
            decision = "L";
        } else if (Boolean.TRUE.equals(entity.getIsSave())) {
            decision = "S";
        }

        return new PitchingGameLogDto(
            game.getId(),
            game.getGameDate(),
            opponent != null ? opponent.getName() : null,
            opponent != null ? opponent.getAbbreviation() : null,
            opponent != null ? opponent.getId() : null,
            isHome,
            result,
            teamScore,
            opponentScore,
            decision,
            entity.getInningsPitched(),
            entity.getHitsAllowed(),
            entity.getRunsAllowed(),
            entity.getEarnedRuns(),
            entity.getWalks(),
            entity.getStrikeouts(),
            entity.getHomeRunsAllowed(),
            entity.getPitchesThrown(),
            entity.getStrikes(),
            entity.getIsStarter()
        );
    }
}
