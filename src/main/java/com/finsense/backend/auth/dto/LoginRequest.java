package com.finsense.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "E-mail e obrigatorio")
        String email,

        @NotBlank(message = "Senha e obrigatoria")
        String password
) {
}
