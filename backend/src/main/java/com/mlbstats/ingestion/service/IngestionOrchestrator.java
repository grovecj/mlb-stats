package com.mlbstats.ingestion.service;

import com.mlbstats.common.config.CacheConfig;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.domain.sync.SyncJob;
import com.mlbstats.domain.sync.SyncJobType;
import com.mlbstats.domain.sync.TriggerType;
import com.mlbstats.domain.user.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
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
    private final BoxScoreIngestionService boxScoreIngestionService;
    private final LinescoreIngestionService linescoreIngestionService;
    private final SyncJobService syncJobService;

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.TEAMS, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_ID, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_LEAGUE, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_DIVISION, allEntries = true),
            @CacheEvict(value = CacheConfig.ROSTERS, allEntries = true),
            @CacheEvict(value = CacheConfig.STANDINGS, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAM_STANDINGS, allEntries = true),
            @CacheEvict(value = CacheConfig.PLAYERS, allEntries = true),
            @CacheEvict(value = CacheConfig.LEADERBOARDS, allEntries = true),
            @CacheEvict(value = CacheConfig.GAMES, allEntries = true),
            @CacheEvict(value = CacheConfig.GAMES_BY_DATE, allEntries = true),
            @CacheEvict(value = CacheConfig.BOX_SCORES, allEntries = true),
            @CacheEvict(value = CacheConfig.SEARCH, allEntries = true)
    })
    public void runFullSync() {
        int season = DateUtils.getCurrentSeason();
        runFullSync(season);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.TEAMS, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_ID, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_LEAGUE, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_DIVISION, allEntries = true),
            @CacheEvict(value = CacheConfig.ROSTERS, allEntries = true),
            @CacheEvict(value = CacheConfig.STANDINGS, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAM_STANDINGS, allEntries = true),
            @CacheEvict(value = CacheConfig.PLAYERS, allEntries = true),
            @CacheEvict(value = CacheConfig.LEADERBOARDS, allEntries = true),
            @CacheEvict(value = CacheConfig.GAMES, allEntries = true),
            @CacheEvict(value = CacheConfig.GAMES_BY_DATE, allEntries = true),
            @CacheEvict(value = CacheConfig.BOX_SCORES, allEntries = true),
            @CacheEvict(value = CacheConfig.SEARCH, allEntries = true)
    })
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

    /**
     * Run a tracked full sync that creates a SyncJob and reports progress.
     * This method runs asynchronously and updates job status via SSE.
     */
    @Async
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.TEAMS, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_ID, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_LEAGUE, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_DIVISION, allEntries = true),
            @CacheEvict(value = CacheConfig.ROSTERS, allEntries = true),
            @CacheEvict(value = CacheConfig.STANDINGS, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAM_STANDINGS, allEntries = true),
            @CacheEvict(value = CacheConfig.PLAYERS, allEntries = true),
            @CacheEvict(value = CacheConfig.LEADERBOARDS, allEntries = true),
            @CacheEvict(value = CacheConfig.GAMES, allEntries = true),
            @CacheEvict(value = CacheConfig.GAMES_BY_DATE, allEntries = true),
            @CacheEvict(value = CacheConfig.BOX_SCORES, allEntries = true),
            @CacheEvict(value = CacheConfig.SEARCH, allEntries = true)
    })
    public void runTrackedFullSync(Long jobId, int season) {
        log.info("Starting tracked full sync for season {} (job {})", season, jobId);
        int totalCreated = 0;
        int totalUpdated = 0;
        int errors = 0;

        try {
            syncJobService.startJob(jobId);
            syncJobService.updateProgress(jobId, 0, 5, "Syncing teams...");

            // Step 1: Sync all teams
            log.info("Step 1: Syncing teams...");
            int teamCount = teamIngestionService.syncAllTeams();
            log.info("Synced {} teams", teamCount);
            totalCreated += teamCount;
            syncJobService.updateProgress(jobId, 1, 5, "Syncing rosters...");

            // Step 2: Sync all rosters
            log.info("Step 2: Syncing rosters...");
            int rosterCount = rosterIngestionService.syncAllRosters(season);
            log.info("Synced {} roster entries", rosterCount);
            totalCreated += rosterCount;
            syncJobService.updateProgress(jobId, 2, 5, "Syncing games...");

            // Step 3: Sync games
            log.info("Step 3: Syncing games...");
            int gameCount = gameIngestionService.syncGamesForSeason(season);
            log.info("Synced {} games", gameCount);
            totalCreated += gameCount;
            syncJobService.updateProgress(jobId, 3, 5, "Syncing player stats...");

            // Step 4: Sync player stats
            log.info("Step 4: Syncing player stats...");
            int statsCount = statsIngestionService.syncAllPlayerStats(season);
            log.info("Synced stats for {} players", statsCount);
            totalCreated += statsCount;
            syncJobService.updateProgress(jobId, 4, 5, "Syncing standings...");

            // Step 5: Sync standings
            log.info("Step 5: Syncing standings...");
            int standingsCount = standingsIngestionService.syncStandings(season);
            log.info("Synced {} team standings", standingsCount);
            totalCreated += standingsCount;

            syncJobService.completeJob(jobId, totalCreated, totalUpdated, errors);
            log.info("Tracked full sync completed successfully (job {})", jobId);
        } catch (Exception e) {
            log.error("Tracked full sync failed (job {})", jobId, e);
            syncJobService.failJob(jobId, e.getMessage());
        }
    }

    public SyncJob createAndRunTrackedFullSync(int season, TriggerType trigger, AppUser user) {
        SyncJob job = syncJobService.createJob(SyncJobType.FULL_SYNC, season, trigger, user);
        runTrackedFullSync(job.getId(), season);
        return job;
    }

    @Async
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.TEAMS, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_ID, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_LEAGUE, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_DIVISION, allEntries = true),
            @CacheEvict(value = CacheConfig.SEARCH, allEntries = true)
    })
    public void runTrackedTeamsSync(Long jobId) {
        log.info("Starting tracked teams sync (job {})", jobId);
        try {
            syncJobService.startJob(jobId);
            syncJobService.updateProgress(jobId, 0, 1, "Syncing teams...");

            int teamCount = teamIngestionService.syncAllTeams();

            syncJobService.completeJob(jobId, teamCount, 0, 0);
            log.info("Tracked teams sync completed (job {})", jobId);
        } catch (Exception e) {
            log.error("Tracked teams sync failed (job {})", jobId, e);
            syncJobService.failJob(jobId, e.getMessage());
        }
    }

    public SyncJob createAndRunTrackedTeamsSync(TriggerType trigger, AppUser user) {
        SyncJob job = syncJobService.createJob(SyncJobType.TEAMS, null, trigger, user);
        runTrackedTeamsSync(job.getId());
        return job;
    }

    @Async
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.ROSTERS, allEntries = true),
            @CacheEvict(value = CacheConfig.PLAYERS, allEntries = true)
    })
    public void runTrackedRostersSync(Long jobId, int season) {
        log.info("Starting tracked rosters sync for season {} (job {})", season, jobId);
        try {
            syncJobService.startJob(jobId);
            syncJobService.updateProgress(jobId, 0, 1, "Syncing rosters...");

            int rosterCount = rosterIngestionService.syncAllRosters(season);

            syncJobService.completeJob(jobId, rosterCount, 0, 0);
            log.info("Tracked rosters sync completed (job {})", jobId);
        } catch (Exception e) {
            log.error("Tracked rosters sync failed (job {})", jobId, e);
            syncJobService.failJob(jobId, e.getMessage());
        }
    }

    public SyncJob createAndRunTrackedRostersSync(int season, TriggerType trigger, AppUser user) {
        SyncJob job = syncJobService.createJob(SyncJobType.ROSTERS, season, trigger, user);
        runTrackedRostersSync(job.getId(), season);
        return job;
    }

    @Async
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.GAMES, allEntries = true),
            @CacheEvict(value = CacheConfig.GAMES_BY_DATE, allEntries = true)
    })
    public void runTrackedGamesSync(Long jobId, int season) {
        log.info("Starting tracked games sync for season {} (job {})", season, jobId);
        try {
            syncJobService.startJob(jobId);
            syncJobService.updateProgress(jobId, 0, 1, "Syncing games...");

            int gameCount = gameIngestionService.syncGamesForSeason(season);

            syncJobService.completeJob(jobId, gameCount, 0, 0);
            log.info("Tracked games sync completed (job {})", jobId);
        } catch (Exception e) {
            log.error("Tracked games sync failed (job {})", jobId, e);
            syncJobService.failJob(jobId, e.getMessage());
        }
    }

    public SyncJob createAndRunTrackedGamesSync(int season, TriggerType trigger, AppUser user) {
        SyncJob job = syncJobService.createJob(SyncJobType.GAMES, season, trigger, user);
        runTrackedGamesSync(job.getId(), season);
        return job;
    }

    @Async
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.LEADERBOARDS, allEntries = true),
            @CacheEvict(value = CacheConfig.PLAYERS, allEntries = true)
    })
    public void runTrackedStatsSync(Long jobId, int season) {
        log.info("Starting tracked stats sync for season {} (job {})", season, jobId);
        try {
            syncJobService.startJob(jobId);
            syncJobService.updateProgress(jobId, 0, 1, "Syncing player stats...");

            int statsCount = statsIngestionService.syncAllPlayerStats(season);

            syncJobService.completeJob(jobId, statsCount, 0, 0);
            log.info("Tracked stats sync completed (job {})", jobId);
        } catch (Exception e) {
            log.error("Tracked stats sync failed (job {})", jobId, e);
            syncJobService.failJob(jobId, e.getMessage());
        }
    }

    public SyncJob createAndRunTrackedStatsSync(int season, TriggerType trigger, AppUser user) {
        SyncJob job = syncJobService.createJob(SyncJobType.STATS, season, trigger, user);
        runTrackedStatsSync(job.getId(), season);
        return job;
    }

    @Async
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.STANDINGS, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAM_STANDINGS, allEntries = true)
    })
    public void runTrackedStandingsSync(Long jobId, int season) {
        log.info("Starting tracked standings sync for season {} (job {})", season, jobId);
        try {
            syncJobService.startJob(jobId);
            syncJobService.updateProgress(jobId, 0, 1, "Syncing standings...");

            int standingsCount = standingsIngestionService.syncStandings(season);

            syncJobService.completeJob(jobId, standingsCount, 0, 0);
            log.info("Tracked standings sync completed (job {})", jobId);
        } catch (Exception e) {
            log.error("Tracked standings sync failed (job {})", jobId, e);
            syncJobService.failJob(jobId, e.getMessage());
        }
    }

    public SyncJob createAndRunTrackedStandingsSync(int season, TriggerType trigger, AppUser user) {
        SyncJob job = syncJobService.createJob(SyncJobType.STANDINGS, season, trigger, user);
        runTrackedStandingsSync(job.getId(), season);
        return job;
    }

    @Async
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.BOX_SCORES, allEntries = true),
            @CacheEvict(value = CacheConfig.LINESCORES, allEntries = true),
            @CacheEvict(value = CacheConfig.LEADERBOARDS, allEntries = true)
    })
    public void runTrackedBoxScoresSync(Long jobId, int season) {
        log.info("Starting tracked box scores sync for season {} (job {})", season, jobId);
        try {
            syncJobService.startJob(jobId);
            syncJobService.updateProgress(jobId, 0, 2, "Syncing box scores...");

            int boxScoreCount = boxScoreIngestionService.syncBoxScoresForSeason(season);

            syncJobService.updateProgress(jobId, 1, 2, "Syncing linescores...");
            int linescoreCount = linescoreIngestionService.syncLinescoresForSeason(season);

            syncJobService.completeJob(jobId, boxScoreCount + linescoreCount, 0, 0);
            log.info("Tracked box scores sync completed (job {})", jobId);
        } catch (Exception e) {
            log.error("Tracked box scores sync failed (job {})", jobId, e);
            syncJobService.failJob(jobId, e.getMessage());
        }
    }

    public SyncJob createAndRunTrackedBoxScoresSync(int season, TriggerType trigger, AppUser user) {
        SyncJob job = syncJobService.createJob(SyncJobType.BOX_SCORES, season, trigger, user);
        runTrackedBoxScoresSync(job.getId(), season);
        return job;
    }

    // Legacy untracked methods for backward compatibility

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.TEAMS, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_ID, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_LEAGUE, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAMS_BY_DIVISION, allEntries = true),
            @CacheEvict(value = CacheConfig.SEARCH, allEntries = true)
    })
    public void runTeamsSync() {
        log.info("Running teams-only sync");
        teamIngestionService.syncAllTeams();
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.ROSTERS, allEntries = true),
            @CacheEvict(value = CacheConfig.PLAYERS, allEntries = true)
    })
    public void runRostersSync(int season) {
        log.info("Running rosters-only sync for season {}", season);
        rosterIngestionService.syncAllRosters(season);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.GAMES, allEntries = true),
            @CacheEvict(value = CacheConfig.GAMES_BY_DATE, allEntries = true)
    })
    public void runGamesSync(int season) {
        log.info("Running games-only sync for season {}", season);
        gameIngestionService.syncGamesForSeason(season);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.LEADERBOARDS, allEntries = true),
            @CacheEvict(value = CacheConfig.PLAYERS, allEntries = true)
    })
    public void runStatsSync(int season) {
        log.info("Running stats-only sync for season {}", season);
        statsIngestionService.syncAllPlayerStats(season);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PLAYERS, allEntries = true)
    })
    public int runIncompletePlayersSync() {
        log.info("Running sync for players with incomplete data");
        return playerIngestionService.syncIncompletePlayers();
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.STANDINGS, allEntries = true),
            @CacheEvict(value = CacheConfig.TEAM_STANDINGS, allEntries = true)
    })
    public int runStandingsSync(int season) {
        log.info("Running standings sync for season {}", season);
        return standingsIngestionService.syncStandings(season);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.BOX_SCORES, allEntries = true),
            @CacheEvict(value = CacheConfig.LINESCORES, allEntries = true),
            @CacheEvict(value = CacheConfig.LEADERBOARDS, allEntries = true)
    })
    public int runBoxScoresSync(int season) {
        log.info("Running box scores sync for season {}", season);
        int boxScoreCount = boxScoreIngestionService.syncBoxScoresForSeason(season);
        int linescoreCount = linescoreIngestionService.syncLinescoresForSeason(season);
        return boxScoreCount + linescoreCount;
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.BOX_SCORES, key = "#gameId"),
            @CacheEvict(value = CacheConfig.LINESCORES, key = "#gameId")
    })
    public int runBoxScoreForGame(Long gameId) {
        log.info("Running box score sync for game {}", gameId);
        boxScoreIngestionService.syncBoxScoreForGame(gameId);
        linescoreIngestionService.syncLinescoreForGame(gameId);
        return 1;
    }
}
