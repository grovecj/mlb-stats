package com.mlbstats.domain.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByMlbId(Integer mlbId);

    List<Game> findBySeason(Integer season);

    List<Game> findByGameDate(LocalDate gameDate);

    @Query("SELECT g FROM Game g WHERE g.gameDate BETWEEN :startDate AND :endDate ORDER BY g.gameDate")
    List<Game> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam WHERE g.gameDate = :date ORDER BY g.gameDate")
    List<Game> findByDateWithTeams(@Param("date") LocalDate date);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "WHERE g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId ORDER BY g.gameDate DESC")
    List<Game> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "WHERE (g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId) AND g.season = :season ORDER BY g.gameDate")
    List<Game> findByTeamIdAndSeason(@Param("teamId") Long teamId, @Param("season") Integer season);

    Page<Game> findBySeasonOrderByGameDateDesc(Integer season, Pageable pageable);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "WHERE g.season = :season AND g.status = :status ORDER BY g.gameDate")
    List<Game> findBySeasonAndStatus(@Param("season") Integer season, @Param("status") String status);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam WHERE g.id = :id")
    Optional<Game> findByIdWithTeams(@Param("id") Long id);

    boolean existsByMlbId(Integer mlbId);

    @Query("SELECT DISTINCT g.season FROM Game g ORDER BY g.season DESC")
    List<Integer> findDistinctSeasons();

    long countBySeason(Integer season);

    void deleteBySeason(Integer season);
}
