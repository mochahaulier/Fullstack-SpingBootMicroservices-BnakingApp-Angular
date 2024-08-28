package dev.mochahaulier.productservice.dto;

import java.util.List;

public record ProductDefinitionProcessResponse(
        List<String> errors,
        List<String> successes) {
}
