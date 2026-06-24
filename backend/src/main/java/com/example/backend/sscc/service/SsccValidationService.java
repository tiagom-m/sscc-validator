package com.example.backend.sscc.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.backend.common.CustomException;
import com.example.backend.sscc.enums.ValidationError;
import com.example.backend.sscc.util.SsccValidationUtils;

@Service
public class SsccValidationService {

    private static final int SSCC_LENGTH = 18;

    public String validate(String sscc, String gs1Prefix) {

        if (sscc == null || sscc.isBlank()) {
            throw new CustomException(HttpStatus.UNPROCESSABLE_CONTENT, ValidationError.EMPTY_INPUT, "SSCC is empty");
        }

        // Remove -, spaces, and () from sscc string
        String parsed = sscc.replaceAll("[\\s\\-()]", "");
        // Removes the initial '00' if the resulting string is exactly 18 digits long
        parsed = parsed.replaceAll("^00(?=\\d{18}$)", "");

        if (parsed.length() != SSCC_LENGTH) {
            throw new CustomException(HttpStatus.UNPROCESSABLE_CONTENT, ValidationError.INVALID_LENGTH,
                    String.format("SSCC must be exactly %d digits, but was %d", SSCC_LENGTH, parsed.length()));
        }

        if (!parsed.matches("\\d+")) {
            throw new CustomException(HttpStatus.UNPROCESSABLE_CONTENT, ValidationError.NON_NUMERIC,
                    "SSCC must contain only digits");
        }

        // Calculate the expected check digit based on the SSCC without the check digit
        // Integrity check to prevent errors related to the SSCC (mistyping)
        int expectedCheckDigit = SsccValidationUtils.calculateCheckDigit(parsed.substring(0, 17));

        // Check digit is in the last position (index 17)
        int checkDigit = Character.getNumericValue(parsed.charAt(17));

        if (expectedCheckDigit != checkDigit) {
            throw new CustomException(HttpStatus.UNPROCESSABLE_CONTENT, ValidationError.INVALID_CHECK_DIGIT,
                    String.format("Check digit mismatch: expected %d, but found %d", expectedCheckDigit,
                            checkDigit));
        }

        // Optional verification: check if the GS1 company prefix matches the provided
        // one (if given)
        if (gs1Prefix != null && !gs1Prefix.isBlank()) {

            // Prefix starts at position 1, immediately after the extension digit
            // Retrieves the GS1 company prefix from the SSCC using the length from the
            // provided prefix (between 7-10 digits)
            String actualPrefix = parsed.substring(1, 1 + gs1Prefix.length());

            if (!actualPrefix.equals(gs1Prefix)) {
                throw new CustomException(HttpStatus.UNPROCESSABLE_CONTENT, ValidationError.PREFIX_MISMATCH,
                        String.format("GS1 prefix mismatch: expected %s, but found %s",
                                gs1Prefix, actualPrefix));
            }
        }

        return parsed;

    }
}
