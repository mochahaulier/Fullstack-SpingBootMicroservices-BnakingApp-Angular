package dev.mochahaulier.transactionservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import dev.mochahaulier.transactionservice.model.TransactionType;

public record TransactionResponse(List<TransactionItem> transactions) {
    
    public static record TransactionItem(            
        Long id,
        Long clientProductId,
        Long clientId,    
        TransactionType transactionType,
        BigDecimal amount,
        LocalDateTime transactionDate) {
    }
}