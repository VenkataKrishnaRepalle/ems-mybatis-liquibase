package com.learning.emsmybatisliquibase.exception.api;

import java.text.MessageFormat;

public class ErrorMessage {
    public static String message(String error, String dynamicValue) {
        return MessageFormat.format(error, dynamicValue);
    }

    public static ErrorResponse errorResponse(String error, String dynamicValue) {
        return ErrorResponse.builder()
                .status(StatusType.ERROR)
                .error(Error.builder()
                        .code(error)
                        .message(dynamicValue)
                        .build())
                .build();
    }

    public static ErrorResponse errorResponse(String error) {
        String[] errorCodeAndMessage = error.split("-", 2);
        String errorCode = errorCodeAndMessage[0];
        String errorMessage = errorCodeAndMessage[1];

        return ErrorResponse.builder()
                .status(StatusType.ERROR)
                .error(Error.builder()
                        .code(errorCode)
                        .message(errorMessage)
                        .build())
                .build();
    }
}
