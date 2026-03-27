package com.mlbstats.ingestion.mapper;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.dto.ExpectedStatsResponse;
import com.mlbstats.ingestion.client.dto.SabermetricsResponse;
import com.mlbstats.ingestion.client.dto.SeasonAdvancedResponse;
import com.mlbstats.ingestion.client.dto.StatsResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class StatsMapper {

    public PlayerBattingStats toBattingStats(StatsResponse.StatData dto, Player player, Team team, Integer season) {
        PlayerBattingStats stats = new PlayerBattingStats();
        stats.setPlayer(player);
        stats.setTeam(team);
        stats.setSeason(season);
        stats.setGameType("R");

        stats.setGamesPlayed(dto.getGamesPlayed());
        stats.setAtBats(dto.getAtBats());
        stats.setRuns(dto.getRuns());
        stats.setHits(dto.getHits());
        stats.setDoubles(dto.getDoubles());
        stats.setTriples(dto.getTriples());
        stats.setHomeRuns(dto.getHomeRuns());
        stats.setRbi(dto.getRbi());
        stats.setStolenBases(dto.getStolenBases());
        stats.setCaughtStealing(dto.getCaughtStealing());
        stats.setWalks(dto.getBaseOnBalls());
        stats.setStrikeouts(dto.getStrikeOuts());
        stats.setPlateAppearances(dto.getPlateAppearances());
        stats.setTotalBases(dto.getTotalBases());
        stats.setIntentionalWalks(dto.getIntentionalWalks());
        stats.setHitByPitch(dto.getHitByPitch());
        stats.setSacFlies(dto.getSacFlies());
        stats.setGroundIntoDp(dto.getGroundIntoDoublePlay());

        stats.setBattingAvg(parseDecimal(dto.getAvg()));
        stats.setObp(parseDecimal(dto.getObp()));
        stats.setSlg(parseDecimal(dto.getSlg()));
        stats.setOps(parseDecimal(dto.getOps()));

        // Calculate advanced stats
        if (stats.getSlg() != null && stats.getBattingAvg() != null) {
            stats.setIso(stats.getSlg().subtract(stats.getBattingAvg()));
        }

        Integer extraBaseHits = 0;
        if (dto.getDoubles() != null) extraBaseHits += dto.getDoubles();
        if (dto.getTriples() != null) extraBaseHits += dto.getTriples();
        if (dto.getHomeRuns() != null) extraBaseHits += dto.getHomeRuns();
        stats.setExtraBaseHits(extraBaseHits);

        return stats;
    }

    public void updateBattingStats(PlayerBattingStats existing, StatsResponse.StatData dto) {
        existing.setGamesPlayed(dto.getGamesPlayed());
        existing.setAtBats(dto.getAtBats());
        existing.setRuns(dto.getRuns());
        existing.setHits(dto.getHits());
        existing.setDoubles(dto.getDoubles());
        existing.setTriples(dto.getTriples());
        existing.setHomeRuns(dto.getHomeRuns());
        existing.setRbi(dto.getRbi());
        existing.setStolenBases(dto.getStolenBases());
        existing.setCaughtStealing(dto.getCaughtStealing());
        existing.setWalks(dto.getBaseOnBalls());
        existing.setStrikeouts(dto.getStrikeOuts());
        existing.setPlateAppearances(dto.getPlateAppearances());
        existing.setTotalBases(dto.getTotalBases());
        existing.setIntentionalWalks(dto.getIntentionalWalks());
        existing.setHitByPitch(dto.getHitByPitch());
        existing.setSacFlies(dto.getSacFlies());
        existing.setGroundIntoDp(dto.getGroundIntoDoublePlay());

        existing.setBattingAvg(parseDecimal(dto.getAvg()));
        existing.setObp(parseDecimal(dto.getObp()));
        existing.setSlg(parseDecimal(dto.getSlg()));
        existing.setOps(parseDecimal(dto.getOps()));

        if (existing.getSlg() != null && existing.getBattingAvg() != null) {
            existing.setIso(existing.getSlg().subtract(existing.getBattingAvg()));
        }

        Integer extraBaseHits = 0;
        if (dto.getDoubles() != null) extraBaseHits += dto.getDoubles();
        if (dto.getTriples() != null) extraBaseHits += dto.getTriples();
        if (dto.getHomeRuns() != null) extraBaseHits += dto.getHomeRuns();
        existing.setExtraBaseHits(extraBaseHits);
    }

    public PlayerPitchingStats toPitchingStats(StatsResponse.StatData dto, Player player, Team team, Integer season) {
        PlayerPitchingStats stats = new PlayerPitchingStats();
        stats.setPlayer(player);
        stats.setTeam(team);
        stats.setSeason(season);
        stats.setGameType("R");

        stats.setGamesPlayed(dto.getGamesPlayed());
        stats.setGamesStarted(dto.getGamesStarted());
        stats.setWins(dto.getWins());
        stats.setLosses(dto.getLosses());
        stats.setSaves(dto.getSaves());
        stats.setHolds(dto.getHolds());
        stats.setHitsAllowed(dto.getHits());
        stats.setRunsAllowed(dto.getRuns());
        stats.setEarnedRuns(dto.getEarnedRuns());
        stats.setHomeRunsAllowed(dto.getHomeRunsAllowed());
        stats.setWalks(dto.getBaseOnBalls());
        stats.setStrikeouts(dto.getStrikeOuts());
        stats.setPitchesThrown(dto.getNumberOfPitches());
        stats.setStrikes(dto.getStrikes());
        stats.setCompleteGames(dto.getCompleteGames());
        stats.setShutouts(dto.getShutouts());

        stats.setInningsPitched(parseDecimal(dto.getInningsPitched()));
        stats.setEra(parseDecimal(dto.getEra()));
        stats.setWhip(parseDecimal(dto.getWhip()));

        // Calculate rate stats
        if (stats.getInningsPitched() != null && stats.getInningsPitched().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ip = stats.getInningsPitched();
            BigDecimal nine = new BigDecimal("9");

            if (stats.getStrikeouts() != null) {
                stats.setKPer9(new BigDecimal(stats.getStrikeouts()).multiply(nine).divide(ip, 2, BigDecimal.ROUND_HALF_UP));
            }
            if (stats.getWalks() != null) {
                stats.setBbPer9(new BigDecimal(stats.getWalks()).multiply(nine).divide(ip, 2, BigDecimal.ROUND_HALF_UP));
            }
            if (stats.getHitsAllowed() != null) {
                stats.setHPer9(new BigDecimal(stats.getHitsAllowed()).multiply(nine).divide(ip, 2, BigDecimal.ROUND_HALF_UP));
            }
        }

        return stats;
    }

    public void updatePitchingStats(PlayerPitchingStats existing, StatsResponse.StatData dto) {
        existing.setGamesPlayed(dto.getGamesPlayed());
        existing.setGamesStarted(dto.getGamesStarted());
        existing.setWins(dto.getWins());
        existing.setLosses(dto.getLosses());
        existing.setSaves(dto.getSaves());
        existing.setHolds(dto.getHolds());
        existing.setHitsAllowed(dto.getHits());
        existing.setRunsAllowed(dto.getRuns());
        existing.setEarnedRuns(dto.getEarnedRuns());
        existing.setHomeRunsAllowed(dto.getHomeRunsAllowed());
        existing.setWalks(dto.getBaseOnBalls());
        existing.setStrikeouts(dto.getStrikeOuts());
        existing.setPitchesThrown(dto.getNumberOfPitches());
        existing.setStrikes(dto.getStrikes());
        existing.setCompleteGames(dto.getCompleteGames());
        existing.setShutouts(dto.getShutouts());

        existing.setInningsPitched(parseDecimal(dto.getInningsPitched()));
        existing.setEra(parseDecimal(dto.getEra()));
        existing.setWhip(parseDecimal(dto.getWhip()));

        if (existing.getInningsPitched() != null && existing.getInningsPitched().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ip = existing.getInningsPitched();
            BigDecimal nine = new BigDecimal("9");

            if (existing.getStrikeouts() != null) {
                existing.setKPer9(new BigDecimal(existing.getStrikeouts()).multiply(nine).divide(ip, 2, BigDecimal.ROUND_HALF_UP));
            }
            if (existing.getWalks() != null) {
                existing.setBbPer9(new BigDecimal(existing.getWalks()).multiply(nine).divide(ip, 2, BigDecimal.ROUND_HALF_UP));
            }
            if (existing.getHitsAllowed() != null) {
                existing.setHPer9(new BigDecimal(existing.getHitsAllowed()).multiply(nine).divide(ip, 2, BigDecimal.ROUND_HALF_UP));
            }
        }
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isEmpty() || value.equals("-.--") || value.equals("*.**")) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ================================================================================
    // SABERMETRICS MAPPING
    // ================================================================================

    /**
     * Applies sabermetric stats (WAR, wOBA, wRC+) to batting stats.
     */
    public void applySabermetrics(PlayerBattingStats stats, SabermetricsResponse.SabermetricData saber) {
        if (saber == null) return;

        stats.setWar(saber.getWar());
        stats.setWoba(saber.getWoba());
        if (saber.getWRcPlus() != null) {
            stats.setWrcPlus(saber.getWRcPlus().intValue());
        }
    }

    /**
     * Applies sabermetric stats (WAR, FIP, xFIP) to pitching stats.
     */
    public void applySabermetrics(PlayerPitchingStats stats, SabermetricsResponse.SabermetricData saber) {
        if (saber == null) return;

        stats.setWar(saber.getWar());
        stats.setFip(saber.getFip());
        stats.setXfip(saber.getXfip());
    }

    /**
     * Applies expected stats (xBA, xSLG, xwOBA) to batting stats.
     */
    public void applyExpectedStats(PlayerBattingStats stats, ExpectedStatsResponse.ExpectedStatData expected) {
        if (expected == null) return;

        stats.setXba(parseDecimal(expected.getAvg()));
        stats.setXslg(parseDecimal(expected.getSlg()));
        stats.setXwoba(parseDecimal(expected.getWoba()));
    }

    /**
     * Applies season advanced stats (BABIP, K%, BB%) to batting stats.
     */
    public void applySeasonAdvanced(PlayerBattingStats stats, SeasonAdvancedResponse.AdvancedStatData advanced) {
        if (advanced == null) return;

        stats.setBabip(parseDecimal(advanced.getBabip()));

        // Convert decimal rates to percentages (e.g., 0.222 -> 22.2)
        BigDecimal kPct = parseDecimalToPercent(advanced.getStrikeoutsPerPlateAppearance());
        BigDecimal bbPct = parseDecimalToPercent(advanced.getWalksPerPlateAppearance());

        if (kPct != null) stats.setKPct(kPct);
        if (bbPct != null) stats.setBbPct(bbPct);
    }

    /**
     * Applies season advanced stats (QS, whiff%, GB%, FB%) to pitching stats.
     */
    public void applySeasonAdvanced(PlayerPitchingStats stats, SeasonAdvancedResponse.AdvancedStatData advanced) {
        if (advanced == null) return;

        stats.setQualityStarts(advanced.getQualityStarts());

        // Convert decimal rates to percentages
        BigDecimal whiffPct = parseDecimalToPercent(advanced.getWhiffPercentage());
        BigDecimal fbPct = parseDecimalToPercent(advanced.getFlyBallPercentage());

        if (whiffPct != null) stats.setWhiffPct(whiffPct);
        if (fbPct != null) stats.setFbPct(fbPct);

        // Calculate GB% from batted ball data
        BigDecimal gbPct = calculateGroundBallPct(advanced);
        if (gbPct != null) stats.setGbPct(gbPct);
    }

    /**
     * Parses a decimal string and converts to percentage.
     * E.g., "0.222" -> 22.2
     */
    private BigDecimal parseDecimalToPercent(String value) {
        BigDecimal decimal = parseDecimal(value);
        if (decimal == null) return null;
        return decimal.multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * Calculates ground ball percentage from batted ball data.
     * GB% = groundOuts / (groundOuts + flyOuts + lineOuts + popOuts) * 100
     */
    private BigDecimal calculateGroundBallPct(SeasonAdvancedResponse.AdvancedStatData advanced) {
        Integer groundOuts = advanced.getGroundOuts();
        Integer flyOuts = advanced.getFlyOuts();
        Integer lineOuts = advanced.getLineOuts();
        Integer popOuts = advanced.getPopOuts();

        if (groundOuts == null || flyOuts == null || lineOuts == null || popOuts == null) {
            return null;
        }

        int total = groundOuts + flyOuts + lineOuts + popOuts;
        if (total == 0) return null;

        return new BigDecimal(groundOuts)
                .divide(new BigDecimal(total), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(1, RoundingMode.HALF_UP);
    }
}
