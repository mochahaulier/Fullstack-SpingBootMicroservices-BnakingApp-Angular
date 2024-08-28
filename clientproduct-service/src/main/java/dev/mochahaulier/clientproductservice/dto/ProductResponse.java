package dev.mochahaulier.clientproductservice.dto;

import java.math.BigDecimal;
import java.util.List;

import dev.mochahaulier.clientproductservice.model.ProductType;
import dev.mochahaulier.clientproductservice.model.RateType;

public record ProductResponse(List<ProductItem> products) {
        public static record ProductItem(Long id,
        String productDefinitionKey,
        ProductType productType,
        RateType rateType,
        BigDecimal rate) {
}}

