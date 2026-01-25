package com.mlbstats.domain.player;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByMlbId(Integer mlbId);

    List<Player> findByPosition(String position);

    List<Player> findByPositionType(String positionType);

    Page<Player> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Player p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Player> searchByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Player p WHERE p.active = true AND " +
           "(LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.position) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Player> searchPlayers(@Param("search") String search, Pageable pageable);

    boolean existsByMlbId(Integer mlbId);

    @Query("SELECT p FROM Player p WHERE p.bats IS NULL OR p.height IS NULL OR p.birthDate IS NULL")
    List<Player> findIncomplete();
}
