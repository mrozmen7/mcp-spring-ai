package com.mcp_ai.controller;


import com.mcp_ai.app.IbanValidatorService;
import com.mcp_ai.domain.Iban;
import com.mcp_ai.domain.IbanValidationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tools/iban")
public class IbanController {

    private final IbanValidatorService validator;

    public IbanController(IbanValidatorService validator) {
        this.validator = validator;
    }

    // DTO: request body
    public record IbanRequest(String iban) {}

    @PostMapping("/validate")
    @Operation(
            summary = "Swiss IBAN validation",
            description = "Normalizes the IBAN, applies validation rules, and returns a masked IBAN with PII hygiene"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Doğrulama sonucu",
            content = @Content(schema = @Schema(implementation = IbanValidationResult.class))
    )
    public ResponseEntity<IbanValidationResult> validate(@RequestBody IbanRequest request) {
        try {
            Iban iban = Iban.of(request.iban());
            IbanValidationResult result = validator.validateSwiss(iban);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(IbanValidationResult.fail("??", ex.getMessage(), "****"));
        }
    }
}