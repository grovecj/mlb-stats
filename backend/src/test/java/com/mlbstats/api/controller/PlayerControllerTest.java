package com.mlbstats.api.controller;

import com.mlbstats.BaseIntegrationTest;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PlayerControllerTest extends BaseIntegrationTest {

    private Team yankees;
    private Player judge;
    private Player cole;

    @BeforeEach
    void setUpData() {
        yankees = createTestTeam(147, "New York Yankees", "NYY", "American League", "East");
        judge = createTestPlayer(592450, "Aaron Judge", "RF");
        cole = createTestPlayer(650402, "Gerrit Cole", "P");
        cole.setPositionType("Pitcher");
        playerRepository.save(cole);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayers_shouldReturnPaginatedPlayers() throws Exception {
        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[*].fullName", hasItems("Aaron Judge", "Gerrit Cole")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayers_shouldSupportPagination() throws Exception {
        mockMvc.perform(get("/api/players")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayers_shouldSearchByName() throws Exception {
        mockMvc.perform(get("/api/players")
                        .param("search", "Judge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("Aaron Judge"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayers_shouldReturnEmptyForNoMatches() throws Exception {
        mockMvc.perform(get("/api/players")
                        .param("search", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayerById_shouldReturnPlayer() throws Exception {
        mockMvc.perform(get("/api/players/{id}", judge.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Aaron Judge"))
                .andExpect(jsonPath("$.mlbId").value(592450))
                .andExpect(jsonPath("$.position").value("RF"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayerById_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/players/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayerBattingStats_shouldReturnStats() throws Exception {
        // Given
        createTestRosterEntry(yankees, judge, 2024);
        createTestBattingStats(judge, yankees, 2024);

        // When/Then
        mockMvc.perform(get("/api/players/{id}/batting-stats", judge.getId())
                        .param("season", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].homeRuns").value(20))
                .andExpect(jsonPath("$[0].battingAvg").value(0.300));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayerPitchingStats_shouldReturnStats() throws Exception {
        // Given
        createTestRosterEntry(yankees, cole, 2024);
        createTestPitchingStats(cole, yankees, 2024);

        // When/Then
        mockMvc.perform(get("/api/players/{id}/pitching-stats", cole.getId())
                        .param("season", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].wins").value(15))
                .andExpect(jsonPath("$[0].era").value(3.0));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getHomeRunLeaders_shouldReturnTopHitters() throws Exception {
        // Given
        Player soto = createTestPlayer(545361, "Juan Soto", "LF");
        createTestRosterEntry(yankees, judge, 2024);
        createTestRosterEntry(yankees, soto, 2024);

        var judgeStats = createTestBattingStats(judge, yankees, 2024);
        judgeStats.setHomeRuns(58);
        battingStatsRepository.save(judgeStats);

        var sotoStats = createTestBattingStats(soto, yankees, 2024);
        sotoStats.setHomeRuns(41);
        battingStatsRepository.save(sotoStats);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/home-runs")
                        .param("season", "2024")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].homeRuns").value(58))
                .andExpect(jsonPath("$[1].homeRuns").value(41));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBattingAverageLeaders_shouldReturnTopBatters() throws Exception {
        // Given
        createTestRosterEntry(yankees, judge, 2024);
        var stats = createTestBattingStats(judge, yankees, 2024);
        stats.setAtBats(400);
        battingStatsRepository.save(stats);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/batting-average")
                        .param("season", "2024")
                        .param("minAtBats", "100")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getWinsLeaders_shouldReturnTopPitchers() throws Exception {
        // Given
        createTestRosterEntry(yankees, cole, 2024);
        createTestPitchingStats(cole, yankees, 2024);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/wins")
                        .param("season", "2024")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].wins").value(15));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStrikeoutLeaders_shouldReturnTopPitchers() throws Exception {
        // Given
        createTestRosterEntry(yankees, cole, 2024);
        createTestPitchingStats(cole, yankees, 2024);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/strikeouts")
                        .param("season", "2024")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].strikeouts").value(200));
    }

    @Test
    void getPlayers_shouldRequireAuthentication() throws Exception {
        // Spring Security redirects unauthenticated requests to login page
        mockMvc.perform(get("/api/players"))
                .andExpect(status().is3xxRedirection());
    }

    // Leaderboard tests (from PR #139)

    @Test
    @WithMockUser(roles = "USER")
    void getRbiLeaders_shouldReturnTopHitters() throws Exception {
        // Given
        createTestRosterEntry(yankees, judge, 2024);
        var stats = createTestBattingStats(judge, yankees, 2024);
        stats.setRbi(130);
        battingStatsRepository.save(stats);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/rbi")
                        .param("season", "2024")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].rbi").value(130));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRunsLeaders_shouldReturnTopHitters() throws Exception {
        // Given
        createTestRosterEntry(yankees, judge, 2024);
        var stats = createTestBattingStats(judge, yankees, 2024);
        stats.setRuns(120);
        battingStatsRepository.save(stats);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/runs")
                        .param("season", "2024")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].runs").value(120));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getHitsLeaders_shouldReturnTopHitters() throws Exception {
        // Given
        createTestRosterEntry(yankees, judge, 2024);
        var stats = createTestBattingStats(judge, yankees, 2024);
        stats.setHits(200);
        battingStatsRepository.save(stats);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/hits")
                        .param("season", "2024")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].hits").value(200));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStolenBaseLeaders_shouldReturnTopHitters() throws Exception {
        // Given
        createTestRosterEntry(yankees, judge, 2024);
        var stats = createTestBattingStats(judge, yankees, 2024);
        stats.setStolenBases(50);
        battingStatsRepository.save(stats);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/stolen-bases")
                        .param("season", "2024")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].stolenBases").value(50));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOpsLeaders_shouldReturnTopHitters() throws Exception {
        // Given
        createTestRosterEntry(yankees, judge, 2024);
        var stats = createTestBattingStats(judge, yankees, 2024);
        stats.setAtBats(400);
        stats.setOps(new java.math.BigDecimal("1.050"));
        battingStatsRepository.save(stats);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/ops")
                        .param("season", "2024")
                        .param("minAtBats", "100")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEraLeaders_shouldReturnTopPitchers() throws Exception {
        // Given
        createTestRosterEntry(yankees, cole, 2024);
        var stats = createTestPitchingStats(cole, yankees, 2024);
        stats.setEra(new java.math.BigDecimal("2.50"));
        pitchingStatsRepository.save(stats);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/era")
                        .param("season", "2024")
                        .param("minInnings", "50")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getSavesLeaders_shouldReturnTopPitchers() throws Exception {
        // Given
        createTestRosterEntry(yankees, cole, 2024);
        var stats = createTestPitchingStats(cole, yankees, 2024);
        stats.setSaves(40);
        pitchingStatsRepository.save(stats);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/saves")
                        .param("season", "2024")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].saves").value(40));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getWhipLeaders_shouldReturnTopPitchers() throws Exception {
        // Given
        createTestRosterEntry(yankees, cole, 2024);
        var stats = createTestPitchingStats(cole, yankees, 2024);
        stats.setWhip(new java.math.BigDecimal("0.95"));
        pitchingStatsRepository.save(stats);

        // When/Then
        mockMvc.perform(get("/api/players/leaders/whip")
                        .param("season", "2024")
                        .param("minInnings", "50")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // Filter tests (from PR #140)

    @Test
    @WithMockUser(roles = "USER")
    void getPlayers_shouldFilterByPosition() throws Exception {
        // Judge is RF, Cole is P
        mockMvc.perform(get("/api/players")
                        .param("position", "RF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("Aaron Judge"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayers_shouldFilterByPositionType() throws Exception {
        // Cole is Pitcher
        mockMvc.perform(get("/api/players")
                        .param("positionType", "Pitcher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("Gerrit Cole"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayers_shouldFilterByBats() throws Exception {
        // Modify judge to bat left
        judge.setBats("L");
        playerRepository.save(judge);

        mockMvc.perform(get("/api/players")
                        .param("bats", "L"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("Aaron Judge"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayers_shouldFilterByThrows() throws Exception {
        // Modify cole to throw left
        cole.setThrowsHand("L");
        playerRepository.save(cole);

        mockMvc.perform(get("/api/players")
                        .param("throws", "L"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("Gerrit Cole"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayers_shouldFilterByActiveStatus() throws Exception {
        // Set judge as inactive
        judge.setActive(false);
        playerRepository.save(judge);

        mockMvc.perform(get("/api/players")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("Gerrit Cole"));

        mockMvc.perform(get("/api/players")
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("Aaron Judge"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPlayers_shouldFilterByMultipleCriteria() throws Exception {
        // Add a third player: left-handed pitcher
        Player leftyPitcher = createTestPlayer(123456, "Clayton Kershaw", "P");
        leftyPitcher.setPositionType("Pitcher");
        leftyPitcher.setThrowsHand("L");
        playerRepository.save(leftyPitcher);

        // Filter by position type and throws - should find only Kershaw
        mockMvc.perform(get("/api/players")
                        .param("positionType", "Pitcher")
                        .param("throws", "L"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("Clayton Kershaw"));
    }
}
