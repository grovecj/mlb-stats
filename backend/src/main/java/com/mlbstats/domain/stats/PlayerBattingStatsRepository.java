package com.mlbstats.domain.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerBattingStatsRepository extends JpaRepository<PlayerBattingStats, Long> {

    List<PlayerBattingStats> findByPlayerIdAndSeason(Long playerId, Integer season);

    List<PlayerBattingStats> findByPlayerId(Long playerId);

    List<PlayerBattingStats> findByTeamIdAndSeason(Long teamId, Integer season);

    Optional<PlayerBattingStats> findByPlayerIdAndTeamIdAndSeasonAndGameType(
            Long playerId, Long teamId, Integer season, String gameType);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player WHERE pbs.team.id = :teamId AND pbs.season = :season ORDER BY pbs.battingAvg DESC")
    List<PlayerBattingStats> findByTeamIdAndSeasonWithPlayer(@Param("teamId") Long teamId, @Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season ORDER BY pbs.homeRuns DESC")
    List<PlayerBattingStats> findTopHomeRunHitters(@Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season AND pbs.atBats >= :minAtBats ORDER BY pbs.battingAvg DESC")
    List<PlayerBattingStats> findTopBattingAverage(@Param("season") Integer season, @Param("minAtBats") Integer minAtBats);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season ORDER BY pbs.rbi DESC")
    List<PlayerBattingStats> findTopRbi(@Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season ORDER BY pbs.runs DESC")
    List<PlayerBattingStats> findTopRuns(@Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season ORDER BY pbs.hits DESC")
    List<PlayerBattingStats> findTopHits(@Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season ORDER BY pbs.stolenBases DESC")
    List<PlayerBattingStats> findTopStolenBases(@Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season AND pbs.atBats >= :minAtBats ORDER BY pbs.ops DESC")
    List<PlayerBattingStats> findTopOps(@Param("season") Integer season, @Param("minAtBats") Integer minAtBats);

    long countBySeason(Integer season);

    void deleteBySeason(Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player WHERE pbs.player.id IN :playerIds")
    List<PlayerBattingStats> findByPlayerIdIn(@Param("playerIds") List<Long> playerIds);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player WHERE pbs.player.id = :playerId AND pbs.season = :season AND pbs.gameType = 'R'")
    Optional<PlayerBattingStats> findByPlayerIdAndSeasonSingle(@Param("playerId") Long playerId, @Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.player.id IN :playerIds AND pbs.season = :season AND pbs.gameType = 'R'")
    List<PlayerBattingStats> findByPlayerIdsAndSeason(@Param("playerIds") List<Long> playerIds, @Param("season") Integer season);
}
