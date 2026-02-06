package com.mlbstats.domain.sync;

public enum SyncJobType {
    FULL_SYNC("Full Sync"),
    TEAMS("Teams"),
    ROSTERS("Rosters"),
    GAMES("Games"),
    STATS("Stats"),
    STANDINGS("Standings"),
    BOX_SCORES("Box Scores"),
    LINESCORES("Linescores"),
    SABERMETRICS("Sabermetrics");

    private final String displayName;

    SyncJobType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
