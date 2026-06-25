package com.example.backend.common;

import com.example.backend.sscc.enums.ValidationError;

public record ErrorResponseDto(ValidationError errorCode, String errorMessage) {
}
