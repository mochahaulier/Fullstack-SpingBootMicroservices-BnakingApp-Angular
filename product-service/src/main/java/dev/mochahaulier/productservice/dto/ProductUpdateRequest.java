package dev.mochahaulier.productservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ProductUpdateRequest(
        @NotNull(message = "Product ID is required.") Long productDefinitionKey,
        @NotNull(message = "Rate is required.") @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0.") BigDecimal adjustedRate) {
}
