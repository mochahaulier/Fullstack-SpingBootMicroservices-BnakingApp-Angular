package dev.mochahaulier.clientproductservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import dev.mochahaulier.clientproductservice.model.ProductType;

public record AccountProductResponse(List<AccountProductItem> accountproducts) {
        public static record AccountProductItem(Long id,
                Long clientId,
                Long productId,
                ProductType type,
                LocalDate lastChargeDate,
                BigDecimal accountBalance,
                LocalDate startDate
        ) {}
}
