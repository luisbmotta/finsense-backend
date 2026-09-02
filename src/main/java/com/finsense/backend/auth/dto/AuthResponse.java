package com.finsense.backend.auth.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
