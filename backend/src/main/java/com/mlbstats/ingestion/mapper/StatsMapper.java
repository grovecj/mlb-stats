package com.mlbstats.ingestion.mapper;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.dto.StatsResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
}
