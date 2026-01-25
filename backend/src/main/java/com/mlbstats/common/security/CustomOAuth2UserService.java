package com.mlbstats.common.security;

import com.mlbstats.common.config.AuthProperties;
import com.mlbstats.domain.user.AppUser;
import com.mlbstats.domain.user.AppUserRepository;
import com.mlbstats.domain.user.Role;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AppUserRepository appUserRepository;
    private final AuthProperties authProperties;

    public CustomOAuth2UserService(AppUserRepository appUserRepository, AuthProperties authProperties) {
        this.appUserRepository = appUserRepository;
        this.authProperties = authProperties;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        AppUser appUser = appUserRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, name, picture))
                .orElseGet(() -> createNewUser(email, name, picture));

        return new AppUserPrincipal(oauth2User, appUser);
    }

    private AppUser updateExistingUser(AppUser user, String name, String picture) {
        user.setName(name);
        user.setPictureUrl(picture);
        user.recordLogin();
        return appUserRepository.save(user);
    }

    private AppUser createNewUser(String email, String name, String picture) {
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setName(name);
        user.setPictureUrl(picture);

        // Set OWNER role if email matches the configured owner email
        if (authProperties.isOwnerEmail(email)) {
            user.setRole(Role.OWNER);
        } else {
            user.setRole(Role.USER);
        }

        return appUserRepository.save(user);
    }
}
