package com.mlbstats.domain.user;

public enum Role {
    USER(0),
    ADMIN(1),
    OWNER(2);

    private final int level;

    Role(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isAtLeast(Role other) {
        return this.level >= other.level;
    }
}
