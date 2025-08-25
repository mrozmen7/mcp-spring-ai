package com.mcp_ai.mcp;

import com.mcp_ai.app.IbanValidatorService;
import com.mcp_ai.domain.Iban;
import com.mcp_ai.domain.IbanValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class IbanMcpTools {

    private static final Logger log = LoggerFactory.getLogger(IbanMcpTools.class);
    private final IbanValidatorService validator;

    public IbanMcpTools(IbanValidatorService validator) {
        this.validator = validator;
    }

    /**
     * LLM (Claude/ChatGPT) tarafından çağrılacak MCP Tool.
     * Amaç: İsviçre IBAN'ını (CH) doğrulamak ve PII hijyeniyle sonucu döndürmek.
     *
     * name:     LLM tarafında "available tools" listesinde görünecek sabit addır.
     * desc:     Modelin tool'u doğru anlaması için net ve kısa açıklama.
     * param:    Ham IBAN string (kullanıcıdan geldiği gibi).
     * dönüş:    IbanValidationResult (valid/country/message/maskedIban)
     */
    @Tool(
            name = "chIban.validate",
            description = "İsviçre (CH) IBAN doğrular; masked IBAN ile güvenli sonuç döndürür"
    )
    public IbanValidationResult validate(
            @ToolParam(description = "Doğrulanacak IBAN (ham girdi)") String rawIban) {

        // Güvenlik: Ham IBAN'ı asla loglama
        // Önce VO'ya dönüştür (normalize olur), maske üret
        try {
            Iban iban = Iban.of(rawIban);
            IbanValidationResult result = validator.validateSwiss(iban);

            // Sadece masked değeri loglayın (PII hijyeni)
            log.info("Tool chIban.validate called → maskedIban={}, valid={}",
                    iban.masked(), result.valid());

            return result;

        } catch (IllegalArgumentException ex) {
            // VO oluşturma sırasında minimum biçimsel kontrollerde hata
            // LLM'e sade ve güvenli (PII içermeyen) açıklama dön.
            // Ülke bilgisi ham girdiden türetilemeyecek durumdaysa "??"
            String country = safeCountry(rawIban);
            String masked = safeMasked(rawIban);
            log.warn("Tool chIban.validate bad input → maskedIban={}, reason={}", masked, ex.getMessage());
            return IbanValidationResult.fail(country, ex.getMessage(), masked);
        } catch (Exception ex) {
            // Beklenmeyen durumlar için genel, güvenli hata
            String masked = safeMasked(rawIban);
            log.error("Tool chIban.validate unexpected error → maskedIban={}, err={}",
                    masked, ex.toString());
            return IbanValidationResult.fail("CH", "İşlem sırasında beklenmeyen bir hata oluştu", masked);
        }
    }

    /** Ham girdiden güvenli şekilde (PII sızdırmadan) ülke tahmini */
    private String safeCountry(String raw) {
        if (raw == null || raw.length() < 2) return "??";
        String cc = raw.substring(0, 2).toUpperCase();
        return cc.matches("[A-Z]{2}") ? cc : "??";
    }

    /** Ham girdiyi maskeler (log/cevap güvenliği için) */
    private String safeMasked(String raw) {
        if (raw == null) return "****";
        String v = raw.replaceAll("\\s+", "").toUpperCase();
        int n = v.length();
        if (n <= 4) return v + "****";
        String prefix = v.substring(0, Math.min(4, n));
        String suffix = n > 8 ? v.substring(n - 4) : "";
        String middle = "*".repeat(Math.max(0, n - (prefix.length() + suffix.length())));
        return prefix + middle + suffix;
    }
}