package com.mlbstats.api.dto;

import com.mlbstats.domain.stats.PlayerGameBatting;

public record GameBattingDto(
    Long id,
    Long playerId,
    String playerName,
    String headshotUrl,
    Integer battingOrder,
    String position,
    Integer atBats,
    Integer runs,
    Integer hits,
    Integer doubles,
    Integer triples,
    Integer homeRuns,
    Integer rbi,
    Integer walks,
    Integer strikeouts,
    Integer stolenBases
) {
    public static GameBattingDto fromEntity(PlayerGameBatting entity) {
        String headshotUrl = entity.getPlayer().getMlbId() != null
            ? "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/" + entity.getPlayer().getMlbId() + "/headshot/67/current"
            : null;

        return new GameBattingDto(
            entity.getId(),
            entity.getPlayer().getId(),
            entity.getPlayer().getFullName(),
            headshotUrl,
            entity.getBattingOrder(),
            entity.getPositionPlayed(),
            entity.getAtBats(),
            entity.getRuns(),
            entity.getHits(),
            entity.getDoubles(),
            entity.getTriples(),
            entity.getHomeRuns(),
            entity.getRbi(),
            entity.getWalks(),
            entity.getStrikeouts(),
            entity.getStolenBases()
        );
    }
}
