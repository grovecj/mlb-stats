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
        String division,
        String logoUrl
) {
    private static final String LOGO_URL_TEMPLATE = "https://www.mlbstatic.com/team-logos/%d.svg";

    public static TeamDto fromEntity(Team team) {
        String logoUrl = team.getMlbId() != null
                ? String.format(LOGO_URL_TEMPLATE, team.getMlbId())
                : null;

        return new TeamDto(
                team.getId(),
                team.getMlbId(),
                team.getName(),
                team.getAbbreviation(),
                team.getLocationName(),
                team.getVenueName(),
                team.getLeague(),
                team.getDivision(),
                logoUrl
        );
    }
}
