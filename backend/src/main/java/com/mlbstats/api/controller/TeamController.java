package com.mlbstats.api.controller;

import com.mlbstats.api.dto.BattingStatsDto;
import com.mlbstats.api.dto.GameDto;
import com.mlbstats.api.dto.RosterEntryDto;
import com.mlbstats.api.dto.TeamDto;
import com.mlbstats.api.dto.TeamStandingDto;
import com.mlbstats.api.service.TeamApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams", description = "Team management APIs")
@RequiredArgsConstructor
public class TeamController {

    private final TeamApiService teamApiService;

    @GetMapping
    @Operation(summary = "Get all teams", description = "Returns all MLB teams ordered by league and division")
    public ResponseEntity<List<TeamDto>> getAllTeams(
            @RequestParam(required = false) String league,
            @RequestParam(required = false) String division) {

        List<TeamDto> teams;
        if (league != null && division != null) {
            teams = teamApiService.getTeamsByDivision(league, division);
        } else if (league != null) {
            teams = teamApiService.getTeamsByLeague(league);
        } else {
            teams = teamApiService.getAllTeams();
        }
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID", description = "Returns a single team by its ID")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamApiService.getTeamById(id));
    }

    @GetMapping("/{id}/roster")
    @Operation(summary = "Get team roster", description = "Returns the roster for a team for a given season")
    public ResponseEntity<List<RosterEntryDto>> getTeamRoster(
            @PathVariable Long id,
            @RequestParam(required = false) Integer season) {
        return ResponseEntity.ok(teamApiService.getTeamRoster(id, season));
    }

    @GetMapping("/{id}/games")
    @Operation(summary = "Get team games", description = "Returns all games for a team for a given season")
    public ResponseEntity<List<GameDto>> getTeamGames(
            @PathVariable Long id,
            @RequestParam(required = false) Integer season) {
        return ResponseEntity.ok(teamApiService.getTeamGames(id, season));
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get team batting stats", description = "Returns batting stats for all players on a team")
    public ResponseEntity<List<BattingStatsDto>> getTeamStats(
            @PathVariable Long id,
            @RequestParam(required = false) Integer season) {
        return ResponseEntity.ok(teamApiService.getTeamBattingStats(id, season));
    }

    @GetMapping("/standings")
    @Operation(summary = "Get standings", description = "Returns standings for all teams for a given season")
    public ResponseEntity<List<TeamStandingDto>> getStandings(
            @RequestParam(required = false) Integer season) {
        return ResponseEntity.ok(teamApiService.getStandings(season));
    }

    @GetMapping("/{id}/standing")
    @Operation(summary = "Get team standing", description = "Returns the standing for a specific team")
    public ResponseEntity<TeamStandingDto> getTeamStanding(
            @PathVariable Long id,
            @RequestParam(required = false) Integer season) {
        TeamStandingDto standing = teamApiService.getTeamStanding(id, season);
        if (standing == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(standing);
    }
}
