package com.example.backend.common;

import org.springframework.http.HttpStatus;

import com.example.backend.sscc.enums.ValidationError;

public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final ValidationError errorCode;

    public CustomException(HttpStatus status, ValidationError errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ValidationError getErrorCode() {
        return errorCode;
    }

}
