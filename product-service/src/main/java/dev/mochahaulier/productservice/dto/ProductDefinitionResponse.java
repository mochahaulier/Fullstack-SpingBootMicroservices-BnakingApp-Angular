package dev.mochahaulier.productservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductDefinitionResponse(List<DefinitionResponse> definitions) {
    
    public static record DefinitionResponse(            
        String productKey,
        String description,
        String type,
        BigDecimal rate,
        PayRateDto payRate,
        Instant createdDate,
        Instant modifiedDate) {

        public static record PayRateDto(
            String unit,
            Integer value) {
        }
    }
}