package com.mlbstats.api.dto;

import java.math.BigDecimal;

/**
 * DTO for displaying a detailed breakdown of gWAR components.
 * All component values are in runs above average/replacement.
 */
public record GwarBreakdownDto(
        PlayerDto player,
        Integer season,
        BigDecimal gwar,
        BigDecimal officialWar,
        // Component breakdown (in runs)
        BigDecimal batting,
        BigDecimal baserunning,
        BigDecimal fielding,
        BigDecimal positional,
        BigDecimal replacement,
        // Additional context
        String position,
        Integer oaa,
        String methodologyUrl
) {
    public static final String METHODOLOGY_URL = "/docs/GWAR_METHODOLOGY.md";

    public static GwarBreakdownDto forBatter(
            PlayerDto player,
            Integer season,
            BigDecimal gwar,
            BigDecimal officialWar,
            BigDecimal batting,
            BigDecimal baserunning,
            BigDecimal fielding,
            BigDecimal positional,
            BigDecimal replacement,
            String position,
            Integer oaa
    ) {
        return new GwarBreakdownDto(
                player,
                season,
                gwar,
                officialWar,
                batting,
                baserunning,
                fielding,
                positional,
                replacement,
                position,
                oaa,
                METHODOLOGY_URL
        );
    }

    public static GwarBreakdownDto forPitcher(
            PlayerDto player,
            Integer season,
            BigDecimal gwar,
            BigDecimal officialWar,
            BigDecimal pitching,
            BigDecimal replacement
    ) {
        return new GwarBreakdownDto(
                player,
                season,
                gwar,
                officialWar,
                pitching,          // pitching runs stored in batting slot
                null,              // no baserunning
                null,              // no fielding for pitchers
                null,              // no positional for pitchers
                replacement,
                "P",
                null,
                METHODOLOGY_URL
        );
    }
}
