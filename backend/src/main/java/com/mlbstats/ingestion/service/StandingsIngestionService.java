package com.mlbstats.ingestion.service;

import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import com.mlbstats.domain.team.TeamStanding;
import com.mlbstats.domain.team.TeamStandingRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.StandingsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StandingsIngestionService {

    private final MlbApiClient mlbApiClient;
    private final TeamRepository teamRepository;
    private final TeamStandingRepository standingRepository;

    @Transactional
    public int syncStandings(Integer season) {
        log.info("Syncing standings for season {}", season);

        StandingsResponse response = mlbApiClient.getStandings(season);
        if (response == null || response.getRecords() == null) {
            log.warn("No standings data returned for season {}", season);
            return 0;
        }

        int count = 0;
        for (StandingsResponse.StandingsRecord record : response.getRecords()) {
            if (record.getTeamRecords() == null) continue;

            for (StandingsResponse.TeamRecord teamRecord : record.getTeamRecords()) {
                if (teamRecord.getTeam() == null) continue;

                Optional<Team> teamOpt = teamRepository.findByMlbId(teamRecord.getTeam().getId());
                if (teamOpt.isEmpty()) {
                    log.warn("Team not found for MLB ID: {}", teamRecord.getTeam().getId());
                    continue;
                }

                Team team = teamOpt.get();
                TeamStanding standing = standingRepository.findByTeamAndSeason(team, season)
                        .orElse(new TeamStanding());

                standing.setTeam(team);
                standing.setSeason(season);
                standing.setWins(teamRecord.getWins() != null ? teamRecord.getWins() : 0);
                standing.setLosses(teamRecord.getLosses() != null ? teamRecord.getLosses() : 0);

                if (teamRecord.getWinningPercentage() != null) {
                    try {
                        standing.setWinningPercentage(new BigDecimal(teamRecord.getWinningPercentage()));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid winning percentage: {}", teamRecord.getWinningPercentage());
                    }
                }

                standing.setGamesBack(teamRecord.getGamesBack());
                standing.setWildCardGamesBack(teamRecord.getWildCardGamesBack());
                standing.setDivisionRank(teamRecord.getDivisionRank());
                standing.setLeagueRank(teamRecord.getLeagueRank());
                standing.setWildCardRank(teamRecord.getWildCardRank());
                standing.setRunsScored(teamRecord.getRunsScored());
                standing.setRunsAllowed(teamRecord.getRunsAllowed());
                standing.setRunDifferential(teamRecord.getRunDifferential());

                if (teamRecord.getStreak() != null) {
                    standing.setStreakCode(teamRecord.getStreak().getStreakCode());
                }

                // Parse home/away splits
                if (teamRecord.getRecords() != null && teamRecord.getRecords().getSplitRecords() != null) {
                    for (StandingsResponse.SplitRecord split : teamRecord.getRecords().getSplitRecords()) {
                        if ("home".equals(split.getType())) {
                            standing.setHomeWins(split.getWins());
                            standing.setHomeLosses(split.getLosses());
                        } else if ("away".equals(split.getType())) {
                            standing.setAwayWins(split.getWins());
                            standing.setAwayLosses(split.getLosses());
                        }
                    }
                }

                standingRepository.save(standing);
                count++;
            }
        }

        log.info("Synced {} team standings for season {}", count, season);
        return count;
    }
}
