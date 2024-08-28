package dev.mochahaulier.transactionservice.dto;

import java.time.LocalDate;

import dev.mochahaulier.transactionservice.model.ProductType;

public record ClientProductResponse(
        Long id,
        Long clientId,
        Long productId,
        ProductType type,
        LocalDate lastChargeDate) {
}
