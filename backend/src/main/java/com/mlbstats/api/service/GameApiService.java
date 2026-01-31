package com.mlbstats.api.service;

import com.mlbstats.api.dto.*;
import com.mlbstats.common.config.CacheConfig;
import com.mlbstats.common.exception.ResourceNotFoundException;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.game.GameInning;
import com.mlbstats.domain.game.GameInningRepository;
import com.mlbstats.domain.game.GameRepository;
import com.mlbstats.domain.stats.PlayerGameBatting;
import com.mlbstats.domain.stats.PlayerGameBattingRepository;
import com.mlbstats.domain.stats.PlayerGamePitching;
import com.mlbstats.domain.stats.PlayerGamePitchingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameApiService {

    private final GameRepository gameRepository;
    private final GameInningRepository gameInningRepository;
    private final PlayerGameBattingRepository gameBattingRepository;
    private final PlayerGamePitchingRepository gamePitchingRepository;

    public PageDto<GameDto> getGamesBySeason(Integer season, Pageable pageable) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        Page<Game> page = gameRepository.findBySeasonOrderByGameDateDesc(season, pageable);
        return PageDto.fromPage(page, GameDto::fromEntity);
    }

    @Cacheable(value = CacheConfig.GAMES_BY_DATE, key = "#date")
    public List<GameDto> getGamesByDate(LocalDate date) {
        return gameRepository.findByDateWithTeams(date).stream()
                .map(GameDto::fromEntity)
                .toList();
    }

    public List<GameDto> getGamesByDateRange(LocalDate startDate, LocalDate endDate) {
        return gameRepository.findByDateRange(startDate, endDate).stream()
                .map(GameDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.GAMES, key = "#id")
    public GameDto getGameById(Long id) {
        Game game = gameRepository.findByIdWithTeams(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game", id));
        return GameDto.fromEntity(game);
    }

    public List<GameDto> getTeamGames(Long teamId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return gameRepository.findByTeamIdAndSeason(teamId, season).stream()
                .map(GameDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.BOX_SCORES, key = "#gameId")
    public BoxScoreDto getBoxScore(Long gameId) {
        Game game = gameRepository.findByIdWithTeams(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", gameId));

        List<PlayerGameBatting> batting = gameBattingRepository.findByGameIdWithPlayer(gameId);
        List<PlayerGamePitching> pitching = gamePitchingRepository.findByGameIdWithPlayer(gameId);

        Long awayTeamId = game.getAwayTeam().getId();
        Long homeTeamId = game.getHomeTeam().getId();

        List<GameBattingDto> awayBatting = batting.stream()
                .filter(b -> b.getTeam().getId().equals(awayTeamId))
                .map(GameBattingDto::fromEntity)
                .collect(Collectors.toList());

        List<GameBattingDto> homeBatting = batting.stream()
                .filter(b -> b.getTeam().getId().equals(homeTeamId))
                .map(GameBattingDto::fromEntity)
                .collect(Collectors.toList());

        List<GamePitchingDto> awayPitching = pitching.stream()
                .filter(p -> p.getTeam().getId().equals(awayTeamId))
                .map(GamePitchingDto::fromEntity)
                .collect(Collectors.toList());

        List<GamePitchingDto> homePitching = pitching.stream()
                .filter(p -> p.getTeam().getId().equals(homeTeamId))
                .map(GamePitchingDto::fromEntity)
                .collect(Collectors.toList());

        return new BoxScoreDto(
                GameDto.fromEntity(game),
                awayBatting,
                awayPitching,
                homeBatting,
                homePitching
        );
    }

    @Cacheable(value = CacheConfig.LINESCORES, key = "#gameId")
    public LinescoreDto getLinescore(Long gameId) {
        Game game = gameRepository.findByIdWithTeams(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", gameId));

        List<GameInning> innings = gameInningRepository.findByGameIdOrderByInningNumber(gameId);

        return LinescoreDto.fromEntities(game, innings);
    }

    // ==================== Calendar APIs ====================

    /**
     * Get lightweight game data for calendar views.
     * Uses optimized query with JOIN FETCH to avoid N+1.
     */
    @Cacheable(value = CacheConfig.CALENDAR_GAMES, key = "#startDate + '-' + #endDate + '-' + #teamId")
    public List<CalendarGameDto> getCalendarGames(LocalDate startDate, LocalDate endDate, Long teamId) {
        List<Game> games;
        if (teamId != null) {
            games = gameRepository.findByDateRangeAndTeamWithTeams(startDate, endDate, teamId);
        } else {
            games = gameRepository.findByDateRangeWithTeams(startDate, endDate);
        }
        return games.stream()
                .map(CalendarGameDto::fromEntity)
                .toList();
    }

    /**
     * Get game counts by date for monthly calendar overview.
     * Very lightweight - just date and count per day.
     */
    @Cacheable(value = CacheConfig.CALENDAR_COUNTS, key = "#startDate + '-' + #endDate + '-' + #teamId")
    public List<GameCountDto> getGameCounts(LocalDate startDate, LocalDate endDate, Long teamId) {
        if (teamId != null) {
            return gameRepository.countTeamGamesByDateRange(teamId, startDate, endDate).stream()
                    .map(row -> GameCountDto.forTeam(
                            (LocalDate) row[0],
                            ((Number) row[1]).intValue(),
                            ((Number) row[2]).intValue()
                    ))
                    .toList();
        } else {
            return gameRepository.countGamesByDateRange(startDate, endDate).stream()
                    .map(row -> GameCountDto.forAllTeams(
                            (LocalDate) row[0],
                            ((Number) row[1]).intValue()
                    ))
                    .toList();
        }
    }
}
