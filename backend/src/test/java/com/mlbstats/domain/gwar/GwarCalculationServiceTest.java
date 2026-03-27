package com.mlbstats.domain.gwar;

import com.mlbstats.domain.constants.LeagueConstants;
import com.mlbstats.domain.constants.LeagueConstantsRepository;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GwarCalculationServiceTest {

    @Mock
    private LeagueConstantsRepository constantsRepo;

    private GwarCalculationService gwarService;

    private LeagueConstants leagueConstants;

    @BeforeEach
    void setUp() {
        gwarService = new GwarCalculationService(constantsRepo);

        // Set up 2024 league constants (approximating FanGraphs values)
        leagueConstants = new LeagueConstants();
        leagueConstants.setSeason(2024);
        leagueConstants.setLgWoba(new BigDecimal("0.310"));
        leagueConstants.setWobaScale(new BigDecimal("1.177"));
        leagueConstants.setLgRPerPa(new BigDecimal("0.112"));
        leagueConstants.setFipConstant(new BigDecimal("3.15"));
        leagueConstants.setRunsPerWin(new BigDecimal("10.0"));

        lenient().when(constantsRepo.findBySeason(2024)).thenReturn(Optional.of(leagueConstants));
    }

    @Test
    void calculateAndApply_shouldCalculateGwarForBatter() {
        // Create a star batter (MVP-caliber stats like Ohtani 2024)
        PlayerBattingStats stats = createBattingStats(2024, 636, 50, 10, 5);
        stats.setWoba(new BigDecimal("0.430"));  // Elite wOBA
        stats.setOaa(5);  // Above average fielder

        gwarService.calculateAndApply(stats, "DH");

        // Verify gWAR is calculated
        assertThat(stats.getGwar()).isNotNull();
        assertThat(stats.getGwarBatting()).isNotNull();
        assertThat(stats.getGwarBaserunning()).isNotNull();
        assertThat(stats.getGwarFielding()).isNotNull();
        assertThat(stats.getGwarPositional()).isNotNull();
        assertThat(stats.getGwarReplacement()).isNotNull();

        // For an elite batter, gWAR should be positive and significant
        assertThat(stats.getGwar()).isGreaterThan(BigDecimal.ZERO);

        // Batting component should be positive for above-league wOBA
        assertThat(stats.getGwarBatting()).isGreaterThan(BigDecimal.ZERO);

        // Baserunning should reflect SB/CS: 50 SB * 0.2 + 10 CS * -0.41 = 10 - 4.1 = 5.9
        assertThat(stats.getGwarBaserunning()).isEqualByComparingTo("5.9");

        // Fielding = OAA * 0.9 = 5 * 0.9 = 4.5
        assertThat(stats.getGwarFielding()).isEqualByComparingTo("4.5");

        // DH positional adjustment is negative (-17.5 prorated)
        assertThat(stats.getGwarPositional()).isLessThan(BigDecimal.ZERO);

        // Replacement level is always positive
        assertThat(stats.getGwarReplacement()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void calculateAndApply_shouldHandleZeroPa() {
        PlayerBattingStats stats = createBattingStats(2024, 0, 0, 0, 0);

        gwarService.calculateAndApply(stats, "SS");

        assertThat(stats.getGwar()).isEqualByComparingTo("0.0");
        assertThat(stats.getGwarBatting()).isEqualByComparingTo("0.0");
        assertThat(stats.getGwarBaserunning()).isEqualByComparingTo("0.0");
        assertThat(stats.getGwarReplacement()).isEqualByComparingTo("0.0");
    }

    @Test
    void calculateAndApply_shouldHandleNullWoba() {
        PlayerBattingStats stats = createBattingStats(2024, 400, 20, 5, 120);
        stats.setWoba(null);

        gwarService.calculateAndApply(stats, "CF");

        assertThat(stats.getGwar()).isNotNull();
        // Batting should be 0 if wOBA is null
        assertThat(stats.getGwarBatting()).isEqualByComparingTo("0.0");
    }

    @Test
    void calculateAndApply_shouldHandleNullOaa() {
        PlayerBattingStats stats = createBattingStats(2024, 400, 20, 5, 120);
        stats.setWoba(new BigDecimal("0.350"));
        stats.setOaa(null);

        gwarService.calculateAndApply(stats, "2B");

        assertThat(stats.getGwar()).isNotNull();
        // Fielding should be 0 if OAA is null
        assertThat(stats.getGwarFielding()).isEqualByComparingTo("0.0");
    }

    @Test
    void calculateAndApply_shouldApplyCorrectPositionalAdjustment() {
        PlayerBattingStats stats = createBattingStats(2024, 600, 20, 5, 150);
        stats.setWoba(new BigDecimal("0.320"));

        // Catcher gets +12.5 per 162 games
        gwarService.calculateAndApply(stats, "C");
        BigDecimal catcherPositional = stats.getGwarPositional();

        // Prorated: 150/162 * 12.5 ≈ 11.6
        assertThat(catcherPositional).isGreaterThan(new BigDecimal("11.0"));

        // DH gets -17.5 per 162 games
        gwarService.calculateAndApply(stats, "DH");
        BigDecimal dhPositional = stats.getGwarPositional();

        // Prorated: 150/162 * -17.5 ≈ -16.2
        assertThat(dhPositional).isLessThan(new BigDecimal("-16.0"));
    }

    @Test
    void calculateAndApply_shouldCalculateGwarForPitcher() {
        PlayerPitchingStats stats = createPitchingStats(2024, new BigDecimal("180.0"));
        stats.setFip(new BigDecimal("2.50"));  // Elite FIP

        gwarService.calculateAndApply(stats);

        assertThat(stats.getGwar()).isNotNull();
        assertThat(stats.getGwarPitching()).isNotNull();
        assertThat(stats.getGwarReplacement()).isNotNull();

        // Elite pitcher should have positive gWAR
        assertThat(stats.getGwar()).isGreaterThan(BigDecimal.ZERO);

        // Below-league FIP should give positive pitching runs
        assertThat(stats.getGwarPitching()).isGreaterThan(BigDecimal.ZERO);

        // Replacement level is always positive
        assertThat(stats.getGwarReplacement()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void calculateAndApply_shouldHandleZeroInnings() {
        PlayerPitchingStats stats = createPitchingStats(2024, BigDecimal.ZERO);
        stats.setFip(new BigDecimal("4.00"));

        gwarService.calculateAndApply(stats);

        assertThat(stats.getGwar()).isEqualByComparingTo("0.0");
        assertThat(stats.getGwarPitching()).isEqualByComparingTo("0.0");
        assertThat(stats.getGwarReplacement()).isEqualByComparingTo("0.0");
    }

    @Test
    void calculateAndApply_shouldHandleNullFip() {
        PlayerPitchingStats stats = createPitchingStats(2024, new BigDecimal("150.0"));
        stats.setFip(null);

        gwarService.calculateAndApply(stats);

        assertThat(stats.getGwar()).isNotNull();
        // Pitching should be 0 if FIP is null
        assertThat(stats.getGwarPitching()).isEqualByComparingTo("0.0");
    }

    @Test
    void calculateAndApply_shouldSkipIfNoLeagueConstants() {
        // Need to set up specific mock for this test since setUp uses 2024
        when(constantsRepo.findBySeason(2020)).thenReturn(Optional.empty());

        PlayerBattingStats stats = createBattingStats(2020, 500, 20, 5, 140);
        stats.setWoba(new BigDecimal("0.350"));

        gwarService.calculateAndApply(stats, "SS");

        // Should not throw, but gWAR should remain null
        assertThat(stats.getGwar()).isNull();
    }

    @Test
    void calculateAndApply_shouldUseLeagueConstants() {
        // Verify that the setup works correctly
        PlayerBattingStats stats = createBattingStats(2024, 500, 20, 5, 140);
        stats.setWoba(new BigDecimal("0.350"));

        gwarService.calculateAndApply(stats, "SS");

        // Should calculate gWAR with 2024 constants
        assertThat(stats.getGwar()).isNotNull();
    }

    @Test
    void calculateAndApply_shouldNormalizePositions() {
        PlayerBattingStats stats = createBattingStats(2024, 600, 20, 5, 150);
        stats.setWoba(new BigDecimal("0.320"));

        // Test various position formats
        gwarService.calculateAndApply(stats, "Shortstop");
        BigDecimal ssPositional = stats.getGwarPositional();

        gwarService.calculateAndApply(stats, "SS");
        BigDecimal ssAbbrevPositional = stats.getGwarPositional();

        assertThat(ssPositional).isEqualByComparingTo(ssAbbrevPositional);
    }

    // ================================================================================
    // HELPER METHODS
    // ================================================================================

    private PlayerBattingStats createBattingStats(int season, int pa, int sb, int cs, int games) {
        Player player = new Player();
        player.setId(1L);
        player.setMlbId(660271);
        player.setFullName("Shohei Ohtani");

        PlayerBattingStats stats = new PlayerBattingStats();
        stats.setPlayer(player);
        stats.setSeason(season);
        stats.setPlateAppearances(pa);
        stats.setStolenBases(sb);
        stats.setCaughtStealing(cs);
        stats.setGamesPlayed(games);
        return stats;
    }

    private PlayerPitchingStats createPitchingStats(int season, BigDecimal ip) {
        Player player = new Player();
        player.setId(2L);
        player.setMlbId(592789);
        player.setFullName("Blake Snell");

        PlayerPitchingStats stats = new PlayerPitchingStats();
        stats.setPlayer(player);
        stats.setSeason(season);
        stats.setInningsPitched(ip);
        return stats;
    }
}
