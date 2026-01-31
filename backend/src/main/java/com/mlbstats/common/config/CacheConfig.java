package com.mlbstats.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class CacheConfig {

    // Cache name constants
    public static final String TEAMS = "teams";
    public static final String TEAMS_BY_ID = "teamsById";
    public static final String TEAMS_BY_LEAGUE = "teamsByLeague";
    public static final String TEAMS_BY_DIVISION = "teamsByDivision";
    public static final String ROSTERS = "rosters";
    public static final String STANDINGS = "standings";
    public static final String TEAM_STANDINGS = "teamStandings";
    public static final String PLAYERS = "players";
    public static final String LEADERBOARDS = "leaderboards";
    public static final String GAMES = "games";
    public static final String GAMES_BY_DATE = "gamesByDate";
    public static final String BOX_SCORES = "boxScores";
    public static final String LINESCORES = "linescores";
    public static final String CALENDAR_GAMES = "calendarGames";
    public static final String CALENDAR_COUNTS = "calendarCounts";
    public static final String SEARCH = "search";
    public static final String TEAM_AGGREGATE_STATS = "teamAggregateStats";
    public static final String PLAYER_COMPARISON = "playerComparison";

    private static final Map<String, CacheSpec> CACHE_SPECS = Map.ofEntries(
            Map.entry(TEAMS, new CacheSpec(Duration.ofHours(24), 1)),
            Map.entry(TEAMS_BY_ID, new CacheSpec(Duration.ofHours(24), 50)),
            Map.entry(TEAMS_BY_LEAGUE, new CacheSpec(Duration.ofHours(24), 10)),
            Map.entry(TEAMS_BY_DIVISION, new CacheSpec(Duration.ofHours(24), 20)),
            Map.entry(ROSTERS, new CacheSpec(Duration.ofDays(7), 100)),
            Map.entry(STANDINGS, new CacheSpec(Duration.ofMinutes(15), 10)),
            Map.entry(TEAM_STANDINGS, new CacheSpec(Duration.ofMinutes(15), 100)),
            Map.entry(PLAYERS, new CacheSpec(Duration.ofHours(24), 1000)),
            Map.entry(LEADERBOARDS, new CacheSpec(Duration.ofMinutes(30), 50)),
            Map.entry(GAMES, new CacheSpec(Duration.ofHours(1), 500)),
            Map.entry(GAMES_BY_DATE, new CacheSpec(Duration.ofHours(1), 60)),
            Map.entry(BOX_SCORES, new CacheSpec(Duration.ofHours(24), 200)),
            Map.entry(LINESCORES, new CacheSpec(Duration.ofHours(24), 500)),
            Map.entry(CALENDAR_GAMES, new CacheSpec(Duration.ofMinutes(15), 100)),
            Map.entry(CALENDAR_COUNTS, new CacheSpec(Duration.ofMinutes(30), 200)),
            Map.entry(SEARCH, new CacheSpec(Duration.ofMinutes(5), 100)),
            Map.entry(TEAM_AGGREGATE_STATS, new CacheSpec(Duration.ofMinutes(30), 100)),
            Map.entry(PLAYER_COMPARISON, new CacheSpec(Duration.ofMinutes(30), 200))
    );

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAsyncCacheMode(false);

        // Register each cache with its specific configuration
        CACHE_SPECS.forEach((cacheName, spec) ->
            cacheManager.registerCustomCache(cacheName, buildCache(spec)));

        return cacheManager;
    }

    private com.github.benmanes.caffeine.cache.Cache<Object, Object> buildCache(CacheSpec spec) {
        return Caffeine.newBuilder()
                .expireAfterWrite(spec.ttl())
                .maximumSize(spec.maxSize())
                .recordStats()
                .build();
    }

    private record CacheSpec(Duration ttl, int maxSize) {}
}
