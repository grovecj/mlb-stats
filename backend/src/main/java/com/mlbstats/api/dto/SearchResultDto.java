package com.mlbstats.api.dto;

import java.util.List;

public record SearchResultDto(
    List<TeamDto> teams,
    List<PlayerDto> players
) {
}
