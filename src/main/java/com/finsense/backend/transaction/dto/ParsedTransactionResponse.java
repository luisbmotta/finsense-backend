package com.finsense.backend.transaction.dto;

import com.finsense.backend.ai.dto.TransacaoExtraida;
import com.finsense.backend.transaction.Category;

import java.math.BigDecimal;

public record ParsedTransactionResponse(
        BigDecimal amount,
        Category category,
        String description
) {
    public static ParsedTransactionResponse from(TransacaoExtraida extraida) {
        return new ParsedTransactionResponse(extraida.valor(), extraida.categoria(), extraida.descricao());
    }
}
