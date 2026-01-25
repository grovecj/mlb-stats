package com.mlbstats.ingestion.service;

import com.mlbstats.domain.player.Player;
import com.mlbstats.domain.player.PlayerRepository;
import com.mlbstats.ingestion.client.MlbApiClient;
import com.mlbstats.ingestion.client.dto.PlayerResponse;
import com.mlbstats.ingestion.mapper.PlayerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerIngestionService {

    private static final Logger log = LoggerFactory.getLogger(PlayerIngestionService.class);

    private final MlbApiClient mlbApiClient;
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public PlayerIngestionService(MlbApiClient mlbApiClient, PlayerRepository playerRepository, PlayerMapper playerMapper) {
        this.mlbApiClient = mlbApiClient;
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

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

    public Player getPlayerByMlbId(Integer mlbId) {
        return playerRepository.findByMlbId(mlbId).orElse(null);
    }
}
