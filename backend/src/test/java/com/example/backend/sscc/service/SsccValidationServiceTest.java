package com.example.backend.sscc.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.backend.common.CustomException;
import com.example.backend.sscc.enums.ValidationError;
import com.example.backend.sscc.util.SsccValidationUtils;

public class SsccValidationServiceTest {

    private SsccValidationService service;

    @BeforeEach
    void setUp() {
        service = new SsccValidationService();
    }

    @Test
    @DisplayName("Whitespace must be removed")
    void whitespaceRemoval() {
        String result = service.validate("  340123450000000017  ", null);
        assertEquals("340123450000000017", result);
    }

    @Test
    @DisplayName("Dashes, spaces, and parentheses must be removed")
    void specialCharacterRemoval() {
        String result = service.validate("34-0123-4500-0000-0017", null);
        assertEquals("340123450000000017", result);
    }

    @Test
    @DisplayName("Leading 00 CCSS identifier should be removed")
    void ccssIdentifierRemoval() {
        String result = service.validate("00340123450000000017", null);
        assertEquals("340123450000000017", result);
    }

    @Test
    @DisplayName("Leading (00) CCSS identifier should be removed")
    void ccssIdentifierWhitespaceRemoval() {
        String result = service.validate("(00) 340123450000000017", null);
        assertEquals("340123450000000017", result);
    }

    @Test
    @DisplayName("Valid SSCC with matching GS1 prefix should pass")
    void validPrefix() {
        String result = service.validate("340123450000000017", "4012345");
        assertEquals("340123450000000017", result);
    }

    @Test
    @DisplayName("Null GS1 prefix should skip prefix check")
    void nullPrefixSkipped() {
        assertDoesNotThrow(() -> service.validate("340123450000000017", null));
    }

    @Test
    @DisplayName("Check digit calculation should follow GS1 mod-10 algorithm")
    void checkDigitCalculation() {
        assertEquals(7, SsccValidationUtils.calculateCheckDigit("34012345000000001"));
        assertEquals(0, SsccValidationUtils.calculateCheckDigit("37613032110910342"));
    }

    @ParameterizedTest
    @DisplayName("Null and blank inputs should throw EMPTY_INPUT")
    @NullAndEmptySource
    @ValueSource(strings = { "   ", "\t", "\n" })
    void emptyInput(String sscc) {
        CustomException ex = assertThrows(CustomException.class,
                () -> service.validate(sscc, null));
        assertEquals(ValidationError.EMPTY_INPUT, ex.getErrorCode());
    }

    @ParameterizedTest
    @DisplayName("Wrong length should throw INVALID_LENGTH")
    @ValueSource(strings = { "1234567890", "12345678901234567", "1234567890123456789" })
    void invalidLength(String sscc) {
        CustomException ex = assertThrows(CustomException.class,
                () -> service.validate(sscc, null));
        assertEquals(ValidationError.INVALID_LENGTH, ex.getErrorCode());
    }

    @ParameterizedTest
    @DisplayName("Non-numeric characters should throw NON_NUMERIC")
    @ValueSource(strings = { "12345678901234567A", "ABCDEFGHIJKLMNOPQR" })
    void nonNumericValues(String sscc) {
        CustomException ex = assertThrows(CustomException.class,
                () -> service.validate(sscc, null));
        assertEquals(ValidationError.NON_NUMERIC, ex.getErrorCode());
    }

    @Test
    @DisplayName("Wrong check digit should throw INVALID_CHECK_DIGIT")
    void invalidCheckDigit() {
        CustomException ex = assertThrows(CustomException.class,
                () -> service.validate("340123450000000019", null));
        assertEquals(ValidationError.INVALID_CHECK_DIGIT, ex.getErrorCode());
        assertTrue(ex.getMessage().contains("expected 7"));
        assertTrue(ex.getMessage().contains("found 9"));
    }

    @Test
    @DisplayName("Mismatched GS1 prefix should throw PREFIX_MISMATCH")
    void invalidPrefix() {
        CustomException ex = assertThrows(CustomException.class,
                () -> service.validate("340123450000000017", "9999999"));
        assertEquals(ValidationError.PREFIX_MISMATCH, ex.getErrorCode());
    }

    @ParameterizedTest
    @DisplayName("Valid SSCCs should pass validation")
    @ValueSource(strings = {
            "340123450000000017",
            "000000000000000000",
            "376130321109103420"
    })
    void validate(String sscc) {
        String result = service.validate(sscc, null);
        assertEquals(sscc, result);
    }

}
