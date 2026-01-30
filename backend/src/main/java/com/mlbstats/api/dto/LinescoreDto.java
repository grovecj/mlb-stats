package com.mlbstats.api.dto;

import com.mlbstats.domain.game.Game;
import com.mlbstats.domain.game.GameInning;

import java.util.List;

/**
 * DTO for game linescore data including inning-by-inning scoring.
 */
public record LinescoreDto(
        Long gameId,
        List<InningDto> innings,
        TeamTotalsDto awayTotals,
        TeamTotalsDto homeTotals,
        LiveStateDto liveState
) {

    public record InningDto(
            Integer inning,
            Integer awayRuns,
            Integer homeRuns
    ) {
        public static InningDto fromEntity(GameInning entity) {
            return new InningDto(
                    entity.getInningNumber(),
                    entity.getAwayRuns(),
                    entity.getHomeRuns()
            );
        }
    }

    public record TeamTotalsDto(
            Integer runs,
            Integer hits,
            Integer errors
    ) {}

    public record LiveStateDto(
            Integer currentInning,
            String inningHalf,
            Integer outs,
            Integer balls,
            Integer strikes,
            Boolean runnerOnFirst,
            Boolean runnerOnSecond,
            Boolean runnerOnThird,
            Boolean isLive
    ) {
        public static LiveStateDto fromGame(Game game) {
            boolean isLive = game.getStatus() != null &&
                    (game.getStatus().equals("In Progress") ||
                     game.getStatus().contains("Top") ||
                     game.getStatus().contains("Bottom") ||
                     game.getStatus().contains("Middle") ||
                     game.getStatus().contains("End"));

            return new LiveStateDto(
                    game.getCurrentInning(),
                    game.getInningHalf(),
                    game.getOuts(),
                    game.getBalls(),
                    game.getStrikes(),
                    game.getRunnerOnFirst(),
                    game.getRunnerOnSecond(),
                    game.getRunnerOnThird(),
                    isLive
            );
        }
    }

    public static LinescoreDto fromEntities(Game game, List<GameInning> innings) {
        List<InningDto> inningDtos = innings.stream()
                .map(InningDto::fromEntity)
                .toList();

        TeamTotalsDto awayTotals = new TeamTotalsDto(
                game.getAwayScore(),
                game.getAwayHits(),
                game.getAwayErrors()
        );

        TeamTotalsDto homeTotals = new TeamTotalsDto(
                game.getHomeScore(),
                game.getHomeHits(),
                game.getHomeErrors()
        );

        LiveStateDto liveState = LiveStateDto.fromGame(game);

        return new LinescoreDto(
                game.getId(),
                inningDtos,
                awayTotals,
                homeTotals,
                liveState
        );
    }
}
