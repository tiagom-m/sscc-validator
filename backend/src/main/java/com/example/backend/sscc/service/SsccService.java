package com.example.backend.sscc.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.backend.common.CustomException;
import com.example.backend.sscc.dto.SsccResponseDto;
import com.example.backend.sscc.enums.ValidationError;

@Service
public class SsccService {

    private final SsccValidationService validationService;
    private final SsccStorageService storageService;

    public SsccService(SsccValidationService validationService,
            SsccStorageService storageService) {
        this.validationService = validationService;
        this.storageService = storageService;
    }

    public SsccResponseDto save(String sscc, String gs1Prefix) {

        // Parsed and validated SSCC
        String result = validationService.validate(sscc, gs1Prefix);

        // If SSCC is valid - saves it
        boolean added = storageService.add(result);

        if (!added) {
            throw new CustomException(HttpStatus.CONFLICT, ValidationError.DUPLICATE,
                    String.format("SSCC %s is already in the system",
                            result));
        }

        return SsccResponseDto.success(result);
    }


}
