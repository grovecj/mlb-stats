package com.mlbstats.domain.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteTeamRepository extends JpaRepository<UserFavoriteTeam, Long> {

    @Query("SELECT f FROM UserFavoriteTeam f JOIN FETCH f.team WHERE f.user.id = :userId ORDER BY f.team.name")
    List<UserFavoriteTeam> findByUserIdWithTeam(Long userId);

    @Query("SELECT f.team.id FROM UserFavoriteTeam f WHERE f.user.id = :userId ORDER BY f.createdAt")
    List<Long> findTeamIdsByUserId(@Param("userId") Long userId, Pageable pageable);

    long countByUserId(Long userId);

    Optional<UserFavoriteTeam> findByUserIdAndTeamId(Long userId, Long teamId);

    void deleteByUserIdAndTeamId(Long userId, Long teamId);

    boolean existsByUserIdAndTeamId(Long userId, Long teamId);
}
