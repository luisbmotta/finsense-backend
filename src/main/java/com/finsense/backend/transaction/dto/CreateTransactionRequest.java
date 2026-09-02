package com.finsense.backend.transaction.dto;

import com.finsense.backend.transaction.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(

        @NotNull(message = "Descricao e obrigatoria")
        @Size(min = 1, max = 200, message = "Descricao deve ter entre 1 e 200 caracteres")
        String description,

        @NotNull(message = "Valor e obrigatorio")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal amount,

        @NotNull(message = "Categoria e obrigatoria")
        Category category,

        @NotNull(message = "Data e obrigatoria")
        LocalDate date
) {
}
