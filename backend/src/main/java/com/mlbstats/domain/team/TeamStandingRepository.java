package com.mlbstats.domain.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamStandingRepository extends JpaRepository<TeamStanding, Long> {

    @Query("SELECT ts FROM TeamStanding ts JOIN FETCH ts.team WHERE ts.season = :season ORDER BY ts.team.league, ts.team.division, ts.divisionRank")
    List<TeamStanding> findBySeasonWithTeam(Integer season);

    @Query("SELECT ts FROM TeamStanding ts JOIN FETCH ts.team WHERE ts.team.id = :teamId AND ts.season = :season")
    Optional<TeamStanding> findByTeamIdAndSeason(Long teamId, Integer season);

    Optional<TeamStanding> findByTeamAndSeason(Team team, Integer season);

    @Query("SELECT DISTINCT ts.season FROM TeamStanding ts ORDER BY ts.season DESC")
    List<Integer> findDistinctSeasons();
}
