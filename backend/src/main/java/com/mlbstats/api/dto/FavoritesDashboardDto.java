package com.mlbstats.api.dto;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerGameBatting;
import com.mlbstats.domain.stats.PlayerGamePitching;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import com.mlbstats.domain.team.TeamStanding;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record FavoritesDashboardDto(
        List<FavoriteTeamDashboardDto> teams,
        List<FavoritePlayerDashboardDto> players,
        boolean hasMoreTeams,
        boolean hasMorePlayers,
        int totalTeamCount,
        int totalPlayerCount
) {

    public record FavoriteTeamDashboardDto(
            TeamDto team,
            GameSummaryDto todaysGame,
            GameSummaryDto nextGame,
            TeamStandingSnapshotDto standing
    ) {}

    public record FavoritePlayerDashboardDto(
            PlayerDto player,
            TeamDto currentTeam,
            String playerType,
            BatterLastGameDto lastBattingGame,
            PitcherLastGameDto lastPitchingGame,
            BatterSeasonSnapshotDto seasonBatting,
            PitcherSeasonSnapshotDto seasonPitching
    ) {}

    public record GameSummaryDto(
            Long id,
            LocalDate gameDate,
            LocalTime scheduledTime,
            String status,
            TeamDto opponent,
            boolean isHome,
            Integer teamScore,
            Integer opponentScore,
            String venueName
    ) {
        public static GameSummaryDto fromEntity(Game game, Long favoriteTeamId) {
            boolean isHome = game.getHomeTeam().getId().equals(favoriteTeamId);
            return new GameSummaryDto(
                    game.getId(),
                    game.getGameDate(),
                    game.getScheduledTime(),
                    game.getStatus(),
                    isHome ? TeamDto.fromEntity(game.getAwayTeam()) : TeamDto.fromEntity(game.getHomeTeam()),
                    isHome,
                    isHome ? game.getHomeScore() : game.getAwayScore(),
                    isHome ? game.getAwayScore() : game.getHomeScore(),
                    game.getVenueName()
            );
        }
    }

    public record TeamStandingSnapshotDto(
            Integer wins,
            Integer losses,
            Integer divisionRank,
            String gamesBack,
            String streakCode,
            BigDecimal winningPercentage
    ) {
        public static TeamStandingSnapshotDto fromEntity(TeamStanding standing) {
            if (standing == null) return null;
            return new TeamStandingSnapshotDto(
                    standing.getWins(),
                    standing.getLosses(),
                    standing.getDivisionRank(),
                    standing.getGamesBack(),
                    standing.getStreakCode(),
                    standing.getWinningPercentage()
            );
        }
    }

    public record BatterLastGameDto(
            Long gameId,
            LocalDate gameDate,
            String opponent,
            Integer atBats,
            Integer hits,
            Integer runs,
            Integer rbi,
            Integer homeRuns,
            Integer walks,
            Integer strikeouts
    ) {
        public static BatterLastGameDto fromEntity(PlayerGameBatting batting) {
            if (batting == null || batting.getGame() == null) return null;
            Game game = batting.getGame();
            // Determine opponent based on which team the player was on
            String opponent;
            if (batting.getTeam() != null && game.getHomeTeam() != null && game.getAwayTeam() != null) {
                boolean isHome = batting.getTeam().getId().equals(game.getHomeTeam().getId());
                opponent = isHome ? game.getAwayTeam().getAbbreviation() : game.getHomeTeam().getAbbreviation();
            } else {
                opponent = null;
            }
            return new BatterLastGameDto(
                    game.getId(),
                    game.getGameDate(),
                    opponent,
                    batting.getAtBats(),
                    batting.getHits(),
                    batting.getRuns(),
                    batting.getRbi(),
                    batting.getHomeRuns(),
                    batting.getWalks(),
                    batting.getStrikeouts()
            );
        }
    }

    public record PitcherLastGameDto(
            Long gameId,
            LocalDate gameDate,
            String opponent,
            BigDecimal inningsPitched,
            Integer hitsAllowed,
            Integer earnedRuns,
            Integer strikeouts,
            Integer walks,
            Boolean isWinner,
            Boolean isLoser,
            Boolean isSave
    ) {
        public static PitcherLastGameDto fromEntity(PlayerGamePitching pitching) {
            if (pitching == null || pitching.getGame() == null) return null;
            Game game = pitching.getGame();
            String opponent;
            if (pitching.getTeam() != null && game.getHomeTeam() != null && game.getAwayTeam() != null) {
                boolean isHome = pitching.getTeam().getId().equals(game.getHomeTeam().getId());
                opponent = isHome ? game.getAwayTeam().getAbbreviation() : game.getHomeTeam().getAbbreviation();
            } else {
                opponent = null;
            }
            return new PitcherLastGameDto(
                    game.getId(),
                    game.getGameDate(),
                    opponent,
                    pitching.getInningsPitched(),
                    pitching.getHitsAllowed(),
                    pitching.getEarnedRuns(),
                    pitching.getStrikeouts(),
                    pitching.getWalks(),
                    pitching.getIsWinner(),
                    pitching.getIsLoser(),
                    pitching.getIsSave()
            );
        }
    }

    public record BatterSeasonSnapshotDto(
            Integer season,
            Integer gamesPlayed,
            Integer atBats,
            Integer hits,
            Integer homeRuns,
            Integer rbi,
            Integer runs,
            Integer stolenBases,
            BigDecimal battingAvg,
            BigDecimal obp,
            BigDecimal slg,
            BigDecimal ops
    ) {
        public static BatterSeasonSnapshotDto fromEntity(PlayerBattingStats stats) {
            if (stats == null) return null;
            return new BatterSeasonSnapshotDto(
                    stats.getSeason(),
                    stats.getGamesPlayed(),
                    stats.getAtBats(),
                    stats.getHits(),
                    stats.getHomeRuns(),
                    stats.getRbi(),
                    stats.getRuns(),
                    stats.getStolenBases(),
                    stats.getBattingAvg(),
                    stats.getObp(),
                    stats.getSlg(),
                    stats.getOps()
            );
        }
    }

    public record PitcherSeasonSnapshotDto(
            Integer season,
            Integer gamesPlayed,
            Integer gamesStarted,
            Integer wins,
            Integer losses,
            Integer saves,
            BigDecimal inningsPitched,
            Integer strikeouts,
            BigDecimal era,
            BigDecimal whip,
            BigDecimal kPer9
    ) {
        public static PitcherSeasonSnapshotDto fromEntity(PlayerPitchingStats stats) {
            if (stats == null) return null;
            return new PitcherSeasonSnapshotDto(
                    stats.getSeason(),
                    stats.getGamesPlayed(),
                    stats.getGamesStarted(),
                    stats.getWins(),
                    stats.getLosses(),
                    stats.getSaves(),
                    stats.getInningsPitched(),
                    stats.getStrikeouts(),
                    stats.getEra(),
                    stats.getWhip(),
                    stats.getKPer9()
            );
        }
    }
}
