package com.mlbstats.api.service;

import com.mlbstats.api.dto.SeasonDataDto;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.domain.game.GameRepository;
import com.mlbstats.domain.player.TeamRosterRepository;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.domain.stats.PlayerPitchingStatsRepository;
import com.mlbstats.domain.team.TeamStandingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataManagerService {

    private final GameRepository gameRepository;
    private final PlayerBattingStatsRepository battingStatsRepository;
    private final PlayerPitchingStatsRepository pitchingStatsRepository;
    private final TeamRosterRepository rosterRepository;
    private final TeamStandingRepository standingRepository;

    public List<SeasonDataDto> getSyncedSeasons() {
        // Collect all distinct seasons from all tables
        Set<Integer> allSeasons = new HashSet<>();
        allSeasons.addAll(gameRepository.findDistinctSeasons());
        allSeasons.addAll(standingRepository.findDistinctSeasons());

        int currentSeason = DateUtils.getCurrentSeason();

        return allSeasons.stream()
                .sorted((a, b) -> b - a) // Descending order
                .map(season -> new SeasonDataDto(
                        season,
                        gameRepository.countBySeason(season),
                        battingStatsRepository.countBySeason(season),
                        pitchingStatsRepository.countBySeason(season),
                        rosterRepository.countBySeason(season),
                        standingRepository.countBySeason(season),
                        season == currentSeason
                ))
                .collect(Collectors.toList());
    }

    public List<Integer> getAvailableSeasons() {
        // Return seasons that can be synced (current year and previous 10 years)
        int currentYear = java.time.Year.now().getValue();
        return java.util.stream.IntStream.rangeClosed(currentYear - 10, currentYear)
                .boxed()
                .sorted((a, b) -> b - a)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSeasonData(Integer season) {
        int currentSeason = DateUtils.getCurrentSeason();
        if (season == currentSeason) {
            throw new IllegalArgumentException("Cannot delete current season data");
        }

        log.info("Deleting all data for season {}", season);

        // Delete in order to respect foreign key constraints
        standingRepository.deleteBySeason(season);
        pitchingStatsRepository.deleteBySeason(season);
        battingStatsRepository.deleteBySeason(season);
        rosterRepository.deleteBySeason(season);
        gameRepository.deleteBySeason(season);

        log.info("Successfully deleted all data for season {}", season);
    }
}
