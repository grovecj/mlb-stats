package com.mlbstats.ingestion.service;

import com.mlbstats.domain.gwar.GwarCalculationService;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.ingestion.client.BaseballSavantClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for ingesting OAA (Outs Above Average) data from Baseball Savant.
 * OAA is used as the fielding component of gWAR calculations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OaaIngestionService {

    private final BaseballSavantClient savantClient;
    private final PlayerRepository playerRepo;
    private final PlayerBattingStatsRepository battingRepo;
    private final GwarCalculationService gwarService;

    /**
     * Syncs OAA data for all qualified fielders in a season.
     * After setting OAA, recalculates gWAR for affected players.
     *
     * @param season Season year
     * @return Number of players updated
     */
    @Transactional
    public int syncOaaForSeason(Integer season) {
        log.info("Starting OAA sync for season {}", season);

        Map<Integer, Integer> oaaByMlbId = savantClient.getOaaByPlayerId(season);
        if (oaaByMlbId.isEmpty()) {
            log.warn("No OAA data received from Baseball Savant for season {}", season);
            return 0;
        }

        int updated = 0;

        for (Map.Entry<Integer, Integer> entry : oaaByMlbId.entrySet()) {
            Integer mlbId = entry.getKey();
            Integer oaa = entry.getValue();

            Optional<Player> playerOpt = playerRepo.findByMlbId(mlbId);
            if (playerOpt.isEmpty()) {
                log.trace("Player with MLB ID {} not found in database", mlbId);
                continue;
            }

            Player player = playerOpt.get();
            List<PlayerBattingStats> statsList = battingRepo.findByPlayerIdAndSeason(player.getId(), season);

            for (PlayerBattingStats stats : statsList) {
                stats.setOaa(oaa);

                // Recalculate gWAR with updated fielding component
                gwarService.calculateAndApply(stats, player.getPosition());

                battingRepo.save(stats);
                updated++;
            }
        }

        log.info("OAA sync completed for season {}: {} player-stats updated", season, updated);
        return updated;
    }

    /**
     * Gets OAA for a specific player without persisting.
     *
     * @param mlbId MLB player ID
     * @param season Season year
     * @return OAA value if available, null otherwise
     */
    public Integer getPlayerOaa(Integer mlbId, Integer season) {
        Map<Integer, Integer> oaaData = savantClient.getOaaByPlayerId(season);
        return oaaData.get(mlbId);
    }
}
