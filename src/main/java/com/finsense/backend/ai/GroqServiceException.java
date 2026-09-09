package com.finsense.backend.ai;

public class GroqServiceException extends RuntimeException {
    public GroqServiceException(String message) {
        super(message);
    }

    public GroqServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
