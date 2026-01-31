package com.mlbstats.domain.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoritePlayerRepository extends JpaRepository<UserFavoritePlayer, Long> {

    @Query("SELECT f FROM UserFavoritePlayer f JOIN FETCH f.player WHERE f.user.id = :userId ORDER BY f.player.fullName")
    List<UserFavoritePlayer> findByUserIdWithPlayer(Long userId);

    @Query("SELECT f.player.id FROM UserFavoritePlayer f WHERE f.user.id = :userId ORDER BY f.createdAt")
    List<Long> findPlayerIdsByUserId(@Param("userId") Long userId, Pageable pageable);

    long countByUserId(Long userId);

    Optional<UserFavoritePlayer> findByUserIdAndPlayerId(Long userId, Long playerId);

    void deleteByUserIdAndPlayerId(Long userId, Long playerId);

    boolean existsByUserIdAndPlayerId(Long userId, Long playerId);
}
