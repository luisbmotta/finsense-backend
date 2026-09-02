package com.finsense.backend.user.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateMonthlyIncomeRequest(
        @NotNull(message = "Renda mensal e obrigatoria")
        @DecimalMin(value = "0.01", message = "Renda mensal deve ser maior que zero")
        BigDecimal monthlyIncome
) {
}
