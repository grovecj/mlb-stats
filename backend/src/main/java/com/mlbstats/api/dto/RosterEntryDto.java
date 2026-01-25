package com.mlbstats.api.dto;

import com.mlbstats.domain.player.TeamRoster;

public record RosterEntryDto(
        Long id,
        PlayerDto player,
        Integer season,
        String status,
        String jerseyNumber,
        String position
) {
    public static RosterEntryDto fromEntity(TeamRoster roster) {
        return new RosterEntryDto(
                roster.getId(),
                roster.getPlayer() != null ? PlayerDto.fromEntity(roster.getPlayer()) : null,
                roster.getSeason(),
                roster.getStatus(),
                roster.getJerseyNumber(),
                roster.getPosition()
        );
    }
}
