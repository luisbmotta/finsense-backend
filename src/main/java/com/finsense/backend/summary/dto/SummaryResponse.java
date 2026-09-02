package com.finsense.backend.summary.dto;

import java.math.BigDecimal;
import java.util.Map;

public record SummaryResponse(
        BigDecimal monthlyIncome,
        BigDecimal totalExpenses,
        BigDecimal balance,
        Map<String, BigDecimal> expensesByCategory
) {
}
