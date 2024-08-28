package dev.mochahaulier.clientproductservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.service.annotation.GetExchange;

import dev.mochahaulier.clientproductservice.dto.ClientResponse;
import dev.mochahaulier.clientproductservice.exception.ClientNotFoundException;
import dev.mochahaulier.clientproductservice.exception.ClientServiceUnavailableException;

public interface ClientClient {

    Logger log = LoggerFactory.getLogger(ClientClient.class);

    @GetExchange("/api/v1/clients/{id}")
    @CircuitBreaker(name = "clientService", fallbackMethod = "clientFallback")
    @Retry(name = "clientService")
    ClientResponse getClientById(@PathVariable("id") Long id);

    default ClientResponse getClientByIdOrThrow(Long id) {
    try {
        return getClientById(id);
    } catch (HttpClientErrorException.NotFound e) {
        log.info("Client with ID {} not found.", id);
        throw new ClientNotFoundException("Client with ID " + id + " does not exist.");
    } catch (HttpServerErrorException e) {
        log.error("Client service is currently unavailable: {}", e.getMessage());
        throw new ClientServiceUnavailableException("Client service is currently unavailable");
    } catch (Exception e) {
        log.error("Error while fetching client by ID: {}", id, e);
        throw new RuntimeException("Error while fetching client by ID: " + id, e);
    }
}

    // default boolean isClientExists(Long id) {
    //     try {
    //         ClientResponse response = getClientById(id);
    //         return response != null; // Returns true if the client exists
    //     } catch (HttpClientErrorException.NotFound e) {
    //         log.info("Client with ID {} not found.", id);
    //         return false;
    //     } catch (Exception e) {
    //         log.error("Error while fetching client by ID: {}", id, e);
    //         return false;
    //     }
    // }

    default ClientResponse clientFallback(Long id, Throwable throwable) {
        if (throwable instanceof HttpServerErrorException) {
            throw new ClientServiceUnavailableException("Client service is currently unavailable");
        } else {
            throw new RuntimeException("Unexpected error occurred while fetching client", throwable);
        }
    }

    // default ClientResponse fallbackMethod(Long id, Throwable throwable) {
    //     log.info("Cannot get client for ID {}, failure reason: {}", id, throwable.getMessage());
    //     return new ClientResponse(
    //         id,  
    //         "",
    //         "",
    //         "",
    //         ""
    //     );        
    // }
}