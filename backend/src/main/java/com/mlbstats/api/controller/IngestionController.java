package com.mlbstats.api.controller;

import com.mlbstats.api.dto.DataFreshnessDto;
import com.mlbstats.api.dto.SyncJobDto;
import com.mlbstats.common.security.AppUserPrincipal;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.domain.sync.SyncJob;
import com.mlbstats.domain.sync.TriggerType;
import com.mlbstats.domain.user.AppUser;
import com.mlbstats.ingestion.service.IngestionOrchestrator;
import com.mlbstats.ingestion.service.SyncJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ingestion")
@Tag(name = "Ingestion", description = "Data ingestion management APIs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionOrchestrator orchestrator;
    private final SyncJobService syncJobService;

    // ===== Job Management Endpoints =====

    @GetMapping("/freshness")
    @Operation(summary = "Get data freshness", description = "Returns freshness status for all data types")
    public ResponseEntity<List<DataFreshnessDto>> getDataFreshness() {
        List<DataFreshnessDto> freshness = syncJobService.getDataFreshness().values().stream()
                .map(DataFreshnessDto::fromDataFreshness)
                .toList();
        return ResponseEntity.ok(freshness);
    }

    @GetMapping("/jobs")
    @Operation(summary = "Get recent sync jobs", description = "Returns recent sync job history")
    public ResponseEntity<List<SyncJobDto>> getRecentJobs(
            @RequestParam(defaultValue = "20") int limit) {
        List<SyncJobDto> jobs = syncJobService.getRecentJobs(limit).stream()
                .map(SyncJobDto::fromEntity)
                .toList();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs/active")
    @Operation(summary = "Get active sync jobs", description = "Returns currently running or pending sync jobs")
    public ResponseEntity<List<SyncJobDto>> getActiveJobs() {
        List<SyncJobDto> jobs = syncJobService.getActiveJobs().stream()
                .map(SyncJobDto::fromEntity)
                .toList();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs/{id}")
    @Operation(summary = "Get sync job", description = "Returns details for a specific sync job")
    public ResponseEntity<SyncJobDto> getJob(@PathVariable Long id) {
        SyncJob job = syncJobService.getJob(id);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    @GetMapping(value = "/jobs/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to sync job progress", description = "SSE stream for real-time progress updates")
    public SseEmitter subscribeToJob(@PathVariable Long id) {
        return syncJobService.subscribeToJob(id);
    }

    @PostMapping("/jobs/{id}/cancel")
    @Operation(summary = "Cancel sync job", description = "Cancels a running or pending sync job")
    public ResponseEntity<SyncJobDto> cancelJob(@PathVariable Long id) {
        SyncJob job = syncJobService.cancelJob(id);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    // ===== Tracked Sync Endpoints =====

    @PostMapping("/full-sync")
    @Operation(summary = "Run full sync", description = "Triggers a tracked full data synchronization from MLB API")
    public ResponseEntity<SyncJobDto> runFullSync(
            @RequestParam(required = false) Integer season,
            @AuthenticationPrincipal OAuth2User principal) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        AppUser user = getUserFromPrincipal(principal);
        SyncJob job = orchestrator.createAndRunTrackedFullSync(season, TriggerType.MANUAL, user);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    @PostMapping("/teams")
    @Operation(summary = "Sync teams", description = "Synchronizes all MLB teams")
    public ResponseEntity<SyncJobDto> syncTeams(@AuthenticationPrincipal OAuth2User principal) {
        AppUser user = getUserFromPrincipal(principal);
        SyncJob job = orchestrator.createAndRunTrackedTeamsSync(TriggerType.MANUAL, user);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    @PostMapping("/rosters")
    @Operation(summary = "Sync rosters", description = "Synchronizes team rosters for a season")
    public ResponseEntity<SyncJobDto> syncRosters(
            @RequestParam(required = false) Integer season,
            @AuthenticationPrincipal OAuth2User principal) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        AppUser user = getUserFromPrincipal(principal);
        SyncJob job = orchestrator.createAndRunTrackedRostersSync(season, TriggerType.MANUAL, user);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    @PostMapping("/games")
    @Operation(summary = "Sync games", description = "Synchronizes games for a season")
    public ResponseEntity<SyncJobDto> syncGames(
            @RequestParam(required = false) Integer season,
            @AuthenticationPrincipal OAuth2User principal) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        AppUser user = getUserFromPrincipal(principal);
        SyncJob job = orchestrator.createAndRunTrackedGamesSync(season, TriggerType.MANUAL, user);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    @PostMapping("/stats")
    @Operation(summary = "Sync stats", description = "Synchronizes player statistics for a season")
    public ResponseEntity<SyncJobDto> syncStats(
            @RequestParam(required = false) Integer season,
            @AuthenticationPrincipal OAuth2User principal) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        AppUser user = getUserFromPrincipal(principal);
        SyncJob job = orchestrator.createAndRunTrackedStatsSync(season, TriggerType.MANUAL, user);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    @PostMapping("/standings")
    @Operation(summary = "Sync standings", description = "Synchronizes team standings for a season")
    public ResponseEntity<SyncJobDto> syncStandings(
            @RequestParam(required = false) Integer season,
            @AuthenticationPrincipal OAuth2User principal) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        AppUser user = getUserFromPrincipal(principal);
        SyncJob job = orchestrator.createAndRunTrackedStandingsSync(season, TriggerType.MANUAL, user);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    @PostMapping("/boxscores")
    @Operation(summary = "Sync box scores", description = "Synchronizes game-level player stats for completed games")
    public ResponseEntity<SyncJobDto> syncBoxScores(
            @RequestParam(required = false) Integer season,
            @AuthenticationPrincipal OAuth2User principal) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        AppUser user = getUserFromPrincipal(principal);
        SyncJob job = orchestrator.createAndRunTrackedBoxScoresSync(season, TriggerType.MANUAL, user);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    @PostMapping("/linescores")
    @Operation(summary = "Sync linescores", description = "Synchronizes inning-by-inning linescore data for completed games")
    public ResponseEntity<SyncJobDto> syncLinescores(
            @RequestParam(required = false) Integer season,
            @AuthenticationPrincipal OAuth2User principal) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        AppUser user = getUserFromPrincipal(principal);
        SyncJob job = orchestrator.createAndRunTrackedLinescoresSync(season, TriggerType.MANUAL, user);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    @PostMapping("/sabermetrics")
    @Operation(summary = "Sync sabermetrics", description = "Synchronizes WAR, wOBA, FIP, expected stats, and calculates gWAR")
    public ResponseEntity<SyncJobDto> syncSabermetrics(
            @RequestParam(required = false) Integer season,
            @AuthenticationPrincipal OAuth2User principal) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        AppUser user = getUserFromPrincipal(principal);
        SyncJob job = orchestrator.createAndRunTrackedSabermetricsSync(season, TriggerType.MANUAL, user);
        return ResponseEntity.ok(SyncJobDto.fromEntity(job));
    }

    // ===== Legacy Untracked Endpoints =====

    @PostMapping("/players/incomplete")
    @Operation(summary = "Sync incomplete players", description = "Fetches full data for players missing biographical info")
    public ResponseEntity<Map<String, String>> syncIncompletePlayers() {
        int count = orchestrator.runIncompletePlayersSync();
        return ResponseEntity.ok(Map.of("status", "completed", "synced", String.valueOf(count)));
    }

    @PostMapping("/boxscores/game/{gameId}")
    @Operation(summary = "Sync box score for specific game", description = "Synchronizes game-level player stats for a specific game")
    public ResponseEntity<Map<String, String>> syncGameBoxScore(@PathVariable Long gameId) {
        int count = orchestrator.runBoxScoreForGame(gameId);
        return ResponseEntity.ok(Map.of("status", "completed", "gameId", String.valueOf(gameId), "stats", String.valueOf(count)));
    }

    private AppUser getUserFromPrincipal(OAuth2User principal) {
        if (principal instanceof AppUserPrincipal appUserPrincipal) {
            return appUserPrincipal.getAppUser();
        }
        return null;
    }
}
