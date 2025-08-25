package com.mcp_ai.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MCP Spring AI – Banking Demo")
                        .description("MCP-based AI tool services for Swiss banking/insurance")
                        .version("v0.1.0")
                        .license(new License().name("Proprietary").url("https://example.ch/legal"))
                        .contact(new Contact().name("Platform Team").email("renas.ozmen@gmail.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Developer Handbook")
                        .url("https://example.ch/dev-handbook"));
    }
}