package dev.mochahaulier.clientproductservice.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ClientProductRequest(
                @NotNull Long clientId,
                @NotNull Long productId,
                BigDecimal initialBalance,
                BigDecimal loanAmount,
                @NotNull LocalDate startDate,
                LocalDate endDate,
                BigDecimal fixedInstallment) {
}