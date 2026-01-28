package com.mlbstats.ingestion.mapper;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.dto.ScheduleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameMapperTest {

    @Mock
    private PlayerRepository playerRepository;

    private GameMapper gameMapper;

    private Team homeTeam;
    private Team awayTeam;

    @BeforeEach
    void setUp() {
        gameMapper = new GameMapper(playerRepository);

        homeTeam = new Team();
        homeTeam.setId(1L);
        homeTeam.setMlbId(147);
        homeTeam.setName("New York Yankees");

        awayTeam = new Team();
        awayTeam.setId(2L);
        awayTeam.setMlbId(111);
        awayTeam.setName("Boston Red Sox");
    }

    @Test
    void toEntity_shouldMapAllFields() {
        ScheduleResponse.GameData gameData = createGameData(
                123456, "2024-07-15T19:05:00Z", "R", "Scheduled",
                null, null, "Yankee Stadium", "night", 9, 2024
        );

        Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);

        assertThat(game.getMlbId()).isEqualTo(123456);
        assertThat(game.getGameDate()).isEqualTo(java.time.LocalDate.of(2024, 7, 15));
        assertThat(game.getGameType()).isEqualTo("R");
        assertThat(game.getStatus()).isEqualTo("Scheduled");
        assertThat(game.getVenueName()).isEqualTo("Yankee Stadium");
        assertThat(game.getDayNight()).isEqualTo("night");
        assertThat(game.getScheduledInnings()).isEqualTo(9);
        assertThat(game.getSeason()).isEqualTo(2024);
        assertThat(game.getHomeTeam()).isEqualTo(homeTeam);
        assertThat(game.getAwayTeam()).isEqualTo(awayTeam);
    }

    @Test
    void toEntity_shouldMapScores() {
        ScheduleResponse.GameData gameData = createGameData(
                123456, "2024-07-15T19:05:00Z", "R", "Final",
                5, 3, "Yankee Stadium", "night", 9, 2024
        );

        Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);

        assertThat(game.getHomeScore()).isEqualTo(5);
        assertThat(game.getAwayScore()).isEqualTo(3);
    }

    @Test
    void toEntity_shouldDefaultScheduledInningsToNine() {
        ScheduleResponse.GameData gameData = createGameData(
                123456, "2024-07-15T19:05:00Z", "R", "Scheduled",
                null, null, "Yankee Stadium", "night", null, 2024
        );

        Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);

        assertThat(game.getScheduledInnings()).isEqualTo(9);
    }

    @Test
    void toEntity_shouldMapProbablePitcher_whenPitcherExistsInDatabase() {
        Player homePitcher = createPlayer(543037, "Gerrit Cole");
        Player awayPitcher = createPlayer(452657, "Chris Sale");

        when(playerRepository.findByMlbId(543037)).thenReturn(Optional.of(homePitcher));
        when(playerRepository.findByMlbId(452657)).thenReturn(Optional.of(awayPitcher));

        ScheduleResponse.GameData gameData = createGameDataWithPitchers(
                123456, "2024-07-15T19:05:00Z", "R", "Scheduled",
                543037, "Gerrit Cole", 452657, "Chris Sale"
        );

        Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);

        assertThat(game.getHomeProbablePitcher()).isEqualTo(homePitcher);
        assertThat(game.getAwayProbablePitcher()).isEqualTo(awayPitcher);
    }

    @Test
    void toEntity_shouldSetPitcherToNull_whenPitcherNotInDatabase() {
        when(playerRepository.findByMlbId(543037)).thenReturn(Optional.empty());
        when(playerRepository.findByMlbId(452657)).thenReturn(Optional.empty());

        ScheduleResponse.GameData gameData = createGameDataWithPitchers(
                123456, "2024-07-15T19:05:00Z", "R", "Scheduled",
                543037, "Gerrit Cole", 452657, "Chris Sale"
        );

        Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);

        assertThat(game.getHomeProbablePitcher()).isNull();
        assertThat(game.getAwayProbablePitcher()).isNull();
    }

    @Test
    void toEntity_shouldSetPitcherToNull_whenProbablePitcherDataIsNull() {
        ScheduleResponse.GameData gameData = createGameData(
                123456, "2024-07-15T19:05:00Z", "R", "Scheduled",
                null, null, "Yankee Stadium", "night", 9, 2024
        );

        Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);

        assertThat(game.getHomeProbablePitcher()).isNull();
        assertThat(game.getAwayProbablePitcher()).isNull();
    }

    @Test
    void toEntity_shouldSetPitcherToNull_whenProbablePitcherIdIsNull() {
        ScheduleResponse.GameData gameData = createGameDataWithPitchers(
                123456, "2024-07-15T19:05:00Z", "R", "Scheduled",
                null, "TBD", null, "TBD"
        );

        Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);

        assertThat(game.getHomeProbablePitcher()).isNull();
        assertThat(game.getAwayProbablePitcher()).isNull();
    }

    @Test
    void updateEntity_shouldUpdateStatusAndScores() {
        Game existing = new Game();
        existing.setMlbId(123456);
        existing.setStatus("Scheduled");
        existing.setHomeScore(null);
        existing.setAwayScore(null);

        ScheduleResponse.GameData gameData = createGameData(
                123456, "2024-07-15T19:05:00Z", "R", "Final",
                5, 3, "Yankee Stadium", "night", 9, 2024
        );

        gameMapper.updateEntity(existing, gameData);

        assertThat(existing.getStatus()).isEqualTo("Final");
        assertThat(existing.getHomeScore()).isEqualTo(5);
        assertThat(existing.getAwayScore()).isEqualTo(3);
    }

    @Test
    void updateEntity_shouldUpdateProbablePitchers() {
        Player homePitcher = createPlayer(543037, "Gerrit Cole");
        when(playerRepository.findByMlbId(543037)).thenReturn(Optional.of(homePitcher));
        when(playerRepository.findByMlbId(452657)).thenReturn(Optional.empty());

        Game existing = new Game();
        existing.setMlbId(123456);

        ScheduleResponse.GameData gameData = createGameDataWithPitchers(
                123456, "2024-07-15T19:05:00Z", "R", "Scheduled",
                543037, "Gerrit Cole", 452657, "Chris Sale"
        );

        gameMapper.updateEntity(existing, gameData);

        assertThat(existing.getHomeProbablePitcher()).isEqualTo(homePitcher);
        assertThat(existing.getAwayProbablePitcher()).isNull();
    }

    @Test
    void updateEntity_shouldClearProbablePitchers_whenApiNoLongerIncludesThem() {
        Player existingPitcher = createPlayer(543037, "Gerrit Cole");

        Game existing = new Game();
        existing.setMlbId(123456);
        existing.setHomeProbablePitcher(existingPitcher);
        existing.setAwayProbablePitcher(existingPitcher);

        ScheduleResponse.GameData gameData = createGameData(
                123456, "2024-07-15T19:05:00Z", "R", "In Progress",
                2, 1, "Yankee Stadium", "night", 9, 2024
        );

        gameMapper.updateEntity(existing, gameData);

        assertThat(existing.getHomeProbablePitcher()).isNull();
        assertThat(existing.getAwayProbablePitcher()).isNull();
    }

    @Test
    void toEntity_shouldHandleNullVenue() {
        ScheduleResponse.GameData gameData = createGameData(
                123456, "2024-07-15T19:05:00Z", "R", "Scheduled",
                null, null, null, "night", 9, 2024
        );

        Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);

        assertThat(game.getVenueName()).isNull();
    }

    @Test
    void toEntity_shouldHandleNullStatus() {
        ScheduleResponse.GameData gameData = new ScheduleResponse.GameData();
        gameData.setGamePk(123456);
        gameData.setGameDate("2024-07-15T19:05:00Z");
        gameData.setSeason(2024);

        Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);

        assertThat(game.getStatus()).isNull();
    }

    @Test
    void toEntity_shouldHandleNullTeamsData() {
        ScheduleResponse.GameData gameData = new ScheduleResponse.GameData();
        gameData.setGamePk(123456);
        gameData.setGameDate("2024-07-15T19:05:00Z");
        gameData.setSeason(2024);
        gameData.setScheduledInnings(9);

        Game game = gameMapper.toEntity(gameData, homeTeam, awayTeam);

        assertThat(game.getHomeScore()).isNull();
        assertThat(game.getAwayScore()).isNull();
        assertThat(game.getHomeProbablePitcher()).isNull();
        assertThat(game.getAwayProbablePitcher()).isNull();
    }

    private ScheduleResponse.GameData createGameData(Integer gamePk, String gameDate,
            String gameType, String status, Integer homeScore, Integer awayScore,
            String venueName, String dayNight, Integer scheduledInnings, Integer season) {

        ScheduleResponse.GameData gameData = new ScheduleResponse.GameData();
        gameData.setGamePk(gamePk);
        gameData.setGameDate(gameDate);
        gameData.setGameType(gameType);
        gameData.setDayNight(dayNight);
        gameData.setScheduledInnings(scheduledInnings);
        gameData.setSeason(season);

        if (status != null) {
            ScheduleResponse.StatusData statusData = new ScheduleResponse.StatusData();
            statusData.setDetailedState(status);
            gameData.setStatus(statusData);
        }

        if (venueName != null) {
            ScheduleResponse.VenueData venue = new ScheduleResponse.VenueData();
            venue.setName(venueName);
            gameData.setVenue(venue);
        }

        ScheduleResponse.TeamsData teams = new ScheduleResponse.TeamsData();
        ScheduleResponse.TeamGameData homeTeamData = new ScheduleResponse.TeamGameData();
        homeTeamData.setScore(homeScore);
        teams.setHome(homeTeamData);

        ScheduleResponse.TeamGameData awayTeamData = new ScheduleResponse.TeamGameData();
        awayTeamData.setScore(awayScore);
        teams.setAway(awayTeamData);

        gameData.setTeams(teams);

        return gameData;
    }

    private ScheduleResponse.GameData createGameDataWithPitchers(Integer gamePk, String gameDate,
            String gameType, String status, Integer homePitcherId, String homePitcherName,
            Integer awayPitcherId, String awayPitcherName) {

        ScheduleResponse.GameData gameData = createGameData(
                gamePk, gameDate, gameType, status, null, null,
                "Yankee Stadium", "night", 9, 2024
        );

        if (homePitcherId != null || homePitcherName != null) {
            ScheduleResponse.ProbablePitcherData homePitcher = new ScheduleResponse.ProbablePitcherData();
            homePitcher.setId(homePitcherId);
            homePitcher.setFullName(homePitcherName);
            gameData.getTeams().getHome().setProbablePitcher(homePitcher);
        }

        if (awayPitcherId != null || awayPitcherName != null) {
            ScheduleResponse.ProbablePitcherData awayPitcher = new ScheduleResponse.ProbablePitcherData();
            awayPitcher.setId(awayPitcherId);
            awayPitcher.setFullName(awayPitcherName);
            gameData.getTeams().getAway().setProbablePitcher(awayPitcher);
        }

        return gameData;
    }

    private Player createPlayer(Integer mlbId, String fullName) {
        Player player = new Player();
        player.setMlbId(mlbId);
        player.setFullName(fullName);
        return player;
    }
}
