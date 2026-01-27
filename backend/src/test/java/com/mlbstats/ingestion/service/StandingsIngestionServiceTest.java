package com.mlbstats.ingestion.service;

import com.mlbstats.BaseIntegrationTest;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamStanding;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.StandingsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class StandingsIngestionServiceTest extends BaseIntegrationTest {

    @MockitoBean
    private MlbApiClient mlbApiClient;

    @Autowired
    private StandingsIngestionService standingsIngestionService;

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
    void syncStandings_shouldCreateNewStandings() {
        // Given
        StandingsResponse response = loadFixture("mlb-api/standings-response.json", StandingsResponse.class);
        when(mlbApiClient.getStandings(2024)).thenReturn(response);

        // When
        int count = standingsIngestionService.syncStandings(2024);

        // Then
        assertThat(count).isEqualTo(3);
        List<TeamStanding> standings = teamStandingRepository.findAll();
        assertThat(standings).hasSize(3);

        TeamStanding yankeesStanding = teamStandingRepository.findByTeamAndSeason(yankees, 2024).orElseThrow();
        assertThat(yankeesStanding.getWins()).isEqualTo(94);
        assertThat(yankeesStanding.getLosses()).isEqualTo(68);
        assertThat(yankeesStanding.getWinningPercentage()).isEqualTo(new BigDecimal("0.580"));
        assertThat(yankeesStanding.getDivisionRank()).isEqualTo(1);
        assertThat(yankeesStanding.getStreakCode()).isEqualTo("W3");
        assertThat(yankeesStanding.getHomeWins()).isEqualTo(52);
        assertThat(yankeesStanding.getHomeLosses()).isEqualTo(29);
    }

    @Test
    void syncStandings_shouldUpdateExistingStandings() {
        // Given - create existing standing with old data
        TeamStanding existingStanding = new TeamStanding();
        existingStanding.setTeam(yankees);
        existingStanding.setSeason(2024);
        existingStanding.setWins(80);
        existingStanding.setLosses(50);
        teamStandingRepository.save(existingStanding);

        StandingsResponse response = loadFixture("mlb-api/standings-response.json", StandingsResponse.class);
        when(mlbApiClient.getStandings(2024)).thenReturn(response);

        // When
        int count = standingsIngestionService.syncStandings(2024);

        // Then
        assertThat(count).isEqualTo(3);
        List<TeamStanding> standings = teamStandingRepository.findAll();
        assertThat(standings).hasSize(3);

        TeamStanding updated = teamStandingRepository.findByTeamAndSeason(yankees, 2024).orElseThrow();
        assertThat(updated.getWins()).isEqualTo(94);
        assertThat(updated.getLosses()).isEqualTo(68);
    }

    @Test
    void syncStandings_shouldHandleNullResponse() {
        // Given
        when(mlbApiClient.getStandings(2024)).thenReturn(null);

        // When
        int count = standingsIngestionService.syncStandings(2024);

        // Then
        assertThat(count).isZero();
        assertThat(teamStandingRepository.findAll()).isEmpty();
    }

    @Test
    void syncStandings_shouldHandleEmptyRecords() {
        // Given
        StandingsResponse response = new StandingsResponse();
        response.setRecords(null);
        when(mlbApiClient.getStandings(2024)).thenReturn(response);

        // When
        int count = standingsIngestionService.syncStandings(2024);

        // Then
        assertThat(count).isZero();
        assertThat(teamStandingRepository.findAll()).isEmpty();
    }

    @Test
    void syncStandings_shouldSkipTeamsNotInDatabase() {
        // Given - remove Yankees team
        teamStandingRepository.deleteAll();
        teamRepository.delete(yankees);

        StandingsResponse response = loadFixture("mlb-api/standings-response.json", StandingsResponse.class);
        when(mlbApiClient.getStandings(2024)).thenReturn(response);

        // When
        int count = standingsIngestionService.syncStandings(2024);

        // Then
        assertThat(count).isEqualTo(2); // Only Red Sox and Dodgers saved
    }

    @Test
    void syncStandings_shouldParseRunDifferential() {
        // Given
        StandingsResponse response = loadFixture("mlb-api/standings-response.json", StandingsResponse.class);
        when(mlbApiClient.getStandings(2024)).thenReturn(response);

        // When
        standingsIngestionService.syncStandings(2024);

        // Then
        TeamStanding yankeesStanding = teamStandingRepository.findByTeamAndSeason(yankees, 2024).orElseThrow();
        assertThat(yankeesStanding.getRunsScored()).isEqualTo(825);
        assertThat(yankeesStanding.getRunsAllowed()).isEqualTo(680);
        assertThat(yankeesStanding.getRunDifferential()).isEqualTo(145);

        TeamStanding redSoxStanding = teamStandingRepository.findByTeamAndSeason(redSox, 2024).orElseThrow();
        assertThat(redSoxStanding.getRunDifferential()).isEqualTo(-10);
    }
}
