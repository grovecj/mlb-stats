package com.mlbstats.ingestion.service;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.game.GameRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.ScheduleResponse;
import com.mlbstats.ingestion.mapper.GameMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GameIngestionService {

    private static final Logger log = LoggerFactory.getLogger(GameIngestionService.class);

    private final MlbApiClient mlbApiClient;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final GameMapper gameMapper;

    public GameIngestionService(MlbApiClient mlbApiClient, GameRepository gameRepository,
                                TeamRepository teamRepository, GameMapper gameMapper) {
        this.mlbApiClient = mlbApiClient;
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
        this.gameMapper = gameMapper;
    }

    @Transactional
    public int syncGamesForDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Syncing games from {} to {}", startDate, endDate);
        ScheduleResponse response = mlbApiClient.getSchedule(startDate, endDate);

        if (response == null || response.getDates() == null) {
            log.warn("No schedule data returned");
            return 0;
        }

        int count = 0;
        for (ScheduleResponse.DateEntry dateEntry : response.getDates()) {
            if (dateEntry.getGames() != null) {
                for (ScheduleResponse.GameData gameData : dateEntry.getGames()) {
                    syncGame(gameData);
                    count++;
                }
            }
        }

        log.info("Synced {} games", count);
        return count;
    }

    @Transactional
    public int syncGamesForSeason(Integer season) {
        log.info("Syncing games for season {}", season);
        LocalDate startDate = LocalDate.of(season, 3, 1);
        LocalDate endDate = LocalDate.of(season, 11, 30);
        return syncGamesForDateRange(startDate, endDate);
    }

    @Transactional
    public Game syncGame(ScheduleResponse.GameData gameData) {
        if (gameData.getTeams() == null ||
                gameData.getTeams().getHome() == null ||
                gameData.getTeams().getAway() == null ||
                gameData.getTeams().getHome().getTeam() == null ||
                gameData.getTeams().getAway().getTeam() == null) {
            log.warn("Invalid game data for gamePk {}", gameData.getGamePk());
            return null;
        }

        Integer homeTeamMlbId = gameData.getTeams().getHome().getTeam().getId();
        Integer awayTeamMlbId = gameData.getTeams().getAway().getTeam().getId();

        Team homeTeam = teamRepository.findByMlbId(homeTeamMlbId).orElse(null);
        Team awayTeam = teamRepository.findByMlbId(awayTeamMlbId).orElse(null);

        if (homeTeam == null || awayTeam == null) {
            log.warn("Teams not found for game {}. Home: {}, Away: {}",
                    gameData.getGamePk(), homeTeamMlbId, awayTeamMlbId);
            return null;
        }

        return gameRepository.findByMlbId(gameData.getGamePk())
                .map(existing -> {
                    gameMapper.updateEntity(existing, gameData);
                    return gameRepository.save(existing);
                })
                .orElseGet(() -> {
                    Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);
                    return gameRepository.save(game);
                });
    }

    public List<Game> getGamesByDate(LocalDate date) {
        return gameRepository.findByDateWithTeams(date);
    }
}
