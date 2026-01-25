package com.mlbstats.api.controller;

import com.mlbstats.domain.user.AppUser;
import com.mlbstats.domain.user.AppUserRepository;
import com.mlbstats.domain.user.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Administrative endpoints")
@PreAuthorize("hasRole('OWNER')")
public class AdminController {

    private final AppUserRepository appUserRepository;

    public AdminController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/users")
    @Operation(summary = "List all users", description = "Returns a list of all users sorted by last login")
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<UserResponse> users = appUserRepository.findAllByOrderByLastLoginAtDesc()
                .stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Update user role", description = "Updates the role of a user")
    public ResponseEntity<Map<String, String>> updateUserRole(
            @PathVariable Long id,
            @RequestBody RoleUpdateRequest request) {

        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Prevent changing OWNER role
        if (user.getRole() == Role.OWNER) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cannot change the role of the owner"));
        }

        // Prevent promoting to OWNER
        if (request.role() == Role.OWNER) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cannot promote users to owner"));
        }

        user.setRole(request.role());
        appUserRepository.save(user);

        return ResponseEntity.ok(Map.of("status", "updated", "role", user.getRole().name()));
    }

    public record UserResponse(
            Long id,
            String email,
            String name,
            String pictureUrl,
            String role,
            String lastLoginAt
    ) {
        public static UserResponse from(AppUser user) {
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPictureUrl(),
                    user.getRole().name(),
                    user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null
            );
        }
    }

    public record RoleUpdateRequest(Role role) {}
}
