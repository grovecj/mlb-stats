package com.mlbstats.ingestion.service;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.ingestion.client.BaseballSavantClient;
import com.mlbstats.ingestion.client.BaseballSavantClient.ExpectedStatsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for ingesting Statcast expected stats from Baseball Savant.
 * Populates xBA, xSLG, xwOBA, exit velocity, barrel%, hard hit%, and sprint speed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatcastIngestionService {

    private final BaseballSavantClient savantClient;
    private final PlayerRepository playerRepo;
    private final PlayerBattingStatsRepository battingRepo;

    /**
     * Syncs expected stats for all qualified batters in a season.
     *
     * @param season Season year
     * @return Number of players updated
     */
    @Transactional
    public int syncExpectedStats(Integer season) {
        log.info("Starting expected stats sync for season {}", season);

        Map<Integer, ExpectedStatsData> statsByMlbId = savantClient.getExpectedStatsByPlayerId(season);
        Map<Integer, BigDecimal> sprintByMlbId = savantClient.getSprintSpeedByPlayerId(season);

        if (statsByMlbId.isEmpty()) {
            log.warn("No expected stats data received from Baseball Savant for season {}", season);
            return 0;
        }

        int updated = 0;

        for (Map.Entry<Integer, ExpectedStatsData> entry : statsByMlbId.entrySet()) {
            Integer mlbId = entry.getKey();
            ExpectedStatsData data = entry.getValue();

            Optional<Player> playerOpt = playerRepo.findByMlbId(mlbId);
            if (playerOpt.isEmpty()) {
                log.trace("Player with MLB ID {} not found in database", mlbId);
                continue;
            }

            Player player = playerOpt.get();
            List<PlayerBattingStats> statsList = battingRepo.findByPlayerIdAndSeason(player.getId(), season);

            // Get sprint speed if available
            BigDecimal sprintSpeed = sprintByMlbId.get(mlbId);

            for (PlayerBattingStats stats : statsList) {
                applyExpectedStats(stats, data, sprintSpeed);
                battingRepo.save(stats);
                updated++;
            }
        }

        log.info("Expected stats sync completed for season {}: {} player-stats updated", season, updated);
        return updated;
    }

    /**
     * Applies expected stats data to a batting stats entity.
     */
    private void applyExpectedStats(PlayerBattingStats stats, ExpectedStatsData data, BigDecimal sprintSpeed) {
        if (data == null) return;

        // xStats
        if (data.xba() != null) stats.setXba(data.xba());
        if (data.xslg() != null) stats.setXslg(data.xslg());
        if (data.xwoba() != null) stats.setXwoba(data.xwoba());

        // Statcast metrics
        if (data.avgExitVelocity() != null) stats.setAvgExitVelocity(data.avgExitVelocity());
        if (data.avgLaunchAngle() != null) stats.setAvgLaunchAngle(data.avgLaunchAngle());
        if (data.barrelPct() != null) stats.setBarrelPct(data.barrelPct());
        if (data.hardHitPct() != null) stats.setHardHitPct(data.hardHitPct());

        // Sprint speed (from separate endpoint)
        if (sprintSpeed != null) stats.setSprintSpeed(sprintSpeed);
    }

    /**
     * Syncs all Statcast data for a season.
     * This includes expected stats and sprint speed.
     *
     * @param season Season year
     * @return Number of players updated
     */
    @Transactional
    public int syncAllStatcastData(Integer season) {
        return syncExpectedStats(season);
    }
}
