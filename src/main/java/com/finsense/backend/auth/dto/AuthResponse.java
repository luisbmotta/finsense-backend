package com.finsense.backend.auth.dto;

import com.finsense.backend.user.dto.UserResponse;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
