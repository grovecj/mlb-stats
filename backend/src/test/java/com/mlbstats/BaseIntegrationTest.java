package com.mlbstats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.game.GameRepository;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.player.TeamRoster;
import com.mlbstats.domain.player.TeamRosterRepository;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import com.mlbstats.domain.stats.PlayerPitchingStatsRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import com.mlbstats.domain.team.TeamStanding;
import com.mlbstats.domain.team.TeamStandingRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Base class for integration tests providing common setup and utilities.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // Create ObjectMapper manually since it's not auto-configured in Spring Boot 4.x test context
    protected ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    protected TeamRepository teamRepository;

    @Autowired
    protected PlayerRepository playerRepository;

    @Autowired
    protected TeamRosterRepository teamRosterRepository;

    @Autowired
    protected GameRepository gameRepository;

    @Autowired
    protected PlayerBattingStatsRepository battingStatsRepository;

    @Autowired
    protected PlayerPitchingStatsRepository pitchingStatsRepository;

    @Autowired
    protected TeamStandingRepository teamStandingRepository;

    @BeforeEach
    void setUp() {
        // Set up MockMvc with Spring Security
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // Clean in order respecting foreign key constraints
        battingStatsRepository.deleteAll();
        pitchingStatsRepository.deleteAll();
        teamStandingRepository.deleteAll();
        teamRosterRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
        teamRepository.deleteAll();
    }

    // ==================== Fixture Loading ====================

    /**
     * Load a JSON fixture file from the test resources.
     */
    protected <T> T loadFixture(String path, Class<T> type) {
        try {
            InputStream inputStream = new ClassPathResource("fixtures/" + path).getInputStream();
            return objectMapper.readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load fixture: " + path, e);
        }
    }

    /**
     * Load a JSON fixture file as a generic type.
     */
    protected <T> T loadFixture(String path, TypeReference<T> typeRef) {
        try {
            InputStream inputStream = new ClassPathResource("fixtures/" + path).getInputStream();
            return objectMapper.readValue(inputStream, typeRef);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load fixture: " + path, e);
        }
    }

    // ==================== Entity Factories ====================

    /**
     * Create and save a test team.
     */
    protected Team createTestTeam(Integer mlbId, String name, String abbreviation) {
        Team team = new Team();
        team.setMlbId(mlbId);
        team.setName(name);
        team.setAbbreviation(abbreviation);
        team.setLocationName(name.split(" ")[0]);
        team.setVenueName(name + " Stadium");
        team.setLeague("American League");
        team.setDivision("East");
        return teamRepository.save(team);
    }

    /**
     * Create and save a test team with full details.
     */
    protected Team createTestTeam(Integer mlbId, String name, String abbreviation,
                                   String league, String division) {
        Team team = new Team();
        team.setMlbId(mlbId);
        team.setName(name);
        team.setAbbreviation(abbreviation);
        team.setLocationName(name.split(" ")[0]);
        team.setVenueName(name + " Stadium");
        team.setLeague(league);
        team.setDivision(division);
        return teamRepository.save(team);
    }

    /**
     * Create and save a test player.
     */
    protected Player createTestPlayer(Integer mlbId, String fullName, String position) {
        Player player = new Player();
        player.setMlbId(mlbId);
        player.setFullName(fullName);
        String[] parts = fullName.split(" ");
        player.setFirstName(parts[0]);
        player.setLastName(parts.length > 1 ? parts[1] : "");
        player.setPosition(position);
        player.setPositionType(position.equals("P") ? "Pitcher" : "Hitter");
        player.setBats("R");
        player.setThrowsHand("R");
        player.setActive(true);
        return playerRepository.save(player);
    }

    /**
     * Create and save a roster entry linking a player to a team.
     */
    protected TeamRoster createTestRosterEntry(Team team, Player player, Integer season) {
        TeamRoster roster = new TeamRoster();
        roster.setTeam(team);
        roster.setPlayer(player);
        roster.setSeason(season);
        roster.setJerseyNumber("99");
        roster.setPosition(player.getPosition());
        roster.setStatus("A");
        return teamRosterRepository.save(roster);
    }

    /**
     * Create and save a test game.
     */
    protected Game createTestGame(Integer mlbId, Team homeTeam, Team awayTeam, LocalDate gameDate) {
        Game game = new Game();
        game.setMlbId(mlbId);
        game.setHomeTeam(homeTeam);
        game.setAwayTeam(awayTeam);
        game.setGameDate(gameDate);
        game.setGameType("R");
        game.setStatus("Final");
        game.setSeason(gameDate.getYear());
        game.setHomeScore(5);
        game.setAwayScore(3);
        game.setVenueName(homeTeam.getVenueName());
        game.setDayNight("N");
        game.setScheduledInnings(9);
        return gameRepository.save(game);
    }

    /**
     * Create and save batting stats for a player.
     */
    protected PlayerBattingStats createTestBattingStats(Player player, Team team, Integer season) {
        PlayerBattingStats stats = new PlayerBattingStats();
        stats.setPlayer(player);
        stats.setTeam(team);
        stats.setSeason(season);
        stats.setGamesPlayed(100);
        stats.setAtBats(400);
        stats.setRuns(60);
        stats.setHits(120);
        stats.setDoubles(25);
        stats.setTriples(3);
        stats.setHomeRuns(20);
        stats.setRbi(70);
        stats.setStolenBases(10);
        stats.setWalks(50);
        stats.setStrikeouts(80);
        stats.setBattingAvg(new BigDecimal("0.300"));
        stats.setObp(new BigDecimal("0.380"));
        stats.setSlg(new BigDecimal("0.500"));
        stats.setOps(new BigDecimal("0.880"));
        return battingStatsRepository.save(stats);
    }

    /**
     * Create and save pitching stats for a player.
     */
    protected PlayerPitchingStats createTestPitchingStats(Player player, Team team, Integer season) {
        PlayerPitchingStats stats = new PlayerPitchingStats();
        stats.setPlayer(player);
        stats.setTeam(team);
        stats.setSeason(season);
        stats.setGamesPlayed(30);
        stats.setGamesStarted(30);
        stats.setWins(15);
        stats.setLosses(8);
        stats.setSaves(0);
        stats.setInningsPitched(new BigDecimal("180.0"));
        stats.setStrikeouts(200);
        stats.setWalks(50);
        stats.setHitsAllowed(160);
        stats.setEarnedRuns(60);
        stats.setEra(new BigDecimal("3.00"));
        stats.setWhip(new BigDecimal("1.17"));
        return pitchingStatsRepository.save(stats);
    }

    /**
     * Create and save a team standing.
     */
    protected TeamStanding createTestStanding(Team team, Integer season, Integer wins, Integer losses) {
        TeamStanding standing = new TeamStanding();
        standing.setTeam(team);
        standing.setSeason(season);
        standing.setWins(wins);
        standing.setLosses(losses);
        double pct = (double) wins / (wins + losses);
        standing.setWinningPercentage(new BigDecimal(String.format("%.3f", pct)));
        standing.setGamesBack("-");
        standing.setDivisionRank(1);
        standing.setLeagueRank(1);
        standing.setRunsScored(700);
        standing.setRunsAllowed(600);
        standing.setRunDifferential(100);
        standing.setStreakCode("W3");
        return teamStandingRepository.save(standing);
    }
}
