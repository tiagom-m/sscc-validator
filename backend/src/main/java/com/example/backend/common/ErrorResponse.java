package com.example.backend.common;

import com.example.backend.sscc.enums.ValidationError;

public record ErrorResponse(ValidationError errorCode, String errorMessage) {
}
