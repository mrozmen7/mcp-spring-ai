package com.mcp_ai.app;

import com.mcp_ai.domain.Iban;
import com.mcp_ai.domain.IbanValidationResult;
import org.springframework.stereotype.Service;

@Service
public class IbanValidatorService {


    public IbanValidationResult validateSwiss(Iban iban) {
        final String v = iban.value();
        final String masked = iban.masked();

        // 1) Ülke kontrolü
        if (!iban.isSwiss()) {
            return IbanValidationResult.fail(countryOf(v), "Sadece CH (İsviçre) IBAN doğrulanır", masked);
        }

        // 2) Uzunluk kontrolü (CH = 21)
        if (v.length() != 21) {
            return IbanValidationResult.fail("CH", "CH IBAN uzunluğu 21 olmalı", masked);
        }

        // 3) Karakter kümesi kontrolü (CH sonrası yalnızca rakam)
        // v: CCdd + BBAN => CH + 2 check + 17 rakam = toplam 21
        // index 2..20 (dahil) rakam olmalı
        final String afterCountryAndCheck = v.substring(2); // "check+BBAN"
        if (!afterCountryAndCheck.matches("\\d+")) {
            return IbanValidationResult.fail("CH", "CH IBAN ‘CH’ sonrası yalnızca rakam içermeli", masked);
        }

        // 4) Mod-97 kontrolü
        if (!mod97Ok(v)) {
            return IbanValidationResult.fail("CH", "Mod-97 kontrolü başarısız", masked);
        }

        // Başarılı
        return IbanValidationResult.ok("CH", masked);
    }

    /** ISO 13616: IBAN mod-97 kontrolü: rearrange + A=10..Z=35 dönüşümü, kalan 1 olmalı */
    private boolean mod97Ok(String iban) {
        // İlk 4 karakteri sona al
        String rearranged = iban.substring(4) + iban.substring(0, 4);

        // Harfleri sayıya çevir (A=10..Z=35)
        StringBuilder digits = new StringBuilder(rearranged.length() * 2);
        for (char c : rearranged.toCharArray()) {
            if (Character.isDigit(c)) {
                digits.append(c);
            } else {
                int val = c - 'A' + 10; // A=10, B=11, ... Z=35
                digits.append(val);
            }
        }

        // Büyük sayıyı parça parça 97'ye böl
        return mod97(digits.toString()) == 1;
    }

    /** Büyük ondalık string üzerinde mod 97 (overflow yaşamamak için akümülatif) */
    private int mod97(String num) {
        int rem = 0;
        for (int i = 0; i < num.length(); i++) {
            int d = num.charAt(i) - '0';
            rem = (rem * 10 + d) % 97;
        }
        return rem;
    }

    /** En azından 2 harf mevcutsa ülke; değilse "??" */
    private String countryOf(String v) {
        return v.length() >= 2 ? v.substring(0, 2) : "??";
    }
}