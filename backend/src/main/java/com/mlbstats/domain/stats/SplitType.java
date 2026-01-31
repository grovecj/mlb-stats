package com.mlbstats.domain.stats;

/**
 * Types of statistical splits for player performance analysis.
 */
public enum SplitType {
    // Location
    HOME,
    AWAY,

    // Opponent handedness (for batters)
    VS_LHP,
    VS_RHP,

    // Opponent handedness (for pitchers)
    VS_LHB,
    VS_RHB,

    // Season halves
    FIRST_HALF,
    SECOND_HALF,

    // Monthly
    MONTH_MAR,
    MONTH_APR,
    MONTH_MAY,
    MONTH_JUN,
    MONTH_JUL,
    MONTH_AUG,
    MONTH_SEP,
    MONTH_OCT,

    // Day/Night
    DAY,
    NIGHT,

    // Game situation
    RUNNERS_ON,
    RISP,
    BASES_EMPTY
}
