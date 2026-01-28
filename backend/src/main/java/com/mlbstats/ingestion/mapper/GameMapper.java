package com.mlbstats.ingestion.mapper;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.dto.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class GameMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final PlayerRepository playerRepository;

    public Game toEntity(ScheduleResponse.GameData dto, Team homeTeam, Team awayTeam) {
        Game game = new Game();
        game.setMlbId(dto.getGamePk());
        game.setSeason(dto.getSeason());
        game.setGameType(dto.getGameType());
        game.setDayNight(dto.getDayNight());
        game.setScheduledInnings(dto.getScheduledInnings() != null ? dto.getScheduledInnings() : 9);

        if (dto.getGameDate() != null) {
            game.setGameDate(parseDate(dto.getGameDate()));
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

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.substring(0, 10), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
