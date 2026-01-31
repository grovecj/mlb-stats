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

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "LEFT JOIN FETCH g.homeProbablePitcher LEFT JOIN FETCH g.awayProbablePitcher " +
           "WHERE g.gameDate BETWEEN :startDate AND :endDate " +
           "ORDER BY g.gameDate, g.scheduledTime")
    List<Game> findByDateRangeWithTeams(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "LEFT JOIN FETCH g.homeProbablePitcher LEFT JOIN FETCH g.awayProbablePitcher " +
           "WHERE (g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId) " +
           "AND g.gameDate BETWEEN :startDate AND :endDate " +
           "ORDER BY g.gameDate, g.scheduledTime")
    List<Game> findByDateRangeAndTeamWithTeams(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("teamId") Long teamId);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "LEFT JOIN FETCH g.homeProbablePitcher LEFT JOIN FETCH g.awayProbablePitcher " +
           "WHERE g.gameDate = :date ORDER BY g.gameDate")
    List<Game> findByDateWithTeams(@Param("date") LocalDate date);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "LEFT JOIN FETCH g.homeProbablePitcher LEFT JOIN FETCH g.awayProbablePitcher " +
           "WHERE g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId ORDER BY g.gameDate DESC")
    List<Game> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "LEFT JOIN FETCH g.homeProbablePitcher LEFT JOIN FETCH g.awayProbablePitcher " +
           "WHERE (g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId) AND g.season = :season ORDER BY g.gameDate")
    List<Game> findByTeamIdAndSeason(@Param("teamId") Long teamId, @Param("season") Integer season);

    Page<Game> findBySeasonOrderByGameDateDesc(Integer season, Pageable pageable);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "WHERE g.season = :season AND g.status = :status ORDER BY g.gameDate")
    List<Game> findBySeasonAndStatus(@Param("season") Integer season, @Param("status") String status);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "LEFT JOIN FETCH g.homeProbablePitcher LEFT JOIN FETCH g.awayProbablePitcher WHERE g.id = :id")
    Optional<Game> findByIdWithTeams(@Param("id") Long id);

    boolean existsByMlbId(Integer mlbId);

    @Query("SELECT DISTINCT g.season FROM Game g ORDER BY g.season DESC")
    List<Integer> findDistinctSeasons();

    long countBySeason(Integer season);

    void deleteBySeason(Integer season);

    Optional<Game> findTopByOrderByGameDateDesc();

    // Calendar aggregation queries
    @Query("SELECT g.gameDate as date, COUNT(g) as count FROM Game g " +
           "WHERE g.gameDate BETWEEN :startDate AND :endDate " +
           "GROUP BY g.gameDate ORDER BY g.gameDate")
    List<Object[]> countGamesByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT g.gameDate as date, " +
           "SUM(CASE WHEN g.homeTeam.id = :teamId THEN 1 ELSE 0 END) as homeGames, " +
           "SUM(CASE WHEN g.awayTeam.id = :teamId THEN 1 ELSE 0 END) as awayGames " +
           "FROM Game g WHERE (g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId) " +
           "AND g.gameDate BETWEEN :startDate AND :endDate " +
           "GROUP BY g.gameDate ORDER BY g.gameDate")
    List<Object[]> countTeamGamesByDateRange(
            @Param("teamId") Long teamId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Dashboard batch queries
    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "LEFT JOIN FETCH g.homeProbablePitcher LEFT JOIN FETCH g.awayProbablePitcher " +
           "WHERE g.gameDate = :date AND (g.homeTeam.id IN :teamIds OR g.awayTeam.id IN :teamIds) " +
           "ORDER BY g.scheduledTime")
    List<Game> findByDateAndTeamIds(@Param("date") LocalDate date, @Param("teamIds") List<Long> teamIds);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "LEFT JOIN FETCH g.homeProbablePitcher LEFT JOIN FETCH g.awayProbablePitcher " +
           "WHERE g.gameDate > :date AND (g.homeTeam.id IN :teamIds OR g.awayTeam.id IN :teamIds) " +
           "ORDER BY g.gameDate, g.scheduledTime")
    List<Game> findUpcomingByTeamIds(@Param("date") LocalDate date, @Param("teamIds") List<Long> teamIds);
}
