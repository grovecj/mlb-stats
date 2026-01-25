package com.mlbstats.api.controller;

import com.mlbstats.common.config.IngestionProperties;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.ingestion.service.IngestionOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ingestion")
@Tag(name = "Ingestion", description = "Data ingestion management APIs")
public class IngestionController {

    private final IngestionOrchestrator orchestrator;
    private final IngestionProperties ingestionProperties;

    public IngestionController(IngestionOrchestrator orchestrator, IngestionProperties ingestionProperties) {
        this.orchestrator = orchestrator;
        this.ingestionProperties = ingestionProperties;
    }

    @PostMapping("/full-sync")
    @Operation(summary = "Run full sync", description = "Triggers a full data synchronization from MLB API")
    public ResponseEntity<Map<String, String>> runFullSync(
            @RequestParam(required = false) Integer season,
            @Parameter(hidden = true) @RequestHeader(value = "X-API-Key", required = false) String apiKey) {

        ResponseEntity<Map<String, String>> authError = checkAuthorization(apiKey);
        if (authError != null) return authError;

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        orchestrator.runFullSync(season);
        return ResponseEntity.ok(Map.of("status", "completed", "season", String.valueOf(season)));
    }

    @PostMapping("/teams")
    @Operation(summary = "Sync teams", description = "Synchronizes all MLB teams")
    public ResponseEntity<Map<String, String>> syncTeams(
            @Parameter(hidden = true) @RequestHeader(value = "X-API-Key", required = false) String apiKey) {

        ResponseEntity<Map<String, String>> authError = checkAuthorization(apiKey);
        if (authError != null) return authError;

        orchestrator.runTeamsSync();
        return ResponseEntity.ok(Map.of("status", "completed"));
    }

    @PostMapping("/rosters")
    @Operation(summary = "Sync rosters", description = "Synchronizes team rosters for a season")
    public ResponseEntity<Map<String, String>> syncRosters(
            @RequestParam(required = false) Integer season,
            @Parameter(hidden = true) @RequestHeader(value = "X-API-Key", required = false) String apiKey) {

        ResponseEntity<Map<String, String>> authError = checkAuthorization(apiKey);
        if (authError != null) return authError;

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        orchestrator.runRostersSync(season);
        return ResponseEntity.ok(Map.of("status", "completed", "season", String.valueOf(season)));
    }

    @PostMapping("/games")
    @Operation(summary = "Sync games", description = "Synchronizes games for a season")
    public ResponseEntity<Map<String, String>> syncGames(
            @RequestParam(required = false) Integer season,
            @Parameter(hidden = true) @RequestHeader(value = "X-API-Key", required = false) String apiKey) {

        ResponseEntity<Map<String, String>> authError = checkAuthorization(apiKey);
        if (authError != null) return authError;

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        orchestrator.runGamesSync(season);
        return ResponseEntity.ok(Map.of("status", "completed", "season", String.valueOf(season)));
    }

    @PostMapping("/stats")
    @Operation(summary = "Sync stats", description = "Synchronizes player statistics for a season")
    public ResponseEntity<Map<String, String>> syncStats(
            @RequestParam(required = false) Integer season,
            @Parameter(hidden = true) @RequestHeader(value = "X-API-Key", required = false) String apiKey) {

        ResponseEntity<Map<String, String>> authError = checkAuthorization(apiKey);
        if (authError != null) return authError;

        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        orchestrator.runStatsSync(season);
        return ResponseEntity.ok(Map.of("status", "completed", "season", String.valueOf(season)));
    }

    private ResponseEntity<Map<String, String>> checkAuthorization(String providedApiKey) {
        if (!ingestionProperties.isEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Ingestion endpoints are disabled"));
        }

        if (ingestionProperties.isApiKeyRequired()) {
            if (providedApiKey == null || !providedApiKey.equals(ingestionProperties.getApiKey())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing API key"));
            }
        }

        return null;
    }
}
