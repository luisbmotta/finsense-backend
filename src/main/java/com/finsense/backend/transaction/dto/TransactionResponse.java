package com.finsense.backend.transaction.dto;

import com.finsense.backend.transaction.Category;
import com.finsense.backend.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String description,
        BigDecimal amount,
        Category category,
        LocalDate date,
        UUID goalId
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getDate(),
                transaction.getGoal() != null ? transaction.getGoal().getId() : null
        );
    }
}
