package dev.mochahaulier.productservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Operation {
    NEW("N"),
    UPDATE("U"),
    INVALID("");

    private final String code;

    Operation(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Operation fromCode(String code) {
        for (Operation operation : values()) {
            if (operation.getCode().equals(code)) {
                return operation;
            }
        }
        return INVALID; // Return INVALID for unrecognized operations
    }
}
