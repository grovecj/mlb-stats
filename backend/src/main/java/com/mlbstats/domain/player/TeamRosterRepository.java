package com.mlbstats.domain.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRosterRepository extends JpaRepository<TeamRoster, Long> {

    List<TeamRoster> findByTeamIdAndSeason(Long teamId, Integer season);

    List<TeamRoster> findByPlayerIdAndSeason(Long playerId, Integer season);

    @Query("SELECT tr FROM TeamRoster tr JOIN FETCH tr.player WHERE tr.team.id = :teamId AND tr.season = :season")
    List<TeamRoster> findByTeamIdAndSeasonWithPlayer(@Param("teamId") Long teamId, @Param("season") Integer season);

    @Query("SELECT tr FROM TeamRoster tr JOIN FETCH tr.team WHERE tr.player.id = :playerId AND tr.season = :season")
    List<TeamRoster> findByPlayerIdAndSeasonWithTeam(@Param("playerId") Long playerId, @Param("season") Integer season);

    Optional<TeamRoster> findByTeamIdAndPlayerIdAndSeasonAndStartDate(
            Long teamId, Long playerId, Integer season, LocalDate startDate);

    boolean existsByTeamIdAndPlayerIdAndSeason(Long teamId, Long playerId, Integer season);
}
