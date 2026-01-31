package com.mlbstats.ingestion.mapper;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.dto.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class GameMapper {

    private static final ZoneId EASTERN_ZONE = ZoneId.of("America/New_York");

    private final PlayerRepository playerRepository;

    public Game toEntity(ScheduleResponse.GameData dto, Team homeTeam, Team awayTeam) {
        Game game = new Game();
        game.setMlbId(dto.getGamePk());
        game.setSeason(dto.getSeason());
        game.setGameType(dto.getGameType());
        game.setDayNight(dto.getDayNight());
        game.setScheduledInnings(dto.getScheduledInnings() != null ? dto.getScheduledInnings() : 9);

        if (dto.getGameDate() != null) {
            // Parse both date and time from the same zoned instant to ensure consistency
            ZonedDateTime easternTime = parseToEastern(dto.getGameDate());
            if (easternTime != null) {
                game.setGameDate(easternTime.toLocalDate());
                game.setScheduledTime(easternTime.toLocalTime());
            }
        }

        if (dto.getStatus() != null) {
            game.setStatus(dto.getStatus().getDetailedState());
        }

        if (dto.getVenue() != null) {
            game.setVenueName(dto.getVenue().getName());
        }

        game.setHomeTeam(homeTeam);
        game.setAwayTeam(awayTeam);

        if (dto.getTeams() != null) {
            if (dto.getTeams().getHome() != null) {
                game.setHomeScore(dto.getTeams().getHome().getScore());
                setProbablePitcher(dto.getTeams().getHome(), game, true);
            }
            if (dto.getTeams().getAway() != null) {
                game.setAwayScore(dto.getTeams().getAway().getScore());
                setProbablePitcher(dto.getTeams().getAway(), game, false);
            }
        }

        return game;
    }

    private void setProbablePitcher(ScheduleResponse.TeamGameData teamData, Game game, boolean isHome) {
        Player pitcher = null;
        if (teamData.getProbablePitcher() != null && teamData.getProbablePitcher().getId() != null) {
            pitcher = playerRepository.findByMlbId(teamData.getProbablePitcher().getId()).orElse(null);
        }
        if (isHome) {
            game.setHomeProbablePitcher(pitcher);
        } else {
            game.setAwayProbablePitcher(pitcher);
        }
    }

    public void updateEntity(Game existing, ScheduleResponse.GameData dto) {
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus().getDetailedState());
        }

        // Update scheduled time if not already set (backfill for existing games)
        if (existing.getScheduledTime() == null && dto.getGameDate() != null) {
            ZonedDateTime easternTime = parseToEastern(dto.getGameDate());
            if (easternTime != null) {
                existing.setScheduledTime(easternTime.toLocalTime());
            }
        }

        if (dto.getTeams() != null) {
            if (dto.getTeams().getHome() != null) {
                existing.setHomeScore(dto.getTeams().getHome().getScore());
                setProbablePitcher(dto.getTeams().getHome(), existing, true);
            }
            if (dto.getTeams().getAway() != null) {
                existing.setAwayScore(dto.getTeams().getAway().getScore());
                setProbablePitcher(dto.getTeams().getAway(), existing, false);
            }
        }
    }

    /**
     * Parse an ISO 8601 datetime string to Eastern time.
     * MLB API returns times in UTC (e.g., "2024-04-15T23:05:00Z").
     * We convert to US Eastern time and derive both date and time from the same instant
     * to ensure consistency (e.g., a game at 23:00 UTC on April 15 is 7:00 PM ET on April 15).
     */
    private ZonedDateTime parseToEastern(String dateStr) {
        if (dateStr == null || dateStr.length() < 20) {
            return null;
        }
        try {
            OffsetDateTime utcTime = OffsetDateTime.parse(dateStr);
            return utcTime.atZoneSameInstant(EASTERN_ZONE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
