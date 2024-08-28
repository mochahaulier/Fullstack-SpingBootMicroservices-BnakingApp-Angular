package dev.mochahaulier.productservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductCreationRequest(
        @NotNull(message = "Product key is required.") @Size(min = 6, max = 6, message = "Product key must have 6 characters.") String productDefinitionKey,
        @NotNull(message = "Rate is required.") @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0.") BigDecimal adjustedRate) {
}