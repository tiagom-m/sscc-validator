package com.example.backend.sscc.dto;

public record SsccResponseDto(
        String sscc) {

    public static SsccResponseDto success(String sscc) {
        return new SsccResponseDto(sscc);
    }

}
