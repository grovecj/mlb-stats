package com.mlbstats.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

    private String ownerEmail = "";

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public boolean isOwnerEmail(String email) {
        return ownerEmail != null && !ownerEmail.isBlank() && ownerEmail.equalsIgnoreCase(email);
    }
}
