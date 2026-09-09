package com.finsense.backend.common.exception;

public class TransactionLinkedToGoalException extends RuntimeException {
    public TransactionLinkedToGoalException(String message) {
        super(message);
    }
}
