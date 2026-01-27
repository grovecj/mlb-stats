package com.mlbstats.ingestion.service;

import com.mlbstats.BaseIntegrationTest;
import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.ScheduleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GameIngestionServiceTest extends BaseIntegrationTest {

    @MockitoBean
    private MlbApiClient mlbApiClient;

    @Autowired
    private GameIngestionService gameIngestionService;

    private Team yankees;
    private Team redSox;
    private Team dodgers;
    private Team mets;
    private Team guardians;

    @BeforeEach
    void setUpTeams() {
        yankees = createTestTeam(147, "New York Yankees", "NYY", "American League", "East");
        redSox = createTestTeam(111, "Boston Red Sox", "BOS", "American League", "East");
        dodgers = createTestTeam(119, "Los Angeles Dodgers", "LAD", "National League", "West");
        mets = createTestTeam(121, "New York Mets", "NYM", "National League", "East");
        guardians = createTestTeam(114, "Cleveland Guardians", "CLE", "American League", "Central");
    }

    @Test
    void syncGamesForDateRange_shouldCreateNewGames() {
        // Given
        ScheduleResponse response = loadFixture("mlb-api/schedule-response.json", ScheduleResponse.class);
        when(mlbApiClient.getSchedule(any(), any())).thenReturn(response);

        // When
        int count = gameIngestionService.syncGamesForDateRange(
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 4, 2)
        );

        // Then
        assertThat(count).isEqualTo(3);
        List<Game> games = gameRepository.findAll();
        assertThat(games).hasSize(3);

        Game yankeesVsRedSox = gameRepository.findByMlbId(745123).orElseThrow();
        assertThat(yankeesVsRedSox.getHomeTeam().getMlbId()).isEqualTo(147);
        assertThat(yankeesVsRedSox.getAwayTeam().getMlbId()).isEqualTo(111);
        assertThat(yankeesVsRedSox.getHomeScore()).isEqualTo(5);
        assertThat(yankeesVsRedSox.getAwayScore()).isEqualTo(3);
        assertThat(yankeesVsRedSox.getStatus()).isEqualTo("Final");
    }

    @Test
    void syncGamesForDateRange_shouldUpdateExistingGames() {
        // Given - create existing game with old data
        Game existingGame = new Game();
        existingGame.setMlbId(745123);
        existingGame.setHomeTeam(yankees);
        existingGame.setAwayTeam(redSox);
        existingGame.setGameDate(LocalDate.of(2024, 4, 1));
        existingGame.setSeason(2024);
        existingGame.setHomeScore(0);
        existingGame.setAwayScore(0);
        existingGame.setStatus("Preview");
        gameRepository.save(existingGame);

        ScheduleResponse response = loadFixture("mlb-api/schedule-response.json", ScheduleResponse.class);
        when(mlbApiClient.getSchedule(any(), any())).thenReturn(response);

        // When
        int count = gameIngestionService.syncGamesForDateRange(
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 4, 2)
        );

        // Then
        assertThat(count).isEqualTo(3);
        List<Game> games = gameRepository.findAll();
        assertThat(games).hasSize(3);

        Game updated = gameRepository.findByMlbId(745123).orElseThrow();
        assertThat(updated.getHomeScore()).isEqualTo(5);
        assertThat(updated.getAwayScore()).isEqualTo(3);
        assertThat(updated.getStatus()).isEqualTo("Final");
    }

    @Test
    void syncGamesForDateRange_shouldHandleNullResponse() {
        // Given
        when(mlbApiClient.getSchedule(any(), any())).thenReturn(null);

        // When
        int count = gameIngestionService.syncGamesForDateRange(
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 4, 2)
        );

        // Then
        assertThat(count).isZero();
        assertThat(gameRepository.findAll()).isEmpty();
    }

    @Test
    void syncGamesForDateRange_shouldHandleEmptyDates() {
        // Given
        ScheduleResponse response = new ScheduleResponse();
        response.setDates(null);
        when(mlbApiClient.getSchedule(any(), any())).thenReturn(response);

        // When
        int count = gameIngestionService.syncGamesForDateRange(
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 4, 2)
        );

        // Then
        assertThat(count).isZero();
        assertThat(gameRepository.findAll()).isEmpty();
    }

    @Test
    void syncGamesForDateRange_shouldSkipGamesWithMissingTeams() {
        // Given - remove the Guardians team so one game will fail
        teamRepository.delete(guardians);

        ScheduleResponse response = loadFixture("mlb-api/schedule-response.json", ScheduleResponse.class);
        when(mlbApiClient.getSchedule(any(), any())).thenReturn(response);

        // When
        int count = gameIngestionService.syncGamesForDateRange(
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 4, 2)
        );

        // Then
        assertThat(count).isEqualTo(3); // Count includes attempted syncs
        List<Game> games = gameRepository.findAll();
        assertThat(games).hasSize(2); // But only 2 actually saved
    }

    @Test
    void syncGamesForSeason_shouldSyncFullSeason() {
        // Given
        ScheduleResponse response = loadFixture("mlb-api/schedule-response.json", ScheduleResponse.class);
        when(mlbApiClient.getSchedule(any(), any())).thenReturn(response);

        // When
        int count = gameIngestionService.syncGamesForSeason(2024);

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void getGamesByDate_shouldReturnGamesForDate() {
        // Given
        LocalDate gameDate = LocalDate.of(2024, 4, 1);
        createTestGame(745123, yankees, redSox, gameDate);
        createTestGame(745124, dodgers, mets, gameDate);
        createTestGame(745125, guardians, yankees, LocalDate.of(2024, 4, 2));

        // When
        List<Game> games = gameIngestionService.getGamesByDate(gameDate);

        // Then
        assertThat(games).hasSize(2);
    }
}
