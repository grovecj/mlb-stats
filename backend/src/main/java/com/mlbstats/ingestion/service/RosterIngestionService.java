package com.mlbstats.ingestion.service;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.TeamRoster;
import com.mlbstats.domain.player.TeamRosterRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.RosterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RosterIngestionService {

    private static final Logger log = LoggerFactory.getLogger(RosterIngestionService.class);

    private final MlbApiClient mlbApiClient;
    private final TeamRepository teamRepository;
    private final TeamRosterRepository teamRosterRepository;
    private final PlayerIngestionService playerIngestionService;

    public RosterIngestionService(MlbApiClient mlbApiClient, TeamRepository teamRepository,
                                  TeamRosterRepository teamRosterRepository, PlayerIngestionService playerIngestionService) {
        this.mlbApiClient = mlbApiClient;
        this.teamRepository = teamRepository;
        this.teamRosterRepository = teamRosterRepository;
        this.playerIngestionService = playerIngestionService;
    }

    @Transactional
    public int syncAllRosters(Integer season) {
        log.info("Starting roster sync for season {}", season);
        List<Team> teams = teamRepository.findAll();
        int totalPlayers = 0;

        for (Team team : teams) {
            int count = syncTeamRoster(team, season);
            totalPlayers += count;
        }

        log.info("Completed roster sync. Processed {} players across {} teams", totalPlayers, teams.size());
        return totalPlayers;
    }

    @Transactional
    public int syncTeamRoster(Team team, Integer season) {
        log.debug("Syncing roster for team {} season {}", team.getName(), season);
        RosterResponse response = mlbApiClient.getTeamRoster(team.getMlbId(), season);

        if (response == null || response.getRoster() == null) {
            log.warn("No roster returned for team {}", team.getName());
            return 0;
        }

        int count = 0;
        for (RosterResponse.RosterEntry entry : response.getRoster()) {
            syncRosterEntry(team, entry, season);
            count++;
        }

        log.debug("Synced {} players for team {}", count, team.getName());
        return count;
    }

    @Transactional
    public void syncRosterEntry(Team team, RosterResponse.RosterEntry entry, Integer season) {
        if (entry.getPerson() == null) {
            return;
        }

        // Get or create player
        Player player = playerIngestionService.getOrCreatePlayer(
                entry.getPerson().getId(),
                entry.getPerson().getFullName()
        );

        // Check if roster entry already exists
        boolean exists = teamRosterRepository.existsByTeamIdAndPlayerIdAndSeason(
                team.getId(), player.getId(), season);

        if (!exists) {
            TeamRoster roster = new TeamRoster();
            roster.setTeam(team);
            roster.setPlayer(player);
            roster.setSeason(season);
            roster.setStatus(entry.getStatus() != null ? entry.getStatus().getDescription() : null);
            roster.setJerseyNumber(entry.getJerseyNumber());
            roster.setStartDate(LocalDate.of(season, 3, 1));

            if (entry.getPosition() != null) {
                roster.setPosition(entry.getPosition().getAbbreviation());
            }

            teamRosterRepository.save(roster);
        }
    }

    public List<TeamRoster> getTeamRoster(Long teamId, Integer season) {
        return teamRosterRepository.findByTeamIdAndSeasonWithPlayer(teamId, season);
    }
}
