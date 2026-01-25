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
        Boolean active,
        String headshotUrl
) {
    private static final String HEADSHOT_URL_TEMPLATE =
            "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/%d/headshot/67/current";

    public static PlayerDto fromEntity(Player player) {
        String headshotUrl = player.getMlbId() != null
                ? String.format(HEADSHOT_URL_TEMPLATE, player.getMlbId())
                : null;

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
                player.getActive(),
                headshotUrl
        );
    }
}
