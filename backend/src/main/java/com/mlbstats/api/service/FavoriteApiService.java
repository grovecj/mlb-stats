package com.mlbstats.api.service;

import com.mlbstats.api.dto.FavoritesDashboardDto;
import com.mlbstats.api.dto.FavoritesDashboardDto.*;
import com.mlbstats.api.dto.PlayerDto;
import com.mlbstats.api.dto.TeamDto;
import com.mlbstats.common.exception.ResourceNotFoundException;
import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.game.GameRepository;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.stats.*;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import com.mlbstats.domain.team.TeamStanding;
import com.mlbstats.domain.team.TeamStandingRepository;
import com.mlbstats.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteApiService {

    private static final int MAX_DASHBOARD_TEAMS = 5;
    private static final int MAX_DASHBOARD_PLAYERS = 6;

    private final UserFavoriteTeamRepository favoriteTeamRepository;
    private final UserFavoritePlayerRepository favoritePlayerRepository;
    private final AppUserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final TeamStandingRepository standingRepository;
    private final PlayerBattingStatsRepository battingStatsRepository;
    private final PlayerPitchingStatsRepository pitchingStatsRepository;
    private final PlayerGameBattingRepository gameBattingRepository;
    private final PlayerGamePitchingRepository gamePitchingRepository;

    // Team favorites

    @Transactional(readOnly = true)
    public List<TeamDto> getFavoriteTeams(Long userId) {
        return favoriteTeamRepository.findByUserIdWithTeam(userId).stream()
                .map(f -> TeamDto.fromEntity(f.getTeam()))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isTeamFavorite(Long userId, Long teamId) {
        return favoriteTeamRepository.existsByUserIdAndTeamId(userId, teamId);
    }

    @Transactional
    public void addTeamFavorite(Long userId, Long teamId) {
        if (favoriteTeamRepository.existsByUserIdAndTeamId(userId, teamId)) {
            return; // Already a favorite
        }

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", teamId));

        favoriteTeamRepository.save(new UserFavoriteTeam(user, team));
    }

    @Transactional
    public void removeTeamFavorite(Long userId, Long teamId) {
        favoriteTeamRepository.deleteByUserIdAndTeamId(userId, teamId);
    }

    // Player favorites

    @Transactional(readOnly = true)
    public List<PlayerDto> getFavoritePlayers(Long userId) {
        return favoritePlayerRepository.findByUserIdWithPlayer(userId).stream()
                .map(f -> PlayerDto.fromEntity(f.getPlayer()))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isPlayerFavorite(Long userId, Long playerId) {
        return favoritePlayerRepository.existsByUserIdAndPlayerId(userId, playerId);
    }

    @Transactional
    public void addPlayerFavorite(Long userId, Long playerId) {
        if (favoritePlayerRepository.existsByUserIdAndPlayerId(userId, playerId)) {
            return; // Already a favorite
        }

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player", playerId));

        favoritePlayerRepository.save(new UserFavoritePlayer(user, player));
    }

    @Transactional
    public void removePlayerFavorite(Long userId, Long playerId) {
        favoritePlayerRepository.deleteByUserIdAndPlayerId(userId, playerId);
    }

    // Dashboard

    @Transactional(readOnly = true)
    public FavoritesDashboardDto getDashboard(Long userId) {
        LocalDate today = LocalDate.now();
        int currentSeason = Year.now().getValue();

        // Get counts for hasMore flags
        long totalTeamCount = favoriteTeamRepository.countByUserId(userId);
        long totalPlayerCount = favoritePlayerRepository.countByUserId(userId);

        // Get limited team/player IDs for dashboard
        List<Long> teamIds = favoriteTeamRepository.findTeamIdsByUserId(userId, PageRequest.of(0, MAX_DASHBOARD_TEAMS));
        List<Long> playerIds = favoritePlayerRepository.findPlayerIdsByUserId(userId, PageRequest.of(0, MAX_DASHBOARD_PLAYERS));

        // Build team dashboard entries
        List<FavoriteTeamDashboardDto> teamDashboards = buildTeamDashboards(teamIds, today, currentSeason);

        // Build player dashboard entries
        List<FavoritePlayerDashboardDto> playerDashboards = buildPlayerDashboards(playerIds, currentSeason);

        return new FavoritesDashboardDto(
                teamDashboards,
                playerDashboards,
                totalTeamCount > MAX_DASHBOARD_TEAMS,
                totalPlayerCount > MAX_DASHBOARD_PLAYERS,
                Math.toIntExact(totalTeamCount),
                Math.toIntExact(totalPlayerCount)
        );
    }

    private List<FavoriteTeamDashboardDto> buildTeamDashboards(List<Long> teamIds, LocalDate today, int season) {
        if (teamIds.isEmpty()) {
            return List.of();
        }

        // Batch fetch all data
        Map<Long, Team> teamsById = teamRepository.findAllById(teamIds).stream()
                .collect(Collectors.toMap(Team::getId, Function.identity()));

        List<Game> todaysGames = gameRepository.findByDateAndTeamIds(today, teamIds);
        List<Game> upcomingGames = gameRepository.findUpcomingByTeamIds(today, teamIds);
        List<TeamStanding> standings = standingRepository.findByTeamIdsAndSeason(teamIds, season);

        // Index games by team
        Map<Long, Game> todaysGameByTeam = new HashMap<>();
        for (Game game : todaysGames) {
            todaysGameByTeam.putIfAbsent(game.getHomeTeam().getId(), game);
            todaysGameByTeam.putIfAbsent(game.getAwayTeam().getId(), game);
        }

        Map<Long, Game> nextGameByTeam = new HashMap<>();
        for (Game game : upcomingGames) {
            nextGameByTeam.putIfAbsent(game.getHomeTeam().getId(), game);
            nextGameByTeam.putIfAbsent(game.getAwayTeam().getId(), game);
        }

        Map<Long, TeamStanding> standingByTeam = standings.stream()
                .collect(Collectors.toMap(ts -> ts.getTeam().getId(), Function.identity()));

        // Build dashboard entries in order
        return teamIds.stream()
                .map(teamId -> {
                    Team team = teamsById.get(teamId);
                    if (team == null) return null;

                    Game todaysGame = todaysGameByTeam.get(teamId);
                    Game nextGame = nextGameByTeam.get(teamId);
                    TeamStanding standing = standingByTeam.get(teamId);

                    return new FavoriteTeamDashboardDto(
                            TeamDto.fromEntity(team),
                            todaysGame != null ? GameSummaryDto.fromEntity(todaysGame, teamId) : null,
                            nextGame != null ? GameSummaryDto.fromEntity(nextGame, teamId) : null,
                            TeamStandingSnapshotDto.fromEntity(standing)
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<FavoritePlayerDashboardDto> buildPlayerDashboards(List<Long> playerIds, int season) {
        if (playerIds.isEmpty()) {
            return List.of();
        }

        // Batch fetch all data
        Map<Long, Player> playersById = playerRepository.findAllById(playerIds).stream()
                .collect(Collectors.toMap(Player::getId, Function.identity()));

        List<PlayerBattingStats> battingStats = battingStatsRepository.findByPlayerIdsAndSeason(playerIds, season);
        List<PlayerPitchingStats> pitchingStats = pitchingStatsRepository.findByPlayerIdsAndSeason(playerIds, season);
        List<PlayerGameBatting> lastBattingGames = gameBattingRepository.findLatestByPlayerIds(playerIds);
        List<PlayerGamePitching> lastPitchingGames = gamePitchingRepository.findLatestByPlayerIds(playerIds);

        // Index stats by player
        Map<Long, PlayerBattingStats> battingByPlayer = battingStats.stream()
                .collect(Collectors.toMap(s -> s.getPlayer().getId(), Function.identity(), (a, b) -> a));
        Map<Long, PlayerPitchingStats> pitchingByPlayer = pitchingStats.stream()
                .collect(Collectors.toMap(s -> s.getPlayer().getId(), Function.identity(), (a, b) -> a));
        Map<Long, PlayerGameBatting> lastBattingByPlayer = lastBattingGames.stream()
                .collect(Collectors.toMap(g -> g.getPlayer().getId(), Function.identity()));
        Map<Long, PlayerGamePitching> lastPitchingByPlayer = lastPitchingGames.stream()
                .collect(Collectors.toMap(g -> g.getPlayer().getId(), Function.identity()));

        // Build dashboard entries in order
        return playerIds.stream()
                .map(playerId -> {
                    Player player = playersById.get(playerId);
                    if (player == null) return null;

                    PlayerBattingStats batting = battingByPlayer.get(playerId);
                    PlayerPitchingStats pitching = pitchingByPlayer.get(playerId);
                    PlayerGameBatting lastBatting = lastBattingByPlayer.get(playerId);
                    PlayerGamePitching lastPitching = lastPitchingByPlayer.get(playerId);

                    // Determine player type based on positionType
                    String playerType = "Pitcher".equals(player.getPositionType()) ? "PITCHER" : "BATTER";

                    // Get current team from stats or last game
                    Team currentTeam = null;
                    if (batting != null && batting.getTeam() != null) {
                        currentTeam = batting.getTeam();
                    } else if (pitching != null && pitching.getTeam() != null) {
                        currentTeam = pitching.getTeam();
                    } else if (lastBatting != null && lastBatting.getTeam() != null) {
                        currentTeam = lastBatting.getTeam();
                    } else if (lastPitching != null && lastPitching.getTeam() != null) {
                        currentTeam = lastPitching.getTeam();
                    }

                    return new FavoritePlayerDashboardDto(
                            PlayerDto.fromEntity(player),
                            currentTeam != null ? TeamDto.fromEntity(currentTeam) : null,
                            playerType,
                            BatterLastGameDto.fromEntity(lastBatting),
                            PitcherLastGameDto.fromEntity(lastPitching),
                            BatterSeasonSnapshotDto.fromEntity(batting),
                            PitcherSeasonSnapshotDto.fromEntity(pitching)
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
