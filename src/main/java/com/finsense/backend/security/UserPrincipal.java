package com.finsense.backend.security;

import com.finsense.backend.user.User;

import java.util.UUID;

/**
 * Authenticated principal resolved from the JWT subject claim by
 * {@link JwtAuthenticationFilter} and injected into controllers via
 * {@code @AuthenticationPrincipal}.
 */
public record UserPrincipal(User user) {

    public UUID getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }
}
