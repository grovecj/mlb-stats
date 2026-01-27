package com.mlbstats.domain.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameInningRepository extends JpaRepository<GameInning, Long> {

    List<GameInning> findByGameIdOrderByInningNumber(Long gameId);

    @Query("SELECT gi FROM GameInning gi WHERE gi.game.id = :gameId ORDER BY gi.inningNumber")
    List<GameInning> findByGameId(@Param("gameId") Long gameId);

    void deleteByGameId(Long gameId);
}
