package com.mlbstats.domain.stats;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerGameBattingRepository extends JpaRepository<PlayerGameBatting, Long> {

    List<PlayerGameBatting> findByGameId(Long gameId);

    List<PlayerGameBatting> findByPlayerId(Long playerId);

    Optional<PlayerGameBatting> findByPlayerIdAndGameId(Long playerId, Long gameId);

    @Query("SELECT pgb FROM PlayerGameBatting pgb JOIN FETCH pgb.player JOIN FETCH pgb.team WHERE pgb.game.id = :gameId ORDER BY pgb.team.id, pgb.battingOrder")
    List<PlayerGameBatting> findByGameIdWithPlayer(@Param("gameId") Long gameId);

    @Query("SELECT pgb FROM PlayerGameBatting pgb JOIN FETCH pgb.game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam WHERE pgb.player.id = :playerId ORDER BY g.gameDate DESC")
    Page<PlayerGameBatting> findGameLogByPlayerId(@Param("playerId") Long playerId, Pageable pageable);

    @Query("SELECT pgb FROM PlayerGameBatting pgb JOIN FETCH pgb.game g WHERE pgb.player.id = :playerId AND g.season = :season ORDER BY g.gameDate DESC")
    List<PlayerGameBatting> findByPlayerIdAndSeason(@Param("playerId") Long playerId, @Param("season") Integer season);

    @Query("SELECT pgb FROM PlayerGameBatting pgb " +
           "JOIN FETCH pgb.game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "JOIN FETCH pgb.team " +
           "WHERE pgb.player.id IN :playerIds " +
           "AND pgb.id IN (SELECT MAX(pgb2.id) FROM PlayerGameBatting pgb2 WHERE pgb2.player.id IN :playerIds GROUP BY pgb2.player.id)")
    List<PlayerGameBatting> findLatestByPlayerIds(@Param("playerIds") List<Long> playerIds);
}
