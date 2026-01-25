package com.mlbstats.common.security;

import com.mlbstats.domain.user.AppUser;
import com.mlbstats.domain.user.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AppUserPrincipal implements OAuth2User {

    private final OAuth2User oauth2User;
    private final AppUser appUser;

    public AppUserPrincipal(OAuth2User oauth2User, AppUser appUser) {
        this.oauth2User = oauth2User;
        this.appUser = appUser;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        Role role = appUser.getRole();

        // OWNER gets all roles
        if (role == Role.OWNER) {
            authorities.add(new SimpleGrantedAuthority("ROLE_OWNER"));
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        // ADMIN gets ADMIN and USER
        else if (role == Role.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        // USER gets only USER
        else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }

    @Override
    public String getName() {
        return oauth2User.getName();
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public Role getRole() {
        return appUser.getRole();
    }

    public String getEmail() {
        return appUser.getEmail();
    }
}
