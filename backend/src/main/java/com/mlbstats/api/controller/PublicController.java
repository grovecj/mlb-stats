package com.mlbstats.api.controller;

import com.mlbstats.api.dto.PublicStatsDto;
import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.game.GameRepository;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.team.TeamRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Year;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public", description = "Public endpoints that don't require authentication")
@RequiredArgsConstructor
public class PublicController {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    @GetMapping("/stats")
    @Operation(summary = "Get public statistics", description = "Returns high-level statistics about the database")
    public PublicStatsDto getPublicStats() {
        long teamCount = teamRepository.count();
        long playerCount = playerRepository.count();
        long gameCount = gameRepository.count();

        int currentSeason = Year.now().getValue();
        long currentSeasonGames = gameRepository.countBySeason(currentSeason);

        LocalDate lastUpdated = gameRepository.findTopByOrderByGameDateDesc()
                .map(Game::getGameDate)
                .orElse(null);

        return new PublicStatsDto(
                teamCount,
                playerCount,
                gameCount,
                currentSeasonGames,
                lastUpdated
        );
    }
}
