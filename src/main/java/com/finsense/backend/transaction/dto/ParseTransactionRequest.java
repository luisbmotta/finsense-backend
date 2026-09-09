package com.finsense.backend.transaction.dto;

import jakarta.validation.constraints.NotBlank;

public record ParseTransactionRequest(

        @NotBlank(message = "Texto e obrigatorio")
        String text
) {
}
