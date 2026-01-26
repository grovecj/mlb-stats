package com.mlbstats.api.dto;

import java.util.List;

public record BoxScoreDto(
    GameDto game,
    List<GameBattingDto> awayBatting,
    List<GamePitchingDto> awayPitching,
    List<GameBattingDto> homeBatting,
    List<GamePitchingDto> homePitching
) {
}
