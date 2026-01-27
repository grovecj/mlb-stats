package com.mlbstats.api.controller;

import com.mlbstats.BaseIntegrationTest;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TeamControllerTest extends BaseIntegrationTest {

    private Team yankees;
    private Team redSox;
    private Team dodgers;

    @BeforeEach
    void setUpTeams() {
        yankees = createTestTeam(147, "New York Yankees", "NYY", "American League", "East");
        redSox = createTestTeam(111, "Boston Red Sox", "BOS", "American League", "East");
        dodgers = createTestTeam(119, "Los Angeles Dodgers", "LAD", "National League", "West");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTeams_shouldReturnAllTeams() throws Exception {
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", hasItems("New York Yankees", "Boston Red Sox", "Los Angeles Dodgers")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTeams_shouldFilterByLeague() throws Exception {
        mockMvc.perform(get("/api/teams")
                        .param("league", "American League"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", hasItems("New York Yankees", "Boston Red Sox")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTeams_shouldFilterByLeagueAndDivision() throws Exception {
        mockMvc.perform(get("/api/teams")
                        .param("league", "American League")
                        .param("division", "East"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].abbreviation", hasItems("NYY", "BOS")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTeamById_shouldReturnTeam() throws Exception {
        mockMvc.perform(get("/api/teams/{id}", yankees.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New York Yankees"))
                .andExpect(jsonPath("$.abbreviation").value("NYY"))
                .andExpect(jsonPath("$.league").value("American League"))
                .andExpect(jsonPath("$.division").value("East"))
                .andExpect(jsonPath("$.logoUrl").value("https://www.mlbstatic.com/team-logos/147.svg"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTeamById_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/teams/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTeamRoster_shouldReturnRosterEntries() throws Exception {
        // Given
        Player judge = createTestPlayer(592450, "Aaron Judge", "RF");
        Player cole = createTestPlayer(650402, "Gerrit Cole", "P");
        createTestRosterEntry(yankees, judge, 2024);
        createTestRosterEntry(yankees, cole, 2024);

        // When/Then
        mockMvc.perform(get("/api/teams/{id}/roster", yankees.getId())
                        .param("season", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].player.fullName", hasItems("Aaron Judge", "Gerrit Cole")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTeamGames_shouldReturnGames() throws Exception {
        // Given
        createTestGame(745123, yankees, redSox, LocalDate.of(2024, 4, 1));
        createTestGame(745124, redSox, yankees, LocalDate.of(2024, 4, 2));

        // When/Then
        mockMvc.perform(get("/api/teams/{id}/games", yankees.getId())
                        .param("season", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTeamStats_shouldReturnBattingStats() throws Exception {
        // Given
        Player judge = createTestPlayer(592450, "Aaron Judge", "RF");
        createTestRosterEntry(yankees, judge, 2024);
        createTestBattingStats(judge, yankees, 2024);

        // When/Then
        mockMvc.perform(get("/api/teams/{id}/stats", yankees.getId())
                        .param("season", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].homeRuns").value(20));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStandings_shouldReturnStandings() throws Exception {
        // Given
        createTestStanding(yankees, 2024, 94, 68);
        createTestStanding(redSox, 2024, 81, 81);
        createTestStanding(dodgers, 2024, 98, 64);

        // When/Then
        mockMvc.perform(get("/api/teams/standings")
                        .param("season", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTeamStanding_shouldReturnStanding() throws Exception {
        // Given
        createTestStanding(yankees, 2024, 94, 68);

        // When/Then
        mockMvc.perform(get("/api/teams/{id}/standing", yankees.getId())
                        .param("season", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wins").value(94))
                .andExpect(jsonPath("$.losses").value(68));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTeamStanding_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/teams/{id}/standing", yankees.getId())
                        .param("season", "2024"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTeamAggregateStats_shouldReturnAggregatedStats() throws Exception {
        // Given - create multiple players with batting and pitching stats
        Player judge = createTestPlayer(592450, "Aaron Judge", "RF");
        Player stanton = createTestPlayer(519317, "Giancarlo Stanton", "DH");
        Player cole = createTestPlayer(650402, "Gerrit Cole", "P");

        createTestBattingStats(judge, yankees, 2024);
        createTestBattingStats(stanton, yankees, 2024);
        createTestPitchingStats(cole, yankees, 2024);

        // When/Then
        mockMvc.perform(get("/api/teams/{id}/aggregate-stats", yankees.getId())
                        .param("season", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value(yankees.getId()))
                .andExpect(jsonPath("$.season").value(2024))
                .andExpect(jsonPath("$.batting").exists())
                .andExpect(jsonPath("$.batting.homeRuns").value(40)) // 20 + 20 from two players
                .andExpect(jsonPath("$.batting.battingAvg").exists())
                .andExpect(jsonPath("$.pitching").exists())
                .andExpect(jsonPath("$.pitching.wins").value(15))
                .andExpect(jsonPath("$.pitching.era").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTeamAggregateStats_shouldReturnNullAggregatesWhenNoStats() throws Exception {
        // When/Then - team exists but has no stats
        mockMvc.perform(get("/api/teams/{id}/aggregate-stats", yankees.getId())
                        .param("season", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value(yankees.getId()))
                .andExpect(jsonPath("$.season").value(2024))
                .andExpect(jsonPath("$.batting").doesNotExist())
                .andExpect(jsonPath("$.pitching").doesNotExist());
    }

    @Test
    void getAllTeams_shouldRequireAuthentication() throws Exception {
        // Spring Security redirects unauthenticated requests to login page
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().is3xxRedirection());
    }
}
