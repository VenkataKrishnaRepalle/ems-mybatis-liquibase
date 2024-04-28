package com.learning.emsmybatisliquibase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FOUND, reason = "Value already found!")
public class AlreadyFoundException extends RuntimeException {

    public AlreadyFoundException() {
    }

    public AlreadyFoundException(String message) {
        super(message);
    }

    public AlreadyFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyFoundException(Throwable cause) {
        super(cause);
    }

    public AlreadyFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}