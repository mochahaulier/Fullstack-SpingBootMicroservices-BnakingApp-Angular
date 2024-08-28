package dev.mochahaulier.clientproductservice.controller;

import dev.mochahaulier.clientproductservice.dto.AccountProductResponse;
import dev.mochahaulier.clientproductservice.dto.ClientProductRequest;
import dev.mochahaulier.clientproductservice.dto.ClientProductResponse;
import dev.mochahaulier.clientproductservice.dto.LoanProductResponse;
import dev.mochahaulier.clientproductservice.service.ClientProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client-products")
public class ClientProductController {

    private final ClientProductService clientProductService;

    @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, String>> createClientProduct(@RequestBody @Valid ClientProductRequest request) {
        clientProductService.createClientProduct(request);
       
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Client-Product created.");
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);         
    }

    public CompletableFuture<String> fallbackMethod(ClientProductRequest request, RuntimeException runtimeException) {
          return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, try again later.");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ClientProductResponse getAllClientProducts() {
        return clientProductService.getAllClientProducts();
    }

    @GetMapping("/client/{clientId}")
    public ClientProductResponse getClientProductsByClientId(@PathVariable Long clientId) {
        return clientProductService.getClientProductsByClientId(clientId);
    }

    @GetMapping("/client/{clientId}/accounts")
    public AccountProductResponse getAccountProductsByClientId(@PathVariable Long clientId) {
        return clientProductService.getClientAccounts(clientId);
    }

    @GetMapping("/client/{clientId}/loans")
    public LoanProductResponse getLoanProductsByClientId(@PathVariable Long clientId) {
        return clientProductService.getClientLoans(clientId);
    }

    @GetMapping("/{clientProductId}")
    public ClientProductResponse getClientProductById(@PathVariable Long clientProductId) {
        return clientProductService.getClientProductById(clientProductId);
    }

    @GetMapping("/accounts/{clientProductId}")
    public AccountProductResponse getAccountProductById(@PathVariable Long clientProductId) {
        return clientProductService.getAccountProductById(clientProductId);
    }

    @GetMapping("/loans/{clientProductId}")
    public LoanProductResponse getLoanProductById(@PathVariable Long clientProductId) {
        return clientProductService.getLoanProductById(clientProductId);
    }

    @PutMapping("/accounts/{clientProductId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateAccountProductById(@PathVariable Long clientProductId,
            @RequestBody ClientProductRequest request) {
        clientProductService.updateAccountProductById(clientProductId, request);
    }

    @PutMapping("/loans/{clientProductId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProductById(@PathVariable Long clientProductId, @RequestBody ClientProductRequest request) {
        clientProductService.updateLoanProductById(clientProductId, request);
    }
}
