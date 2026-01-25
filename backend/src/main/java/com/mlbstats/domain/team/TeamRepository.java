package com.mlbstats.domain.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByMlbId(Integer mlbId);

    List<Team> findByLeague(String league);

    List<Team> findByDivision(String division);

    List<Team> findByLeagueAndDivision(String league, String division);

    @Query("SELECT t FROM Team t ORDER BY t.league, t.division, t.name")
    List<Team> findAllOrderByLeagueAndDivision();

    boolean existsByMlbId(Integer mlbId);
}
