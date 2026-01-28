package com.mlbstats.domain.player;

public record PlayerSearchCriteria(
        String search,
        String position,
        String positionType,
        String bats,
        String throwsHand,
        Boolean active
) {
    public boolean hasAnyFilter() {
        return search != null || position != null || positionType != null ||
               bats != null || throwsHand != null || active != null;
    }
}
