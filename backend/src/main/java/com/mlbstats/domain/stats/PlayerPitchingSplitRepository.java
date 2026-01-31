package com.mlbstats.domain.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerPitchingSplitRepository extends JpaRepository<PlayerPitchingSplit, Long> {

    List<PlayerPitchingSplit> findByPlayerIdAndSeason(Long playerId, Integer season);

    List<PlayerPitchingSplit> findByPlayerIdAndSeasonAndSplitType(Long playerId, Integer season, SplitType splitType);

    Optional<PlayerPitchingSplit> findByPlayerIdAndSeasonAndSplitTypeAndTeamId(
            Long playerId, Integer season, SplitType splitType, Long teamId);

    @Query("SELECT pps FROM PlayerPitchingSplit pps JOIN FETCH pps.player WHERE pps.player.id = :playerId AND pps.season = :season ORDER BY pps.splitType")
    List<PlayerPitchingSplit> findByPlayerIdAndSeasonWithPlayer(@Param("playerId") Long playerId, @Param("season") Integer season);

    @Query("SELECT pps FROM PlayerPitchingSplit pps JOIN FETCH pps.player WHERE pps.player.id IN :playerIds AND pps.season = :season")
    List<PlayerPitchingSplit> findByPlayerIdsAndSeason(@Param("playerIds") List<Long> playerIds, @Param("season") Integer season);

    void deleteByPlayerIdAndSeason(Long playerId, Integer season);

    long countBySeason(Integer season);
}
