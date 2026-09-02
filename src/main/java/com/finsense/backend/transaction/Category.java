package com.finsense.backend.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Values must stay in sync with the Category union type in the frontend
 * (src/app/models/index.ts) - they are serialized exactly as-is over the API.
 */
public enum Category {
    ALIMENTACAO("alimentacao"),
    TRANSPORTE("transporte"),
    LAZER("lazer"),
    SAUDE("saude"),
    OUTROS("outros");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Category fromValue(String value) {
        for (Category category : values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Categoria invalida: " + value);
    }
}
