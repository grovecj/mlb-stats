package com.mlbstats.ingestion.mapper;

import com.mlbstats.domain.player.Player;
import com.mlbstats.ingestion.client.dto.PlayerResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class PlayerMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Player toEntity(PlayerResponse.PlayerData dto) {
        Player player = new Player();
        player.setMlbId(dto.getId());
        player.setFullName(dto.getFullName());
        player.setFirstName(dto.getFirstName());
        player.setLastName(dto.getLastName());
        player.setJerseyNumber(dto.getPrimaryNumber());
        player.setHeight(dto.getHeight());
        player.setWeight(dto.getWeight());
        player.setActive(dto.getActive() != null ? dto.getActive() : true);

        if (dto.getPrimaryPosition() != null) {
            player.setPosition(dto.getPrimaryPosition().getAbbreviation());
            player.setPositionType(dto.getPrimaryPosition().getType());
        }

        if (dto.getBatSide() != null) {
            player.setBats(dto.getBatSide().getCode());
        }
        if (dto.getPitchHand() != null) {
            player.setThrowsHand(dto.getPitchHand().getCode());
        }

        if (dto.getBirthDate() != null) {
            player.setBirthDate(parseDate(dto.getBirthDate()));
        }

        if (dto.getMlbDebutDate() != null) {
            player.setMlbDebutDate(parseDate(dto.getMlbDebutDate()));
        }

        return player;
    }

    public void updateEntity(Player existing, PlayerResponse.PlayerData dto) {
        existing.setFullName(dto.getFullName());
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setJerseyNumber(dto.getPrimaryNumber());
        existing.setHeight(dto.getHeight());
        existing.setWeight(dto.getWeight());
        existing.setActive(dto.getActive() != null ? dto.getActive() : true);

        if (dto.getPrimaryPosition() != null) {
            existing.setPosition(dto.getPrimaryPosition().getAbbreviation());
            existing.setPositionType(dto.getPrimaryPosition().getType());
        }

        if (dto.getBatSide() != null) {
            existing.setBats(dto.getBatSide().getCode());
        }
        if (dto.getPitchHand() != null) {
            existing.setThrowsHand(dto.getPitchHand().getCode());
        }

        if (dto.getBirthDate() != null) {
            existing.setBirthDate(parseDate(dto.getBirthDate()));
        }

        if (dto.getMlbDebutDate() != null) {
            existing.setMlbDebutDate(parseDate(dto.getMlbDebutDate()));
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.substring(0, 10), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
