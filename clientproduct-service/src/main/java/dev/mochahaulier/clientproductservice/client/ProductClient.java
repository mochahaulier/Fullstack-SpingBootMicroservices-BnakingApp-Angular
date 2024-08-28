package dev.mochahaulier.clientproductservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.service.annotation.GetExchange;

import dev.mochahaulier.clientproductservice.dto.ProductResponse;
import dev.mochahaulier.clientproductservice.exception.ProductNotFoundException;
import dev.mochahaulier.clientproductservice.exception.ProductServiceUnavailableException;

public interface ProductClient {

    Logger log = LoggerFactory.getLogger(ClientClient.class);

    @GetExchange("/api/v1/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "productFallback")
    @Retry(name = "productService")
    ProductResponse getProductById(@PathVariable("id") Long id);

    default ProductResponse getProductByIdOrThrow(Long id) {
        try {
            return getProductById(id);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Product with ID {} not found.", id);
            throw new ProductNotFoundException("Product with ID " + id + " does not exist.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Service Instance cannot be null")) {
                log.error("Product service is currently unavailable. Cannot find any instances of product-service.");
                throw new ProductServiceUnavailableException("Product service is currently unavailable.");
            } else {
                log.error("Unexpected error while fetching product by ID: {}", id, e);
                throw new RuntimeException("Unexpected error while fetching product by ID: " + id, e);
            }
        } catch (Exception e) {
            log.error("Error while fetching product by ID: {}", id, e);
            throw new RuntimeException("Error while fetching product by ID: " + id, e);
        }
}

    // default boolean isProductExists(Long id) {
    //     try {
    //         ProductResponse response = getProductById(id);
    //         return response != null; // Returns true if the client exists
    //     } catch (HttpClientErrorException.NotFound e) {
    //         log.info("Product with ID {} not found.", id);
    //         return false;
    //     } catch (Exception e) {
    //         log.error("Error while fetching product by ID: {}", id, e);
    //         return false;
    //     }
    // }

    // default ProductResponse fallbackMethod(Long id, Throwable throwable) {
    //     log.info("Cannot get product for ID {}, failure reason: {}", id, throwable.getMessage());
    //     return new ProductResponse(
    //         id,  
    //         "",
    //         ProductType.UNKNOWN,
    //         RateType.UNKNOWN,
    //         BigDecimal.ZERO
    //     );
    // }


    default ProductResponse productFallback(Long id, Throwable throwable) {
        if (throwable instanceof HttpServerErrorException) {
            throw new ProductServiceUnavailableException("Product service is currently unavailable");
        } else {
            throw new RuntimeException("Unexpected error occurred while fetching product", throwable);
        }
    }
}