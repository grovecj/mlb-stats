package com.mlbstats.domain.constants;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeagueConstantsRepository extends JpaRepository<LeagueConstants, Long> {

    Optional<LeagueConstants> findBySeason(Integer season);
}
