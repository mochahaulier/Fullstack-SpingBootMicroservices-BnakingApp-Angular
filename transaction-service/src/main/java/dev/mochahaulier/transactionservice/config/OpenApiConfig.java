package dev.mochahaulier.transactionservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI transactionServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Transaction Service API")
                        .description("This is the REST API for the Transaction Service")
                        .version("v1.0")
                        .license(new License().name("Apache 2.0")));
    }
}