package com.mlbstats.api.dto;

import com.mlbstats.domain.stats.PlayerPitchingStats;

import java.math.BigDecimal;

public record PitchingStatsDto(
        Long id,
        PlayerDto player,
        TeamDto team,
        Integer season,
        String gameType,
        Integer gamesPlayed,
        Integer gamesStarted,
        Integer wins,
        Integer losses,
        Integer saves,
        Integer holds,
        BigDecimal inningsPitched,
        Integer hitsAllowed,
        Integer runsAllowed,
        Integer earnedRuns,
        Integer homeRunsAllowed,
        Integer walks,
        Integer strikeouts,
        BigDecimal era,
        BigDecimal whip,
        BigDecimal kPer9,
        BigDecimal bbPer9,
        BigDecimal hPer9,
        Integer completeGames,
        Integer shutouts
) {
    public static PitchingStatsDto fromEntity(PlayerPitchingStats stats) {
        return new PitchingStatsDto(
                stats.getId(),
                stats.getPlayer() != null ? PlayerDto.fromEntity(stats.getPlayer()) : null,
                stats.getTeam() != null ? TeamDto.fromEntity(stats.getTeam()) : null,
                stats.getSeason(),
                stats.getGameType(),
                stats.getGamesPlayed(),
                stats.getGamesStarted(),
                stats.getWins(),
                stats.getLosses(),
                stats.getSaves(),
                stats.getHolds(),
                stats.getInningsPitched(),
                stats.getHitsAllowed(),
                stats.getRunsAllowed(),
                stats.getEarnedRuns(),
                stats.getHomeRunsAllowed(),
                stats.getWalks(),
                stats.getStrikeouts(),
                stats.getEra(),
                stats.getWhip(),
                stats.getKPer9(),
                stats.getBbPer9(),
                stats.getHPer9(),
                stats.getCompleteGames(),
                stats.getShutouts()
        );
    }
}
