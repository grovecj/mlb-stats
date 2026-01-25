package com.mlbstats.api.service;

import com.mlbstats.api.dto.*;
import com.mlbstats.common.exception.ResourceNotFoundException;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.domain.stats.PlayerGameBattingRepository;
import com.mlbstats.domain.stats.PlayerPitchingStatsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PlayerApiService {

    private final PlayerRepository playerRepository;
    private final PlayerBattingStatsRepository battingStatsRepository;
    private final PlayerPitchingStatsRepository pitchingStatsRepository;
    private final PlayerGameBattingRepository gameBattingRepository;

    public PlayerApiService(PlayerRepository playerRepository,
                            PlayerBattingStatsRepository battingStatsRepository,
                            PlayerPitchingStatsRepository pitchingStatsRepository,
                            PlayerGameBattingRepository gameBattingRepository) {
        this.playerRepository = playerRepository;
        this.battingStatsRepository = battingStatsRepository;
        this.pitchingStatsRepository = pitchingStatsRepository;
        this.gameBattingRepository = gameBattingRepository;
    }

    public PageDto<PlayerDto> getAllPlayers(Pageable pageable) {
        Page<Player> page = playerRepository.findByActiveTrue(pageable);
        return PageDto.fromPage(page, PlayerDto::fromEntity);
    }

    public PageDto<PlayerDto> searchPlayers(String search, Pageable pageable) {
        Page<Player> page = playerRepository.searchPlayers(search, pageable);
        return PageDto.fromPage(page, PlayerDto::fromEntity);
    }

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

    public List<BattingStatsDto> getTopHomeRunHitters(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findTopHomeRunHitters(season).stream()
                .limit(limit)
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    public List<BattingStatsDto> getTopBattingAverage(Integer season, int minAtBats, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findTopBattingAverage(season, minAtBats).stream()
                .limit(limit)
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    public List<PitchingStatsDto> getTopWinners(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return pitchingStatsRepository.findTopWinners(season).stream()
                .limit(limit)
                .map(PitchingStatsDto::fromEntity)
                .toList();
    }

    public List<PitchingStatsDto> getTopStrikeouts(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return pitchingStatsRepository.findTopStrikeouts(season).stream()
                .limit(limit)
                .map(PitchingStatsDto::fromEntity)
                .toList();
    }
}
