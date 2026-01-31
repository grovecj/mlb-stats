package com.mlbstats.ingestion.service;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.game.GameInning;
import com.mlbstats.domain.game.GameInningRepository;
import com.mlbstats.domain.game.GameRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.LinescoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinescoreIngestionService {

    private final MlbApiClient mlbApiClient;
    private final GameRepository gameRepository;
    private final GameInningRepository gameInningRepository;

    @Transactional
    public int syncLinescoreForGame(Long gameId) {
        Game game = gameRepository.findByIdWithTeams(gameId).orElse(null);
        if (game == null) {
            log.warn("Game not found: {}", gameId);
            return 0;
        }

        // Only sync for games that have started or are final
        if ("Scheduled".equals(game.getStatus()) || "Pre-Game".equals(game.getStatus())) {
            log.debug("Skipping linescore for game {} - status: {}", gameId, game.getStatus());
            return 0;
        }

        return syncLinescore(game);
    }

    /**
     * Sync linescores for all final games in a season.
     * Note: This intentionally only syncs "Final" games to avoid incomplete data.
     * For in-progress games, use syncLinescoreForGame which allows any started game.
     * Each game is synced in its own transaction to avoid long-running transactions.
     */
    public int syncLinescoresForSeason(Integer season) {
        log.info("Syncing linescores for season {}", season);
        List<Game> games = gameRepository.findBySeasonAndStatus(season, "Final");

        int count = 0;
        for (Game game : games) {
            // Skip if already has linescore data
            if (gameInningRepository.findByGameIdOrderByInningNumber(game.getId()).isEmpty()) {
                // Each game synced in its own transaction via syncLinescoreForGame
                count += syncLinescoreForGame(game.getId());
                // Small delay to avoid overwhelming the API
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("Synced linescores for {} games", count);
        return count;
    }

    private int syncLinescore(Game game) {
        LinescoreResponse response = mlbApiClient.getLinescore(game.getMlbId());
        if (response == null) {
            log.warn("Null response from MLB API for game {} (mlbId: {})", game.getId(), game.getMlbId());
            return 0;
        }

        // Update game totals (hits, errors) from linescore
        updateGameTotals(game, response);

        // Update live state for in-progress games
        updateLiveState(game, response);

        gameRepository.save(game);

        // Process innings
        if (response.getInnings() == null || response.getInnings().isEmpty()) {
            log.debug("No innings data in linescore for game {} (mlbId: {})", game.getId(), game.getMlbId());
            return 0;
        }

        // Clear existing innings and re-sync (for live games that get updated)
        gameInningRepository.deleteByGameId(game.getId());

        int count = 0;
        for (LinescoreResponse.InningData inningData : response.getInnings()) {
            GameInning inning = mapInning(game, inningData);
            gameInningRepository.save(inning);
            count++;
        }

        log.debug("Synced {} innings for game {} (mlbId: {})", count, game.getId(), game.getMlbId());
        return 1;
    }

    private void updateGameTotals(Game game, LinescoreResponse response) {
        if (response.getTeams() != null) {
            if (response.getTeams().getHome() != null) {
                game.setHomeHits(response.getTeams().getHome().getHits());
                game.setHomeErrors(response.getTeams().getHome().getErrors());
            }
            if (response.getTeams().getAway() != null) {
                game.setAwayHits(response.getTeams().getAway().getHits());
                game.setAwayErrors(response.getTeams().getAway().getErrors());
            }
        }
    }

    private void updateLiveState(Game game, LinescoreResponse response) {
        if (response.getCurrentInning() != null) {
            try {
                game.setCurrentInning(Integer.parseInt(response.getCurrentInning()));
            } catch (NumberFormatException e) {
                // Ignore if not a valid number
            }
        }
        game.setInningHalf(response.getInningHalf());
        game.setOuts(response.getOuts());
        game.setBalls(response.getBalls());
        game.setStrikes(response.getStrikes());

        // Runner positions from offense data
        if (response.getOffense() != null) {
            game.setRunnerOnFirst(response.getOffense().getFirst() != null);
            game.setRunnerOnSecond(response.getOffense().getSecond() != null);
            game.setRunnerOnThird(response.getOffense().getThird() != null);
        }
    }

    private GameInning mapInning(Game game, LinescoreResponse.InningData data) {
        GameInning inning = new GameInning();
        inning.setGame(game);
        inning.setInningNumber(data.getNum());

        if (data.getAway() != null) {
            inning.setAwayRuns(data.getAway().getRuns() != null ? data.getAway().getRuns() : 0);
            inning.setAwayHits(data.getAway().getHits());
            inning.setAwayErrors(data.getAway().getErrors());
            inning.setAwayLeftOnBase(data.getAway().getLeftOnBase());
        }

        if (data.getHome() != null) {
            inning.setHomeRuns(data.getHome().getRuns() != null ? data.getHome().getRuns() : 0);
            inning.setHomeHits(data.getHome().getHits());
            inning.setHomeErrors(data.getHome().getErrors());
            inning.setHomeLeftOnBase(data.getHome().getLeftOnBase());
        }

        return inning;
    }
}
