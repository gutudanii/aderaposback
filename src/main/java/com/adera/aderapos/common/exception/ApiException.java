package com.adera.aderapos.common.exception;

/**
 * Custom exception class for API-related errors.
 */
public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

