package com.mlbstats.ingestion.service;

import com.mlbstats.common.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionOrchestrator {

    private final TeamIngestionService teamIngestionService;
    private final RosterIngestionService rosterIngestionService;
    private final GameIngestionService gameIngestionService;
    private final StatsIngestionService statsIngestionService;
    private final PlayerIngestionService playerIngestionService;
    private final StandingsIngestionService standingsIngestionService;

    public void runFullSync() {
        int season = DateUtils.getCurrentSeason();
        runFullSync(season);
    }

    public void runFullSync(int season) {
        log.info("Starting full data sync for season {}", season);
        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Sync all teams
            log.info("Step 1: Syncing teams...");
            int teamCount = teamIngestionService.syncAllTeams();
            log.info("Synced {} teams", teamCount);

            // Step 2: Sync all rosters
            log.info("Step 2: Syncing rosters...");
            int rosterCount = rosterIngestionService.syncAllRosters(season);
            log.info("Synced {} roster entries", rosterCount);

            // Step 3: Sync games
            log.info("Step 3: Syncing games...");
            int gameCount = gameIngestionService.syncGamesForSeason(season);
            log.info("Synced {} games", gameCount);

            // Step 4: Sync player stats
            log.info("Step 4: Syncing player stats...");
            int statsCount = statsIngestionService.syncAllPlayerStats(season);
            log.info("Synced stats for {} players", statsCount);

            // Step 5: Sync standings
            log.info("Step 5: Syncing standings...");
            int standingsCount = standingsIngestionService.syncStandings(season);
            log.info("Synced {} team standings", standingsCount);

            long elapsed = System.currentTimeMillis() - startTime;
            log.info("Full sync completed in {}ms", elapsed);
        } catch (Exception e) {
            log.error("Full sync failed", e);
            throw e;
        }
    }

    public void runTeamsSync() {
        log.info("Running teams-only sync");
        teamIngestionService.syncAllTeams();
    }

    public void runRostersSync(int season) {
        log.info("Running rosters-only sync for season {}", season);
        rosterIngestionService.syncAllRosters(season);
    }

    public void runGamesSync(int season) {
        log.info("Running games-only sync for season {}", season);
        gameIngestionService.syncGamesForSeason(season);
    }

    public void runStatsSync(int season) {
        log.info("Running stats-only sync for season {}", season);
        statsIngestionService.syncAllPlayerStats(season);
    }

    public int runIncompletePlayersSync() {
        log.info("Running sync for players with incomplete data");
        return playerIngestionService.syncIncompletePlayers();
    }

    public int runStandingsSync(int season) {
        log.info("Running standings sync for season {}", season);
        return standingsIngestionService.syncStandings(season);
    }
}
