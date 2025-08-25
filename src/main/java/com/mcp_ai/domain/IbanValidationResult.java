package com.mcp_ai.domain;


import lombok.Data;

public record IbanValidationResult(
        boolean valid,
        String country,
        String message,
        String maskedIban // (örn: CH93**********2957)
) {
    public static IbanValidationResult ok(String country, String masked) {
        return new IbanValidationResult(true, country, "Geçerli IBAN", masked);
    }
    public static IbanValidationResult fail(String country, String reason, String masked) {
        return new IbanValidationResult(false, country, reason, masked);
    }
}