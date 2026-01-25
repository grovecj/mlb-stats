package com.mlbstats.ingestion.service;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.player.TeamRoster;
import com.mlbstats.domain.player.TeamRosterRepository;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import com.mlbstats.domain.stats.PlayerPitchingStatsRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.StatsResponse;
import com.mlbstats.ingestion.mapper.StatsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StatsIngestionService {

    private static final Logger log = LoggerFactory.getLogger(StatsIngestionService.class);

    private final MlbApiClient mlbApiClient;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final TeamRosterRepository teamRosterRepository;
    private final PlayerBattingStatsRepository battingStatsRepository;
    private final PlayerPitchingStatsRepository pitchingStatsRepository;
    private final StatsMapper statsMapper;

    public StatsIngestionService(MlbApiClient mlbApiClient, PlayerRepository playerRepository,
                                 TeamRepository teamRepository, TeamRosterRepository teamRosterRepository,
                                 PlayerBattingStatsRepository battingStatsRepository,
                                 PlayerPitchingStatsRepository pitchingStatsRepository,
                                 StatsMapper statsMapper) {
        this.mlbApiClient = mlbApiClient;
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.teamRosterRepository = teamRosterRepository;
        this.battingStatsRepository = battingStatsRepository;
        this.pitchingStatsRepository = pitchingStatsRepository;
        this.statsMapper = statsMapper;
    }

    @Transactional
    public int syncAllPlayerStats(Integer season) {
        log.info("Starting stats sync for season {}", season);
        List<Team> teams = teamRepository.findAll();
        int totalPlayers = 0;

        for (Team team : teams) {
            List<TeamRoster> roster = teamRosterRepository.findByTeamIdAndSeasonWithPlayer(team.getId(), season);
            for (TeamRoster entry : roster) {
                syncPlayerStats(entry.getPlayer(), team, season);
                totalPlayers++;
            }
        }

        log.info("Completed stats sync. Processed {} players", totalPlayers);
        return totalPlayers;
    }

    @Transactional
    public void syncPlayerStats(Player player, Team team, Integer season) {
        log.debug("Syncing stats for player {} season {}", player.getFullName(), season);

        // Sync batting stats
        try {
            syncBattingStats(player, team, season);
        } catch (Exception e) {
            log.debug("No batting stats for player {}: {}", player.getFullName(), e.getMessage());
        }

        // Sync pitching stats if player is a pitcher
        if ("Pitcher".equals(player.getPositionType()) || "P".equals(player.getPosition())) {
            try {
                syncPitchingStats(player, team, season);
            } catch (Exception e) {
                log.debug("No pitching stats for player {}: {}", player.getFullName(), e.getMessage());
            }
        }
    }

    @Transactional
    public void syncBattingStats(Player player, Team team, Integer season) {
        StatsResponse response = mlbApiClient.getPlayerBattingStats(player.getMlbId(), season);

        if (response == null || response.getStats() == null || response.getStats().isEmpty()) {
            return;
        }

        for (StatsResponse.StatGroup group : response.getStats()) {
            if (group.getSplits() == null || group.getSplits().isEmpty()) {
                continue;
            }

            for (StatsResponse.StatSplit split : group.getSplits()) {
                if (split.getStat() == null) continue;

                // Use team from split if available, otherwise use passed team
                Team statsTeam;
                if (split.getTeam() != null && split.getTeam().getId() != null) {
                    statsTeam = teamRepository.findByMlbId(split.getTeam().getId()).orElse(team);
                } else {
                    statsTeam = team;
                }

                Integer statsSeason = season;
                if (split.getSeason() != null) {
                    try {
                        statsSeason = Integer.parseInt(split.getSeason());
                    } catch (NumberFormatException e) {
                        // Use passed season
                    }
                }

                Integer finalStatsSeason = statsSeason;
                battingStatsRepository.findByPlayerIdAndTeamIdAndSeasonAndGameType(
                                player.getId(), statsTeam.getId(), statsSeason, "R")
                        .ifPresentOrElse(
                                existing -> {
                                    statsMapper.updateBattingStats(existing, split.getStat());
                                    battingStatsRepository.save(existing);
                                },
                                () -> {
                                    PlayerBattingStats stats = statsMapper.toBattingStats(
                                            split.getStat(), player, statsTeam, finalStatsSeason);
                                    battingStatsRepository.save(stats);
                                }
                        );
            }
        }
    }

    @Transactional
    public void syncPitchingStats(Player player, Team team, Integer season) {
        StatsResponse response = mlbApiClient.getPlayerPitchingStats(player.getMlbId(), season);

        if (response == null || response.getStats() == null || response.getStats().isEmpty()) {
            return;
        }

        for (StatsResponse.StatGroup group : response.getStats()) {
            if (group.getSplits() == null || group.getSplits().isEmpty()) {
                continue;
            }

            for (StatsResponse.StatSplit split : group.getSplits()) {
                if (split.getStat() == null) continue;

                Team statsTeam;
                if (split.getTeam() != null && split.getTeam().getId() != null) {
                    statsTeam = teamRepository.findByMlbId(split.getTeam().getId()).orElse(team);
                } else {
                    statsTeam = team;
                }

                Integer statsSeason = season;
                if (split.getSeason() != null) {
                    try {
                        statsSeason = Integer.parseInt(split.getSeason());
                    } catch (NumberFormatException e) {
                        // Use passed season
                    }
                }

                Integer finalStatsSeason = statsSeason;
                pitchingStatsRepository.findByPlayerIdAndTeamIdAndSeasonAndGameType(
                                player.getId(), statsTeam.getId(), statsSeason, "R")
                        .ifPresentOrElse(
                                existing -> {
                                    statsMapper.updatePitchingStats(existing, split.getStat());
                                    pitchingStatsRepository.save(existing);
                                },
                                () -> {
                                    PlayerPitchingStats stats = statsMapper.toPitchingStats(
                                            split.getStat(), player, statsTeam, finalStatsSeason);
                                    pitchingStatsRepository.save(stats);
                                }
                        );
            }
        }
    }
}
