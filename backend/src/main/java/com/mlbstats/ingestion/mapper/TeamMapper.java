package com.mlbstats.ingestion.mapper;

import com.mlbstats.domain.team.Team;
import com.mlbstats.ingestion.client.dto.TeamResponse;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public Team toEntity(TeamResponse.TeamData dto) {
        Team team = new Team();
        team.setMlbId(dto.getId());
        team.setName(dto.getName());
        team.setAbbreviation(dto.getAbbreviation());
        team.setLocationName(dto.getLocationName());

        if (dto.getVenue() != null) {
            team.setVenueName(dto.getVenue().getName());
        }

        if (dto.getLeague() != null) {
            team.setLeague(dto.getLeague().getName());
        }

        if (dto.getDivision() != null) {
            String divisionName = dto.getDivision().getName();
            // Extract just the division part (East, Central, West)
            if (divisionName != null) {
                if (divisionName.contains("East")) {
                    team.setDivision("East");
                } else if (divisionName.contains("Central")) {
                    team.setDivision("Central");
                } else if (divisionName.contains("West")) {
                    team.setDivision("West");
                } else {
                    team.setDivision(divisionName);
                }
            }
        }

        return team;
    }

    public void updateEntity(Team existing, TeamResponse.TeamData dto) {
        existing.setName(dto.getName());
        existing.setAbbreviation(dto.getAbbreviation());
        existing.setLocationName(dto.getLocationName());

        if (dto.getVenue() != null) {
            existing.setVenueName(dto.getVenue().getName());
        }

        if (dto.getLeague() != null) {
            existing.setLeague(dto.getLeague().getName());
        }

        if (dto.getDivision() != null) {
            String divisionName = dto.getDivision().getName();
            if (divisionName != null) {
                if (divisionName.contains("East")) {
                    existing.setDivision("East");
                } else if (divisionName.contains("Central")) {
                    existing.setDivision("Central");
                } else if (divisionName.contains("West")) {
                    existing.setDivision("West");
                } else {
                    existing.setDivision(divisionName);
                }
            }
        }
    }
}
