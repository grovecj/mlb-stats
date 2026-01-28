package com.mlbstats.api.dto;

import com.mlbstats.domain.player.Player;

public record ProbablePitcherDto(
        Long id,
        Integer mlbId,
        String fullName,
        String headshotUrl
) {
    public static ProbablePitcherDto fromEntity(Player player) {
        if (player == null) {
            return null;
        }
        return new ProbablePitcherDto(
                player.getId(),
                player.getMlbId(),
                player.getFullName(),
                "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/" + player.getMlbId() + "/headshot/67/current"
        );
    }
}
