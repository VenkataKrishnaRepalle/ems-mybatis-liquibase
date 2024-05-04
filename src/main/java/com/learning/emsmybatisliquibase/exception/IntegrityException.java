package com.learning.emsmybatisliquibase.exception;

import lombok.Getter;

@Getter
public class IntegrityException extends RuntimeException {
    private final String errorCode;
    private final String dynamicValue;

    public IntegrityException(String errorCode, String dynamicValue) {
        super(String.format("%s : %s", errorCode, dynamicValue));

        this.errorCode = errorCode;
        this.dynamicValue = dynamicValue;
    }
}
