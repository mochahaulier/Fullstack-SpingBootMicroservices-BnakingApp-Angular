package dev.mochahaulier.clientproductservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import dev.mochahaulier.clientproductservice.model.ProductType;

public record LoanProductResponse(List<LoanProductItem> loanproducts) {
        public static record LoanProductItem(Long id,
        Long clientId,
        Long productId,
        ProductType type,
        LocalDate lastChargeDate,
        BigDecimal originalAmount,
        BigDecimal fixedInstallment,
        LocalDate startDate,
        LocalDate endDate
) {}}
