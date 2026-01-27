package com.mlbstats.api.controller;

import com.mlbstats.api.dto.*;
import com.mlbstats.api.service.PlayerApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@Tag(name = "Players", description = "Player management APIs")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerApiService playerApiService;

    @GetMapping
    @Operation(summary = "Get players", description = "Returns a paginated list of players, optionally filtered by search term")
    public ResponseEntity<PageDto<PlayerDto>> getPlayers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName"));

        PageDto<PlayerDto> players;
        if (search != null && !search.isBlank()) {
            players = playerApiService.searchPlayers(search, pageable);
        } else {
            players = playerApiService.getAllPlayers(pageable);
        }
        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get player by ID", description = "Returns a single player by ID")
    public ResponseEntity<PlayerDto> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(playerApiService.getPlayerById(id));
    }

    @GetMapping("/{id}/batting-stats")
    @Operation(summary = "Get player batting stats", description = "Returns batting stats for a player")
    public ResponseEntity<List<BattingStatsDto>> getPlayerBattingStats(
            @PathVariable Long id,
            @RequestParam(required = false) Integer season) {
        return ResponseEntity.ok(playerApiService.getPlayerBattingStats(id, season));
    }

    @GetMapping("/{id}/pitching-stats")
    @Operation(summary = "Get player pitching stats", description = "Returns pitching stats for a player")
    public ResponseEntity<List<PitchingStatsDto>> getPlayerPitchingStats(
            @PathVariable Long id,
            @RequestParam(required = false) Integer season) {
        return ResponseEntity.ok(playerApiService.getPlayerPitchingStats(id, season));
    }

    @GetMapping("/leaders/home-runs")
    @Operation(summary = "Get home run leaders", description = "Returns top home run hitters for a season")
    public ResponseEntity<List<BattingStatsDto>> getHomeRunLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopHomeRunHitters(season, limit));
    }

    @GetMapping("/leaders/batting-average")
    @Operation(summary = "Get batting average leaders", description = "Returns top batting averages for a season")
    public ResponseEntity<List<BattingStatsDto>> getBattingAverageLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "100") int minAtBats,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopBattingAverage(season, minAtBats, limit));
    }

    @GetMapping("/leaders/wins")
    @Operation(summary = "Get wins leaders", description = "Returns top pitchers by wins for a season")
    public ResponseEntity<List<PitchingStatsDto>> getWinsLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopWinners(season, limit));
    }

    @GetMapping("/leaders/strikeouts")
    @Operation(summary = "Get strikeout leaders", description = "Returns top pitchers by strikeouts for a season")
    public ResponseEntity<List<PitchingStatsDto>> getStrikeoutLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopStrikeouts(season, limit));
    }

    @GetMapping("/{id}/batting-game-log")
    @Operation(summary = "Get player batting game log", description = "Returns game-by-game batting stats for a player")
    public ResponseEntity<List<BattingGameLogDto>> getPlayerBattingGameLog(
            @PathVariable Long id,
            @RequestParam(required = false) Integer season) {
        return ResponseEntity.ok(playerApiService.getPlayerBattingGameLog(id, season));
    }

    @GetMapping("/{id}/pitching-game-log")
    @Operation(summary = "Get player pitching game log", description = "Returns game-by-game pitching stats for a player")
    public ResponseEntity<List<PitchingGameLogDto>> getPlayerPitchingGameLog(
            @PathVariable Long id,
            @RequestParam(required = false) Integer season) {
        return ResponseEntity.ok(playerApiService.getPlayerPitchingGameLog(id, season));
    }
}
