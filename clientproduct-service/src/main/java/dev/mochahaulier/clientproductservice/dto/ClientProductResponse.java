package dev.mochahaulier.clientproductservice.dto;

import java.time.LocalDate;
import java.util.List;

import dev.mochahaulier.clientproductservice.model.ProductType;
public record ClientProductResponse(List<ClientProductItem> clientproducts) {
        public static record ClientProductItem(        
                Long id,
                Long clientId,
                Long productId,
                ProductType type,
                LocalDate lastChargeDate) {}
}
