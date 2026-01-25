package com.mlbstats.api.dto;

import com.mlbstats.domain.team.Team;

public record TeamDto(
        Long id,
        Integer mlbId,
        String name,
        String abbreviation,
        String locationName,
        String venueName,
        String league,
        String division
) {
    public static TeamDto fromEntity(Team team) {
        return new TeamDto(
                team.getId(),
                team.getMlbId(),
                team.getName(),
                team.getAbbreviation(),
                team.getLocationName(),
                team.getVenueName(),
                team.getLeague(),
                team.getDivision()
        );
    }
}
