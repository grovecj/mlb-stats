package com.mlbstats.api.controller;

import com.mlbstats.BaseIntegrationTest;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.user.AppUser;
import com.mlbstats.domain.user.AppUserRepository;
import com.mlbstats.domain.user.UserFavoritePlayer;
import com.mlbstats.domain.user.UserFavoritePlayerRepository;
import com.mlbstats.domain.user.UserFavoriteTeam;
import com.mlbstats.domain.user.UserFavoriteTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FavoritesDashboardControllerTest extends BaseIntegrationTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private UserFavoriteTeamRepository favoriteTeamRepository;

    @Autowired
    private UserFavoritePlayerRepository favoritePlayerRepository;

    private AppUser testUser;
    private Team yankees;
    private Team redSox;
    private Player judge;
    private Player cole;

    @BeforeEach
    void setUpData() {
        // Clean favorites first (before base class cleans teams/players)
        favoritePlayerRepository.deleteAll();
        favoriteTeamRepository.deleteAll();

        // Create test user
        testUser = new AppUser();
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser = appUserRepository.save(testUser);

        // Create teams and players
        yankees = createTestTeam(147, "New York Yankees", "NYY", "American League", "East");
        redSox = createTestTeam(111, "Boston Red Sox", "BOS", "American League", "East");
        judge = createTestPlayer(592450, "Aaron Judge", "RF");
        cole = createTestPlayer(650402, "Gerrit Cole", "P");
    }

    @Test
    void getDashboard_shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/favorites/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void getDashboard_shouldReturnEmptyForUserWithNoFavorites() throws Exception {
        mockMvc.perform(get("/api/favorites/dashboard")
                        .with(oauth2Login()
                                .attributes(attrs -> {
                                    attrs.put("email", testUser.getEmail());
                                    attrs.put("name", testUser.getName());
                                })))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.teams", hasSize(0)))
                .andExpect(jsonPath("$.players", hasSize(0)))
                .andExpect(jsonPath("$.hasMoreTeams").value(false))
                .andExpect(jsonPath("$.hasMorePlayers").value(false))
                .andExpect(jsonPath("$.totalTeamCount").value(0))
                .andExpect(jsonPath("$.totalPlayerCount").value(0));
    }

    @Test
    void getDashboard_shouldReturnFavoriteTeamsWithStandings() throws Exception {
        // Given - add favorites and standings
        favoriteTeamRepository.save(new UserFavoriteTeam(testUser, yankees));
        createTestStanding(yankees, LocalDate.now().getYear(), 85, 60);

        mockMvc.perform(get("/api/favorites/dashboard")
                        .with(oauth2Login()
                                .attributes(attrs -> {
                                    attrs.put("email", testUser.getEmail());
                                    attrs.put("name", testUser.getName());
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams", hasSize(1)))
                .andExpect(jsonPath("$.teams[0].team.name").value("New York Yankees"))
                .andExpect(jsonPath("$.teams[0].standing.wins").value(85))
                .andExpect(jsonPath("$.teams[0].standing.losses").value(60))
                .andExpect(jsonPath("$.totalTeamCount").value(1));
    }

    @Test
    void getDashboard_shouldReturnFavoritePlayersWithStats() throws Exception {
        // Given - add favorites and stats
        favoritePlayerRepository.save(new UserFavoritePlayer(testUser, judge));
        createTestBattingStats(judge, yankees, LocalDate.now().getYear());

        mockMvc.perform(get("/api/favorites/dashboard")
                        .with(oauth2Login()
                                .attributes(attrs -> {
                                    attrs.put("email", testUser.getEmail());
                                    attrs.put("name", testUser.getName());
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players", hasSize(1)))
                .andExpect(jsonPath("$.players[0].player.fullName").value("Aaron Judge"))
                .andExpect(jsonPath("$.players[0].playerType").value("BATTER"))
                .andExpect(jsonPath("$.players[0].seasonBatting.homeRuns").value(20))
                .andExpect(jsonPath("$.totalPlayerCount").value(1));
    }

    @Test
    void getDashboard_shouldReturnPitcherWithPitchingStats() throws Exception {
        // Given - add pitcher favorite
        favoritePlayerRepository.save(new UserFavoritePlayer(testUser, cole));
        createTestPitchingStats(cole, yankees, LocalDate.now().getYear());

        mockMvc.perform(get("/api/favorites/dashboard")
                        .with(oauth2Login()
                                .attributes(attrs -> {
                                    attrs.put("email", testUser.getEmail());
                                    attrs.put("name", testUser.getName());
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players", hasSize(1)))
                .andExpect(jsonPath("$.players[0].player.fullName").value("Gerrit Cole"))
                .andExpect(jsonPath("$.players[0].playerType").value("PITCHER"))
                .andExpect(jsonPath("$.players[0].seasonPitching.wins").value(15));
    }

    @Test
    void getDashboard_shouldReturnTodaysGame() throws Exception {
        // Given - add favorites and today's game
        favoriteTeamRepository.save(new UserFavoriteTeam(testUser, yankees));
        createTestGame(745123, yankees, redSox, LocalDate.now());
        createTestStanding(yankees, LocalDate.now().getYear(), 85, 60);

        mockMvc.perform(get("/api/favorites/dashboard")
                        .with(oauth2Login()
                                .attributes(attrs -> {
                                    attrs.put("email", testUser.getEmail());
                                    attrs.put("name", testUser.getName());
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams[0].todaysGame").isNotEmpty())
                .andExpect(jsonPath("$.teams[0].todaysGame.isHome").value(true))
                .andExpect(jsonPath("$.teams[0].todaysGame.opponent.abbreviation").value("BOS"));
    }

    @Test
    void getDashboard_shouldReturnNextGameWhenNoTodaysGame() throws Exception {
        // Given - add favorites and tomorrow's game
        favoriteTeamRepository.save(new UserFavoriteTeam(testUser, yankees));
        createTestGame(745123, yankees, redSox, LocalDate.now().plusDays(1));
        createTestStanding(yankees, LocalDate.now().getYear(), 85, 60);

        mockMvc.perform(get("/api/favorites/dashboard")
                        .with(oauth2Login()
                                .attributes(attrs -> {
                                    attrs.put("email", testUser.getEmail());
                                    attrs.put("name", testUser.getName());
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams[0].todaysGame").isEmpty())
                .andExpect(jsonPath("$.teams[0].nextGame").isNotEmpty())
                .andExpect(jsonPath("$.teams[0].nextGame.isHome").value(true));
    }

    @Test
    void getDashboard_shouldIndicateHasMoreWhenExceedingLimits() throws Exception {
        // Given - add 6 favorite teams (limit is 5)
        for (int i = 0; i < 6; i++) {
            Team team = createTestTeam(100 + i, "Team " + i, "T" + i, "American League", "East");
            favoriteTeamRepository.save(new UserFavoriteTeam(testUser, team));
        }

        mockMvc.perform(get("/api/favorites/dashboard")
                        .with(oauth2Login()
                                .attributes(attrs -> {
                                    attrs.put("email", testUser.getEmail());
                                    attrs.put("name", testUser.getName());
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams", hasSize(5)))
                .andExpect(jsonPath("$.hasMoreTeams").value(true))
                .andExpect(jsonPath("$.totalTeamCount").value(6));
    }
}
