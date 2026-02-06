package com.mlbstats.ingestion.service;

import com.mlbstats.BaseIntegrationTest;
import com.mlbstats.domain.constants.LeagueConstants;
import com.mlbstats.domain.constants.LeagueConstantsRepository;
import com.mlbstats.domain.gwar.GwarCalculationService;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import com.mlbstats.domain.stats.PlayerPitchingStatsRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.SabermetricsResponse;
import com.mlbstats.ingestion.mapper.StatsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class SabermetricsIngestionServiceTest extends BaseIntegrationTest {

    @MockitoBean
    private MlbApiClient mlbApiClient;

    @Autowired
    private SabermetricsIngestionService sabermetricsIngestionService;

    @Autowired
    private PlayerBattingStatsRepository battingRepo;

    @Autowired
    private PlayerPitchingStatsRepository pitchingRepo;

    @Autowired
    private LeagueConstantsRepository constantsRepo;

    @Autowired
    private StatsMapper statsMapper;

    @Autowired
    private GwarCalculationService gwarService;

    private Player testPlayer;
    private PlayerBattingStats testBattingStats;

    @BeforeEach
    void setUp() {
        // Create league constants for 2024
        LeagueConstants lc = new LeagueConstants();
        lc.setSeason(2024);
        lc.setLgWoba(new BigDecimal("0.310"));
        lc.setWobaScale(new BigDecimal("1.177"));
        lc.setLgRPerPa(new BigDecimal("0.112"));
        lc.setFipConstant(new BigDecimal("3.15"));
        lc.setRunsPerWin(new BigDecimal("10.0"));
        constantsRepo.save(lc);

        // Create test player and team
        testPlayer = createTestPlayer(660271, "Shohei Ohtani", "DH");

        // Create batting stats
        testBattingStats = new PlayerBattingStats();
        testBattingStats.setPlayer(testPlayer);
        testBattingStats.setTeam(createTestTeam(119, "Los Angeles Dodgers", "LAD"));
        testBattingStats.setSeason(2024);
        testBattingStats.setGameType("R");
        testBattingStats.setPlateAppearances(636);
        testBattingStats.setGamesPlayed(159);
        testBattingStats.setStolenBases(50);
        testBattingStats.setCaughtStealing(10);
        testBattingStats = battingRepo.save(testBattingStats);
    }

    @Test
    void syncPlayerBattingSabermetrics_shouldApplyWarAndWoba() {
        // Mock MLB API response
        SabermetricsResponse response = createMockSabermetricsResponse(
                new BigDecimal("8.9"),   // WAR
                new BigDecimal("0.431"), // wOBA
                new BigDecimal("180")    // wRC+
        );
        when(mlbApiClient.getBattingSabermetrics(660271, 2024)).thenReturn(response);
        when(mlbApiClient.getPlayerExpectedStats(anyInt(), anyInt(), eq("hitting"))).thenReturn(null);
        when(mlbApiClient.getPlayerSeasonAdvanced(anyInt(), anyInt(), eq("hitting"))).thenReturn(null);

        boolean updated = sabermetricsIngestionService.syncPlayerBattingSabermetrics(testPlayer, 2024);

        assertThat(updated).isTrue();

        PlayerBattingStats stats = battingRepo.findById(testBattingStats.getId()).orElseThrow();
        assertThat(stats.getWar()).isEqualByComparingTo("8.9");
        assertThat(stats.getWoba()).isEqualByComparingTo("0.431");
        assertThat(stats.getWrcPlus()).isEqualTo(180);

        // gWAR should also be calculated
        assertThat(stats.getGwar()).isNotNull();
        assertThat(stats.getGwarBatting()).isNotNull();
    }

    @Test
    void syncPlayerBattingSabermetrics_shouldHandleApiError() {
        when(mlbApiClient.getBattingSabermetrics(660271, 2024)).thenReturn(null);
        when(mlbApiClient.getPlayerExpectedStats(anyInt(), anyInt(), eq("hitting"))).thenReturn(null);
        when(mlbApiClient.getPlayerSeasonAdvanced(anyInt(), anyInt(), eq("hitting"))).thenReturn(null);

        boolean updated = sabermetricsIngestionService.syncPlayerBattingSabermetrics(testPlayer, 2024);

        // Should return true (stats exist) but WAR should remain null
        assertThat(updated).isTrue();

        PlayerBattingStats stats = battingRepo.findById(testBattingStats.getId()).orElseThrow();
        assertThat(stats.getWar()).isNull();
    }

    @Test
    void syncPlayerPitchingSabermetrics_shouldApplyFipAndWar() {
        // Create test pitcher
        Player pitcher = createTestPlayer(592789, "Blake Snell", "P");

        PlayerPitchingStats pitchingStats = new PlayerPitchingStats();
        pitchingStats.setPlayer(pitcher);
        pitchingStats.setTeam(createTestTeam(137, "San Francisco Giants", "SF"));
        pitchingStats.setSeason(2024);
        pitchingStats.setGameType("R");
        pitchingStats.setInningsPitched(new BigDecimal("180.0"));
        pitchingStats = pitchingRepo.save(pitchingStats);

        // Mock API response
        SabermetricsResponse response = createMockPitchingSabermetricsResponse(
                new BigDecimal("5.5"),   // WAR
                new BigDecimal("2.85"),  // FIP
                new BigDecimal("3.10")   // xFIP
        );
        when(mlbApiClient.getPitchingSabermetrics(592789, 2024)).thenReturn(response);
        when(mlbApiClient.getPlayerSeasonAdvanced(anyInt(), anyInt(), eq("pitching"))).thenReturn(null);

        boolean updated = sabermetricsIngestionService.syncPlayerPitchingSabermetrics(pitcher, 2024);

        assertThat(updated).isTrue();

        PlayerPitchingStats stats = pitchingRepo.findById(pitchingStats.getId()).orElseThrow();
        assertThat(stats.getWar()).isEqualByComparingTo("5.5");
        assertThat(stats.getFip()).isEqualByComparingTo("2.85");
        assertThat(stats.getXfip()).isEqualByComparingTo("3.10");

        // gWAR should be calculated
        assertThat(stats.getGwar()).isNotNull();
        assertThat(stats.getGwarPitching()).isNotNull();
    }

    // ================================================================================
    // HELPER METHODS
    // ================================================================================

    private SabermetricsResponse createMockSabermetricsResponse(BigDecimal war, BigDecimal woba, BigDecimal wrcPlus) {
        SabermetricsResponse response = new SabermetricsResponse();

        SabermetricsResponse.SabermetricData data = new SabermetricsResponse.SabermetricData();
        data.setWar(war);
        data.setWoba(woba);
        data.setWRcPlus(wrcPlus);

        SabermetricsResponse.StatSplit split = new SabermetricsResponse.StatSplit();
        split.setSeason("2024");
        split.setStat(data);

        SabermetricsResponse.StatGroup group = new SabermetricsResponse.StatGroup();
        group.setSplits(List.of(split));

        response.setStats(List.of(group));
        return response;
    }

    private SabermetricsResponse createMockPitchingSabermetricsResponse(BigDecimal war, BigDecimal fip, BigDecimal xfip) {
        SabermetricsResponse response = new SabermetricsResponse();

        SabermetricsResponse.SabermetricData data = new SabermetricsResponse.SabermetricData();
        data.setWar(war);
        data.setFip(fip);
        data.setXfip(xfip);

        SabermetricsResponse.StatSplit split = new SabermetricsResponse.StatSplit();
        split.setSeason("2024");
        split.setStat(data);

        SabermetricsResponse.StatGroup group = new SabermetricsResponse.StatGroup();
        group.setSplits(List.of(split));

        response.setStats(List.of(group));
        return response;
    }
}
