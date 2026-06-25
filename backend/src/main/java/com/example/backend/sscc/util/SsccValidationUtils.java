package com.example.backend.sscc.util;

public class SsccValidationUtils {

    /**
     * Calculates the GS1 mod-10 check digit for a 17-digit data string
     *
     * Algorithm (GS1 General Specifications):
     * 1. Right to Left digit, assign weights alternating by 3 and 1 (3, 1, 3, 1)
     * 2. Multiply each digit by its weight and sum all results
     * 3. Calculate check digit = (10 - (sum mod 10)) mod 10
     */
    public static int calculateCheckDigit(String data) {
        int sum = 0;

        for (int i = data.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(data.charAt(i));
            int weight = ((data.length() - 1 - i) % 2 == 0) ? 3 : 1;
            sum += digit * weight;
        }

        return (10 - (sum % 10)) % 10;
    }
}
