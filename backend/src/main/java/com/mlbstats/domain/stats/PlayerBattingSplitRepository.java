package com.mlbstats.domain.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerBattingSplitRepository extends JpaRepository<PlayerBattingSplit, Long> {

    List<PlayerBattingSplit> findByPlayerIdAndSeason(Long playerId, Integer season);

    List<PlayerBattingSplit> findByPlayerIdAndSeasonAndSplitType(Long playerId, Integer season, SplitType splitType);

    Optional<PlayerBattingSplit> findByPlayerIdAndSeasonAndSplitTypeAndTeamId(
            Long playerId, Integer season, SplitType splitType, Long teamId);

    @Query("SELECT pbs FROM PlayerBattingSplit pbs JOIN FETCH pbs.player WHERE pbs.player.id = :playerId AND pbs.season = :season ORDER BY pbs.splitType")
    List<PlayerBattingSplit> findByPlayerIdAndSeasonWithPlayer(@Param("playerId") Long playerId, @Param("season") Integer season);

    @Query("SELECT pbs FROM PlayerBattingSplit pbs JOIN FETCH pbs.player WHERE pbs.player.id IN :playerIds AND pbs.season = :season")
    List<PlayerBattingSplit> findByPlayerIdsAndSeason(@Param("playerIds") List<Long> playerIds, @Param("season") Integer season);

    void deleteByPlayerIdAndSeason(Long playerId, Integer season);

    long countBySeason(Integer season);
}
