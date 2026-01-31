package com.mlbstats.api.service;

import com.mlbstats.api.dto.*;
import com.mlbstats.common.config.CacheConfig;
import com.mlbstats.common.exception.ResourceNotFoundException;
import com.mlbstats.common.util.DateUtils;
import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.domain.player.PlayerSearchCriteria;
import com.mlbstats.domain.player.PlayerSpecification;
import com.mlbstats.domain.stats.PlayerBattingStats;
import com.mlbstats.domain.stats.PlayerBattingStatsRepository;
import com.mlbstats.domain.stats.PlayerGameBattingRepository;
import com.mlbstats.domain.stats.PlayerGamePitchingRepository;
import com.mlbstats.domain.stats.PlayerPitchingStats;
import com.mlbstats.domain.stats.PlayerPitchingStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public PageDto<PlayerDto> searchPlayersWithFilters(PlayerSearchCriteria criteria, Pageable pageable) {
        Page<Player> page = playerRepository.findAll(PlayerSpecification.withCriteria(criteria), pageable);
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

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'rbi_' + #season + '_' + #limit")
    public List<BattingStatsDto> getTopRbi(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findTopRbi(season).stream()
                .limit(limit)
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'runs_' + #season + '_' + #limit")
    public List<BattingStatsDto> getTopRuns(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findTopRuns(season).stream()
                .limit(limit)
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'hits_' + #season + '_' + #limit")
    public List<BattingStatsDto> getTopHits(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findTopHits(season).stream()
                .limit(limit)
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'sb_' + #season + '_' + #limit")
    public List<BattingStatsDto> getTopStolenBases(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findTopStolenBases(season).stream()
                .limit(limit)
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'ops_' + #season + '_' + #minAtBats + '_' + #limit")
    public List<BattingStatsDto> getTopOps(Integer season, int minAtBats, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return battingStatsRepository.findTopOps(season, minAtBats).stream()
                .limit(limit)
                .map(BattingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'era_' + #season + '_' + #minInnings + '_' + #limit")
    public List<PitchingStatsDto> getTopEra(Integer season, java.math.BigDecimal minInnings, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return pitchingStatsRepository.findTopEra(season, minInnings).stream()
                .limit(limit)
                .map(PitchingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'saves_' + #season + '_' + #limit")
    public List<PitchingStatsDto> getTopSaves(Integer season, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return pitchingStatsRepository.findTopSaves(season).stream()
                .limit(limit)
                .map(PitchingStatsDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheConfig.LEADERBOARDS, key = "'whip_' + #season + '_' + #minInnings + '_' + #limit")
    public List<PitchingStatsDto> getTopWhip(Integer season, java.math.BigDecimal minInnings, int limit) {
        if (season == null) {
            season = DateUtils.getCurrentSeason();
        }
        return pitchingStatsRepository.findTopWhip(season, minInnings).stream()
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

    @Cacheable(value = CacheConfig.PLAYER_COMPARISON, key = "'compare_' + #playerIds + '_' + #seasons + '_' + #careerMode")
    public PlayerComparisonDto comparePlayerStats(List<Long> playerIds, List<Integer> seasons, boolean careerMode) {
        // Validate player count
        if (playerIds.size() < 2 || playerIds.size() > 4) {
            throw new IllegalArgumentException("Must compare between 2 and 4 players");
        }

        // Validate seasons for season mode
        if (!careerMode && (seasons == null || seasons.size() != playerIds.size())) {
            throw new IllegalArgumentException("Season mode requires one season per player");
        }

        // Fetch all players first
        Map<Long, Player> playersMap = playerRepository.findAllById(playerIds).stream()
                .collect(Collectors.toMap(Player::getId, p -> p));

        // Verify all players exist
        for (Long playerId : playerIds) {
            if (!playersMap.containsKey(playerId)) {
                throw new ResourceNotFoundException("Player", playerId);
            }
        }

        List<PlayerComparisonDto.PlayerComparisonEntry> entries;

        if (careerMode) {
            entries = buildCareerEntries(playerIds, playersMap);
        } else {
            entries = buildSeasonEntries(playerIds, seasons, playersMap);
        }

        PlayerComparisonDto.ComparisonLeaders leaders = calculateLeaders(entries);

        return new PlayerComparisonDto(
                careerMode ? "career" : "season",
                entries,
                leaders
        );
    }

    private List<PlayerComparisonDto.PlayerComparisonEntry> buildCareerEntries(
            List<Long> playerIds, Map<Long, Player> playersMap) {

        // Batch fetch all stats for all players
        List<PlayerBattingStats> allBattingStats = battingStatsRepository.findByPlayerIdIn(playerIds);
        List<PlayerPitchingStats> allPitchingStats = pitchingStatsRepository.findByPlayerIdIn(playerIds);

        // Group by player
        Map<Long, List<PlayerBattingStats>> battingByPlayer = allBattingStats.stream()
                .collect(Collectors.groupingBy(s -> s.getPlayer().getId()));
        Map<Long, List<PlayerPitchingStats>> pitchingByPlayer = allPitchingStats.stream()
                .collect(Collectors.groupingBy(s -> s.getPlayer().getId()));

        return playerIds.stream().map(playerId -> {
            Player player = playersMap.get(playerId);
            List<PlayerBattingStats> battingStats = battingByPlayer.getOrDefault(playerId, List.of());
            List<PlayerPitchingStats> pitchingStats = pitchingByPlayer.getOrDefault(playerId, List.of());

            return new PlayerComparisonDto.PlayerComparisonEntry(
                    PlayerDto.fromEntity(player),
                    null, // career mode has no season
                    PlayerComparisonDto.ComparisonBattingStats.aggregateCareer(battingStats),
                    PlayerComparisonDto.ComparisonPitchingStats.aggregateCareer(pitchingStats)
            );
        }).toList();
    }

    private List<PlayerComparisonDto.PlayerComparisonEntry> buildSeasonEntries(
            List<Long> playerIds, List<Integer> seasons, Map<Long, Player> playersMap) {

        return java.util.stream.IntStream.range(0, playerIds.size())
                .mapToObj(i -> {
                    Long playerId = playerIds.get(i);
                    Integer season = seasons.get(i);
                    Player player = playersMap.get(playerId);

                    // Use list-returning methods and aggregate (handles traded players with multiple team rows)
                    List<PlayerBattingStats> battingStatsList = battingStatsRepository
                            .findByPlayerIdAndSeason(playerId, season);
                    List<PlayerPitchingStats> pitchingStatsList = pitchingStatsRepository
                            .findByPlayerIdAndSeason(playerId, season);

                    // Aggregate stats across all teams for the season
                    PlayerComparisonDto.ComparisonBattingStats battingStats =
                            PlayerComparisonDto.ComparisonBattingStats.aggregateCareer(battingStatsList);
                    PlayerComparisonDto.ComparisonPitchingStats pitchingStats =
                            PlayerComparisonDto.ComparisonPitchingStats.aggregateCareer(pitchingStatsList);

                    return new PlayerComparisonDto.PlayerComparisonEntry(
                            PlayerDto.fromEntity(player),
                            season,
                            battingStats,
                            pitchingStats
                    );
                }).toList();
    }

    private PlayerComparisonDto.ComparisonLeaders calculateLeaders(
            List<PlayerComparisonDto.PlayerComparisonEntry> entries) {

        Map<String, Long> battingLeaders = new HashMap<>();
        Map<String, Long> pitchingLeaders = new HashMap<>();

        // Batting stats - higher is better
        Set<String> battingHigherBetter = Set.of(
                "gamesPlayed", "atBats", "runs", "hits", "doubles", "triples",
                "homeRuns", "rbi", "stolenBases", "walks", "battingAvg", "obp",
                "slg", "ops", "plateAppearances", "totalBases", "extraBaseHits"
        );
        // Batting stats - lower is better
        Set<String> battingLowerBetter = Set.of("strikeouts", "caughtStealing");

        // Pitching stats - higher is better
        Set<String> pitchingHigherBetter = Set.of(
                "gamesPlayed", "gamesStarted", "wins", "saves", "holds",
                "inningsPitched", "strikeouts", "kPer9", "completeGames", "shutouts"
        );
        // Pitching stats - lower is better
        Set<String> pitchingLowerBetter = Set.of(
                "losses", "hitsAllowed", "runsAllowed", "earnedRuns",
                "homeRunsAllowed", "walks", "era", "whip", "bbPer9"
        );

        // Calculate batting leaders
        for (String stat : battingHigherBetter) {
            findBattingLeader(entries, stat, true).ifPresent(id -> battingLeaders.put(stat, id));
        }
        for (String stat : battingLowerBetter) {
            findBattingLeader(entries, stat, false).ifPresent(id -> battingLeaders.put(stat, id));
        }

        // Calculate pitching leaders
        for (String stat : pitchingHigherBetter) {
            findPitchingLeader(entries, stat, true).ifPresent(id -> pitchingLeaders.put(stat, id));
        }
        for (String stat : pitchingLowerBetter) {
            findPitchingLeader(entries, stat, false).ifPresent(id -> pitchingLeaders.put(stat, id));
        }

        return new PlayerComparisonDto.ComparisonLeaders(battingLeaders, pitchingLeaders);
    }

    private java.util.Optional<Long> findBattingLeader(
            List<PlayerComparisonDto.PlayerComparisonEntry> entries, String stat, boolean higherIsBetter) {

        Long leaderId = null;
        java.math.BigDecimal bestValue = null;

        for (var entry : entries) {
            var batting = entry.battingStats();
            if (batting == null) continue;

            java.math.BigDecimal value = getBattingStatValue(batting, stat);
            if (value == null) continue;

            if (bestValue == null ||
                    (higherIsBetter && value.compareTo(bestValue) > 0) ||
                    (!higherIsBetter && value.compareTo(bestValue) < 0)) {
                bestValue = value;
                leaderId = entry.player().id();
            }
        }

        return java.util.Optional.ofNullable(leaderId);
    }

    private java.util.Optional<Long> findPitchingLeader(
            List<PlayerComparisonDto.PlayerComparisonEntry> entries, String stat, boolean higherIsBetter) {

        Long leaderId = null;
        java.math.BigDecimal bestValue = null;

        for (var entry : entries) {
            var pitching = entry.pitchingStats();
            if (pitching == null) continue;

            java.math.BigDecimal value = getPitchingStatValue(pitching, stat);
            if (value == null) continue;

            if (bestValue == null ||
                    (higherIsBetter && value.compareTo(bestValue) > 0) ||
                    (!higherIsBetter && value.compareTo(bestValue) < 0)) {
                bestValue = value;
                leaderId = entry.player().id();
            }
        }

        return java.util.Optional.ofNullable(leaderId);
    }

    private java.math.BigDecimal getBattingStatValue(PlayerComparisonDto.ComparisonBattingStats stats, String stat) {
        return switch (stat) {
            case "gamesPlayed" -> stats.gamesPlayed() != null ? java.math.BigDecimal.valueOf(stats.gamesPlayed()) : null;
            case "atBats" -> stats.atBats() != null ? java.math.BigDecimal.valueOf(stats.atBats()) : null;
            case "runs" -> stats.runs() != null ? java.math.BigDecimal.valueOf(stats.runs()) : null;
            case "hits" -> stats.hits() != null ? java.math.BigDecimal.valueOf(stats.hits()) : null;
            case "doubles" -> stats.doubles() != null ? java.math.BigDecimal.valueOf(stats.doubles()) : null;
            case "triples" -> stats.triples() != null ? java.math.BigDecimal.valueOf(stats.triples()) : null;
            case "homeRuns" -> stats.homeRuns() != null ? java.math.BigDecimal.valueOf(stats.homeRuns()) : null;
            case "rbi" -> stats.rbi() != null ? java.math.BigDecimal.valueOf(stats.rbi()) : null;
            case "stolenBases" -> stats.stolenBases() != null ? java.math.BigDecimal.valueOf(stats.stolenBases()) : null;
            case "caughtStealing" -> stats.caughtStealing() != null ? java.math.BigDecimal.valueOf(stats.caughtStealing()) : null;
            case "walks" -> stats.walks() != null ? java.math.BigDecimal.valueOf(stats.walks()) : null;
            case "strikeouts" -> stats.strikeouts() != null ? java.math.BigDecimal.valueOf(stats.strikeouts()) : null;
            case "battingAvg" -> stats.battingAvg();
            case "obp" -> stats.obp();
            case "slg" -> stats.slg();
            case "ops" -> stats.ops();
            case "plateAppearances" -> stats.plateAppearances() != null ? java.math.BigDecimal.valueOf(stats.plateAppearances()) : null;
            case "totalBases" -> stats.totalBases() != null ? java.math.BigDecimal.valueOf(stats.totalBases()) : null;
            case "extraBaseHits" -> stats.extraBaseHits() != null ? java.math.BigDecimal.valueOf(stats.extraBaseHits()) : null;
            default -> null;
        };
    }

    private java.math.BigDecimal getPitchingStatValue(PlayerComparisonDto.ComparisonPitchingStats stats, String stat) {
        return switch (stat) {
            case "gamesPlayed" -> stats.gamesPlayed() != null ? java.math.BigDecimal.valueOf(stats.gamesPlayed()) : null;
            case "gamesStarted" -> stats.gamesStarted() != null ? java.math.BigDecimal.valueOf(stats.gamesStarted()) : null;
            case "wins" -> stats.wins() != null ? java.math.BigDecimal.valueOf(stats.wins()) : null;
            case "losses" -> stats.losses() != null ? java.math.BigDecimal.valueOf(stats.losses()) : null;
            case "saves" -> stats.saves() != null ? java.math.BigDecimal.valueOf(stats.saves()) : null;
            case "holds" -> stats.holds() != null ? java.math.BigDecimal.valueOf(stats.holds()) : null;
            case "inningsPitched" -> stats.inningsPitched();
            case "hitsAllowed" -> stats.hitsAllowed() != null ? java.math.BigDecimal.valueOf(stats.hitsAllowed()) : null;
            case "runsAllowed" -> stats.runsAllowed() != null ? java.math.BigDecimal.valueOf(stats.runsAllowed()) : null;
            case "earnedRuns" -> stats.earnedRuns() != null ? java.math.BigDecimal.valueOf(stats.earnedRuns()) : null;
            case "homeRunsAllowed" -> stats.homeRunsAllowed() != null ? java.math.BigDecimal.valueOf(stats.homeRunsAllowed()) : null;
            case "walks" -> stats.walks() != null ? java.math.BigDecimal.valueOf(stats.walks()) : null;
            case "strikeouts" -> stats.strikeouts() != null ? java.math.BigDecimal.valueOf(stats.strikeouts()) : null;
            case "era" -> stats.era();
            case "whip" -> stats.whip();
            case "kPer9" -> stats.kPer9();
            case "bbPer9" -> stats.bbPer9();
            case "completeGames" -> stats.completeGames() != null ? java.math.BigDecimal.valueOf(stats.completeGames()) : null;
            case "shutouts" -> stats.shutouts() != null ? java.math.BigDecimal.valueOf(stats.shutouts()) : null;
            default -> null;
        };
    }
}
