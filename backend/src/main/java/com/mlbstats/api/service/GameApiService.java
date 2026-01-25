package com.mlbstats.api.service;

import com.mlbstats.api.dto.GameDto;
import com.mlbstats.api.dto.PageDto;
import com.mlbstats.common.exception.ResourceNotFoundException;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.game.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameApiService {

    private final GameRepository gameRepository;

    public PageDto<GameDto> getGamesBySeason(Integer season, Pageable pageable) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        Page<Game> page = gameRepository.findBySeasonOrderByGameDateDesc(season, pageable);
        return PageDto.fromPage(page, GameDto::fromEntity);
    }

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
}
