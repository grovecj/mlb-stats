package com.mlbstats.api.controller;

import com.mlbstats.common.util.DateUtils;
import com.mlbstats.ingestion.service.IngestionOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ingestion")
@Tag(name = "Ingestion", description = "Data ingestion management APIs")
@PreAuthorize("hasRole('ADMIN')")
public class IngestionController {

    private final IngestionOrchestrator orchestrator;

    public IngestionController(IngestionOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/full-sync")
    @Operation(summary = "Run full sync", description = "Triggers a full data synchronization from MLB API")
    public ResponseEntity<Map<String, String>> runFullSync(
            @RequestParam(required = false) Integer season) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        orchestrator.runFullSync(season);
        return ResponseEntity.ok(Map.of("status", "completed", "season", String.valueOf(season)));
    }

    @PostMapping("/teams")
    @Operation(summary = "Sync teams", description = "Synchronizes all MLB teams")
    public ResponseEntity<Map<String, String>> syncTeams() {
        orchestrator.runTeamsSync();
        return ResponseEntity.ok(Map.of("status", "completed"));
    }

    @PostMapping("/rosters")
    @Operation(summary = "Sync rosters", description = "Synchronizes team rosters for a season")
    public ResponseEntity<Map<String, String>> syncRosters(
            @RequestParam(required = false) Integer season) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        orchestrator.runRostersSync(season);
        return ResponseEntity.ok(Map.of("status", "completed", "season", String.valueOf(season)));
    }

    @PostMapping("/games")
    @Operation(summary = "Sync games", description = "Synchronizes games for a season")
    public ResponseEntity<Map<String, String>> syncGames(
            @RequestParam(required = false) Integer season) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        orchestrator.runGamesSync(season);
        return ResponseEntity.ok(Map.of("status", "completed", "season", String.valueOf(season)));
    }

    @PostMapping("/stats")
    @Operation(summary = "Sync stats", description = "Synchronizes player statistics for a season")
    public ResponseEntity<Map<String, String>> syncStats(
            @RequestParam(required = false) Integer season) {

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        orchestrator.runStatsSync(season);
        return ResponseEntity.ok(Map.of("status", "completed", "season", String.valueOf(season)));
    }

    @PostMapping("/players/incomplete")
    @Operation(summary = "Sync incomplete players", description = "Fetches full data for players missing biographical info")
    public ResponseEntity<Map<String, String>> syncIncompletePlayers() {
        int count = orchestrator.runIncompletePlayersSync();
        return ResponseEntity.ok(Map.of("status", "completed", "synced", String.valueOf(count)));
    }
}
