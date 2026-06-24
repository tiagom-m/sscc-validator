package com.example.backend.sscc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public record SsccRequestDto(String sscc,
        @JsonInclude(JsonInclude.Include.NON_NULL) String gs1Prefix) {
}