package com.finsense.backend.auth.dto;

import com.finsense.backend.user.User;

import java.math.BigDecimal;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        BigDecimal monthlyIncome
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getMonthlyIncome());
    }
}
