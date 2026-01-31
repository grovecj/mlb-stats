package com.mlbstats.ingestion.client;

import com.mlbstats.common.exception.IngestionException;
import com.mlbstats.ingestion.client.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class MlbApiClient {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final RestClient restClient;

    public MlbApiClient(RestClient mlbApiRestClient) {
        this.restClient = mlbApiRestClient;
    }

    public TeamResponse getAllTeams() {
        log.info("Fetching all MLB teams");
        try {
            return restClient.get()
                    .uri("/teams?sportId=1")
                    .retrieve()
                    .body(TeamResponse.class);
        } catch (RestClientException e) {
            throw new IngestionException("Failed to fetch teams from MLB API", e);
        }
    }

    public RosterResponse getTeamRoster(Integer teamId, Integer season) {
        log.info("Fetching roster for team {} season {}", teamId, season);
        try {
            return restClient.get()
                    .uri("/teams/{teamId}/roster?season={season}&rosterType=40Man", teamId, season)
                    .retrieve()
                    .body(RosterResponse.class);
        } catch (RestClientException e) {
            throw new IngestionException("Failed to fetch roster for team " + teamId, e);
        }
    }

    public PlayerResponse getPlayer(Integer playerId) {
        log.info("Fetching player {}", playerId);
        try {
            return restClient.get()
                    .uri("/people/{playerId}", playerId)
                    .retrieve()
                    .body(PlayerResponse.class);
        } catch (RestClientException e) {
            throw new IngestionException("Failed to fetch player " + playerId, e);
        }
    }

    public ScheduleResponse getSchedule(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching schedule from {} to {}", startDate, endDate);
        try {
            return restClient.get()
                    .uri("/schedule?sportId=1&startDate={startDate}&endDate={endDate}&gameType=R,P&hydrate=probablePitcher",
                            startDate.format(DATE_FORMAT), endDate.format(DATE_FORMAT))
                    .retrieve()
                    .body(ScheduleResponse.class);
        } catch (RestClientException e) {
            throw new IngestionException("Failed to fetch schedule", e);
        }
    }

    public ScheduleResponse getScheduleForSeason(Integer season) {
        log.info("Fetching schedule for season {}", season);
        LocalDate startDate = LocalDate.of(season, 3, 1);
        LocalDate endDate = LocalDate.of(season, 11, 30);
        return getSchedule(startDate, endDate);
    }

    public StatsResponse getPlayerStats(Integer playerId, Integer season, String statGroup) {
        log.debug("Fetching {} stats for player {} season {}", statGroup, playerId, season);
        try {
            StatsResponse response = restClient.get()
                    .uri("/people/{playerId}/stats?stats=season&season={season}&group={group}",
                            playerId, season, statGroup)
                    .retrieve()
                    .body(StatsResponse.class);

            if (response != null && response.getStats() != null) {
                int splitCount = response.getStats().stream()
                        .filter(g -> g.getSplits() != null)
                        .mapToInt(g -> g.getSplits().size())
                        .sum();
                log.debug("MLB API returned {} stat groups with {} total splits for player {} ({} stats)",
                        response.getStats().size(), splitCount, playerId, statGroup);
            } else {
                log.debug("MLB API returned null or empty response for player {} ({} stats)", playerId, statGroup);
            }

            return response;
        } catch (RestClientException e) {
            log.warn("API error fetching {} stats for player {}: {}", statGroup, playerId, e.getMessage());
            throw new IngestionException("Failed to fetch stats for player " + playerId, e);
        }
    }

    public StatsResponse getPlayerBattingStats(Integer playerId, Integer season) {
        return getPlayerStats(playerId, season, "hitting");
    }

    public StatsResponse getPlayerPitchingStats(Integer playerId, Integer season) {
        return getPlayerStats(playerId, season, "pitching");
    }

    public StandingsResponse getStandings(Integer season) {
        log.info("Fetching standings for season {}", season);
        try {
            return restClient.get()
                    .uri("/standings?leagueId=103,104&season={season}&standingsTypes=regularSeason", season)
                    .retrieve()
                    .body(StandingsResponse.class);
        } catch (RestClientException e) {
            throw new IngestionException("Failed to fetch standings for season " + season, e);
        }
    }

    public BoxScoreResponse getBoxScore(Integer gamePk) {
        log.debug("Fetching box score for game {}", gamePk);
        try {
            BoxScoreResponse response = restClient.get()
                    .uri("/game/{gamePk}/boxscore", gamePk)
                    .retrieve()
                    .body(BoxScoreResponse.class);

            // Diagnostic logging
            if (response != null && response.getTeams() != null) {
                var away = response.getTeams().getAway();
                var home = response.getTeams().getHome();
                log.info("Box score for game {}: away players={}, home players={}",
                        gamePk,
                        away != null && away.getPlayers() != null ? away.getPlayers().size() : 0,
                        home != null && home.getPlayers() != null ? home.getPlayers().size() : 0);
            } else {
                log.warn("Box score response for game {} has null teams", gamePk);
            }

            return response;
        } catch (RestClientException e) {
            log.warn("Failed to fetch box score for game {}: {}", gamePk, e.getMessage());
            return null;
        }
    }

    public LinescoreResponse getLinescore(Integer gamePk) {
        log.debug("Fetching linescore for game {}", gamePk);
        try {
            LinescoreResponse response = restClient.get()
                    .uri("/game/{gamePk}/linescore", gamePk)
                    .retrieve()
                    .body(LinescoreResponse.class);

            if (response != null && response.getInnings() != null) {
                log.debug("Linescore for game {}: {} innings", gamePk, response.getInnings().size());
            }

            return response;
        } catch (RestClientException e) {
            log.warn("Failed to fetch linescore for game {}: {}", gamePk, e.getMessage());
            return null;
        }
    }
}
