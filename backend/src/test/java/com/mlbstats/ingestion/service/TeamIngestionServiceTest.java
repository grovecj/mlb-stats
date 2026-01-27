package com.mlbstats.ingestion.service;

import com.mlbstats.BaseIntegrationTest;
import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.TeamResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TeamIngestionServiceTest extends BaseIntegrationTest {

    @MockitoBean
    private MlbApiClient mlbApiClient;

    @Autowired
    private TeamIngestionService teamIngestionService;

    @Test
    void syncAllTeams_shouldCreateNewTeams() {
        // Given
        TeamResponse response = loadFixture("mlb-api/teams-response.json", TeamResponse.class);
        when(mlbApiClient.getAllTeams()).thenReturn(response);

        // When
        int count = teamIngestionService.syncAllTeams();

        // Then
        assertThat(count).isEqualTo(5);
        List<Team> teams = teamRepository.findAll();
        assertThat(teams).hasSize(5);

        Team yankees = teamRepository.findByMlbId(147).orElseThrow();
        assertThat(yankees.getName()).isEqualTo("New York Yankees");
        assertThat(yankees.getAbbreviation()).isEqualTo("NYY");
        assertThat(yankees.getLeague()).isEqualTo("American League");
        assertThat(yankees.getDivision()).isEqualTo("East");
        assertThat(yankees.getVenueName()).isEqualTo("Yankee Stadium");
    }

    @Test
    void syncAllTeams_shouldUpdateExistingTeams() {
        // Given - create existing team with old data
        Team existingTeam = new Team();
        existingTeam.setMlbId(147);
        existingTeam.setName("Old Name");
        existingTeam.setAbbreviation("OLD");
        existingTeam.setLocationName("Old Location");
        teamRepository.save(existingTeam);

        TeamResponse response = loadFixture("mlb-api/teams-response.json", TeamResponse.class);
        when(mlbApiClient.getAllTeams()).thenReturn(response);

        // When
        int count = teamIngestionService.syncAllTeams();

        // Then
        assertThat(count).isEqualTo(5);
        List<Team> teams = teamRepository.findAll();
        assertThat(teams).hasSize(5);

        Team yankees = teamRepository.findByMlbId(147).orElseThrow();
        assertThat(yankees.getName()).isEqualTo("New York Yankees");
        assertThat(yankees.getAbbreviation()).isEqualTo("NYY");
    }

    @Test
    void syncAllTeams_shouldHandleNullResponse() {
        // Given
        when(mlbApiClient.getAllTeams()).thenReturn(null);

        // When
        int count = teamIngestionService.syncAllTeams();

        // Then
        assertThat(count).isZero();
        assertThat(teamRepository.findAll()).isEmpty();
    }

    @Test
    void syncAllTeams_shouldHandleEmptyTeamsList() {
        // Given
        TeamResponse response = new TeamResponse();
        response.setTeams(null);
        when(mlbApiClient.getAllTeams()).thenReturn(response);

        // When
        int count = teamIngestionService.syncAllTeams();

        // Then
        assertThat(count).isZero();
        assertThat(teamRepository.findAll()).isEmpty();
    }

    @Test
    void syncAllTeams_shouldExtractDivisionCorrectly() {
        // Given
        TeamResponse response = loadFixture("mlb-api/teams-response.json", TeamResponse.class);
        when(mlbApiClient.getAllTeams()).thenReturn(response);

        // When
        teamIngestionService.syncAllTeams();

        // Then
        Team yankees = teamRepository.findByMlbId(147).orElseThrow();
        assertThat(yankees.getDivision()).isEqualTo("East");

        Team guardians = teamRepository.findByMlbId(114).orElseThrow();
        assertThat(guardians.getDivision()).isEqualTo("Central");

        Team dodgers = teamRepository.findByMlbId(119).orElseThrow();
        assertThat(dodgers.getDivision()).isEqualTo("West");
    }

    @Test
    void getAllTeams_shouldReturnAllTeams() {
        // Given
        createTestTeam(147, "New York Yankees", "NYY", "American League", "East");
        createTestTeam(111, "Boston Red Sox", "BOS", "American League", "East");

        // When
        List<Team> teams = teamIngestionService.getAllTeams();

        // Then
        assertThat(teams).hasSize(2);
    }

    @Test
    void getTeamByMlbId_shouldReturnTeam() {
        // Given
        createTestTeam(147, "New York Yankees", "NYY");

        // When
        Team team = teamIngestionService.getTeamByMlbId(147);

        // Then
        assertThat(team).isNotNull();
        assertThat(team.getName()).isEqualTo("New York Yankees");
    }

    @Test
    void getTeamByMlbId_shouldReturnNullWhenNotFound() {
        // When
        Team team = teamIngestionService.getTeamByMlbId(999);

        // Then
        assertThat(team).isNull();
    }
}
