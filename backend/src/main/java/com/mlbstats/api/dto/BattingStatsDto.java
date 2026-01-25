package com.mlbstats.api.dto;

import com.mlbstats.domain.stats.PlayerBattingStats;

import java.math.BigDecimal;

public record BattingStatsDto(
        Long id,
        PlayerDto player,
        TeamDto team,
        Integer season,
        String gameType,
        Integer gamesPlayed,
        Integer atBats,
        Integer runs,
        Integer hits,
        Integer doubles,
        Integer triples,
        Integer homeRuns,
        Integer rbi,
        Integer stolenBases,
        Integer caughtStealing,
        Integer walks,
        Integer strikeouts,
        BigDecimal battingAvg,
        BigDecimal obp,
        BigDecimal slg,
        BigDecimal ops,
        BigDecimal babip,
        BigDecimal iso,
        Integer plateAppearances,
        Integer totalBases,
        Integer extraBaseHits
) {
    public static BattingStatsDto fromEntity(PlayerBattingStats stats) {
        return new BattingStatsDto(
                stats.getId(),
                stats.getPlayer() != null ? PlayerDto.fromEntity(stats.getPlayer()) : null,
                stats.getTeam() != null ? TeamDto.fromEntity(stats.getTeam()) : null,
                stats.getSeason(),
                stats.getGameType(),
                stats.getGamesPlayed(),
                stats.getAtBats(),
                stats.getRuns(),
                stats.getHits(),
                stats.getDoubles(),
                stats.getTriples(),
                stats.getHomeRuns(),
                stats.getRbi(),
                stats.getStolenBases(),
                stats.getCaughtStealing(),
                stats.getWalks(),
                stats.getStrikeouts(),
                stats.getBattingAvg(),
                stats.getObp(),
                stats.getSlg(),
                stats.getOps(),
                stats.getBabip(),
                stats.getIso(),
                stats.getPlateAppearances(),
                stats.getTotalBases(),
                stats.getExtraBaseHits()
        );
    }
}
