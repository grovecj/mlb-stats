package com.mlbstats.ingestion.client;

import com.mlbstats.common.exception.IngestionException;
import com.mlbstats.ingestion.client.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class MlbApiClient {

    private static final Logger log = LoggerFactory.getLogger(MlbApiClient.class);
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
                    .uri("/schedule?sportId=1&startDate={startDate}&endDate={endDate}&gameType=R,P",
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
        log.info("Fetching {} stats for player {} season {}", statGroup, playerId, season);
        try {
            return restClient.get()
                    .uri("/people/{playerId}/stats?stats=season&season={season}&group={group}",
                            playerId, season, statGroup)
                    .retrieve()
                    .body(StatsResponse.class);
        } catch (RestClientException e) {
            throw new IngestionException("Failed to fetch stats for player " + playerId, e);
        }
    }

    public StatsResponse getPlayerBattingStats(Integer playerId, Integer season) {
        return getPlayerStats(playerId, season, "hitting");
    }

    public StatsResponse getPlayerPitchingStats(Integer playerId, Integer season) {
        return getPlayerStats(playerId, season, "pitching");
    }
}
