package com.mlbstats.api.controller;

import com.mlbstats.BaseIntegrationTest;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PublicControllerTest extends BaseIntegrationTest {

    @Test
    void getPublicStats_shouldNotRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/public/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getPublicStats_shouldReturnCorrectCounts() throws Exception {
        // Given
        Team yankees = createTestTeam(147, "New York Yankees", "NYY");
        Team redSox = createTestTeam(111, "Boston Red Sox", "BOS");
        Player judge = createTestPlayer(592450, "Aaron Judge", "RF");
        Player cole = createTestPlayer(650402, "Gerrit Cole", "P");
        createTestGame(745123, yankees, redSox, LocalDate.of(2024, 4, 1));

        // When/Then
        mockMvc.perform(get("/api/public/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamCount").value(2))
                .andExpect(jsonPath("$.playerCount").value(2))
                .andExpect(jsonPath("$.gameCount").value(1));
    }

    @Test
    void getPublicStats_shouldReturnZeroCountsWhenEmpty() throws Exception {
        mockMvc.perform(get("/api/public/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamCount").value(0))
                .andExpect(jsonPath("$.playerCount").value(0))
                .andExpect(jsonPath("$.gameCount").value(0));
    }
}
