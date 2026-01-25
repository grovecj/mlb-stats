package com.mlbstats.api.service;

import com.mlbstats.api.dto.BattingStatsDto;
import com.mlbstats.api.dto.GameDto;
import com.mlbstats.api.dto.RosterEntryDto;
import com.mlbstats.api.dto.TeamDto;
import com.mlbstats.common.exception.ResourceNotFoundException;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.domain.game.GameRepository;
import com.mlbstats.domain.player.TeamRosterRepository;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TeamApiService {

    private final TeamRepository teamRepository;
    private final TeamRosterRepository rosterRepository;
    private final GameRepository gameRepository;
    private final PlayerBattingStatsRepository battingStatsRepository;

    public TeamApiService(TeamRepository teamRepository, TeamRosterRepository rosterRepository,
                          GameRepository gameRepository, PlayerBattingStatsRepository battingStatsRepository) {
        this.teamRepository = teamRepository;
        this.rosterRepository = rosterRepository;
        this.gameRepository = gameRepository;
        this.battingStatsRepository = battingStatsRepository;
    }

    @Cacheable("teams")
    public List<TeamDto> getAllTeams() {
        return teamRepository.findAllOrderByLeagueAndDivision().stream()
                .map(TeamDto::fromEntity)
                .toList();
    }

    public List<TeamDto> getTeamsByLeague(String league) {
        return teamRepository.findByLeague(league).stream()
                .map(TeamDto::fromEntity)
                .toList();
    }

    public List<TeamDto> getTeamsByDivision(String league, String division) {
        return teamRepository.findByLeagueAndDivision(league, division).stream()
                .map(TeamDto::fromEntity)
                .toList();
    }

    public TeamDto getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
        return TeamDto.fromEntity(team);
    }

    public List<RosterEntryDto> getTeamRoster(Long teamId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return rosterRepository.findByTeamIdAndSeasonWithPlayer(teamId, season).stream()
                .map(RosterEntryDto::fromEntity)
                .toList();
    }

    public List<GameDto> getTeamGames(Long teamId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return gameRepository.findByTeamIdAndSeason(teamId, season).stream()
                .map(GameDto::fromEntity)
                .toList();
    }

    public List<BattingStatsDto> getTeamBattingStats(Long teamId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findByTeamIdAndSeasonWithPlayer(teamId, season).stream()
                .map(BattingStatsDto::fromEntity)
                .toList();
    }
}
