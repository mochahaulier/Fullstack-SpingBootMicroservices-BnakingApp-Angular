package dev.mochahaulier.transactionservice.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DepositRequest(@JsonProperty("accountId") Long accountId, @JsonProperty("amount") BigDecimal amount) {
}
