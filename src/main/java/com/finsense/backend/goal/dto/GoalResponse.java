package com.finsense.backend.goal.dto;

import com.finsense.backend.goal.Goal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GoalResponse(
        UUID id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        String emoji,
        LocalDate deadline,
        String color
) {
    public static GoalResponse from(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getEmoji(),
                goal.getDeadline(),
                goal.getColor()
        );
    }
}
