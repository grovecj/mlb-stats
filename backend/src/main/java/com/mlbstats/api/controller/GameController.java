package com.mlbstats.api.controller;

import com.mlbstats.api.dto.BoxScoreDto;
import com.mlbstats.api.dto.GameDto;
import com.mlbstats.api.dto.PageDto;
import com.mlbstats.api.service.GameApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@Tag(name = "Games", description = "Game management APIs")
@RequiredArgsConstructor
public class GameController {

    private final GameApiService gameApiService;

    @GetMapping
    @Operation(summary = "Get games", description = "Returns games for a given season or date range")
    public ResponseEntity<?> getGames(
            @RequestParam(required = false) Integer season,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // If specific date is requested
        if (date != null) {
            return ResponseEntity.ok(gameApiService.getGamesByDate(date));
        }

        // If date range is requested
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(gameApiService.getGamesByDateRange(startDate, endDate));
        }

        // If team games are requested
        if (teamId != null) {
            return ResponseEntity.ok(gameApiService.getTeamGames(teamId, season));
        }

        // Default: paginated by season
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(gameApiService.getGamesBySeason(season, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get game by ID", description = "Returns a single game by ID")
    public ResponseEntity<GameDto> getGameById(@PathVariable Long id) {
        return ResponseEntity.ok(gameApiService.getGameById(id));
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's games", description = "Returns all games scheduled for today")
    public ResponseEntity<List<GameDto>> getTodaysGames() {
        return ResponseEntity.ok(gameApiService.getGamesByDate(LocalDate.now()));
    }

    @GetMapping("/{id}/boxscore")
    @Operation(summary = "Get game box score", description = "Returns detailed batting and pitching stats for a game")
    public ResponseEntity<BoxScoreDto> getBoxScore(@PathVariable Long id) {
        return ResponseEntity.ok(gameApiService.getBoxScore(id));
    }
}
