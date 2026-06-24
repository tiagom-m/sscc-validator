package com.example.backend.sscc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.backend.common.CustomException;
import com.example.backend.sscc.dto.SsccResponseDto;
import com.example.backend.sscc.enums.ValidationError;

@ExtendWith(MockitoExtension.class)
public class SsccServiceTest {

    @Mock
    private SsccValidationService validationService;

    @Mock
    private SsccStorageService storageService;

    @InjectMocks
    private SsccService ssccService;

    private static final String INPUT_SSCC = "340123450000000017";
    private static final String PARSED_SSCC = "340123450000000017";
    private static final String COMPANY_PREFIX = "4012345";

    @Test
    @DisplayName("Valid new SSCC should be saved and returned")
    void save() {
        when(validationService.validate(INPUT_SSCC, null)).thenReturn(PARSED_SSCC);
        when(storageService.add(PARSED_SSCC)).thenReturn(true);

        SsccResponseDto result = ssccService.saveSscc(INPUT_SSCC, null);

        assertEquals(PARSED_SSCC, result.sscc());
        verify(validationService).validate(INPUT_SSCC, null);
        verify(storageService).add(PARSED_SSCC);
    }

    @Test
    @DisplayName("Valid new SSCC with company prefix should be validated")
    void validWithCompanyPrefix() {
        when(validationService.validate(PARSED_SSCC, COMPANY_PREFIX)).thenReturn(PARSED_SSCC);
        when(storageService.add(PARSED_SSCC)).thenReturn(true);

        SsccResponseDto result = ssccService.saveSscc(PARSED_SSCC, COMPANY_PREFIX);

        assertEquals(PARSED_SSCC, result.sscc());
        verify(validationService).validate(PARSED_SSCC, COMPANY_PREFIX);
    }

    @Test
    @DisplayName("Duplicate valid SSCC should throw CONFLICT")
    void duplicateSscc() {
        when(validationService.validate(INPUT_SSCC, null)).thenReturn(PARSED_SSCC);
        when(storageService.add(PARSED_SSCC)).thenReturn(false);

        CustomException ex = assertThrows(CustomException.class,
                () -> ssccService.saveSscc(INPUT_SSCC, null));

        assertEquals(ValidationError.DUPLICATE, ex.getErrorCode());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(storageService).add(PARSED_SSCC);
    }

    @Test
    @DisplayName("Invalid SSCC should not be saved")
    void invalidSscc() {
        when(validationService.validate(INPUT_SSCC, null))
                .thenThrow(new CustomException(HttpStatus.UNPROCESSABLE_CONTENT,
                        ValidationError.INVALID_CHECK_DIGIT, "Check digit mismatch"));

        assertThrows(CustomException.class,
                () -> ssccService.saveSscc(INPUT_SSCC, null));

        verify(storageService, never()).add(any());
    }

}
