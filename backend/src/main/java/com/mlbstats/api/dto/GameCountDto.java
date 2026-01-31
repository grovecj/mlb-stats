package com.mlbstats.api.dto;

import java.time.LocalDate;

/**
 * DTO for calendar monthly view showing game counts per day.
 * Extremely lightweight for efficient month-at-a-glance rendering.
 */
public record GameCountDto(
        LocalDate date,
        int totalGames,
        int homeGames,
        int awayGames
) {
    /**
     * Factory for all-teams view (no team filter).
     */
    public static GameCountDto forAllTeams(LocalDate date, int totalGames) {
        return new GameCountDto(date, totalGames, 0, 0);
    }

    /**
     * Factory for team-specific view with home/away breakdown.
     */
    public static GameCountDto forTeam(LocalDate date, int homeGames, int awayGames) {
        return new GameCountDto(date, homeGames + awayGames, homeGames, awayGames);
    }
}
