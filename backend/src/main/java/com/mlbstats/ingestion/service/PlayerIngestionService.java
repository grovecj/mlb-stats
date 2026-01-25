package com.mlbstats.ingestion.service;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.PlayerResponse;
import com.mlbstats.ingestion.mapper.PlayerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerIngestionService {

    private final MlbApiClient mlbApiClient;
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    @Transactional
    public Player syncPlayer(Integer mlbPlayerId) {
        log.debug("Syncing player {}", mlbPlayerId);
        PlayerResponse response = mlbApiClient.getPlayer(mlbPlayerId);

        if (response == null || response.getPeople() == null || response.getPeople().isEmpty()) {
            log.warn("No player data returned for ID {}", mlbPlayerId);
            return null;
        }

        PlayerResponse.PlayerData playerData = response.getPeople().get(0);
        return syncPlayer(playerData);
    }

    @Transactional
    public Player syncPlayer(PlayerResponse.PlayerData playerData) {
        return playerRepository.findByMlbId(playerData.getId())
                .map(existing -> {
                    playerMapper.updateEntity(existing, playerData);
                    return playerRepository.save(existing);
                })
                .orElseGet(() -> {
                    Player player = playerMapper.toEntity(playerData);
                    return playerRepository.save(player);
                });
    }

    @Transactional
    public Player getOrCreatePlayer(Integer mlbPlayerId, String fullName) {
        return playerRepository.findByMlbId(mlbPlayerId)
                .map(existing -> {
                    // If player exists but is missing key fields, try to sync
                    if (isIncomplete(existing)) {
                        try {
                            log.debug("Player {} has incomplete data, syncing", mlbPlayerId);
                            return syncPlayer(mlbPlayerId);
                        } catch (Exception e) {
                            log.warn("Failed to sync incomplete player {}: {}", mlbPlayerId, e.getMessage());
                            return existing;
                        }
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    // Try to fetch from API first
                    try {
                        return syncPlayer(mlbPlayerId);
                    } catch (Exception e) {
                        log.warn("Failed to fetch player {} from API, creating minimal record", mlbPlayerId);
                        // Create minimal player record if API fails
                        Player player = new Player();
                        player.setMlbId(mlbPlayerId);
                        player.setFullName(fullName);
                        player.setActive(true);
                        return playerRepository.save(player);
                    }
                });
    }

    private boolean isIncomplete(Player player) {
        // Check if essential biographical fields are missing
        return player.getBats() == null || player.getHeight() == null || player.getBirthDate() == null;
    }

    public Player getPlayerByMlbId(Integer mlbId) {
        return playerRepository.findByMlbId(mlbId).orElse(null);
    }

    @Transactional
    public int syncIncompletePlayers() {
        var incompletePlayers = playerRepository.findIncomplete();
        log.info("Found {} players with incomplete data", incompletePlayers.size());

        int synced = 0;
        for (Player player : incompletePlayers) {
            try {
                syncPlayer(player.getMlbId());
                synced++;
            } catch (Exception e) {
                log.warn("Failed to sync player {}: {}", player.getMlbId(), e.getMessage());
            }
        }

        log.info("Successfully synced {} of {} incomplete players", synced, incompletePlayers.size());
        return synced;
    }
}
