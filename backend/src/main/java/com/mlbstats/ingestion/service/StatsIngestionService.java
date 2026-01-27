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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsIngestionService {

    private final MlbApiClient mlbApiClient;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final TeamRosterRepository teamRosterRepository;
    private final PlayerBattingStatsRepository battingStatsRepository;
    private final PlayerPitchingStatsRepository pitchingStatsRepository;
    private final StatsMapper statsMapper;

    /**
     * Syncs stats for all players. Each player is processed in a separate transaction
     * to prevent session corruption from affecting other players.
     */
    public int syncAllPlayerStats(Integer season) {
        log.info("Starting stats sync for season {}", season);
        List<Team> teams = teamRepository.findAll();
        log.info("Found {} teams to process", teams.size());

        int totalPlayers = 0;
        int battingStatsSaved = 0;
        int pitchingStatsSaved = 0;
        int errors = 0;

        for (Team team : teams) {
            List<TeamRoster> roster = teamRosterRepository.findByTeamIdAndSeasonWithPlayer(team.getId(), season);
            log.info("Team {}: processing {} roster entries", team.getName(), roster.size());

            for (TeamRoster entry : roster) {
                try {
                    // Each player sync runs in its own transaction to isolate failures
                    int[] result = syncSinglePlayerStats(entry.getPlayer().getId(), team.getId(), season);
                    battingStatsSaved += result[0];
                    pitchingStatsSaved += result[1];
                    totalPlayers++;
                } catch (Exception e) {
                    errors++;
                    log.error("Failed to sync stats for player {} (mlbId={}): {}",
                            entry.getPlayer().getFullName(), entry.getPlayer().getMlbId(), e.getMessage());
                }
            }
        }

        log.info("Completed stats sync. Processed {} players, saved {} batting stats, {} pitching stats, {} errors",
                totalPlayers, battingStatsSaved, pitchingStatsSaved, errors);
        return totalPlayers;
    }

    /**
     * Syncs a single player's stats in a new transaction.
     * This isolates any Hibernate session issues to just this player.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int[] syncSinglePlayerStats(Long playerId, Long teamId, Integer season) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        return syncPlayerStatsWithCount(player, team, season);
    }

    @Transactional
    public void syncPlayerStats(Player player, Team team, Integer season) {
        syncPlayerStatsWithCount(player, team, season);
    }

    /**
     * Syncs player stats and returns count of [battingStatsSaved, pitchingStatsSaved]
     */
    private int[] syncPlayerStatsWithCount(Player player, Team team, Integer season) {
        log.debug("Syncing stats for player {} (mlbId={}, position={}) season {}",
                player.getFullName(), player.getMlbId(), player.getPosition(), season);

        int battingCount = 0;
        int pitchingCount = 0;

        // Sync batting stats
        try {
            battingCount = syncBattingStatsWithCount(player, team, season);
            if (battingCount > 0) {
                log.debug("Saved {} batting stats for {}", battingCount, player.getFullName());
            }
        } catch (Exception e) {
            log.warn("Failed to sync batting stats for player {} (mlbId={}): {}",
                    player.getFullName(), player.getMlbId(), e.getMessage());
            log.debug("Batting stats error details:", e);
        }

        // Sync pitching stats if player is a pitcher
        if ("Pitcher".equals(player.getPositionType()) || "P".equals(player.getPosition())) {
            try {
                pitchingCount = syncPitchingStatsWithCount(player, team, season);
                if (pitchingCount > 0) {
                    log.debug("Saved {} pitching stats for {}", pitchingCount, player.getFullName());
                }
            } catch (Exception e) {
                log.warn("Failed to sync pitching stats for player {} (mlbId={}): {}",
                        player.getFullName(), player.getMlbId(), e.getMessage());
                log.debug("Pitching stats error details:", e);
            }
        }

        return new int[]{battingCount, pitchingCount};
    }

    @Transactional
    public void syncBattingStats(Player player, Team team, Integer season) {
        syncBattingStatsWithCount(player, team, season);
    }

    private int syncBattingStatsWithCount(Player player, Team team, Integer season) {
        log.debug("Fetching batting stats for player {} (mlbId={}) season {}",
                player.getFullName(), player.getMlbId(), season);

        StatsResponse response = mlbApiClient.getPlayerBattingStats(player.getMlbId(), season);

        if (response == null) {
            log.debug("Batting stats API returned null for player {} (mlbId={})",
                    player.getFullName(), player.getMlbId());
            return 0;
        }

        if (response.getStats() == null || response.getStats().isEmpty()) {
            log.debug("Batting stats API returned empty stats array for player {} (mlbId={})",
                    player.getFullName(), player.getMlbId());
            return 0;
        }

        log.debug("Batting stats API returned {} stat groups for player {}",
                response.getStats().size(), player.getFullName());

        int savedCount = 0;

        for (StatsResponse.StatGroup group : response.getStats()) {
            if (group.getSplits() == null || group.getSplits().isEmpty()) {
                log.debug("Stat group has no splits for player {}", player.getFullName());
                continue;
            }

            log.debug("Processing {} splits for player {}", group.getSplits().size(), player.getFullName());

            for (StatsResponse.StatSplit split : group.getSplits()) {
                if (split.getStat() == null) {
                    log.debug("Split has null stat object for player {}", player.getFullName());
                    continue;
                }

                // Use team from split if available, otherwise use passed team
                Team statsTeam;
                if (split.getTeam() != null && split.getTeam().getId() != null) {
                    statsTeam = teamRepository.findByMlbId(split.getTeam().getId()).orElse(team);
                    log.debug("Using team from split: {} (mlbId={})", statsTeam.getName(), split.getTeam().getId());
                } else {
                    statsTeam = team;
                    log.debug("Using passed team: {}", team.getName());
                }

                Integer statsSeason = season;
                if (split.getSeason() != null) {
                    try {
                        statsSeason = Integer.parseInt(split.getSeason());
                    } catch (NumberFormatException e) {
                        log.warn("Could not parse season '{}' for player {}, using {}",
                                split.getSeason(), player.getFullName(), season);
                    }
                }

                Integer finalStatsSeason = statsSeason;
                final int[] saved = {0};
                battingStatsRepository.findByPlayerIdAndTeamIdAndSeasonAndGameType(
                                player.getId(), statsTeam.getId(), statsSeason, "R")
                        .ifPresentOrElse(
                                existing -> {
                                    log.debug("Updating existing batting stats for player {} team {} season {}",
                                            player.getFullName(), statsTeam.getName(), finalStatsSeason);
                                    statsMapper.updateBattingStats(existing, split.getStat());
                                    battingStatsRepository.save(existing);
                                    saved[0] = 1;
                                },
                                () -> {
                                    log.debug("Creating new batting stats for player {} team {} season {}",
                                            player.getFullName(), statsTeam.getName(), finalStatsSeason);
                                    PlayerBattingStats stats = statsMapper.toBattingStats(
                                            split.getStat(), player, statsTeam, finalStatsSeason);
                                    log.debug("Mapped batting stats: games={}, atBats={}, hits={}, avg={}",
                                            stats.getGamesPlayed(), stats.getAtBats(), stats.getHits(), stats.getBattingAvg());
                                    battingStatsRepository.save(stats);
                                    saved[0] = 1;
                                }
                        );
                savedCount += saved[0];
            }
        }

        return savedCount;
    }

    @Transactional
    public void syncPitchingStats(Player player, Team team, Integer season) {
        syncPitchingStatsWithCount(player, team, season);
    }

    private int syncPitchingStatsWithCount(Player player, Team team, Integer season) {
        log.debug("Fetching pitching stats for player {} (mlbId={}) season {}",
                player.getFullName(), player.getMlbId(), season);

        StatsResponse response = mlbApiClient.getPlayerPitchingStats(player.getMlbId(), season);

        if (response == null) {
            log.debug("Pitching stats API returned null for player {} (mlbId={})",
                    player.getFullName(), player.getMlbId());
            return 0;
        }

        if (response.getStats() == null || response.getStats().isEmpty()) {
            log.debug("Pitching stats API returned empty stats array for player {} (mlbId={})",
                    player.getFullName(), player.getMlbId());
            return 0;
        }

        log.debug("Pitching stats API returned {} stat groups for player {}",
                response.getStats().size(), player.getFullName());

        int savedCount = 0;

        for (StatsResponse.StatGroup group : response.getStats()) {
            if (group.getSplits() == null || group.getSplits().isEmpty()) {
                log.debug("Stat group has no splits for player {}", player.getFullName());
                continue;
            }

            log.debug("Processing {} splits for player {}", group.getSplits().size(), player.getFullName());

            for (StatsResponse.StatSplit split : group.getSplits()) {
                if (split.getStat() == null) {
                    log.debug("Split has null stat object for player {}", player.getFullName());
                    continue;
                }

                Team statsTeam;
                if (split.getTeam() != null && split.getTeam().getId() != null) {
                    statsTeam = teamRepository.findByMlbId(split.getTeam().getId()).orElse(team);
                    log.debug("Using team from split: {} (mlbId={})", statsTeam.getName(), split.getTeam().getId());
                } else {
                    statsTeam = team;
                    log.debug("Using passed team: {}", team.getName());
                }

                Integer statsSeason = season;
                if (split.getSeason() != null) {
                    try {
                        statsSeason = Integer.parseInt(split.getSeason());
                    } catch (NumberFormatException e) {
                        log.warn("Could not parse season '{}' for player {}, using {}",
                                split.getSeason(), player.getFullName(), season);
                    }
                }

                Integer finalStatsSeason = statsSeason;
                final int[] saved = {0};
                pitchingStatsRepository.findByPlayerIdAndTeamIdAndSeasonAndGameType(
                                player.getId(), statsTeam.getId(), statsSeason, "R")
                        .ifPresentOrElse(
                                existing -> {
                                    log.debug("Updating existing pitching stats for player {} team {} season {}",
                                            player.getFullName(), statsTeam.getName(), finalStatsSeason);
                                    statsMapper.updatePitchingStats(existing, split.getStat());
                                    pitchingStatsRepository.save(existing);
                                    saved[0] = 1;
                                },
                                () -> {
                                    log.debug("Creating new pitching stats for player {} team {} season {}",
                                            player.getFullName(), statsTeam.getName(), finalStatsSeason);
                                    PlayerPitchingStats stats = statsMapper.toPitchingStats(
                                            split.getStat(), player, statsTeam, finalStatsSeason);
                                    log.debug("Mapped pitching stats: games={}, wins={}, losses={}, era={}",
                                            stats.getGamesPlayed(), stats.getWins(), stats.getLosses(), stats.getEra());
                                    pitchingStatsRepository.save(stats);
                                    saved[0] = 1;
                                }
                        );
                savedCount += saved[0];
            }
        }

        return savedCount;
    }
}
