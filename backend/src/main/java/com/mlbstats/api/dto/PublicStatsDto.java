package com.mlbstats.api.dto;

import java.time.LocalDate;

public record PublicStatsDto(
        long teamCount,
        long playerCount,
        long gameCount,
        long currentSeasonGames,
        LocalDate lastUpdated
) {
}
