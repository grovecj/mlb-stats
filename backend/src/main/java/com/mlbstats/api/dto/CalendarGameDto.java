package com.mlbstats.api.dto;

import com.mlbstats.domain.game.Game;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Lightweight DTO for calendar views. Contains only essential fields
 * for displaying games in week/month calendar grids.
 * ~60% smaller than full GameDto.
 */
public record CalendarGameDto(
        Long id,
        LocalDate gameDate,
        LocalTime scheduledTime,
        String status,
        Long homeTeamId,
        String homeTeamAbbr,
        Long awayTeamId,
        String awayTeamAbbr,
        Integer homeScore,
        Integer awayScore
) {
    public static CalendarGameDto fromEntity(Game game) {
        return new CalendarGameDto(
                game.getId(),
                game.getGameDate(),
                game.getScheduledTime(),
                game.getStatus(),
                game.getHomeTeam() != null ? game.getHomeTeam().getId() : null,
                game.getHomeTeam() != null ? game.getHomeTeam().getAbbreviation() : null,
                game.getAwayTeam() != null ? game.getAwayTeam().getId() : null,
                game.getAwayTeam() != null ? game.getAwayTeam().getAbbreviation() : null,
                game.getHomeScore(),
                game.getAwayScore()
        );
    }
}
