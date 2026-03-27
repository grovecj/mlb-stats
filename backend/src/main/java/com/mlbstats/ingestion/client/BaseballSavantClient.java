package com.mlbstats.ingestion.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Client for fetching data from Baseball Savant.
 * Retrieves OAA (Outs Above Average) and expected stats from the Statcast leaderboards.
 */
@Slf4j
@Component
public class BaseballSavantClient {

    private final RestClient restClient;

    // Baseball Savant CSV endpoints
    private static final String OAA_LEADERBOARD_URL =
            "https://baseballsavant.mlb.com/leaderboard/outs_above_average?type=Fielder&year={year}&min=q&csv=true";

    private static final String EXPECTED_STATS_URL =
            "https://baseballsavant.mlb.com/leaderboard/expected_statistics?type=batter&year={year}&min=q&csv=true";

    private static final String STATCAST_RUNNING_URL =
            "https://baseballsavant.mlb.com/leaderboard/sprint_speed?year={year}&min=q&csv=true";

    public BaseballSavantClient(RestClient baseballSavantRestClient) {
        this.restClient = baseballSavantRestClient;
    }

    /**
     * Fetches OAA (Outs Above Average) for all qualified fielders.
     *
     * @param season Season year
     * @return Map of MLB player ID to OAA value
     */
    public Map<Integer, Integer> getOaaByPlayerId(Integer season) {
        log.info("Fetching OAA leaderboard from Baseball Savant for {}", season);
        try {
            String csv = restClient.get()
                    .uri(OAA_LEADERBOARD_URL, season)
                    .retrieve()
                    .body(String.class);

            return parseOaaCsv(csv);
        } catch (RestClientException e) {
            log.error("Failed to fetch OAA leaderboard: {}", e.getMessage());
            return Map.of();
        }
    }

    /**
     * Fetches expected statistics (xBA, xSLG, xwOBA) for all qualified batters.
     *
     * @param season Season year
     * @return Map of MLB player ID to expected stats data
     */
    public Map<Integer, ExpectedStatsData> getExpectedStatsByPlayerId(Integer season) {
        log.info("Fetching expected stats leaderboard from Baseball Savant for {}", season);
        try {
            String csv = restClient.get()
                    .uri(EXPECTED_STATS_URL, season)
                    .retrieve()
                    .body(String.class);

            return parseExpectedStatsCsv(csv);
        } catch (RestClientException e) {
            log.error("Failed to fetch expected stats leaderboard: {}", e.getMessage());
            return Map.of();
        }
    }

    /**
     * Fetches sprint speed for all qualified runners.
     *
     * @param season Season year
     * @return Map of MLB player ID to sprint speed (ft/sec)
     */
    public Map<Integer, BigDecimal> getSprintSpeedByPlayerId(Integer season) {
        log.info("Fetching sprint speed leaderboard from Baseball Savant for {}", season);
        try {
            String csv = restClient.get()
                    .uri(STATCAST_RUNNING_URL, season)
                    .retrieve()
                    .body(String.class);

            return parseSprintSpeedCsv(csv);
        } catch (RestClientException e) {
            log.error("Failed to fetch sprint speed leaderboard: {}", e.getMessage());
            return Map.of();
        }
    }

    // ================================================================================
    // CSV PARSING
    // ================================================================================

    /**
     * Parses OAA CSV response.
     * Expected columns: player_id, last_name, first_name, fielding_runs_outs_above_average, ...
     */
    private Map<Integer, Integer> parseOaaCsv(String csv) {
        Map<Integer, Integer> result = new HashMap<>();
        if (csv == null || csv.isBlank()) return result;

        try (BufferedReader reader = new BufferedReader(new StringReader(csv))) {
            String header = reader.readLine();
            if (header == null) return result;

            // Find column indices
            String[] columns = parseCsvLine(header);
            int playerIdIdx = findColumnIndex(columns, "player_id");
            int oaaIdx = findColumnIndex(columns, "fielding_runs_outs_above_average", "outs_above_average", "oaa");

            if (playerIdIdx < 0 || oaaIdx < 0) {
                log.warn("Could not find required columns in OAA CSV. Header: {}", header);
                return result;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] values = parseCsvLine(line);
                    if (values.length > Math.max(playerIdIdx, oaaIdx)) {
                        Integer playerId = parseInteger(values[playerIdIdx]);
                        Integer oaa = parseInteger(values[oaaIdx]);
                        if (playerId != null && oaa != null) {
                            result.put(playerId, oaa);
                        }
                    }
                } catch (Exception e) {
                    log.trace("Skipping malformed OAA row: {}", line);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing OAA CSV: {}", e.getMessage());
        }

        log.debug("Parsed {} OAA entries from Baseball Savant", result.size());
        return result;
    }

    /**
     * Parses expected stats CSV response.
     */
    private Map<Integer, ExpectedStatsData> parseExpectedStatsCsv(String csv) {
        Map<Integer, ExpectedStatsData> result = new HashMap<>();
        if (csv == null || csv.isBlank()) return result;

        try (BufferedReader reader = new BufferedReader(new StringReader(csv))) {
            String header = reader.readLine();
            if (header == null) return result;

            String[] columns = parseCsvLine(header);
            int playerIdIdx = findColumnIndex(columns, "player_id");
            int xbaIdx = findColumnIndex(columns, "est_ba", "xba");
            int xslgIdx = findColumnIndex(columns, "est_slg", "xslg");
            int xwobaIdx = findColumnIndex(columns, "est_woba", "xwoba");
            int evIdx = findColumnIndex(columns, "avg_hit_speed", "exit_velocity_avg");
            int laIdx = findColumnIndex(columns, "avg_hit_angle", "launch_angle_avg");
            int barrelPctIdx = findColumnIndex(columns, "brl_percent", "barrel_batted_rate");
            int hardHitPctIdx = findColumnIndex(columns, "hard_hit_percent", "hard_hit_rate");

            if (playerIdIdx < 0) {
                log.warn("Could not find player_id column in expected stats CSV");
                return result;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] values = parseCsvLine(line);
                    if (values.length > playerIdIdx) {
                        Integer playerId = parseInteger(values[playerIdIdx]);
                        if (playerId != null) {
                            ExpectedStatsData data = new ExpectedStatsData(
                                    playerId,
                                    safeGet(values, xbaIdx),
                                    safeGet(values, xslgIdx),
                                    safeGet(values, xwobaIdx),
                                    safeGet(values, evIdx),
                                    safeGet(values, laIdx),
                                    safeGet(values, barrelPctIdx),
                                    safeGet(values, hardHitPctIdx),
                                    null  // sprint speed comes from separate endpoint
                            );
                            result.put(playerId, data);
                        }
                    }
                } catch (Exception e) {
                    log.trace("Skipping malformed expected stats row: {}", line);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing expected stats CSV: {}", e.getMessage());
        }

        log.debug("Parsed {} expected stats entries from Baseball Savant", result.size());
        return result;
    }

    /**
     * Parses sprint speed CSV response.
     */
    private Map<Integer, BigDecimal> parseSprintSpeedCsv(String csv) {
        Map<Integer, BigDecimal> result = new HashMap<>();
        if (csv == null || csv.isBlank()) return result;

        try (BufferedReader reader = new BufferedReader(new StringReader(csv))) {
            String header = reader.readLine();
            if (header == null) return result;

            String[] columns = parseCsvLine(header);
            int playerIdIdx = findColumnIndex(columns, "player_id");
            int speedIdx = findColumnIndex(columns, "hp_to_1b", "sprint_speed");

            if (playerIdIdx < 0 || speedIdx < 0) {
                log.warn("Could not find required columns in sprint speed CSV");
                return result;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] values = parseCsvLine(line);
                    if (values.length > Math.max(playerIdIdx, speedIdx)) {
                        Integer playerId = parseInteger(values[playerIdIdx]);
                        BigDecimal speed = parseDecimal(values[speedIdx]);
                        if (playerId != null && speed != null) {
                            result.put(playerId, speed);
                        }
                    }
                } catch (Exception e) {
                    log.trace("Skipping malformed sprint speed row: {}", line);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing sprint speed CSV: {}", e.getMessage());
        }

        log.debug("Parsed {} sprint speed entries from Baseball Savant", result.size());
        return result;
    }

    // ================================================================================
    // UTILITY METHODS
    // ================================================================================

    /**
     * Parses a CSV line using a regex pattern.
     * Handles standard quoted fields but may not handle all edge cases
     * (e.g., escaped quotes within fields like "Player ""Nickname"" Name").
     * Baseball Savant's CSV output uses standard formatting, so this is sufficient.
     */
    private String[] parseCsvLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    private int findColumnIndex(String[] columns, String... names) {
        for (int i = 0; i < columns.length; i++) {
            String col = columns[i].toLowerCase().trim().replace("\"", "");
            for (String name : names) {
                if (col.equals(name.toLowerCase())) {
                    return i;
                }
            }
        }
        return -1;
    }

    private BigDecimal safeGet(String[] values, int index) {
        if (index < 0 || index >= values.length) return null;
        return parseDecimal(values[index]);
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value.trim().replace("\"", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a numeric value from Baseball Savant CSV.
     * <p>
     * This method:
     * <ul>
     *     <li>Trims whitespace and removes surrounding quotes</li>
     *     <li>Strips any '%' characters from the value</li>
     *     <li>Parses the remaining text as a BigDecimal without scaling</li>
     * </ul>
     * <p>
     * Baseball Savant returns percentages already in 0-100 form (e.g., "15.3" or "15.3%"
     * both represent 15.3%). Values are parsed as-is without conversion.
     *
     * @param value The string value from the CSV
     * @return The parsed BigDecimal, or null if parsing fails
     */
    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.trim().replace("\"", "").replace("%", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ================================================================================
    // DATA CLASSES
    // ================================================================================

    /**
     * Expected stats data from Baseball Savant.
     */
    public record ExpectedStatsData(
            Integer playerId,
            BigDecimal xba,
            BigDecimal xslg,
            BigDecimal xwoba,
            BigDecimal avgExitVelocity,
            BigDecimal avgLaunchAngle,
            BigDecimal barrelPct,
            BigDecimal hardHitPct,
            BigDecimal sprintSpeed
    ) {}
}
