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

    // Advanced Stats Leaderboards
    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season AND pbs.war IS NOT NULL ORDER BY pbs.war DESC")
    List<PlayerBattingStats> findTopWar(@Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season AND pbs.plateAppearances >= :minPa AND pbs.woba IS NOT NULL ORDER BY pbs.woba DESC")
    List<PlayerBattingStats> findTopWoba(@Param("season") Integer season, @Param("minPa") Integer minPa);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season AND pbs.plateAppearances >= :minPa AND pbs.wrcPlus IS NOT NULL ORDER BY pbs.wrcPlus DESC")
    List<PlayerBattingStats> findTopWrcPlus(@Param("season") Integer season, @Param("minPa") Integer minPa);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season AND pbs.plateAppearances >= :minPa AND pbs.avgExitVelocity IS NOT NULL ORDER BY pbs.avgExitVelocity DESC")
    List<PlayerBattingStats> findTopExitVelocity(@Param("season") Integer season, @Param("minPa") Integer minPa);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season AND pbs.plateAppearances >= :minPa AND pbs.barrelPct IS NOT NULL ORDER BY pbs.barrelPct DESC")
    List<PlayerBattingStats> findTopBarrelPct(@Param("season") Integer season, @Param("minPa") Integer minPa);

    // gWAR Leaderboards
    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season AND pbs.gameType = 'R' AND pbs.gwar IS NOT NULL ORDER BY pbs.gwar DESC")
    List<PlayerBattingStats> findTopGwar(@Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season AND pbs.gameType = 'R' AND pbs.oaa IS NOT NULL ORDER BY pbs.oaa DESC")
    List<PlayerBattingStats> findTopOaa(@Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingStats pbs JOIN FETCH pbs.player JOIN FETCH pbs.team WHERE pbs.season = :season AND pbs.gameType = 'R'")
    List<PlayerBattingStats> findBySeasonRegularSeason(@Param("season") Integer season);
}
