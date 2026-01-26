package com.mlbstats.api.dto;

public record SeasonDataDto(
        Integer season,
        long gamesCount,
        long battingStatsCount,
        long pitchingStatsCount,
        long rosterEntriesCount,
        long standingsCount,
        boolean isCurrent
) {
}
