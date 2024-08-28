package dev.mochahaulier.clientproductservice.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionRequest(
        @JsonProperty("accountId") Long accountId,
        @JsonProperty("amount") BigDecimal amount) {

}
