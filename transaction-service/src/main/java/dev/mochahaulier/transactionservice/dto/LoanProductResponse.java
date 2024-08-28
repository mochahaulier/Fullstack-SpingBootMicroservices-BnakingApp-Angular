package dev.mochahaulier.transactionservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import dev.mochahaulier.transactionservice.model.ProductType;

public record LoanProductResponse(
                Long id,
                Long clientId,
                Long productId,
                ProductType type,
                LocalDate lastChargeDate,
                BigDecimal originalAmount,
                BigDecimal fixedInstallment,
                LocalDate startDate,
                LocalDate endDate) {

}
