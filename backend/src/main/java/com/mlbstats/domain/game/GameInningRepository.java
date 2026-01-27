package com.mlbstats.domain.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameInningRepository extends JpaRepository<GameInning, Long> {

    List<GameInning> findByGameIdOrderByInningNumber(Long gameId);

    void deleteByGameId(Long gameId);
}
