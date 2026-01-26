package com.mlbstats.ingestion.mapper;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.stats.PlayerGameBatting;
import com.mlbstats.domain.stats.PlayerGamePitching;
import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.dto.BoxScoreResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BoxScoreMapper {

    public PlayerGameBatting toGameBatting(BoxScoreResponse.PlayerStats playerStats,
                                           Player player, Game game, Team team) {
        PlayerGameBatting batting = new PlayerGameBatting();
        batting.setPlayer(player);
        batting.setGame(game);
        batting.setTeam(team);
        batting.setBattingOrder(playerStats.getBattingOrder());

        if (playerStats.getPosition() != null) {
            batting.setPositionPlayed(playerStats.getPosition().getAbbreviation());
        }

        if (playerStats.getStats() != null && playerStats.getStats().getBatting() != null) {
            BoxScoreResponse.BattingStatLine stats = playerStats.getStats().getBatting();
            batting.setAtBats(stats.getAtBats());
            batting.setRuns(stats.getRuns());
            batting.setHits(stats.getHits());
            batting.setDoubles(stats.getDoubles());
            batting.setTriples(stats.getTriples());
            batting.setHomeRuns(stats.getHomeRuns());
            batting.setRbi(stats.getRbi());
            batting.setWalks(stats.getBaseOnBalls());
            batting.setStrikeouts(stats.getStrikeOuts());
            batting.setStolenBases(stats.getStolenBases());
        }

        return batting;
    }

    public PlayerGamePitching toGamePitching(BoxScoreResponse.PlayerStats playerStats,
                                              Player player, Game game, Team team,
                                              boolean isStarter) {
        PlayerGamePitching pitching = new PlayerGamePitching();
        pitching.setPlayer(player);
        pitching.setGame(game);
        pitching.setTeam(team);
        pitching.setIsStarter(isStarter);

        if (playerStats.getStats() != null && playerStats.getStats().getPitching() != null) {
            BoxScoreResponse.PitchingStatLine stats = playerStats.getStats().getPitching();
            pitching.setInningsPitched(parseInningsPitched(stats.getInningsPitched()));
            pitching.setHitsAllowed(stats.getHits());
            pitching.setRunsAllowed(stats.getRuns());
            pitching.setEarnedRuns(stats.getEarnedRuns());
            pitching.setWalks(stats.getBaseOnBalls());
            pitching.setStrikeouts(stats.getStrikeOuts());
            pitching.setHomeRunsAllowed(stats.getHomeRuns());
            pitching.setPitchesThrown(stats.getNumberOfPitches());
            pitching.setStrikes(stats.getStrikes());

            // Parse decision from note (e.g., "(W, 5-2)", "(L, 3-4)", "(S, 10)")
            if (stats.getNote() != null) {
                String note = stats.getNote();
                pitching.setIsWinner(note.contains("(W,"));
                pitching.setIsLoser(note.contains("(L,"));
                pitching.setIsSave(note.contains("(S,") || note.contains("(SV,"));
            } else {
                pitching.setIsWinner(false);
                pitching.setIsLoser(false);
                pitching.setIsSave(false);
            }
        }

        return pitching;
    }

    private BigDecimal parseInningsPitched(String ip) {
        if (ip == null || ip.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(ip);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
