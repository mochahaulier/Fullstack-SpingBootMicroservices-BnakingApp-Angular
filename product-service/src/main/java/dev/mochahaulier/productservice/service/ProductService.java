package dev.mochahaulier.productservice.service;

import dev.mochahaulier.productservice.dto.ProductCreationRequest;
import dev.mochahaulier.productservice.dto.ProductResponse;
import dev.mochahaulier.productservice.dto.ProductUpdateRequest;
import dev.mochahaulier.productservice.model.Product;
import dev.mochahaulier.productservice.model.ProductDefinition;
import dev.mochahaulier.productservice.repository.ProductDefinitionRepository;
import dev.mochahaulier.productservice.repository.ProductRepository;
import dev.mochahaulier.productservice.exception.ProductNotFoundException;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductDefinitionRepository productDefinitionRepository;

    @Transactional
    public void createProduct(ProductCreationRequest productRequest) {
        ProductDefinition productDefinition = productDefinitionRepository
                .findById(productRequest.productDefinitionKey())
                .orElseThrow(() -> new IllegalArgumentException("Product definition not found"));

        BigDecimal customRate = productRequest.adjustedRate();
        validateCustomRate(productDefinition, customRate);

        Product product = new Product();
        product.setProductDefinition(productDefinition);
        product.setRate(customRate);
        product.setProductType(productDefinition.getProductType());
        product.setRateType(productDefinition.getRateType());
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Cacheable("products")
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
               
        List<ProductResponse.ProductItem> productList =  products.stream().map(this::mapToProductResponse).toList();

        log.info("Fetching all products.");
        return new ProductResponse(productList);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Assert.notNull(id, "Product ID must not be null");
        Assert.isTrue(id > 0, "Product ID must be a positive number");

        log.info("Fetching product with id {}", id);
        ProductResponse.ProductItem product = productRepository.findById(id)
                .map(this::mapToProductResponse)
                .orElseThrow(() -> {
                    log.error("Product with id {} not found", id);
                    return new ProductNotFoundException(id);
                });

        return new ProductResponse(List.of(product));
    }

    private ProductResponse.ProductItem mapToProductResponse(Product product) {
        return new ProductResponse.ProductItem(product.getId(), product.getProductDefinition().getProductKey(),
                product.getProductType(), product.getRateType(), product.getRate());
    }

    @Transactional
    public Product updateProductRate(ProductUpdateRequest productRequest) {
        Product product = productRepository.findById(productRequest.productDefinitionKey())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        BigDecimal newRate = productRequest.adjustedRate();
        validateCustomRate(product.getProductDefinition(), newRate);

        product.setRate(newRate);
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductsByDefinition(String productDefinitionKey) {
        Assert.notNull(productDefinitionKey, "Product definition key must not be null");
        Assert.isTrue(productDefinitionKey.length() == 6, "Product definition key must have length of 6.");

        log.info("Fetching product definition with key {}", productDefinitionKey);

        ProductDefinition productDefinition = productDefinitionRepository.findById(productDefinitionKey)
                .orElseThrow(() -> new IllegalArgumentException("Product Definition not found"));

        List<Product> products = productRepository.findByProductDefinition(productDefinition);

        products.stream().findAny()
                .orElseThrow(() -> new IllegalArgumentException("No products found for the given definition"));

        List<ProductResponse.ProductItem> productList = products.stream()
                .map(this::mapToProductResponse)
                .toList();

        return new ProductResponse(productList);
    }

    private void validateCustomRate(ProductDefinition productDefinition, BigDecimal customRate) {
        switch (productDefinition.getRateType()) {
            case FIXED:
                validateFixedRate(productDefinition.getRate(), customRate);
                break;
            case PERCENTAGE:
                validatePercentageRate(productDefinition.getRate(), customRate);
                break;
            default:
                throw new IllegalArgumentException("Unknown rate type: " + productDefinition.getRateType());
        }
    }

    private void validateFixedRate(BigDecimal baseRate, BigDecimal customRate) {
        BigDecimal rateChange = customRate.subtract(baseRate);

        if (!isValidFixedRate(rateChange)) {
            throw new IllegalArgumentException("Custom rate out of allowed difference +-250");
        }

        if (customRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Final rate can't be negative: " + customRate);
        }
    }

    private void validatePercentageRate(BigDecimal baseRate, BigDecimal customRate) {
        BigDecimal rateChange = customRate.divide(baseRate, RoundingMode.HALF_UP).subtract(BigDecimal.ONE);

        if (!isValidPercentageRate(rateChange)) {
            throw new IllegalArgumentException("Custom rate out of allowed range +-0.2");
        }
    }

    public Boolean isValidFixedRate(BigDecimal rateChange) {
        if (rateChange.compareTo(BigDecimal.valueOf(-250)) < 0
                || rateChange.compareTo(BigDecimal.valueOf(250)) > 0)
            return false;
        return true;
    }

    public Boolean isValidPercentageRate(BigDecimal rateChange) {
        if (rateChange.compareTo(BigDecimal.valueOf(-0.2)) < 0
                || rateChange.compareTo(BigDecimal.valueOf(0.2)) > 0)
            return false;
        return true;
    }
}