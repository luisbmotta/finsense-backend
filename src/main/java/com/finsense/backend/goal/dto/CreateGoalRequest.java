package com.finsense.backend.goal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGoalRequest(

        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres")
        String name,

        @NotNull(message = "Valor alvo e obrigatorio")
        @DecimalMin(value = "0.01", message = "Valor alvo deve ser maior que zero")
        BigDecimal targetAmount,

        @NotBlank(message = "Emoji e obrigatorio")
        @Size(max = 16, message = "Emoji deve ter no maximo 16 caracteres")
        String emoji,

        @NotNull(message = "Prazo e obrigatorio")
        @FutureOrPresent(message = "Prazo deve ser hoje ou no futuro")
        LocalDate deadline,

        @NotBlank(message = "Cor e obrigatoria")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato hexadecimal, ex: #1565C0")
        String color
) {
}
