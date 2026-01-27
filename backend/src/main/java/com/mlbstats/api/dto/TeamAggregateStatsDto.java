package com.mlbstats.api.dto;

import java.math.BigDecimal;

public record TeamAggregateStatsDto(
        Long teamId,
        Integer season,
        TeamBattingAggregateDto batting,
        TeamPitchingAggregateDto pitching
) {
    public record TeamBattingAggregateDto(
            Integer gamesPlayed,
            Integer atBats,
            Integer runs,
            Integer hits,
            Integer doubles,
            Integer triples,
            Integer homeRuns,
            Integer rbi,
            Integer stolenBases,
            Integer walks,
            Integer strikeouts,
            Integer plateAppearances,
            BigDecimal battingAvg,
            BigDecimal obp,
            BigDecimal slg,
            BigDecimal ops
    ) {}

    public record TeamPitchingAggregateDto(
            Integer gamesPlayed,
            Integer wins,
            Integer losses,
            Integer saves,
            BigDecimal inningsPitched,
            Integer hitsAllowed,
            Integer earnedRuns,
            Integer walks,
            Integer strikeouts,
            Integer homeRunsAllowed,
            Integer qualityStarts,
            BigDecimal era,
            BigDecimal whip,
            BigDecimal kPer9
    ) {}
}
