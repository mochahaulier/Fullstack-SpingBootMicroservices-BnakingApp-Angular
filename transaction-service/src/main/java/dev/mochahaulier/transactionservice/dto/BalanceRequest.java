package dev.mochahaulier.transactionservice.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BalanceRequest(@JsonProperty("initialBalance") BigDecimal amount) {
}
