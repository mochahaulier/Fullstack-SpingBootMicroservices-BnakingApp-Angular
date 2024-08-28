package dev.mochahaulier.productservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

import dev.mochahaulier.productservice.model.Operation;
import dev.mochahaulier.productservice.validation.ValidationGroup;

public record ProductDefinitionRequest(
                @Valid @NotNull(message = "Definitions list cannot be null.") List<DefinitionRequest> definitions) {
        public static record DefinitionRequest(
                        @NotNull(message = "Operation is required.") Operation operation,
                        @NotNull(message = "Product key is required.", groups = {
                                        ValidationGroup.NewOperation.class,
                                        ValidationGroup.UpdateOperation.class }) @Size(min = 6, max = 6, message = "Product key must have 6 characters.", groups = {
                                                        ValidationGroup.NewOperation.class,
                                                        ValidationGroup.UpdateOperation.class }) String productKey,
                        @NotNull(message = "Please provide a description.", groups = ValidationGroup.NewOperation.class) String description,
                        @NotNull(message = "Type is required.", groups = ValidationGroup.NewOperation.class) @Pattern(regexp = "^(ACCOUNT|LOAN)$", message = "Type must be 'ACCOUNT' or 'LOAN'.", groups = ValidationGroup.NewOperation.class) String type,
                        @NotNull(message = "Rate is required.", groups = ValidationGroup.NewOperation.class) @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0.", groups = {
                                        ValidationGroup.NewOperation.class,
                                        ValidationGroup.UpdateOperation.class }) BigDecimal rate,
                        @NotNull(message = "Pay rate unit is required.", groups = ValidationGroup.NewOperation.class) PayRateDto payRate){
                public static record PayRateDto(
                                @NotNull(message = "Pay rate unit is required.", groups = ValidationGroup.NewOperation.class) @Pattern(regexp = "^(DAY|MONTH)$", message = "Unit must be 'DAY' or 'MONTH'.", groups = {
                                                ValidationGroup.NewOperation.class,
                                                ValidationGroup.UpdateOperation.class }) String unit,
                                @NotNull(message = "Pay rate value is required.", groups = ValidationGroup.NewOperation.class) @DecimalMin(value = "1", message = "Pay rate value must be at least 1.", groups = {
                                                ValidationGroup.NewOperation.class,
                                                ValidationGroup.UpdateOperation.class }) Integer value){
                }
        }
}
