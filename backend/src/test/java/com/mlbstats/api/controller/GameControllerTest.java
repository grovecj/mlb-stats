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
}
