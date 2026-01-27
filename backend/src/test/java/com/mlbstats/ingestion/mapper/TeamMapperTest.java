package com.mlbstats.ingestion.mapper;

import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.dto.TeamResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TeamMapperTest {

    private TeamMapper teamMapper;

    @BeforeEach
    void setUp() {
        teamMapper = new TeamMapper();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // Given
        TeamResponse.TeamData teamData = createTeamData(
                147, "New York Yankees", "NYY", "Bronx",
                "Yankee Stadium", "American League", "American League East"
        );

        // When
        Team team = teamMapper.toEntity(teamData);

        // Then
        assertThat(team.getMlbId()).isEqualTo(147);
        assertThat(team.getName()).isEqualTo("New York Yankees");
        assertThat(team.getAbbreviation()).isEqualTo("NYY");
        assertThat(team.getLocationName()).isEqualTo("Bronx");
        assertThat(team.getVenueName()).isEqualTo("Yankee Stadium");
        assertThat(team.getLeague()).isEqualTo("American League");
        assertThat(team.getDivision()).isEqualTo("East");
    }

    @Test
    void toEntity_shouldExtractEastDivision() {
        // Given
        TeamResponse.TeamData teamData = createTeamData(
                147, "New York Yankees", "NYY", "Bronx",
                "Yankee Stadium", "American League", "American League East"
        );

        // When
        Team team = teamMapper.toEntity(teamData);

        // Then
        assertThat(team.getDivision()).isEqualTo("East");
    }

    @Test
    void toEntity_shouldExtractCentralDivision() {
        // Given
        TeamResponse.TeamData teamData = createTeamData(
                114, "Cleveland Guardians", "CLE", "Cleveland",
                "Progressive Field", "American League", "American League Central"
        );

        // When
        Team team = teamMapper.toEntity(teamData);

        // Then
        assertThat(team.getDivision()).isEqualTo("Central");
    }

    @Test
    void toEntity_shouldExtractWestDivision() {
        // Given
        TeamResponse.TeamData teamData = createTeamData(
                119, "Los Angeles Dodgers", "LAD", "Los Angeles",
                "Dodger Stadium", "National League", "National League West"
        );

        // When
        Team team = teamMapper.toEntity(teamData);

        // Then
        assertThat(team.getDivision()).isEqualTo("West");
    }

    @Test
    void toEntity_shouldHandleNullVenue() {
        // Given
        TeamResponse.TeamData teamData = createTeamData(
                147, "New York Yankees", "NYY", "Bronx",
                null, "American League", "American League East"
        );

        // When
        Team team = teamMapper.toEntity(teamData);

        // Then
        assertThat(team.getVenueName()).isNull();
    }

    @Test
    void toEntity_shouldHandleNullLeague() {
        // Given
        TeamResponse.TeamData teamData = createTeamData(
                147, "New York Yankees", "NYY", "Bronx",
                "Yankee Stadium", null, "American League East"
        );

        // When
        Team team = teamMapper.toEntity(teamData);

        // Then
        assertThat(team.getLeague()).isNull();
    }

    @Test
    void toEntity_shouldHandleNullDivision() {
        // Given
        TeamResponse.TeamData teamData = createTeamData(
                147, "New York Yankees", "NYY", "Bronx",
                "Yankee Stadium", "American League", null
        );

        // When
        Team team = teamMapper.toEntity(teamData);

        // Then
        assertThat(team.getDivision()).isNull();
    }

    @Test
    void updateEntity_shouldUpdateAllFields() {
        // Given
        Team existing = new Team();
        existing.setMlbId(147);
        existing.setName("Old Name");
        existing.setAbbreviation("OLD");

        TeamResponse.TeamData teamData = createTeamData(
                147, "New York Yankees", "NYY", "Bronx",
                "Yankee Stadium", "American League", "American League East"
        );

        // When
        teamMapper.updateEntity(existing, teamData);

        // Then
        assertThat(existing.getName()).isEqualTo("New York Yankees");
        assertThat(existing.getAbbreviation()).isEqualTo("NYY");
        assertThat(existing.getLocationName()).isEqualTo("Bronx");
        assertThat(existing.getVenueName()).isEqualTo("Yankee Stadium");
        assertThat(existing.getLeague()).isEqualTo("American League");
        assertThat(existing.getDivision()).isEqualTo("East");
    }

    @Test
    void toEntity_shouldHandleUnknownDivision() {
        // Given
        TeamResponse.TeamData teamData = createTeamData(
                999, "Test Team", "TST", "Test City",
                "Test Stadium", "Test League", "Some Unknown Division"
        );

        // When
        Team team = teamMapper.toEntity(teamData);

        // Then
        assertThat(team.getDivision()).isEqualTo("Some Unknown Division");
    }

    private TeamResponse.TeamData createTeamData(Integer id, String name, String abbreviation,
                                                   String locationName, String venueName,
                                                   String leagueName, String divisionName) {
        TeamResponse.TeamData teamData = new TeamResponse.TeamData();
        teamData.setId(id);
        teamData.setName(name);
        teamData.setAbbreviation(abbreviation);
        teamData.setLocationName(locationName);

        if (venueName != null) {
            TeamResponse.VenueData venue = new TeamResponse.VenueData();
            venue.setName(venueName);
            teamData.setVenue(venue);
        }

        if (leagueName != null) {
            TeamResponse.LeagueData league = new TeamResponse.LeagueData();
            league.setName(leagueName);
            teamData.setLeague(league);
        }

        if (divisionName != null) {
            TeamResponse.DivisionData division = new TeamResponse.DivisionData();
            division.setName(divisionName);
            teamData.setDivision(division);
        }

        return teamData;
    }
}
