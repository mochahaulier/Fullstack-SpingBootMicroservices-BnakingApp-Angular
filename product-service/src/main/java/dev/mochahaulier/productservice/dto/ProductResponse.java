package dev.mochahaulier.productservice.dto;

import java.math.BigDecimal;
import java.util.List;

import dev.mochahaulier.productservice.model.ProductType;
import dev.mochahaulier.productservice.model.RateType;

public record ProductResponse(List<ProductItem> products) {
    public static record ProductItem(
                Long id,
                String productDefinitionKey,
                ProductType productType,
                RateType rateType,
                BigDecimal rate) {}
}

