package dev.mochahaulier.productservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;

import dev.mochahaulier.productservice.dto.ProductCreationRequest;
import dev.mochahaulier.productservice.dto.ProductResponse;
import dev.mochahaulier.productservice.dto.ProductUpdateRequest;
import dev.mochahaulier.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody @Valid ProductCreationRequest productRequest) {
        productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProductRate(@RequestBody @Valid ProductUpdateRequest productRequest) {
        productService.updateProductRate(productRequest);
    }

    @GetMapping("/by-definition/{definitionId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductsByDefinition(@PathVariable String definitionId) {
        ProductResponse products = productService.getProductsByDefinition(definitionId);
        return products;
    }
}
