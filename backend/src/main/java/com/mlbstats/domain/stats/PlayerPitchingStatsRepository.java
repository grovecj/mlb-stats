package com.mlbstats.domain.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerPitchingStatsRepository extends JpaRepository<PlayerPitchingStats, Long> {

    List<PlayerPitchingStats> findByPlayerIdAndSeason(Long playerId, Integer season);

    List<PlayerPitchingStats> findByPlayerId(Long playerId);

    List<PlayerPitchingStats> findByTeamIdAndSeason(Long teamId, Integer season);

    Optional<PlayerPitchingStats> findByPlayerIdAndTeamIdAndSeasonAndGameType(
            Long playerId, Long teamId, Integer season, String gameType);

    @Query("SELECT pps FROM PlayerPitchingStats pps JOIN FETCH pps.player WHERE pps.team.id = :teamId AND pps.season = :season ORDER BY pps.wins DESC")
    List<PlayerPitchingStats> findByTeamIdAndSeasonWithPlayer(@Param("teamId") Long teamId, @Param("season") Integer season);

    @Query("SELECT pps FROM PlayerPitchingStats pps JOIN FETCH pps.player JOIN FETCH pps.team WHERE pps.season = :season ORDER BY pps.wins DESC")
    List<PlayerPitchingStats> findTopWinners(@Param("season") Integer season);

    @Query("SELECT pps FROM PlayerPitchingStats pps JOIN FETCH pps.player JOIN FETCH pps.team WHERE pps.season = :season AND pps.inningsPitched >= :minInnings ORDER BY pps.era ASC")
    List<PlayerPitchingStats> findTopEra(@Param("season") Integer season, @Param("minInnings") BigDecimal minInnings);

    @Query("SELECT pps FROM PlayerPitchingStats pps JOIN FETCH pps.player JOIN FETCH pps.team WHERE pps.season = :season ORDER BY pps.strikeouts DESC")
    List<PlayerPitchingStats> findTopStrikeouts(@Param("season") Integer season);

    @Query("SELECT pps FROM PlayerPitchingStats pps JOIN FETCH pps.player JOIN FETCH pps.team WHERE pps.season = :season ORDER BY pps.saves DESC")
    List<PlayerPitchingStats> findTopSaves(@Param("season") Integer season);

    @Query("SELECT pps FROM PlayerPitchingStats pps JOIN FETCH pps.player JOIN FETCH pps.team WHERE pps.season = :season AND pps.inningsPitched >= :minInnings ORDER BY pps.whip ASC")
    List<PlayerPitchingStats> findTopWhip(@Param("season") Integer season, @Param("minInnings") BigDecimal minInnings);

    long countBySeason(Integer season);

    void deleteBySeason(Integer season);
}
