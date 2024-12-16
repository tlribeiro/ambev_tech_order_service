package br.com.tlr.ambev.tech.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ambev Tech - Order Service")
                        .description("Teste para desenvolvedor java")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Thiago Ribeiro")
                                .email("tlribeiro@outlook.com")
                                .url("https://www.linkedin.com/in/tlribeiro/"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
