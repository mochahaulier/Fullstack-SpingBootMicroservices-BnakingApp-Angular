package dev.mochahaulier.transactionservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import dev.mochahaulier.transactionservice.dto.ClientProductResponse;
import dev.mochahaulier.transactionservice.dto.DepositRequest;
import dev.mochahaulier.transactionservice.dto.TransactionResponse;
import dev.mochahaulier.transactionservice.service.TransactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.OK)
    public void processDeposit(@RequestBody DepositRequest depositRequest) {
        log.info("New request {}", depositRequest);
        transactionService.processAccountDeposit(depositRequest.accountId(), depositRequest.amount());
    }

    @GetMapping("/clients/{clientId}")
    public TransactionResponse getClientProductsByClientId(@PathVariable Long clientId) {
        log.info("Getting tranasctions for client {}", clientId);
        return transactionService.getTransactionsByClientId(clientId);
    }
}