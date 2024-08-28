package dev.mochahaulier.productservice.service;

import dev.mochahaulier.productservice.dto.ProductDefinitionRequest;
import dev.mochahaulier.productservice.dto.ProductDefinitionResponse;
import dev.mochahaulier.productservice.dto.ProductDefinitionProcessResponse;
import dev.mochahaulier.productservice.model.PayRate;
import dev.mochahaulier.productservice.model.PayRateUnit;
import dev.mochahaulier.productservice.model.Product;
import dev.mochahaulier.productservice.model.ProductDefinition;
import dev.mochahaulier.productservice.model.ProductType;
import dev.mochahaulier.productservice.repository.ProductDefinitionRepository;
import dev.mochahaulier.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductDefinitionService {

    private final ProductDefinitionRepository productDefinitionRepository;

    private final ProductRepository productRepository;

    @Transactional
    public ProductDefinitionProcessResponse processProductDefinitions(
            Map<Integer, ProductDefinitionRequest.DefinitionRequest> definitions) {
        List<String> errors = new ArrayList<>();
        List<String> successes = new ArrayList<>();

        for (Map.Entry<Integer, ProductDefinitionRequest.DefinitionRequest> entry : definitions.entrySet()) {
            int index = entry.getKey();
            ProductDefinitionRequest.DefinitionRequest definition = entry.getValue();
            try {
                processSingleDefinition(definition);
                successes.add("[" + index + "]: [PROCESSING SUCCESS]: Definition " + definition.productKey()
                        + " processed.");
            } catch (Exception e) {
                errors.add("[" + index + "]: [PROCESSING ERROR]: " + e.getMessage());
            }
        }

        return new ProductDefinitionProcessResponse(errors, successes);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleDefinition(ProductDefinitionRequest.DefinitionRequest request) {
        ProductDefinition existingDefinition = productDefinitionRepository.findById(request.productKey())
                .orElse(null);
        switch (request.operation()) {
            case NEW:
                if (existingDefinition != null) {
                    throw new IllegalArgumentException(
                            "Product definition with key " + request.productKey() + " already exists");
                }
                ProductDefinition productDefinition = new ProductDefinition();
                productDefinition.setProductKey(request.productKey());
                productDefinition.setDescription(request.description());
                productDefinition.setProductType(ProductType.valueOf(request.type()));
                productDefinition.setRate(request.rate());
                PayRate payRate = new PayRate();
                payRate.setUnit(PayRateUnit.valueOf(request.payRate().unit()));
                payRate.setValue(request.payRate().value());
                productDefinition.setPayRate(payRate);
                productDefinitionRepository.save(productDefinition);
                break;
            case UPDATE:
                if (existingDefinition == null) {
                    throw new IllegalArgumentException(
                            "Product definition with key " + request.productKey() + " doesn't exist!");
                }
                // save the oldrate so we can update the derived products
                BigDecimal oldRate = existingDefinition.getRate();
                updateExistingDefinition(existingDefinition, request);
                updateDerivedProducts(existingDefinition, oldRate);
                break;
            default:
                throw new IllegalArgumentException("Invalid operation: " + request.operation());
        }
    }
    
    @Transactional
    private void updateExistingDefinition(ProductDefinition existingDefinition,
            ProductDefinitionRequest.DefinitionRequest request) {
        boolean updated = false;

        if (request.rate() != null && !request.rate().equals(existingDefinition.getRate())) {
            existingDefinition.setRate(request.rate());
            updated = true;
        }

        if (request.payRate() != null) {
            PayRate payRate = existingDefinition.getPayRate();
            if (request.payRate().unit() != null
                    && !request.payRate().unit().equals(payRate.getUnit().name())) {
                payRate.setUnit(PayRateUnit.valueOf(request.payRate().unit()));
                updated = true;
            }
            if (request.payRate().value() != null
                    && !request.payRate().value().equals(payRate.getValue())) {
                payRate.setValue(request.payRate().value());
                updated = true;
            }
        }

        if (updated) {
            productDefinitionRepository.save(existingDefinition);
        }
    }

    @Transactional
    public void updateDerivedProducts(ProductDefinition definition, BigDecimal oldRate) {
        List<Product> products = productRepository.findByProductDefinition(definition);

        for (Product product : products) {
            if (product.getRateType() != definition.getRateType()) {
                resetProductRate(product, definition);
                markProductForReview(product);
            } else {
                updateProductRate(product, definition, oldRate);
            }

            productRepository.save(product);
        }
    }

    @Transactional(readOnly = true)
    @Cacheable("productDefinitions")
    public ProductDefinitionResponse getAllProductDefinitions() {
        List<ProductDefinition> productDefinitions = productDefinitionRepository.findAll();
        List<ProductDefinitionResponse.DefinitionResponse> definitions = productDefinitions.stream()
            .map(this::mapToDefinitionResponse)
            .toList();
        return new ProductDefinitionResponse(definitions);
    }

    private ProductDefinitionResponse.DefinitionResponse mapToDefinitionResponse(ProductDefinition productDefinition) {
        return new ProductDefinitionResponse.DefinitionResponse(
            productDefinition.getProductKey(),
            productDefinition.getDescription(),
            productDefinition.getProductType().name(),
            productDefinition.getRate(),
            mapToPayRateDto(productDefinition.getPayRate()),
            productDefinition.getCreatedDate(),
            productDefinition.getModifiedDate()
        );
    }

    private ProductDefinitionResponse.DefinitionResponse.PayRateDto mapToPayRateDto(PayRate payRate) {
        if (payRate == null) {
            return null;
        }
        return new ProductDefinitionResponse.DefinitionResponse.PayRateDto(
            payRate.getUnit().name(),
            payRate.getValue()
        );
    }

    private void resetProductRate(Product product, ProductDefinition definition) {
        product.setRateType(definition.getRateType());
        product.setRate(definition.getRate());
    }

    private void updateProductRate(Product product, ProductDefinition definition, BigDecimal oldRate) {
        BigDecimal newRate;
        switch (definition.getRateType()) {
            case FIXED:
                newRate = calculateNewFixedRate(product, definition, oldRate);
                break;
            case PERCENTAGE:
                newRate = calculateNewPercentageRate(product, definition, oldRate);
                break;
            default:
                throw new IllegalArgumentException("Unknown rate type: " + definition.getRateType());
        }

        product.setRate(newRate);
    }

    private BigDecimal calculateNewFixedRate(Product product, ProductDefinition definition, BigDecimal oldRate) {
        BigDecimal rateModifier = product.getRate().subtract(oldRate);
        BigDecimal newRate = definition.getRate().add(rateModifier);
        return newRate.max(BigDecimal.ZERO);
    }

    private BigDecimal calculateNewPercentageRate(Product product, ProductDefinition definition, BigDecimal oldRate) {
        BigDecimal productRate = product.getRate();
        BigDecimal definitionRate = definition.getRate();
        BigDecimal rateModifier = productRate.divide(oldRate, 4, RoundingMode.HALF_EVEN).subtract(BigDecimal.ONE);
        BigDecimal newRate = definitionRate.multiply(rateModifier.add(BigDecimal.ONE));
        return newRate;
    }

    private void markProductForReview(Product product) {

    }
}
