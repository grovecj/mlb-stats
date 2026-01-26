package com.mlbstats.ingestion.service;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.game.GameRepository;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.stats.PlayerGameBatting;
import com.mlbstats.domain.stats.PlayerGameBattingRepository;
import com.mlbstats.domain.stats.PlayerGamePitching;
import com.mlbstats.domain.stats.PlayerGamePitchingRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.BoxScoreResponse;
import com.mlbstats.ingestion.mapper.BoxScoreMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoxScoreIngestionService {

    private final MlbApiClient mlbApiClient;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerGameBattingRepository gameBattingRepository;
    private final PlayerGamePitchingRepository gamePitchingRepository;
    private final BoxScoreMapper boxScoreMapper;

    @Transactional
    public int syncBoxScoreForGame(Long gameId) {
        Game game = gameRepository.findByIdWithTeams(gameId).orElse(null);
        if (game == null) {
            log.warn("Game not found: {}", gameId);
            return 0;
        }

        if (!"Final".equals(game.getStatus())) {
            log.debug("Skipping box score for game {} - status: {}", gameId, game.getStatus());
            return 0;
        }

        return syncBoxScore(game);
    }

    @Transactional
    public int syncBoxScoresForSeason(Integer season) {
        log.info("Syncing box scores for season {}", season);
        List<Game> games = gameRepository.findBySeasonAndStatus(season, "Final");

        int count = 0;
        for (Game game : games) {
            // Skip if already has box score data
            if (gameBattingRepository.findByGameId(game.getId()).isEmpty()) {
                count += syncBoxScore(game);
                // Small delay to avoid overwhelming the API
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("Synced box scores for {} games", count);
        return count;
    }

    private int syncBoxScore(Game game) {
        BoxScoreResponse response = mlbApiClient.getBoxScore(game.getMlbId());
        if (response == null) {
            log.warn("Null response from MLB API for game {} (mlbId: {})", game.getId(), game.getMlbId());
            return 0;
        }
        if (response.getTeams() == null) {
            log.warn("No teams data in box score for game {} (mlbId: {})", game.getId(), game.getMlbId());
            return 0;
        }

        int count = 0;

        // Process away team
        if (response.getTeams().getAway() != null) {
            int awayCount = processTeamBoxScore(game, game.getAwayTeam(), response.getTeams().getAway());
            log.info("Processed {} player stats for away team in game {}", awayCount, game.getId());
            count += awayCount;
        } else {
            log.warn("No away team data in box score for game {}", game.getId());
        }

        // Process home team
        if (response.getTeams().getHome() != null) {
            int homeCount = processTeamBoxScore(game, game.getHomeTeam(), response.getTeams().getHome());
            log.info("Processed {} player stats for home team in game {}", homeCount, game.getId());
            count += homeCount;
        } else {
            log.warn("No home team data in box score for game {}", game.getId());
        }

        return count > 0 ? 1 : 0;
    }

    private int processTeamBoxScore(Game game, Team team, BoxScoreResponse.TeamBoxScore teamData) {
        if (teamData.getPlayers() == null) {
            log.warn("No players map in team box score for game {}", game.getId());
            return 0;
        }

        log.debug("Processing {} player entries for team {} in game {}",
                teamData.getPlayers().size(), team.getName(), game.getId());

        int count = 0;
        int playersNotFound = 0;
        Set<Integer> starterIds = new HashSet<>();

        // First pitcher in the list is typically the starter
        if (teamData.getPitchers() != null && !teamData.getPitchers().isEmpty()) {
            starterIds.add(teamData.getPitchers().get(0));
        }

        for (Map.Entry<String, BoxScoreResponse.PlayerStats> entry : teamData.getPlayers().entrySet()) {
            String playerId = entry.getKey().replace("ID", "");
            BoxScoreResponse.PlayerStats playerStats = entry.getValue();

            if (playerStats.getPerson() == null) {
                log.debug("No person data for player entry: {}", entry.getKey());
                continue;
            }

            Integer mlbPlayerId = playerStats.getPerson().getId();
            Player player = playerRepository.findByMlbId(mlbPlayerId).orElse(null);

            if (player == null) {
                log.debug("Player not found in database: {} ({})", playerStats.getPerson().getFullName(), mlbPlayerId);
                playersNotFound++;
                continue;
            }

            // Check if player batted
            if (playerStats.getStats() != null && playerStats.getStats().getBatting() != null &&
                    playerStats.getStats().getBatting().getAtBats() != null) {

                // Check if already exists
                if (gameBattingRepository.findByPlayerIdAndGameId(player.getId(), game.getId()).isEmpty()) {
                    PlayerGameBatting batting = boxScoreMapper.toGameBatting(playerStats, player, game, team);
                    gameBattingRepository.save(batting);
                    count++;
                }
            }

            // Check if player pitched
            if (playerStats.getStats() != null && playerStats.getStats().getPitching() != null &&
                    playerStats.getStats().getPitching().getInningsPitched() != null) {

                // Check if already exists
                if (gamePitchingRepository.findByPlayerIdAndGameId(player.getId(), game.getId()).isEmpty()) {
                    boolean isStarter = starterIds.contains(mlbPlayerId);
                    PlayerGamePitching pitching = boxScoreMapper.toGamePitching(playerStats, player, game, team, isStarter);
                    gamePitchingRepository.save(pitching);
                    count++;
                }
            }
        }

        if (playersNotFound > 0) {
            log.warn("{} players not found in database for team {} in game {}",
                    playersNotFound, team.getName(), game.getId());
        }

        return count;
    }
}
