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
public interface PlayerGamePitchingRepository extends JpaRepository<PlayerGamePitching, Long> {

    List<PlayerGamePitching> findByGameId(Long gameId);

    List<PlayerGamePitching> findByPlayerId(Long playerId);

    Optional<PlayerGamePitching> findByPlayerIdAndGameId(Long playerId, Long gameId);

    @Query("SELECT pgp FROM PlayerGamePitching pgp JOIN FETCH pgp.player JOIN FETCH pgp.team WHERE pgp.game.id = :gameId ORDER BY pgp.team.id, pgp.isStarter DESC")
    List<PlayerGamePitching> findByGameIdWithPlayer(@Param("gameId") Long gameId);

    @Query("SELECT pgp FROM PlayerGamePitching pgp JOIN FETCH pgp.game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam WHERE pgp.player.id = :playerId ORDER BY g.gameDate DESC")
    Page<PlayerGamePitching> findGameLogByPlayerId(@Param("playerId") Long playerId, Pageable pageable);

    @Query("SELECT pgp FROM PlayerGamePitching pgp JOIN FETCH pgp.game g WHERE pgp.player.id = :playerId AND g.season = :season ORDER BY g.gameDate DESC")
    List<PlayerGamePitching> findByPlayerIdAndSeason(@Param("playerId") Long playerId, @Param("season") Integer season);

    @Query("SELECT pgp FROM PlayerGamePitching pgp " +
           "JOIN FETCH pgp.game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "JOIN FETCH pgp.team " +
           "WHERE pgp.player.id IN :playerIds " +
           "AND pgp.id IN (SELECT MAX(pgp2.id) FROM PlayerGamePitching pgp2 WHERE pgp2.player.id IN :playerIds GROUP BY pgp2.player.id)")
    List<PlayerGamePitching> findLatestByPlayerIds(@Param("playerIds") List<Long> playerIds);
}
