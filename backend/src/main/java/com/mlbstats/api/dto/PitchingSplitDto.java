package com.mlbstats.api.dto;

import com.mlbstats.domain.stats.PlayerPitchingSplit;
import com.mlbstats.domain.stats.SplitType;

import java.math.BigDecimal;

public record PitchingSplitDto(
        Long id,
        Long playerId,
        Long teamId,
        Integer season,
        SplitType splitType,
        String splitTypeDisplay,
        Integer gamesPlayed,
        Integer gamesStarted,
        BigDecimal inningsPitched,
        Integer wins,
        Integer losses,
        Integer saves,
        Integer holds,
        Integer hitsAllowed,
        Integer earnedRuns,
        Integer walks,
        Integer strikeouts,
        BigDecimal era,
        BigDecimal whip,
        BigDecimal kPer9,
        BigDecimal bbPer9
) {
    public static PitchingSplitDto fromEntity(PlayerPitchingSplit split) {
        return new PitchingSplitDto(
                split.getId(),
                split.getPlayer() != null ? split.getPlayer().getId() : null,
                split.getTeam() != null ? split.getTeam().getId() : null,
                split.getSeason(),
                split.getSplitType(),
                formatSplitType(split.getSplitType()),
                split.getGamesPlayed(),
                split.getGamesStarted(),
                split.getInningsPitched(),
                split.getWins(),
                split.getLosses(),
                split.getSaves(),
                split.getHolds(),
                split.getHitsAllowed(),
                split.getEarnedRuns(),
                split.getWalks(),
                split.getStrikeouts(),
                split.getEra(),
                split.getWhip(),
                split.getKPer9(),
                split.getBbPer9()
        );
    }

    private static String formatSplitType(SplitType type) {
        return switch (type) {
            case HOME -> "Home";
            case AWAY -> "Away";
            case VS_LHP -> "vs LHP";
            case VS_RHP -> "vs RHP";
            case VS_LHB -> "vs LHB";
            case VS_RHB -> "vs RHB";
            case FIRST_HALF -> "First Half";
            case SECOND_HALF -> "Second Half";
            case MONTH_MAR -> "March";
            case MONTH_APR -> "April";
            case MONTH_MAY -> "May";
            case MONTH_JUN -> "June";
            case MONTH_JUL -> "July";
            case MONTH_AUG -> "August";
            case MONTH_SEP -> "September";
            case MONTH_OCT -> "October";
            case DAY -> "Day";
            case NIGHT -> "Night";
            case RUNNERS_ON -> "Runners On";
            case RISP -> "RISP";
            case BASES_EMPTY -> "Bases Empty";
        };
    }
}
