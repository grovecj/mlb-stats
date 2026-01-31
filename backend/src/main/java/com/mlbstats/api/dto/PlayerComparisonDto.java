package com.mlbstats.api.dto;

import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerPitchingStats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public record PlayerComparisonDto(
        String mode,
        List<PlayerComparisonEntry> players,
        ComparisonLeaders leaders
) {

    public record PlayerComparisonEntry(
            PlayerDto player,
            Integer season,
            ComparisonBattingStats battingStats,
            ComparisonPitchingStats pitchingStats
    ) {}

    public record ComparisonBattingStats(
            Integer gamesPlayed,
            Integer atBats,
            Integer runs,
            Integer hits,
            Integer doubles,
            Integer triples,
            Integer homeRuns,
            Integer rbi,
            Integer stolenBases,
            Integer caughtStealing,
            Integer walks,
            Integer strikeouts,
            BigDecimal battingAvg,
            BigDecimal obp,
            BigDecimal slg,
            BigDecimal ops,
            Integer plateAppearances,
            Integer totalBases,
            Integer extraBaseHits
    ) {
        public static ComparisonBattingStats fromEntity(PlayerBattingStats stats) {
            if (stats == null) {
                return null;
            }
            return new ComparisonBattingStats(
                    stats.getGamesPlayed(),
                    stats.getAtBats(),
                    stats.getRuns(),
                    stats.getHits(),
                    stats.getDoubles(),
                    stats.getTriples(),
                    stats.getHomeRuns(),
                    stats.getRbi(),
                    stats.getStolenBases(),
                    stats.getCaughtStealing(),
                    stats.getWalks(),
                    stats.getStrikeouts(),
                    stats.getBattingAvg(),
                    stats.getObp(),
                    stats.getSlg(),
                    stats.getOps(),
                    stats.getPlateAppearances(),
                    stats.getTotalBases(),
                    stats.getExtraBaseHits()
            );
        }

        public static ComparisonBattingStats aggregateCareer(List<PlayerBattingStats> statsList) {
            if (statsList == null || statsList.isEmpty()) {
                return null;
            }

            int gamesPlayed = 0, atBats = 0, runs = 0, hits = 0, doubles = 0, triples = 0;
            int homeRuns = 0, rbi = 0, stolenBases = 0, caughtStealing = 0, walks = 0, strikeouts = 0;
            int plateAppearances = 0, totalBases = 0, extraBaseHits = 0;

            for (PlayerBattingStats s : statsList) {
                gamesPlayed += nullSafe(s.getGamesPlayed());
                atBats += nullSafe(s.getAtBats());
                runs += nullSafe(s.getRuns());
                hits += nullSafe(s.getHits());
                doubles += nullSafe(s.getDoubles());
                triples += nullSafe(s.getTriples());
                homeRuns += nullSafe(s.getHomeRuns());
                rbi += nullSafe(s.getRbi());
                stolenBases += nullSafe(s.getStolenBases());
                caughtStealing += nullSafe(s.getCaughtStealing());
                walks += nullSafe(s.getWalks());
                strikeouts += nullSafe(s.getStrikeouts());
                plateAppearances += nullSafe(s.getPlateAppearances());
                totalBases += nullSafe(s.getTotalBases());
                extraBaseHits += nullSafe(s.getExtraBaseHits());
            }

            // Recalculate rate stats
            BigDecimal battingAvg = atBats > 0 ? divide(hits, atBats, 3) : null;
            BigDecimal slg = atBats > 0 ? divide(totalBases, atBats, 3) : null;
            BigDecimal obp = plateAppearances > 0
                    ? divide(hits + walks, plateAppearances, 3)
                    : null;
            BigDecimal ops = (obp != null && slg != null) ? obp.add(slg) : null;

            return new ComparisonBattingStats(
                    gamesPlayed, atBats, runs, hits, doubles, triples, homeRuns,
                    rbi, stolenBases, caughtStealing, walks, strikeouts,
                    battingAvg, obp, slg, ops,
                    plateAppearances, totalBases, extraBaseHits
            );
        }

        private static int nullSafe(Integer value) {
            return value != null ? value : 0;
        }

        private static BigDecimal divide(int numerator, int denominator, int scale) {
            return BigDecimal.valueOf(numerator)
                    .divide(BigDecimal.valueOf(denominator), scale, RoundingMode.HALF_UP);
        }
    }

    public record ComparisonPitchingStats(
            Integer gamesPlayed,
            Integer gamesStarted,
            Integer wins,
            Integer losses,
            Integer saves,
            Integer holds,
            BigDecimal inningsPitched,
            Integer hitsAllowed,
            Integer runsAllowed,
            Integer earnedRuns,
            Integer homeRunsAllowed,
            Integer walks,
            Integer strikeouts,
            BigDecimal era,
            BigDecimal whip,
            BigDecimal kPer9,
            BigDecimal bbPer9,
            Integer completeGames,
            Integer shutouts
    ) {
        public static ComparisonPitchingStats fromEntity(PlayerPitchingStats stats) {
            if (stats == null) {
                return null;
            }
            return new ComparisonPitchingStats(
                    stats.getGamesPlayed(),
                    stats.getGamesStarted(),
                    stats.getWins(),
                    stats.getLosses(),
                    stats.getSaves(),
                    stats.getHolds(),
                    stats.getInningsPitched(),
                    stats.getHitsAllowed(),
                    stats.getRunsAllowed(),
                    stats.getEarnedRuns(),
                    stats.getHomeRunsAllowed(),
                    stats.getWalks(),
                    stats.getStrikeouts(),
                    stats.getEra(),
                    stats.getWhip(),
                    stats.getKPer9(),
                    stats.getBbPer9(),
                    stats.getCompleteGames(),
                    stats.getShutouts()
            );
        }

        public static ComparisonPitchingStats aggregateCareer(List<PlayerPitchingStats> statsList) {
            if (statsList == null || statsList.isEmpty()) {
                return null;
            }

            int gamesPlayed = 0, gamesStarted = 0, wins = 0, losses = 0, saves = 0, holds = 0;
            int hitsAllowed = 0, runsAllowed = 0, earnedRuns = 0, homeRunsAllowed = 0;
            int walks = 0, strikeouts = 0, completeGames = 0, shutouts = 0;
            BigDecimal inningsPitched = BigDecimal.ZERO;

            for (PlayerPitchingStats s : statsList) {
                gamesPlayed += nullSafe(s.getGamesPlayed());
                gamesStarted += nullSafe(s.getGamesStarted());
                wins += nullSafe(s.getWins());
                losses += nullSafe(s.getLosses());
                saves += nullSafe(s.getSaves());
                holds += nullSafe(s.getHolds());
                hitsAllowed += nullSafe(s.getHitsAllowed());
                runsAllowed += nullSafe(s.getRunsAllowed());
                earnedRuns += nullSafe(s.getEarnedRuns());
                homeRunsAllowed += nullSafe(s.getHomeRunsAllowed());
                walks += nullSafe(s.getWalks());
                strikeouts += nullSafe(s.getStrikeouts());
                completeGames += nullSafe(s.getCompleteGames());
                shutouts += nullSafe(s.getShutouts());
                if (s.getInningsPitched() != null) {
                    inningsPitched = inningsPitched.add(s.getInningsPitched());
                }
            }

            // Recalculate rate stats
            // ERA = (earnedRuns / inningsPitched) * 9
            BigDecimal era = null;
            BigDecimal whip = null;
            BigDecimal kPer9 = null;
            BigDecimal bbPer9 = null;

            if (inningsPitched.compareTo(BigDecimal.ZERO) > 0) {
                era = BigDecimal.valueOf(earnedRuns)
                        .multiply(BigDecimal.valueOf(9))
                        .divide(inningsPitched, 2, RoundingMode.HALF_UP);
                whip = BigDecimal.valueOf(walks + hitsAllowed)
                        .divide(inningsPitched, 2, RoundingMode.HALF_UP);
                kPer9 = BigDecimal.valueOf(strikeouts)
                        .multiply(BigDecimal.valueOf(9))
                        .divide(inningsPitched, 2, RoundingMode.HALF_UP);
                bbPer9 = BigDecimal.valueOf(walks)
                        .multiply(BigDecimal.valueOf(9))
                        .divide(inningsPitched, 2, RoundingMode.HALF_UP);
            }

            return new ComparisonPitchingStats(
                    gamesPlayed, gamesStarted, wins, losses, saves, holds,
                    inningsPitched, hitsAllowed, runsAllowed, earnedRuns,
                    homeRunsAllowed, walks, strikeouts,
                    era, whip, kPer9, bbPer9,
                    completeGames, shutouts
            );
        }

        private static int nullSafe(Integer value) {
            return value != null ? value : 0;
        }
    }

    public record ComparisonLeaders(
            Map<String, Long> batting,
            Map<String, Long> pitching
    ) {}
}
