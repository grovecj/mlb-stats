package com.mlbstats.api.service;

import com.mlbstats.api.dto.BattingStatsDto;
import com.mlbstats.api.dto.GameDto;
import com.mlbstats.api.dto.RosterEntryDto;
import com.mlbstats.api.dto.TeamAggregateStatsDto;
import com.mlbstats.api.dto.TeamAggregateStatsDto.TeamBattingAggregateDto;
import com.mlbstats.api.dto.TeamAggregateStatsDto.TeamPitchingAggregateDto;
import com.mlbstats.api.dto.TeamDto;
import com.mlbstats.api.dto.TeamStandingDto;
import com.mlbstats.common.config.CacheConfig;
import com.mlbstats.common.exception.ResourceNotFoundException;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.domain.game.GameRepository;
import com.mlbstats.domain.player.TeamRosterRepository;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import com.mlbstats.domain.stats.PlayerPitchingStatsRepository;
import com.mlbstats.domain.team.Team;
import com.mlbstats.domain.team.TeamRepository;
import com.mlbstats.domain.team.TeamStandingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamApiService {

    private final TeamRepository teamRepository;
    private final TeamRosterRepository rosterRepository;
    private final GameRepository gameRepository;
    private final PlayerBattingStatsRepository battingStatsRepository;
    private final PlayerPitchingStatsRepository pitchingStatsRepository;
    private final TeamStandingRepository standingRepository;

    @Cacheable(CacheConfig.TEAMS)
    public List<TeamDto> getAllTeams() {
        return teamRepository.findAllOrderByLeagueAndDivision().stream()
                .map(TeamDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.SEARCH, key = "'team_' + #search")
    public List<TeamDto> searchTeams(String search) {
        return teamRepository.searchTeams(search).stream()
                .map(TeamDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.TEAMS_BY_LEAGUE, key = "#league")
    public List<TeamDto> getTeamsByLeague(String league) {
        return teamRepository.findByLeague(league).stream()
                .map(TeamDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.TEAMS_BY_DIVISION, key = "#league + '_' + #division")
    public List<TeamDto> getTeamsByDivision(String league, String division) {
        return teamRepository.findByLeagueAndDivision(league, division).stream()
                .map(TeamDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.TEAMS_BY_ID, key = "#id")
    public TeamDto getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
        return TeamDto.fromEntity(team);
    }

    @Cacheable(value = CacheConfig.ROSTERS, key = "#teamId + '_' + #season")
    public List<RosterEntryDto> getTeamRoster(Long teamId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return rosterRepository.findByTeamIdAndSeasonWithPlayer(teamId, season).stream()
                .map(RosterEntryDto::fromEntity)
                .toList();
    }

    public List<GameDto> getTeamGames(Long teamId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return gameRepository.findByTeamIdAndSeason(teamId, season).stream()
                .map(GameDto::fromEntity)
                .toList();
    }

    public List<BattingStatsDto> getTeamBattingStats(Long teamId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findByTeamIdAndSeasonWithPlayer(teamId, season).stream()
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.STANDINGS, key = "#season")
    public List<TeamStandingDto> getStandings(Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return standingRepository.findBySeasonWithTeam(season).stream()
                .map(TeamStandingDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.TEAM_STANDINGS, key = "#teamId + '_' + #season")
    public TeamStandingDto getTeamStanding(Long teamId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return standingRepository.findByTeamIdAndSeason(teamId, season)
                .map(TeamStandingDto::fromEntity)
                .orElse(null);
    }

    @Cacheable(value = CacheConfig.TEAM_AGGREGATE_STATS, key = "#teamId + '_' + #season")
    public TeamAggregateStatsDto getTeamAggregateStats(Long teamId, Integer season) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }

        List<PlayerBattingStats> battingStats = battingStatsRepository.findByTeamIdAndSeason(teamId, season);
        List<PlayerPitchingStats> pitchingStats = pitchingStatsRepository.findByTeamIdAndSeason(teamId, season);

        TeamBattingAggregateDto battingAggregate = aggregateBattingStats(battingStats);
        TeamPitchingAggregateDto pitchingAggregate = aggregatePitchingStats(pitchingStats);

        return new TeamAggregateStatsDto(teamId, season, battingAggregate, pitchingAggregate);
    }

    private TeamBattingAggregateDto aggregateBattingStats(List<PlayerBattingStats> stats) {
        if (stats.isEmpty()) {
            return null;
        }

        int gamesPlayed = stats.stream().mapToInt(s -> nullToZero(s.getGamesPlayed())).max().orElse(0);
        int atBats = stats.stream().mapToInt(s -> nullToZero(s.getAtBats())).sum();
        int runs = stats.stream().mapToInt(s -> nullToZero(s.getRuns())).sum();
        int hits = stats.stream().mapToInt(s -> nullToZero(s.getHits())).sum();
        int doubles = stats.stream().mapToInt(s -> nullToZero(s.getDoubles())).sum();
        int triples = stats.stream().mapToInt(s -> nullToZero(s.getTriples())).sum();
        int homeRuns = stats.stream().mapToInt(s -> nullToZero(s.getHomeRuns())).sum();
        int rbi = stats.stream().mapToInt(s -> nullToZero(s.getRbi())).sum();
        int stolenBases = stats.stream().mapToInt(s -> nullToZero(s.getStolenBases())).sum();
        int walks = stats.stream().mapToInt(s -> nullToZero(s.getWalks())).sum();
        int strikeouts = stats.stream().mapToInt(s -> nullToZero(s.getStrikeouts())).sum();
        int plateAppearances = stats.stream().mapToInt(s -> nullToZero(s.getPlateAppearances())).sum();
        int hitByPitch = stats.stream().mapToInt(s -> nullToZero(s.getHitByPitch())).sum();
        int sacFlies = stats.stream().mapToInt(s -> nullToZero(s.getSacFlies())).sum();
        int totalBases = stats.stream().mapToInt(s -> nullToZero(s.getTotalBases())).sum();

        // Calculate rate stats
        BigDecimal battingAvg = atBats > 0
                ? BigDecimal.valueOf(hits).divide(BigDecimal.valueOf(atBats), 3, RoundingMode.HALF_UP)
                : null;

        // OBP = (H + BB + HBP) / (AB + BB + HBP + SF)
        int obpDenominator = atBats + walks + hitByPitch + sacFlies;
        BigDecimal obp = obpDenominator > 0
                ? BigDecimal.valueOf(hits + walks + hitByPitch).divide(BigDecimal.valueOf(obpDenominator), 3, RoundingMode.HALF_UP)
                : null;

        // SLG = TB / AB
        BigDecimal slg = atBats > 0
                ? BigDecimal.valueOf(totalBases).divide(BigDecimal.valueOf(atBats), 3, RoundingMode.HALF_UP)
                : null;

        // OPS = OBP + SLG
        BigDecimal ops = (obp != null && slg != null) ? obp.add(slg) : null;

        return new TeamBattingAggregateDto(
                gamesPlayed, atBats, runs, hits, doubles, triples, homeRuns, rbi,
                stolenBases, walks, strikeouts, plateAppearances,
                battingAvg, obp, slg, ops
        );
    }

    private TeamPitchingAggregateDto aggregatePitchingStats(List<PlayerPitchingStats> stats) {
        if (stats.isEmpty()) {
            return null;
        }

        int gamesPlayed = stats.stream().mapToInt(s -> nullToZero(s.getGamesPlayed())).max().orElse(0);
        int wins = stats.stream().mapToInt(s -> nullToZero(s.getWins())).sum();
        int losses = stats.stream().mapToInt(s -> nullToZero(s.getLosses())).sum();
        int saves = stats.stream().mapToInt(s -> nullToZero(s.getSaves())).sum();
        BigDecimal inningsPitched = stats.stream()
                .map(PlayerPitchingStats::getInningsPitched)
                .filter(ip -> ip != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int hitsAllowed = stats.stream().mapToInt(s -> nullToZero(s.getHitsAllowed())).sum();
        int earnedRuns = stats.stream().mapToInt(s -> nullToZero(s.getEarnedRuns())).sum();
        int walks = stats.stream().mapToInt(s -> nullToZero(s.getWalks())).sum();
        int strikeouts = stats.stream().mapToInt(s -> nullToZero(s.getStrikeouts())).sum();
        int homeRunsAllowed = stats.stream().mapToInt(s -> nullToZero(s.getHomeRunsAllowed())).sum();
        int qualityStarts = stats.stream().mapToInt(s -> nullToZero(s.getQualityStarts())).sum();

        // Calculate rate stats
        // ERA = (ER * 9) / IP
        BigDecimal era = inningsPitched.compareTo(BigDecimal.ZERO) > 0
                ? BigDecimal.valueOf(earnedRuns).multiply(BigDecimal.valueOf(9))
                        .divide(inningsPitched, 2, RoundingMode.HALF_UP)
                : null;

        // WHIP = (H + BB) / IP
        BigDecimal whip = inningsPitched.compareTo(BigDecimal.ZERO) > 0
                ? BigDecimal.valueOf(hitsAllowed + walks).divide(inningsPitched, 2, RoundingMode.HALF_UP)
                : null;

        // K/9 = (K * 9) / IP
        BigDecimal kPer9 = inningsPitched.compareTo(BigDecimal.ZERO) > 0
                ? BigDecimal.valueOf(strikeouts).multiply(BigDecimal.valueOf(9))
                        .divide(inningsPitched, 2, RoundingMode.HALF_UP)
                : null;

        return new TeamPitchingAggregateDto(
                gamesPlayed, wins, losses, saves, inningsPitched,
                hitsAllowed, earnedRuns, walks, strikeouts, homeRunsAllowed,
                qualityStarts, era, whip, kPer9
        );
    }

    private int nullToZero(Integer value) {
        return value != null ? value : 0;
    }
}
