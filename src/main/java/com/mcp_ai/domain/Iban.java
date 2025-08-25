package com.mcp_ai.domain;


import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Iban {

    private static final Pattern COUNTRY_CODE = Pattern.compile("[A-Z]{2}");

    // IBAN
    private final String value;

    private Iban(String normalized) {
        this.value = normalized;
    }

    public static Iban of(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("IBAN null olamaz");
        }

        String normalized = raw.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);

        if (normalized.length() < 4) {
            throw new IllegalArgumentException("IBAN en az 4 karakter olmalı (ülke+check)");
        }
        String country = normalized.substring(0, 2);
        if (!COUNTRY_CODE.matcher(country).matches()) {
            throw new IllegalArgumentException("İlk 2 karakter ülke kodu (A-Z) olmalı");
        }

        char c2 = normalized.charAt(2);
        char c3 = normalized.charAt(3);
        if (!Character.isDigit(c2) || !Character.isDigit(c3)) {
            throw new IllegalArgumentException("3-4. karakterler kontrol rakamları olmalı");
        }

        return new Iban(normalized);
    }

    public String value() {
        return value;
    }

    public String countryCode() {
        return value.substring(0, 2);
    }

    public String checkDigits() {
        return value.substring(2, 4);
    }

    public String bban() {
        return value.substring(4);
    }

    public boolean isSwiss() {
        return "CH".equals(countryCode());
    }

    public String masked() {
        int n = value.length();
        int head = Math.min(4, n);
        int tail = Math.min(4, Math.max(0, n - head));
        String prefix = value.substring(0, head);
        String suffix = value.substring(n - tail);
        String middle = "*".repeat(Math.max(0, n - (head + tail)));
        return prefix + middle + suffix;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Iban other)) return false;
        return value.equals(other.value);
    }

    @Override public int hashCode() {
        return Objects.hash(value);
    }

    @Override public String toString() {
        // PII hijyeni: tam IBAN yerine maskeli yaz
        return "Iban[" + masked() + "]";
    }
}