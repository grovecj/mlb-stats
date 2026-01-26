package com.mlbstats.api.controller;

import com.mlbstats.api.dto.SeasonDataDto;
import com.mlbstats.api.service.DataManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data-manager")
@Tag(name = "Data Manager", description = "Season data management APIs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class DataManagerController {

    private final DataManagerService dataManagerService;

    @GetMapping("/seasons")
    @Operation(summary = "Get synced seasons", description = "Returns all seasons with synced data and their metrics")
    public ResponseEntity<List<SeasonDataDto>> getSyncedSeasons() {
        return ResponseEntity.ok(dataManagerService.getSyncedSeasons());
    }

    @GetMapping("/seasons/available")
    @Operation(summary = "Get available seasons", description = "Returns seasons available for syncing")
    public ResponseEntity<List<Integer>> getAvailableSeasons() {
        return ResponseEntity.ok(dataManagerService.getAvailableSeasons());
    }

    @DeleteMapping("/seasons/{season}")
    @Operation(summary = "Delete season data", description = "Deletes all data for a specific season")
    public ResponseEntity<Map<String, String>> deleteSeasonData(@PathVariable Integer season) {
        dataManagerService.deleteSeasonData(season);
        return ResponseEntity.ok(Map.of("status", "deleted", "season", String.valueOf(season)));
    }
}
