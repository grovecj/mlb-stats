package com.mlbstats.ingestion.service;

import com.mlbstats.domain.gwar.GwarCalculationService;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import com.mlbstats.domain.stats.PlayerPitchingStatsRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.ExpectedStatsResponse;
import com.mlbstats.ingestion.client.dto.SabermetricsResponse;
import com.mlbstats.ingestion.client.dto.SeasonAdvancedResponse;
import com.mlbstats.ingestion.mapper.StatsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for ingesting sabermetric statistics from MLB Stats API.
 * Fetches official WAR, wOBA, FIP, and other advanced stats, then calculates gWAR.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SabermetricsIngestionService {

    private final MlbApiClient mlbApiClient;
    private final PlayerRepository playerRepository;
    private final PlayerBattingStatsRepository battingRepo;
    private final PlayerPitchingStatsRepository pitchingRepo;
    private final StatsMapper statsMapper;
    private final GwarCalculationService gwarService;

    /**
     * Syncs sabermetrics for all players with stats in a given season.
     * All operations run in a single transaction. If any player sync fails,
     * it is logged and skipped, but does not roll back the entire batch.
     */
    @Transactional
    public int syncAllPlayerSabermetrics(Integer season) {
        log.info("Starting sabermetrics sync for season {}", season);

        // Get all players with batting or pitching stats for this season (regular season only)
        List<PlayerBattingStats> battingStats = battingRepo.findBySeasonRegularSeason(season);
        List<PlayerPitchingStats> pitchingStats = pitchingRepo.findBySeasonRegularSeason(season);

        AtomicInteger count = new AtomicInteger(0);

        // Process batters
        battingStats.stream()
                .map(PlayerBattingStats::getPlayer)
                .distinct()
                .forEach(player -> {
                    if (syncPlayerBattingSabermetrics(player, season)) {
                        count.incrementAndGet();
                    }
                });

        // Process pitchers
        pitchingStats.stream()
                .map(PlayerPitchingStats::getPlayer)
                .distinct()
                .forEach(player -> {
                    if (syncPlayerPitchingSabermetrics(player, season)) {
                        count.incrementAndGet();
                    }
                });

        log.info("Sabermetrics sync completed for season {}: {} players updated", season, count.get());
        return count.get();
    }

    /**
     * Syncs batting sabermetrics for a single player.
     * Note: When called from syncAllPlayerSabermetrics, runs within the parent transaction.
     */
    public boolean syncPlayerBattingSabermetrics(Player player, Integer season) {
        try {
            // Fetch sabermetrics from MLB API
            SabermetricsResponse saberResponse = mlbApiClient.getBattingSabermetrics(player.getMlbId(), season);
            ExpectedStatsResponse expectedResponse = mlbApiClient.getPlayerExpectedStats(player.getMlbId(), season, "hitting");
            SeasonAdvancedResponse advancedResponse = mlbApiClient.getPlayerSeasonAdvanced(player.getMlbId(), season, "hitting");

            // Extract data from responses
            SabermetricsResponse.SabermetricData saberData = extractBattingData(saberResponse);
            ExpectedStatsResponse.ExpectedStatData expectedData = extractExpectedData(expectedResponse);
            SeasonAdvancedResponse.AdvancedStatData advancedData = extractAdvancedData(advancedResponse);

            // Find existing batting stats
            List<PlayerBattingStats> statsList = battingRepo.findByPlayerIdAndSeason(player.getId(), season);

            for (PlayerBattingStats stats : statsList) {
                // Apply sabermetrics
                statsMapper.applySabermetrics(stats, saberData);
                statsMapper.applyExpectedStats(stats, expectedData);
                statsMapper.applySeasonAdvanced(stats, advancedData);

                // Calculate gWAR
                gwarService.calculateAndApply(stats, player.getPosition());

                battingRepo.save(stats);
            }

            log.debug("Updated batting sabermetrics for {} ({})", player.getFullName(), season);
            return !statsList.isEmpty();

        } catch (Exception e) {
            log.warn("Failed to sync batting sabermetrics for player {}: {}", player.getMlbId(), e.getMessage());
            return false;
        }
    }

    /**
     * Syncs pitching sabermetrics for a single player.
     * Note: When called from syncAllPlayerSabermetrics, runs within the parent transaction.
     */
    public boolean syncPlayerPitchingSabermetrics(Player player, Integer season) {
        try {
            // Fetch sabermetrics from MLB API
            SabermetricsResponse saberResponse = mlbApiClient.getPitchingSabermetrics(player.getMlbId(), season);
            SeasonAdvancedResponse advancedResponse = mlbApiClient.getPlayerSeasonAdvanced(player.getMlbId(), season, "pitching");

            // Extract data from responses
            SabermetricsResponse.SabermetricData saberData = extractPitchingData(saberResponse);
            SeasonAdvancedResponse.AdvancedStatData advancedData = extractAdvancedData(advancedResponse);

            // Find existing pitching stats
            List<PlayerPitchingStats> statsList = pitchingRepo.findByPlayerIdAndSeason(player.getId(), season);

            for (PlayerPitchingStats stats : statsList) {
                // Apply sabermetrics
                statsMapper.applySabermetrics(stats, saberData);
                statsMapper.applySeasonAdvanced(stats, advancedData);

                // Calculate gWAR
                gwarService.calculateAndApply(stats);

                pitchingRepo.save(stats);
            }

            log.debug("Updated pitching sabermetrics for {} ({})", player.getFullName(), season);
            return !statsList.isEmpty();

        } catch (Exception e) {
            log.warn("Failed to sync pitching sabermetrics for player {}: {}", player.getMlbId(), e.getMessage());
            return false;
        }
    }

    /**
     * Syncs all sabermetrics for a single player.
     */
    @Transactional
    public void syncPlayerSabermetrics(Player player, Integer season) {
        syncPlayerBattingSabermetrics(player, season);
        syncPlayerPitchingSabermetrics(player, season);
    }

    // ================================================================================
    // EXTRACTION HELPERS
    // ================================================================================

    private SabermetricsResponse.SabermetricData extractBattingData(SabermetricsResponse response) {
        if (response == null || response.getStats() == null || response.getStats().isEmpty()) {
            return null;
        }

        return response.getStats().stream()
                .filter(g -> g.getSplits() != null && !g.getSplits().isEmpty())
                .flatMap(g -> g.getSplits().stream())
                .filter(s -> s.getStat() != null)
                .map(SabermetricsResponse.StatSplit::getStat)
                .findFirst()
                .orElse(null);
    }

    private SabermetricsResponse.SabermetricData extractPitchingData(SabermetricsResponse response) {
        // Same logic as batting - structure is identical
        return extractBattingData(response);
    }

    private ExpectedStatsResponse.ExpectedStatData extractExpectedData(ExpectedStatsResponse response) {
        if (response == null || response.getStats() == null || response.getStats().isEmpty()) {
            return null;
        }

        return response.getStats().stream()
                .filter(g -> g.getSplits() != null && !g.getSplits().isEmpty())
                .flatMap(g -> g.getSplits().stream())
                .filter(s -> s.getStat() != null)
                .map(ExpectedStatsResponse.StatSplit::getStat)
                .findFirst()
                .orElse(null);
    }

    private SeasonAdvancedResponse.AdvancedStatData extractAdvancedData(SeasonAdvancedResponse response) {
        if (response == null || response.getStats() == null || response.getStats().isEmpty()) {
            return null;
        }

        return response.getStats().stream()
                .filter(g -> g.getSplits() != null && !g.getSplits().isEmpty())
                .flatMap(g -> g.getSplits().stream())
                .filter(s -> s.getStat() != null)
                .map(SeasonAdvancedResponse.StatSplit::getStat)
                .findFirst()
                .orElse(null);
    }
}
