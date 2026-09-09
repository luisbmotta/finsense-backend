package com.finsense.backend.ai.dto;

import com.finsense.backend.transaction.Category;

import java.math.BigDecimal;

public record TransacaoExtraida(BigDecimal valor, Category categoria, String descricao) {
}
