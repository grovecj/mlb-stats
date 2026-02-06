package com.mlbstats.ingestion.mapper;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.dto.ExpectedStatsResponse;
import com.mlbstats.ingestion.client.dto.SabermetricsResponse;
import com.mlbstats.ingestion.client.dto.SeasonAdvancedResponse;
import com.mlbstats.ingestion.client.dto.StatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class StatsMapperTest {

    private StatsMapper statsMapper;
    private Player player;
    private Team team;

    @BeforeEach
    void setUp() {
        statsMapper = new StatsMapper();

        player = new Player();
        player.setId(1L);
        player.setMlbId(660271);
        player.setFullName("Shohei Ohtani");

        team = new Team();
        team.setId(1L);
        team.setMlbId(119);
        team.setName("Los Angeles Dodgers");
    }

    @Test
    void applySabermetrics_shouldSetBattingWARAndWoba() {
        PlayerBattingStats stats = new PlayerBattingStats();
        stats.setPlayer(player);
        stats.setTeam(team);
        stats.setSeason(2024);

        SabermetricsResponse.SabermetricData saber = new SabermetricsResponse.SabermetricData();
        saber.setWar(new BigDecimal("8.9"));
        saber.setWoba(new BigDecimal("0.431"));
        saber.setWRcPlus(new BigDecimal("180"));

        statsMapper.applySabermetrics(stats, saber);

        assertThat(stats.getWar()).isEqualByComparingTo("8.9");
        assertThat(stats.getWoba()).isEqualByComparingTo("0.431");
        assertThat(stats.getWrcPlus()).isEqualTo(180);
    }

    @Test
    void applySabermetrics_shouldSetPitchingWARAndFIP() {
        PlayerPitchingStats stats = new PlayerPitchingStats();
        stats.setPlayer(player);
        stats.setTeam(team);
        stats.setSeason(2024);

        SabermetricsResponse.SabermetricData saber = new SabermetricsResponse.SabermetricData();
        saber.setWar(new BigDecimal("6.0"));
        saber.setFip(new BigDecimal("2.49"));
        saber.setXfip(new BigDecimal("2.83"));

        statsMapper.applySabermetrics(stats, saber);

        assertThat(stats.getWar()).isEqualByComparingTo("6.0");
        assertThat(stats.getFip()).isEqualByComparingTo("2.49");
        assertThat(stats.getXfip()).isEqualByComparingTo("2.83");
    }

    @Test
    void applyExpectedStats_shouldSetXbaXslgXwoba() {
        PlayerBattingStats stats = new PlayerBattingStats();
        stats.setPlayer(player);
        stats.setTeam(team);
        stats.setSeason(2024);

        ExpectedStatsResponse.ExpectedStatData expected = new ExpectedStatsResponse.ExpectedStatData();
        expected.setAvg(".314");
        expected.setSlg(".660");
        expected.setWoba(".438");

        statsMapper.applyExpectedStats(stats, expected);

        assertThat(stats.getXba()).isEqualByComparingTo("0.314");
        assertThat(stats.getXslg()).isEqualByComparingTo("0.660");
        assertThat(stats.getXwoba()).isEqualByComparingTo("0.438");
    }

    @Test
    void applySeasonAdvanced_shouldSetBattingAdvancedStats() {
        PlayerBattingStats stats = new PlayerBattingStats();
        stats.setPlayer(player);
        stats.setTeam(team);
        stats.setSeason(2024);

        SeasonAdvancedResponse.AdvancedStatData advanced = new SeasonAdvancedResponse.AdvancedStatData();
        advanced.setBabip(".336");
        advanced.setWalksPerPlateAppearance(".111");
        advanced.setStrikeoutsPerPlateAppearance(".222");

        statsMapper.applySeasonAdvanced(stats, advanced);

        assertThat(stats.getBabip()).isEqualByComparingTo("0.336");
        assertThat(stats.getBbPct()).isEqualByComparingTo("11.1");
        assertThat(stats.getKPct()).isEqualByComparingTo("22.2");
    }

    @Test
    void applySeasonAdvanced_shouldSetPitchingAdvancedStats() {
        PlayerPitchingStats stats = new PlayerPitchingStats();
        stats.setPlayer(player);
        stats.setTeam(team);
        stats.setSeason(2024);

        SeasonAdvancedResponse.AdvancedStatData advanced = new SeasonAdvancedResponse.AdvancedStatData();
        advanced.setQualityStarts(22);
        advanced.setWhiffPercentage(".319");
        advanced.setFlyBallPercentage(".247");
        advanced.setGroundOuts(169);
        advanced.setFlyOuts(96);
        advanced.setLineOuts(40);
        advanced.setPopOuts(34);
        advanced.setBallsInPlay(481);

        statsMapper.applySeasonAdvanced(stats, advanced);

        assertThat(stats.getQualityStarts()).isEqualTo(22);
        assertThat(stats.getWhiffPct()).isEqualByComparingTo("31.9");
        assertThat(stats.getFbPct()).isEqualByComparingTo("24.7");
        // GB% = groundOuts / (groundOuts + flyOuts + lineOuts + popOuts) * 100
        // = 169 / 339 * 100 = 49.9
        assertThat(stats.getGbPct()).isEqualByComparingTo("49.9");
    }

    @Test
    void applySabermetrics_shouldHandleNullInput() {
        PlayerBattingStats stats = new PlayerBattingStats();
        stats.setPlayer(player);

        statsMapper.applySabermetrics(stats, null);

        assertThat(stats.getWar()).isNull();
        assertThat(stats.getWoba()).isNull();
    }

    @Test
    void applyExpectedStats_shouldHandleNullInput() {
        PlayerBattingStats stats = new PlayerBattingStats();
        stats.setPlayer(player);

        statsMapper.applyExpectedStats(stats, null);

        assertThat(stats.getXba()).isNull();
    }

    @Test
    void applySeasonAdvanced_shouldHandleNullInput() {
        PlayerBattingStats stats = new PlayerBattingStats();
        stats.setPlayer(player);

        statsMapper.applySeasonAdvanced(stats, null);

        assertThat(stats.getBabip()).isNull();
    }
}
