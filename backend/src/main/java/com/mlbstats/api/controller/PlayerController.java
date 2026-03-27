package com.mlbstats.api.controller;

import com.mlbstats.api.dto.*;
import com.mlbstats.api.service.PlayerApiService;
import com.mlbstats.domain.player.PlayerSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/players")
@Tag(name = "Players", description = "Player management APIs")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerApiService playerApiService;

    @GetMapping
    @Operation(summary = "Get players", description = "Returns a paginated list of players with optional filters")
    public ResponseEntity<PageDto<PlayerDto>> getPlayers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String positionType,
            @RequestParam(required = false) String bats,
            @RequestParam(required = false, name = "throws") String throwsHand,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName"));

        PlayerSearchCriteria criteria = new PlayerSearchCriteria(
                search, position, positionType, bats, throwsHand, active
        );

        if (criteria.hasAnyFilter()) {
            return ResponseEntity.ok(playerApiService.searchPlayersWithFilters(criteria, pageable));
        } else {
            return ResponseEntity.ok(playerApiService.getAllPlayers(pageable));
        }
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

    @GetMapping("/leaders/rbi")
    @Operation(summary = "Get RBI leaders", description = "Returns top players by RBI for a season")
    public ResponseEntity<List<BattingStatsDto>> getRbiLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopRbi(season, limit));
    }

    @GetMapping("/leaders/runs")
    @Operation(summary = "Get runs leaders", description = "Returns top players by runs scored for a season")
    public ResponseEntity<List<BattingStatsDto>> getRunsLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopRuns(season, limit));
    }

    @GetMapping("/leaders/hits")
    @Operation(summary = "Get hits leaders", description = "Returns top players by hits for a season")
    public ResponseEntity<List<BattingStatsDto>> getHitsLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopHits(season, limit));
    }

    @GetMapping("/leaders/stolen-bases")
    @Operation(summary = "Get stolen base leaders", description = "Returns top players by stolen bases for a season")
    public ResponseEntity<List<BattingStatsDto>> getStolenBaseLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopStolenBases(season, limit));
    }

    @GetMapping("/leaders/ops")
    @Operation(summary = "Get OPS leaders", description = "Returns top players by OPS for a season")
    public ResponseEntity<List<BattingStatsDto>> getOpsLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "100") int minAtBats,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopOps(season, minAtBats, limit));
    }

    @GetMapping("/leaders/era")
    @Operation(summary = "Get ERA leaders", description = "Returns top pitchers by ERA for a season")
    public ResponseEntity<List<PitchingStatsDto>> getEraLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "50") java.math.BigDecimal minInnings,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopEra(season, minInnings, limit));
    }

    @GetMapping("/leaders/saves")
    @Operation(summary = "Get saves leaders", description = "Returns top pitchers by saves for a season")
    public ResponseEntity<List<PitchingStatsDto>> getSavesLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopSaves(season, limit));
    }

    @GetMapping("/leaders/whip")
    @Operation(summary = "Get WHIP leaders", description = "Returns top pitchers by WHIP for a season")
    public ResponseEntity<List<PitchingStatsDto>> getWhipLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "50") java.math.BigDecimal minInnings,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopWhip(season, minInnings, limit));
    }

    // Advanced Sabermetric Leaderboards

    @GetMapping("/leaders/war/batting")
    @Operation(summary = "Get batting WAR leaders", description = "Returns top position players by WAR for a season")
    public ResponseEntity<List<BattingStatsDto>> getBattingWarLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopBattingWar(season, limit));
    }

    @GetMapping("/leaders/woba")
    @Operation(summary = "Get wOBA leaders", description = "Returns top players by weighted on-base average for a season")
    public ResponseEntity<List<BattingStatsDto>> getWobaLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "200") int minPa,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopWoba(season, minPa, limit));
    }

    @GetMapping("/leaders/wrc-plus")
    @Operation(summary = "Get wRC+ leaders", description = "Returns top players by weighted runs created plus for a season")
    public ResponseEntity<List<BattingStatsDto>> getWrcPlusLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "200") int minPa,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopWrcPlus(season, minPa, limit));
    }

    @GetMapping("/leaders/exit-velocity")
    @Operation(summary = "Get exit velocity leaders", description = "Returns top players by average exit velocity for a season")
    public ResponseEntity<List<BattingStatsDto>> getExitVelocityLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "100") int minPa,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopExitVelocity(season, minPa, limit));
    }

    @GetMapping("/leaders/barrel-pct")
    @Operation(summary = "Get barrel percentage leaders", description = "Returns top players by barrel percentage for a season")
    public ResponseEntity<List<BattingStatsDto>> getBarrelPctLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "100") int minPa,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopBarrelPct(season, minPa, limit));
    }

    @GetMapping("/leaders/war/pitching")
    @Operation(summary = "Get pitching WAR leaders", description = "Returns top pitchers by WAR for a season")
    public ResponseEntity<List<PitchingStatsDto>> getPitchingWarLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopPitchingWar(season, limit));
    }

    @GetMapping("/leaders/fip")
    @Operation(summary = "Get FIP leaders", description = "Returns top pitchers by fielding independent pitching for a season")
    public ResponseEntity<List<PitchingStatsDto>> getFipLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "50") java.math.BigDecimal minInnings,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopFip(season, minInnings, limit));
    }

    @GetMapping("/leaders/xfip")
    @Operation(summary = "Get xFIP leaders", description = "Returns top pitchers by expected fielding independent pitching for a season")
    public ResponseEntity<List<PitchingStatsDto>> getXfipLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "50") java.math.BigDecimal minInnings,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopXfip(season, minInnings, limit));
    }

    @GetMapping("/leaders/xera")
    @Operation(summary = "Get xERA leaders", description = "Returns top pitchers by expected ERA for a season")
    public ResponseEntity<List<PitchingStatsDto>> getXeraLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "50") java.math.BigDecimal minInnings,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopXera(season, minInnings, limit));
    }

    @GetMapping("/leaders/whiff-pct")
    @Operation(summary = "Get whiff percentage leaders", description = "Returns top pitchers by whiff percentage for a season")
    public ResponseEntity<List<PitchingStatsDto>> getWhiffPctLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "50") java.math.BigDecimal minInnings,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopWhiffPct(season, minInnings, limit));
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

    @GetMapping("/compare")
    @Operation(summary = "Compare players", description = "Compare 2-4 players side-by-side with batting and pitching stats")
    public ResponseEntity<PlayerComparisonDto> comparePlayers(
            @RequestParam String players,
            @RequestParam(required = false) String seasons,
            @RequestParam(defaultValue = "season") String mode) {

        List<Long> playerIds;
        try {
            playerIds = Arrays.stream(players.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .toList();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        if (playerIds.size() < 2 || playerIds.size() > 4) {
            return ResponseEntity.badRequest().build();
        }

        boolean careerMode = "career".equalsIgnoreCase(mode);

        List<Integer> seasonList = null;
        if (!careerMode) {
            if (seasons == null || seasons.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            try {
                seasonList = Arrays.stream(seasons.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .toList();
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().build();
            }
            if (seasonList.size() != playerIds.size()) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(playerApiService.comparePlayerStats(playerIds, seasonList, careerMode));
    }

    // ================================================================================
    // gWAR (Grove WAR) Endpoints
    // ================================================================================

    @GetMapping("/leaders/gwar/batting")
    @Operation(summary = "Get batting gWAR leaders", description = "Returns top position players by gWAR (Grove WAR) for a season")
    public ResponseEntity<List<BattingStatsDto>> getBattingGwarLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopBattingGwar(season, limit));
    }

    @GetMapping("/leaders/gwar/pitching")
    @Operation(summary = "Get pitching gWAR leaders", description = "Returns top pitchers by gWAR (Grove WAR) for a season")
    public ResponseEntity<List<PitchingStatsDto>> getPitchingGwarLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopPitchingGwar(season, limit));
    }

    @GetMapping("/leaders/oaa")
    @Operation(summary = "Get OAA leaders", description = "Returns top fielders by Outs Above Average for a season")
    public ResponseEntity<List<BattingStatsDto>> getOaaLeaders(
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerApiService.getTopOaa(season, limit));
    }

    @GetMapping("/{id}/gwar-breakdown")
    @Operation(summary = "Get gWAR breakdown", description = "Returns detailed breakdown of gWAR components for a player")
    public ResponseEntity<GwarBreakdownDto> getGwarBreakdown(
            @PathVariable Long id,
            @RequestParam(required = false) Integer season) {
        return ResponseEntity.ok(playerApiService.getGwarBreakdown(id, season));
    }
}
