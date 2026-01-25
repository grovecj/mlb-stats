package com.mlbstats.api.controller;

import com.mlbstats.common.security.AppUserPrincipal;
import com.mlbstats.domain.user.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }

        response.put("authenticated", true);
        response.put("name", principal.getAttribute("name"));
        response.put("email", principal.getAttribute("email"));
        response.put("picture", principal.getAttribute("picture"));

        // Include role if principal is an AppUserPrincipal
        if (principal instanceof AppUserPrincipal appUserPrincipal) {
            response.put("role", appUserPrincipal.getRole().name());
        } else {
            response.put("role", Role.USER.name());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> authStatus(@AuthenticationPrincipal OAuth2User principal) {
        return ResponseEntity.ok(Map.of("authenticated", principal != null));
    }
}
