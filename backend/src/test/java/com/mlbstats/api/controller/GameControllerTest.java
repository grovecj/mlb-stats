package com.mlbstats.api.controller;

import com.mlbstats.BaseIntegrationTest;
import com.mlbstats.domain.team.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GameControllerTest extends BaseIntegrationTest {

    private Team yankees;
    private Team redSox;
    private Team dodgers;
    private Team mets;

    @BeforeEach
    void setUpTeams() {
        yankees = createTestTeam(147, "New York Yankees", "NYY", "American League", "East");
        redSox = createTestTeam(111, "Boston Red Sox", "BOS", "American League", "East");
        dodgers = createTestTeam(119, "Los Angeles Dodgers", "LAD", "National League", "West");
        mets = createTestTeam(121, "New York Mets", "NYM", "National League", "East");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getGamesByDate_shouldReturnGamesForDate() throws Exception {
        // Given
        LocalDate gameDate = LocalDate.of(2024, 4, 1);
        createTestGame(745123, yankees, redSox, gameDate);
        createTestGame(745124, dodgers, mets, gameDate);
        createTestGame(745125, yankees, redSox, LocalDate.of(2024, 4, 2));

        // When/Then
        mockMvc.perform(get("/api/games")
                        .param("date", "2024-04-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getGamesByDateRange_shouldReturnGamesInRange() throws Exception {
        // Given
        createTestGame(745123, yankees, redSox, LocalDate.of(2024, 4, 1));
        createTestGame(745124, dodgers, mets, LocalDate.of(2024, 4, 3));
        createTestGame(745125, yankees, redSox, LocalDate.of(2024, 4, 5));

        // When/Then
        mockMvc.perform(get("/api/games")
                        .param("startDate", "2024-04-01")
                        .param("endDate", "2024-04-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getGamesByTeam_shouldReturnTeamGames() throws Exception {
        // Given
        createTestGame(745123, yankees, redSox, LocalDate.of(2024, 4, 1));
        createTestGame(745124, redSox, yankees, LocalDate.of(2024, 4, 2));
        createTestGame(745125, dodgers, mets, LocalDate.of(2024, 4, 1));

        // When/Then
        mockMvc.perform(get("/api/games")
                        .param("teamId", yankees.getId().toString())
                        .param("season", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getGamesBySeason_shouldReturnPaginatedGames() throws Exception {
        // Given
        createTestGame(745123, yankees, redSox, LocalDate.of(2024, 4, 1));
        createTestGame(745124, dodgers, mets, LocalDate.of(2024, 4, 1));
        createTestGame(745125, yankees, redSox, LocalDate.of(2024, 4, 2));

        // When/Then
        mockMvc.perform(get("/api/games")
                        .param("season", "2024")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getGameById_shouldReturnGame() throws Exception {
        // Given
        var game = createTestGame(745123, yankees, redSox, LocalDate.of(2024, 4, 1));

        // When/Then
        mockMvc.perform(get("/api/games/{id}", game.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeTeam.name").value("New York Yankees"))
                .andExpect(jsonPath("$.awayTeam.name").value("Boston Red Sox"))
                .andExpect(jsonPath("$.homeScore").value(5))
                .andExpect(jsonPath("$.awayScore").value(3));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getGameById_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/games/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTodaysGames_shouldReturnTodaysGames() throws Exception {
        // Given
        createTestGame(745123, yankees, redSox, LocalDate.now());
        createTestGame(745124, dodgers, mets, LocalDate.now().minusDays(1));

        // When/Then
        mockMvc.perform(get("/api/games/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getGames_shouldRequireAuthentication() throws Exception {
        // Spring Security redirects unauthenticated requests to login page
        mockMvc.perform(get("/api/games"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getLinescore_shouldReturnInningByInningScoring() throws Exception {
        // Given
        var game = createTestGame(745123, yankees, redSox, LocalDate.of(2024, 4, 1));
        game.setHomeHits(9);
        game.setAwayHits(7);
        game.setHomeErrors(0);
        game.setAwayErrors(1);
        gameRepository.save(game);

        // Create innings: BOS scores 1 in 1st, 2 in 6th; NYY scores 2 in 3rd, 1 in 5th, 2 in 7th
        createTestInning(game, 1, 1, 0);
        createTestInning(game, 2, 0, 0);
        createTestInning(game, 3, 0, 2);
        createTestInning(game, 4, 0, 0);
        createTestInning(game, 5, 0, 1);
        createTestInning(game, 6, 2, 0);
        createTestInning(game, 7, 0, 2);
        createTestInning(game, 8, 0, 0);
        createTestInning(game, 9, 0, 0);

        // When/Then
        mockMvc.perform(get("/api/games/{id}/linescore", game.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(game.getId()))
                .andExpect(jsonPath("$.innings", hasSize(9)))
                .andExpect(jsonPath("$.innings[0].inning").value(1))
                .andExpect(jsonPath("$.innings[0].awayRuns").value(1))
                .andExpect(jsonPath("$.innings[0].homeRuns").value(0))
                .andExpect(jsonPath("$.awayTotals.runs").value(3))
                .andExpect(jsonPath("$.awayTotals.hits").value(7))
                .andExpect(jsonPath("$.awayTotals.errors").value(1))
                .andExpect(jsonPath("$.homeTotals.runs").value(5))
                .andExpect(jsonPath("$.homeTotals.hits").value(9))
                .andExpect(jsonPath("$.homeTotals.errors").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getLinescore_shouldReturn404WhenGameNotFound() throws Exception {
        mockMvc.perform(get("/api/games/{id}/linescore", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getLinescore_shouldReturnEmptyInningsWhenNoData() throws Exception {
        // Given
        var game = createTestGame(745123, yankees, redSox, LocalDate.of(2024, 4, 1));

        // When/Then - no innings created
        mockMvc.perform(get("/api/games/{id}/linescore", game.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(game.getId()))
                .andExpect(jsonPath("$.innings", hasSize(0)));
    }
}
