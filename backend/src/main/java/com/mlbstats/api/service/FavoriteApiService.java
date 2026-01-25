package com.mlbstats.api.service;

import com.mlbstats.api.dto.PlayerDto;
import com.mlbstats.api.dto.TeamDto;
import com.mlbstats.common.exception.ResourceNotFoundException;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import com.mlbstats.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteApiService {

    private final UserFavoriteTeamRepository favoriteTeamRepository;
    private final UserFavoritePlayerRepository favoritePlayerRepository;
    private final AppUserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

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
}
