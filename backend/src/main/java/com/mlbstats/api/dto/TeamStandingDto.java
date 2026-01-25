package com.mlbstats.api.dto;

import com.mlbstats.domain.team.TeamStanding;

import java.math.BigDecimal;

public record TeamStandingDto(
        Long id,
        TeamDto team,
        Integer season,
        Integer wins,
        Integer losses,
        BigDecimal winningPercentage,
        String gamesBack,
        String wildCardGamesBack,
        Integer divisionRank,
        Integer leagueRank,
        Integer wildCardRank,
        Integer runsScored,
        Integer runsAllowed,
        Integer runDifferential,
        String streakCode,
        Integer homeWins,
        Integer homeLosses,
        Integer awayWins,
        Integer awayLosses
) {
    public static TeamStandingDto fromEntity(TeamStanding standing) {
        return new TeamStandingDto(
                standing.getId(),
                TeamDto.fromEntity(standing.getTeam()),
                standing.getSeason(),
                standing.getWins(),
                standing.getLosses(),
                standing.getWinningPercentage(),
                standing.getGamesBack(),
                standing.getWildCardGamesBack(),
                standing.getDivisionRank(),
                standing.getLeagueRank(),
                standing.getWildCardRank(),
                standing.getRunsScored(),
                standing.getRunsAllowed(),
                standing.getRunDifferential(),
                standing.getStreakCode(),
                standing.getHomeWins(),
                standing.getHomeLosses(),
                standing.getAwayWins(),
                standing.getAwayLosses()
        );
    }
}
