package com.mlbstats.domain.gwar;

import com.mlbstats.domain.constants.LeagueConstants;
import com.mlbstats.domain.constants.LeagueConstantsRepository;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Calculates gWAR (Grove WAR) - a transparent, simplified WAR metric.
 *
 * <h2>gWAR Formula for Position Players:</h2>
 * <pre>
 * gWAR = (Batting + Baserunning + Fielding + Positional + Replacement) / Runs_Per_Win
 *
 * Where:
 * - Batting (wRAA) = ((wOBA - lgwOBA) / wOBAScale) × PA
 * - Baserunning (wSB) = (SB × 0.2) + (CS × -0.41)
 * - Fielding = OAA × 0.9 (from Baseball Savant)
 * - Positional = Position adjustment × (games / 162)
 * - Replacement = PA × (20.5 / 600)
 * </pre>
 *
 * <h2>gWAR Formula for Pitchers:</h2>
 * <pre>
 * gWAR = (Pitching + Replacement) / Runs_Per_Win
 *
 * Where:
 * - Pitching = ((lgFIP - FIP) / 9) × IP
 * - Replacement = IP × (5.5 / 200)
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GwarCalculationService {

    private final LeagueConstantsRepository constantsRepo;

    // Positional adjustment runs per 162 games (from FanGraphs methodology)
    private static final Map<String, BigDecimal> POSITIONAL_ADJUSTMENTS = Map.ofEntries(
            Map.entry("C", bd("12.5")),
            Map.entry("SS", bd("7.5")),
            Map.entry("2B", bd("3.0")),
            Map.entry("CF", bd("2.5")),
            Map.entry("3B", bd("2.5")),
            Map.entry("LF", bd("-7.5")),
            Map.entry("RF", bd("-7.5")),
            Map.entry("1B", bd("-12.5")),
            Map.entry("DH", bd("-17.5"))
    );

    // Stolen base run values
    private static final BigDecimal SB_RUN_VALUE = bd("0.2");
    private static final BigDecimal CS_RUN_VALUE = bd("-0.41");

    // OAA to runs conversion factor
    private static final BigDecimal OAA_TO_RUNS = bd("0.9");

    // Replacement level constants
    private static final BigDecimal BATTER_REPLACEMENT_RUNS_PER_PA = bd("20.5").divide(bd("600"), 6, RoundingMode.HALF_UP);
    private static final BigDecimal PITCHER_REPLACEMENT_RUNS_PER_IP = bd("5.5").divide(bd("200"), 6, RoundingMode.HALF_UP);

    // League average FIP (approximate, used when calculating pitcher runs)
    private static final BigDecimal LEAGUE_AVG_FIP = bd("4.00");

    /**
     * Calculates and applies gWAR to a batter's stats.
     *
     * @param stats The batting stats entity to update
     * @param position The player's primary position (C, 1B, 2B, SS, 3B, LF, CF, RF, DH)
     */
    public void calculateAndApply(PlayerBattingStats stats, String position) {
        LeagueConstants lc = getConstants(stats.getSeason());
        if (lc == null) {
            log.warn("No league constants for season {}, skipping gWAR calculation", stats.getSeason());
            return;
        }

        // Batting: wRAA = ((wOBA - lgwOBA) / wOBAScale) × PA
        BigDecimal batting = calculateWraa(stats.getWoba(), stats.getPlateAppearances(), lc);

        // Baserunning: wSB = (SB × 0.2) + (CS × -0.41)
        BigDecimal baserunning = calculateWsb(stats.getStolenBases(), stats.getCaughtStealing());

        // Fielding: OAA × 0.9
        BigDecimal fielding = calculateFielding(stats.getOaa());

        // Positional adjustment (prorated by games played)
        BigDecimal positional = calculatePositional(position, stats.getGamesPlayed());

        // Replacement level runs
        BigDecimal replacement = calculateBatterReplacement(stats.getPlateAppearances());

        // Total runs above replacement
        BigDecimal totalRuns = batting.add(baserunning).add(fielding).add(positional).add(replacement);

        // Convert to wins
        BigDecimal gwar = totalRuns.divide(lc.getRunsPerWin(), 1, RoundingMode.HALF_UP);

        // Apply to entity
        stats.setGwar(gwar);
        stats.setGwarBatting(batting.setScale(1, RoundingMode.HALF_UP));
        stats.setGwarBaserunning(baserunning.setScale(1, RoundingMode.HALF_UP));
        stats.setGwarFielding(fielding.setScale(1, RoundingMode.HALF_UP));
        stats.setGwarPositional(positional.setScale(1, RoundingMode.HALF_UP));
        stats.setGwarReplacement(replacement.setScale(1, RoundingMode.HALF_UP));

        log.debug("Calculated gWAR for player {}: {} (bat={}, br={}, fld={}, pos={}, rep={})",
                stats.getPlayer() != null ? stats.getPlayer().getFullName() : "unknown",
                gwar, batting, baserunning, fielding, positional, replacement);
    }

    /**
     * Calculates and applies gWAR to a pitcher's stats.
     *
     * @param stats The pitching stats entity to update
     */
    public void calculateAndApply(PlayerPitchingStats stats) {
        LeagueConstants lc = getConstants(stats.getSeason());
        if (lc == null) {
            log.warn("No league constants for season {}, skipping gWAR calculation", stats.getSeason());
            return;
        }

        // Pitching runs = ((lgFIP - FIP) / 9) × IP
        BigDecimal pitching = calculatePitchingRuns(stats.getFip(), stats.getInningsPitched(), lc);

        // Replacement level runs
        BigDecimal replacement = calculatePitcherReplacement(stats.getInningsPitched());

        // Total runs above replacement
        BigDecimal totalRuns = pitching.add(replacement);

        // Convert to wins
        BigDecimal gwar = totalRuns.divide(lc.getRunsPerWin(), 1, RoundingMode.HALF_UP);

        // Apply to entity
        stats.setGwar(gwar);
        stats.setGwarPitching(pitching.setScale(1, RoundingMode.HALF_UP));
        stats.setGwarReplacement(replacement.setScale(1, RoundingMode.HALF_UP));

        log.debug("Calculated gWAR for pitcher {}: {} (pitch={}, rep={})",
                stats.getPlayer() != null ? stats.getPlayer().getFullName() : "unknown",
                gwar, pitching, replacement);
    }

    /**
     * Calculates wRAA (weighted Runs Above Average) from batting.
     * Formula: ((wOBA - lgwOBA) / wOBAScale) × PA
     */
    private BigDecimal calculateWraa(BigDecimal woba, Integer plateAppearances, LeagueConstants lc) {
        if (woba == null || plateAppearances == null || plateAppearances == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal wobaDiff = woba.subtract(lc.getLgWoba());
        BigDecimal runsPerPa = wobaDiff.divide(lc.getWobaScale(), 6, RoundingMode.HALF_UP);
        return runsPerPa.multiply(new BigDecimal(plateAppearances));
    }

    /**
     * Calculates wSB (weighted Stolen Base runs).
     * Formula: (SB × 0.2) + (CS × -0.41)
     */
    private BigDecimal calculateWsb(Integer stolenBases, Integer caughtStealing) {
        BigDecimal sb = stolenBases != null ? new BigDecimal(stolenBases) : BigDecimal.ZERO;
        BigDecimal cs = caughtStealing != null ? new BigDecimal(caughtStealing) : BigDecimal.ZERO;

        return sb.multiply(SB_RUN_VALUE).add(cs.multiply(CS_RUN_VALUE));
    }

    /**
     * Calculates fielding runs from OAA.
     * Formula: OAA × 0.9
     */
    private BigDecimal calculateFielding(Integer oaa) {
        if (oaa == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(oaa).multiply(OAA_TO_RUNS);
    }

    /**
     * Calculates positional adjustment runs.
     * Formula: Position adjustment × (games / 162)
     */
    private BigDecimal calculatePositional(String position, Integer gamesPlayed) {
        if (position == null || gamesPlayed == null || gamesPlayed == 0) {
            return BigDecimal.ZERO;
        }

        // Normalize position to standard abbreviation
        String normalizedPosition = normalizePosition(position);
        BigDecimal adjustment = POSITIONAL_ADJUSTMENTS.getOrDefault(normalizedPosition, BigDecimal.ZERO);

        // Prorate by games played
        BigDecimal gamesFraction = new BigDecimal(gamesPlayed).divide(bd("162"), 4, RoundingMode.HALF_UP);
        return adjustment.multiply(gamesFraction);
    }

    /**
     * Calculates replacement level runs for batters.
     * Formula: PA × (20.5 / 600)
     */
    private BigDecimal calculateBatterReplacement(Integer plateAppearances) {
        if (plateAppearances == null || plateAppearances == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(plateAppearances).multiply(BATTER_REPLACEMENT_RUNS_PER_PA);
    }

    /**
     * Calculates pitching runs above average.
     * Formula: ((lgFIP - FIP) / 9) × IP
     */
    private BigDecimal calculatePitchingRuns(BigDecimal fip, BigDecimal inningsPitched, LeagueConstants lc) {
        if (fip == null || inningsPitched == null || inningsPitched.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Use FIP constant + 3.2 as approximate league average FIP
        BigDecimal lgFip = lc.getFipConstant().add(bd("0.85"));
        BigDecimal fipDiff = lgFip.subtract(fip);
        BigDecimal runsPerInning = fipDiff.divide(bd("9"), 6, RoundingMode.HALF_UP);
        return runsPerInning.multiply(inningsPitched);
    }

    /**
     * Calculates replacement level runs for pitchers.
     * Formula: IP × (5.5 / 200)
     */
    private BigDecimal calculatePitcherReplacement(BigDecimal inningsPitched) {
        if (inningsPitched == null || inningsPitched.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return inningsPitched.multiply(PITCHER_REPLACEMENT_RUNS_PER_IP);
    }

    /**
     * Gets league constants for a season.
     */
    private LeagueConstants getConstants(Integer season) {
        return constantsRepo.findBySeason(season).orElse(null);
    }

    /**
     * Normalizes position strings to standard abbreviations.
     */
    private String normalizePosition(String position) {
        if (position == null) {
            return null;
        }

        String upper = position.toUpperCase().trim();

        return switch (upper) {
            case "CATCHER", "C" -> "C";
            case "FIRST BASEMAN", "FIRST BASE", "1B" -> "1B";
            case "SECOND BASEMAN", "SECOND BASE", "2B" -> "2B";
            case "SHORTSTOP", "SS" -> "SS";
            case "THIRD BASEMAN", "THIRD BASE", "3B" -> "3B";
            case "LEFT FIELDER", "LEFT FIELD", "LF" -> "LF";
            case "CENTER FIELDER", "CENTER FIELD", "CF" -> "CF";
            case "RIGHT FIELDER", "RIGHT FIELD", "RF" -> "RF";
            case "DESIGNATED HITTER", "DH" -> "DH";
            case "OUTFIELDER", "OF" -> "CF"; // Default OF to CF (neutral)
            case "INFIELDER", "IF" -> "SS"; // Default IF to SS (average IF)
            default -> upper;
        };
    }

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
