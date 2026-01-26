package com.mlbstats.api.controller;

import com.mlbstats.api.dto.PlayerDto;
import com.mlbstats.api.dto.SearchResultDto;
import com.mlbstats.api.dto.TeamDto;
import com.mlbstats.api.service.PlayerApiService;
import com.mlbstats.api.service.TeamApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "Global search APIs")
@RequiredArgsConstructor
public class SearchController {

    private final TeamApiService teamApiService;
    private final PlayerApiService playerApiService;

    private static final int DEFAULT_LIMIT = 5;

    @GetMapping
    @Operation(summary = "Global search", description = "Search teams and players by name")
    public ResponseEntity<SearchResultDto> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int limit) {

        if (q == null || q.isBlank()) {
            return ResponseEntity.ok(new SearchResultDto(List.of(), List.of()));
        }

        String query = q.trim();
        int effectiveLimit = Math.min(limit, 10);

        List<TeamDto> teams = teamApiService.searchTeams(query).stream()
                .limit(effectiveLimit)
                .toList();

        List<PlayerDto> players = playerApiService.searchPlayers(query, effectiveLimit);

        return ResponseEntity.ok(new SearchResultDto(teams, players));
    }
}
