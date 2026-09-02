package com.finsense.backend.common.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String email) {
        super("Ja existe uma conta cadastrada com o e-mail: " + email);
    }
}
