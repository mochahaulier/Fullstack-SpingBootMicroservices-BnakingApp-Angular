package dev.mochahaulier.productservice.controller;

import dev.mochahaulier.productservice.dto.ProductDefinitionRequest;
import dev.mochahaulier.productservice.dto.ProductDefinitionResponse;
import dev.mochahaulier.productservice.dto.ProductDefinitionProcessResponse;
import dev.mochahaulier.productservice.service.ProductDefinitionService;
import dev.mochahaulier.productservice.validation.ValidationGroup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product-definitions")
@Validated
public class ProductDefinitionController {

    private final ProductDefinitionService productDefinitionService;
    private final Validator validator;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductDefinitionResponse getAllProductDefinitions() {
        return productDefinitionService.getAllProductDefinitions();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductDefinitionProcessResponse processProductDefinitions(
            @RequestBody @Valid ProductDefinitionRequest productDefinitionRequest) {
        // This now allows the processing of all validated requests one by one
        // Returns all the errors and all the succesfully processed definitions
        Map<Integer, ProductDefinitionRequest.DefinitionRequest> validDefinitions = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> successes = new ArrayList<>();

        List<ProductDefinitionRequest.DefinitionRequest> definitions = productDefinitionRequest.definitions();
        for (int i = 0; i < definitions.size(); i++) {
            final int index = i;
            ProductDefinitionRequest.DefinitionRequest definition = definitions.get(i);
            try {
                switch (definition.operation()) {
                    case NEW:
                        validate(definition, i, ValidationGroup.NewOperation.class);
                        break;
                    case UPDATE:
                        validate(definition, i, ValidationGroup.UpdateOperation.class);
                        break;
                    case INVALID:
                        throw new IllegalArgumentException(
                                "Invalid operation " + definition.operation());
                }
                validDefinitions.put(index, definition); // Add to valid definitions if no exception
            } catch (ConstraintViolationException e) {
                e.getConstraintViolations()
                        .forEach(violation -> errors
                                .add("[" + index + "]: [VALIDATION ERROR]: " + violation.getMessage()));
            } catch (IllegalArgumentException e) {
                errors.add("[" + index + "]: [VALIDATION ERROR]: " + e.getMessage());
            }
        }

        ProductDefinitionProcessResponse processResponse;

        if (!validDefinitions.isEmpty()) {
            processResponse = productDefinitionService.processProductDefinitions(validDefinitions);
            errors.addAll(processResponse.errors());
            successes.addAll(processResponse.successes());
        }

        // Return a response containing both errors and successful indexes
        return new ProductDefinitionProcessResponse(errors, successes);
    }

    private <T> void validate(T object, int index, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
