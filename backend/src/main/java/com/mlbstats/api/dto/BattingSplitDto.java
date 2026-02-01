package com.mlbstats.api.dto;

import com.mlbstats.domain.stats.PlayerBattingSplit;
import com.mlbstats.domain.stats.SplitType;

import java.math.BigDecimal;

public record BattingSplitDto(
        Long id,
        Long playerId,
        Long teamId,
        Integer season,
        SplitType splitType,
        String splitTypeDisplay,
        Integer gamesPlayed,
        Integer plateAppearances,
        Integer atBats,
        Integer runs,
        Integer hits,
        Integer doubles,
        Integer triples,
        Integer homeRuns,
        Integer rbi,
        Integer walks,
        Integer strikeouts,
        Integer stolenBases,
        BigDecimal battingAvg,
        BigDecimal obp,
        BigDecimal slg,
        BigDecimal ops
) {
    public static BattingSplitDto fromEntity(PlayerBattingSplit split) {
        return new BattingSplitDto(
                split.getId(),
                split.getPlayer() != null ? split.getPlayer().getId() : null,
                split.getTeam() != null ? split.getTeam().getId() : null,
                split.getSeason(),
                split.getSplitType(),
                formatSplitType(split.getSplitType()),
                split.getGamesPlayed(),
                split.getPlateAppearances(),
                split.getAtBats(),
                split.getRuns(),
                split.getHits(),
                split.getDoubles(),
                split.getTriples(),
                split.getHomeRuns(),
                split.getRbi(),
                split.getWalks(),
                split.getStrikeouts(),
                split.getStolenBases(),
                split.getBattingAvg(),
                split.getObp(),
                split.getSlg(),
                split.getOps()
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
