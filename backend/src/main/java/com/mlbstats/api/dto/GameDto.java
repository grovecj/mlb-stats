package com.mlbstats.api.dto;

import com.mlbstats.domain.game.Game;

import java.time.LocalDate;

public record GameDto(
        Long id,
        Integer mlbId,
        Integer season,
        LocalDate gameDate,
        String gameType,
        String status,
        TeamDto homeTeam,
        TeamDto awayTeam,
        Integer homeScore,
        Integer awayScore,
        String venueName,
        String dayNight,
        Integer scheduledInnings
) {
    public static GameDto fromEntity(Game game) {
        return new GameDto(
                game.getId(),
                game.getMlbId(),
                game.getSeason(),
                game.getGameDate(),
                game.getGameType(),
                game.getStatus(),
                game.getHomeTeam() != null ? TeamDto.fromEntity(game.getHomeTeam()) : null,
                game.getAwayTeam() != null ? TeamDto.fromEntity(game.getAwayTeam()) : null,
                game.getHomeScore(),
                game.getAwayScore(),
                game.getVenueName(),
                game.getDayNight(),
                game.getScheduledInnings()
        );
    }
}
