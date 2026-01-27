package com.mlbstats.api.service;

import com.mlbstats.api.dto.*;
import com.mlbstats.common.config.CacheConfig;
import com.mlbstats.common.exception.ResourceNotFoundException;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.domain.stats.PlayerGameBattingRepository;
import com.mlbstats.domain.stats.PlayerGamePitchingRepository;
import com.mlbstats.domain.stats.PlayerPitchingStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlayerApiService {

    private final PlayerRepository playerRepository;
    private final PlayerBattingStatsRepository battingStatsRepository;
    private final PlayerPitchingStatsRepository pitchingStatsRepository;
    private final PlayerGameBattingRepository gameBattingRepository;
    private final PlayerGamePitchingRepository gamePitchingRepository;

    public PageDto<PlayerDto> getAllPlayers(Pageable pageable) {
        Page<Player> page = playerRepository.findByActiveTrue(pageable);
        return PageDto.fromPage(page, PlayerDto::fromEntity);
    }

    public PageDto<PlayerDto> searchPlayers(String search, Pageable pageable) {
        Page<Player> page = playerRepository.searchPlayers(search, pageable);
        return PageDto.fromPage(page, PlayerDto::fromEntity);
    }

    @Cacheable(value = CacheConfig.SEARCH, key = "'player_' + #search + '_' + #limit")
    public List<PlayerDto> searchPlayers(String search, int limit) {
        return playerRepository.searchPlayersByName(search).stream()
                .limit(limit)
                .map(PlayerDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.PLAYERS, key = "#id")
    public PlayerDto getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", id));
        return PlayerDto.fromEntity(player);
    }

    public List<BattingStatsDto> getPlayerBattingStats(Long playerId, Integer season) {
        if (season == null) {
            return battingStatsRepository.findByPlayerId(playerId).stream()
                    .map(BattingStatsDto::fromEntity)
                    .toList();
        }
        return battingStatsRepository.findByPlayerIdAndSeason(playerId, season).stream()
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    public List<PitchingStatsDto> getPlayerPitchingStats(Long playerId, Integer season) {
        if (season == null) {
            return pitchingStatsRepository.findByPlayerId(playerId).stream()
                    .map(PitchingStatsDto::fromEntity)
                    .toList();
        }
        return pitchingStatsRepository.findByPlayerIdAndSeason(playerId, season).stream()
                .map(PitchingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'hr_' + #season + '_' + #limit")
    public List<BattingStatsDto> getTopHomeRunHitters(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findTopHomeRunHitters(season).stream()
                .limit(limit)
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'avg_' + #season + '_' + #minAtBats + '_' + #limit")
    public List<BattingStatsDto> getTopBattingAverage(Integer season, int minAtBats, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findTopBattingAverage(season, minAtBats).stream()
                .limit(limit)
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'wins_' + #season + '_' + #limit")
    public List<PitchingStatsDto> getTopWinners(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return pitchingStatsRepository.findTopWinners(season).stream()
                .limit(limit)
                .map(PitchingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'so_' + #season + '_' + #limit")
    public List<PitchingStatsDto> getTopStrikeouts(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return pitchingStatsRepository.findTopStrikeouts(season).stream()
                .limit(limit)
                .map(PitchingStatsDto::fromEntity)
                .toList();
    }

    public List<BattingGameLogDto> getPlayerBattingGameLog(Long playerId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return gameBattingRepository.findByPlayerIdAndSeason(playerId, season).stream()
                .map(BattingGameLogDto::fromEntity)
                .toList();
    }

    public List<PitchingGameLogDto> getPlayerPitchingGameLog(Long playerId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return gamePitchingRepository.findByPlayerIdAndSeason(playerId, season).stream()
                .map(PitchingGameLogDto::fromEntity)
                .toList();
    }
}
