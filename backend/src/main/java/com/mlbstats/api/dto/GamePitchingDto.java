package com.mlbstats.api.dto;

import com.mlbstats.domain.stats.PlayerGamePitching;

import java.math.BigDecimal;

public record GamePitchingDto(
    Long id,
    Long playerId,
    String playerName,
    String headshotUrl,
    BigDecimal inningsPitched,
    Integer hitsAllowed,
    Integer runsAllowed,
    Integer earnedRuns,
    Integer walks,
    Integer strikeouts,
    Integer homeRunsAllowed,
    Integer pitchesThrown,
    Integer strikes,
    Boolean isStarter,
    Boolean isWinner,
    Boolean isLoser,
    Boolean isSave
) {
    public static GamePitchingDto fromEntity(PlayerGamePitching entity) {
        String headshotUrl = entity.getPlayer().getMlbId() != null
            ? "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/" + entity.getPlayer().getMlbId() + "/headshot/67/current"
            : null;

        return new GamePitchingDto(
            entity.getId(),
            entity.getPlayer().getId(),
            entity.getPlayer().getFullName(),
            headshotUrl,
            entity.getInningsPitched(),
            entity.getHitsAllowed(),
            entity.getRunsAllowed(),
            entity.getEarnedRuns(),
            entity.getWalks(),
            entity.getStrikeouts(),
            entity.getHomeRunsAllowed(),
            entity.getPitchesThrown(),
            entity.getStrikes(),
            entity.getIsStarter(),
            entity.getIsWinner(),
            entity.getIsLoser(),
            entity.getIsSave()
        );
    }
}
