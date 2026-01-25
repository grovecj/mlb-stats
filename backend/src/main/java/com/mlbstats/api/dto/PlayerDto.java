package com.mlbstats.api.dto;

import com.mlbstats.domain.player.Player;

import java.time.LocalDate;

public record PlayerDto(
        Long id,
        Integer mlbId,
        String fullName,
        String firstName,
        String lastName,
        String jerseyNumber,
        String position,
        String positionType,
        String bats,
        String throwsHand,
        LocalDate birthDate,
        String height,
        Integer weight,
        LocalDate mlbDebutDate,
        Boolean active
) {
    public static PlayerDto fromEntity(Player player) {
        return new PlayerDto(
                player.getId(),
                player.getMlbId(),
                player.getFullName(),
                player.getFirstName(),
                player.getLastName(),
                player.getJerseyNumber(),
                player.getPosition(),
                player.getPositionType(),
                player.getBats(),
                player.getThrowsHand(),
                player.getBirthDate(),
                player.getHeight(),
                player.getWeight(),
                player.getMlbDebutDate(),
                player.getActive()
        );
    }
}
