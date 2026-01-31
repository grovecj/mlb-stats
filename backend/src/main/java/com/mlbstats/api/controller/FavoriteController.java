package com.mlbstats.api.controller;

import com.mlbstats.api.dto.FavoritesDashboardDto;
import com.mlbstats.api.dto.PlayerDto;
import com.mlbstats.api.dto.TeamDto;
import com.mlbstats.api.service.FavoriteApiService;
import com.mlbstats.common.security.AppUserPrincipal;
import com.mlbstats.domain.user.AppUser;
import com.mlbstats.domain.user.AppUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "User favorites management")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteApiService favoriteApiService;
    private final AppUserRepository appUserRepository;

    // Dashboard

    @GetMapping("/dashboard")
    @Operation(summary = "Get favorites dashboard", description = "Returns aggregated dashboard data for user's favorite teams and players")
    public ResponseEntity<FavoritesDashboardDto> getDashboard(
            @AuthenticationPrincipal OAuth2User principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(favoriteApiService.getDashboard(userId));
    }

    // Team favorites

    @GetMapping("/teams")
    @Operation(summary = "Get favorite teams", description = "Returns the current user's favorite teams")
    public ResponseEntity<List<TeamDto>> getFavoriteTeams(
            @AuthenticationPrincipal OAuth2User principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(favoriteApiService.getFavoriteTeams(userId));
    }

    @GetMapping("/teams/{teamId}/status")
    @Operation(summary = "Check if team is favorite", description = "Returns whether a team is in user's favorites")
    public ResponseEntity<Map<String, Boolean>> isTeamFavorite(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long teamId) {
        Long userId = getUserId(principal);
        boolean isFavorite = favoriteApiService.isTeamFavorite(userId, teamId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }

    @PostMapping("/teams/{teamId}")
    @Operation(summary = "Add team to favorites", description = "Adds a team to the user's favorites")
    public ResponseEntity<Map<String, String>> addTeamFavorite(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long teamId) {
        Long userId = getUserId(principal);
        favoriteApiService.addTeamFavorite(userId, teamId);
        return ResponseEntity.ok(Map.of("status", "added"));
    }

    @DeleteMapping("/teams/{teamId}")
    @Operation(summary = "Remove team from favorites", description = "Removes a team from the user's favorites")
    public ResponseEntity<Map<String, String>> removeTeamFavorite(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long teamId) {
        Long userId = getUserId(principal);
        favoriteApiService.removeTeamFavorite(userId, teamId);
        return ResponseEntity.ok(Map.of("status", "removed"));
    }

    // Player favorites

    @GetMapping("/players")
    @Operation(summary = "Get favorite players", description = "Returns the current user's favorite players")
    public ResponseEntity<List<PlayerDto>> getFavoritePlayers(
            @AuthenticationPrincipal OAuth2User principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(favoriteApiService.getFavoritePlayers(userId));
    }

    @GetMapping("/players/{playerId}/status")
    @Operation(summary = "Check if player is favorite", description = "Returns whether a player is in user's favorites")
    public ResponseEntity<Map<String, Boolean>> isPlayerFavorite(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long playerId) {
        Long userId = getUserId(principal);
        boolean isFavorite = favoriteApiService.isPlayerFavorite(userId, playerId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }

    @PostMapping("/players/{playerId}")
    @Operation(summary = "Add player to favorites", description = "Adds a player to the user's favorites")
    public ResponseEntity<Map<String, String>> addPlayerFavorite(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long playerId) {
        Long userId = getUserId(principal);
        favoriteApiService.addPlayerFavorite(userId, playerId);
        return ResponseEntity.ok(Map.of("status", "added"));
    }

    @DeleteMapping("/players/{playerId}")
    @Operation(summary = "Remove player from favorites", description = "Removes a player from the user's favorites")
    public ResponseEntity<Map<String, String>> removePlayerFavorite(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long playerId) {
        Long userId = getUserId(principal);
        favoriteApiService.removePlayerFavorite(userId, playerId);
        return ResponseEntity.ok(Map.of("status", "removed"));
    }

    private Long getUserId(OAuth2User principal) {
        // In production, the principal will be AppUserPrincipal (from CustomOAuth2UserService)
        if (principal instanceof AppUserPrincipal appUserPrincipal) {
            return appUserPrincipal.getAppUser().getId();
        }

        // For tests with mock OAuth2User, look up user by email
        String email = principal.getAttribute("email");
        if (email != null) {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found for email: " + email));
            return user.getId();
        }

        throw new IllegalStateException("Cannot determine user ID from principal");
    }
}
