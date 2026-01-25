package com.mlbstats.ingestion.service;

import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.TeamResponse;
import com.mlbstats.ingestion.mapper.TeamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamIngestionService {

    private static final Logger log = LoggerFactory.getLogger(TeamIngestionService.class);

    private final MlbApiClient mlbApiClient;
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public TeamIngestionService(MlbApiClient mlbApiClient, TeamRepository teamRepository, TeamMapper teamMapper) {
        this.mlbApiClient = mlbApiClient;
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
    }

    @Transactional
    public int syncAllTeams() {
        log.info("Starting team sync");
        TeamResponse response = mlbApiClient.getAllTeams();

        if (response == null || response.getTeams() == null) {
            log.warn("No teams returned from MLB API");
            return 0;
        }

        int count = 0;
        for (TeamResponse.TeamData teamData : response.getTeams()) {
            syncTeam(teamData);
            count++;
        }

        log.info("Completed team sync. Processed {} teams", count);
        return count;
    }

    @Transactional
    public Team syncTeam(TeamResponse.TeamData teamData) {
        return teamRepository.findByMlbId(teamData.getId())
                .map(existing -> {
                    teamMapper.updateEntity(existing, teamData);
                    return teamRepository.save(existing);
                })
                .orElseGet(() -> {
                    Team team = teamMapper.toEntity(teamData);
                    return teamRepository.save(team);
                });
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAllOrderByLeagueAndDivision();
    }

    public Team getTeamByMlbId(Integer mlbId) {
        return teamRepository.findByMlbId(mlbId).orElse(null);
    }
}
