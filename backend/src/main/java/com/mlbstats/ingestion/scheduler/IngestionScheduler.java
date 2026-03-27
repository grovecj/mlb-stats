package com.mlbstats.ingestion.scheduler;

import com.mlbstats.common.util.DateUtils;
import com.mlbstats.ingestion.service.GameIngestionService;
import com.mlbstats.ingestion.service.IngestionOrchestrator;
import com.mlbstats.ingestion.service.OaaIngestionService;
import com.mlbstats.ingestion.service.RosterIngestionService;
import com.mlbstats.ingestion.service.SabermetricsIngestionService;
import com.mlbstats.ingestion.service.StatcastIngestionService;
import com.mlbstats.ingestion.service.StatsIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class IngestionScheduler {

    private final IngestionOrchestrator orchestrator;
    private final GameIngestionService gameIngestionService;
    private final RosterIngestionService rosterIngestionService;
    private final StatsIngestionService statsIngestionService;
    private final SabermetricsIngestionService sabermetricsIngestionService;
    private final OaaIngestionService oaaIngestionService;
    private final StatcastIngestionService statcastIngestionService;

    @Value("${mlb.ingestion.enabled:false}")
    private boolean ingestionEnabled;

    /**
     * Daily: Full stats refresh at 6 AM
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void dailyStatsSync() {
        if (!ingestionEnabled) {
            log.debug("Scheduled ingestion is disabled");
            return;
        }

        log.info("Starting daily stats sync");
        try {
            int season = DateUtils.getCurrentSeason();
            statsIngestionService.syncAllPlayerStats(season);
            log.info("Daily stats sync completed");
        } catch (Exception e) {
            log.error("Daily stats sync failed", e);
        }
    }

    /**
     * Hourly: Sync games from yesterday (catch updates)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void hourlyGamesSync() {
        if (!ingestionEnabled) {
            log.debug("Scheduled ingestion is disabled");
            return;
        }

        log.info("Starting hourly games sync");
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalDate today = LocalDate.now();
            gameIngestionService.syncGamesForDateRange(yesterday, today);
            log.info("Hourly games sync completed");
        } catch (Exception e) {
            log.error("Hourly games sync failed", e);
        }
    }

    /**
     * Weekly: Full roster sync on Sunday at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    public void weeklyRosterSync() {
        if (!ingestionEnabled) {
            log.debug("Scheduled ingestion is disabled");
            return;
        }

        log.info("Starting weekly roster sync");
        try {
            int season = DateUtils.getCurrentSeason();
            rosterIngestionService.syncAllRosters(season);
            log.info("Weekly roster sync completed");
        } catch (Exception e) {
            log.error("Weekly roster sync failed", e);
        }
    }

    /**
     * Daily: Sabermetrics sync at 6:30 AM (after stats sync)
     * Fetches WAR, wOBA, FIP from MLB API and calculates gWAR
     */
    @Scheduled(cron = "0 30 6 * * *")
    public void dailySabermetricsSync() {
        if (!ingestionEnabled) {
            log.debug("Scheduled ingestion is disabled");
            return;
        }

        log.info("Starting daily sabermetrics sync");
        try {
            int season = DateUtils.getCurrentSeason();
            sabermetricsIngestionService.syncAllPlayerSabermetrics(season);
            log.info("Daily sabermetrics sync completed");
        } catch (Exception e) {
            log.error("Daily sabermetrics sync failed", e);
        }
    }

    /**
     * Weekly: OAA (Outs Above Average) sync on Sunday at 7 AM
     * Fetches OAA from Baseball Savant and recalculates gWAR
     */
    @Scheduled(cron = "0 0 7 * * SUN")
    public void weeklyOaaSync() {
        if (!ingestionEnabled) {
            log.debug("Scheduled ingestion is disabled");
            return;
        }

        log.info("Starting weekly OAA sync");
        try {
            int season = DateUtils.getCurrentSeason();
            oaaIngestionService.syncOaaForSeason(season);
            log.info("Weekly OAA sync completed");
        } catch (Exception e) {
            log.error("Weekly OAA sync failed", e);
        }
    }

    /**
     * Weekly: Statcast expected stats sync on Sunday at 7:30 AM
     * Fetches xBA, xSLG, xwOBA, exit velocity, barrel% from Baseball Savant
     */
    @Scheduled(cron = "0 30 7 * * SUN")
    public void weeklyStatcastSync() {
        if (!ingestionEnabled) {
            log.debug("Scheduled ingestion is disabled");
            return;
        }

        log.info("Starting weekly Statcast sync");
        try {
            int season = DateUtils.getCurrentSeason();
            statcastIngestionService.syncAllStatcastData(season);
            log.info("Weekly Statcast sync completed");
        } catch (Exception e) {
            log.error("Weekly Statcast sync failed", e);
        }
    }
}
