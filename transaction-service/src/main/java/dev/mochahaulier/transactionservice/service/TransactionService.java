package dev.mochahaulier.transactionservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import dev.mochahaulier.clientproductservice.event.ClientProductCreatedEvent;
import dev.mochahaulier.transactionservice.dto.AccountProductResponse;
import dev.mochahaulier.transactionservice.dto.BalanceRequest;
import dev.mochahaulier.transactionservice.dto.ClientProductResponse;
import dev.mochahaulier.transactionservice.dto.TransactionResponse;
import dev.mochahaulier.transactionservice.model.Transaction;
import dev.mochahaulier.transactionservice.model.TransactionType;
import dev.mochahaulier.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

        @Value("${clientproduct.service.url}")
        private String clientproductServiceUrl;

        private final TransactionRepository transactionRepository;

        private final WebClient.Builder webClientBuilder;

        @KafkaListener(topics="accountCreateTopic", groupId="transaction-service")
        public void handleAccountCreated(ClientProductCreatedEvent event) {
                log.info("Received event for account creation deposit: {}", event.getClientProductId());                
                log.info("Event class: {}", event.getClass().getName());
                processAccountDeposit(event.getClientProductId(), event.getInitialBalance());
        }

        @Transactional
        public void processAccountDeposit(Long accountId, BigDecimal amount) {
                
                // TODO: get rid of first, make new method to add balance to account
                // get the account from cpservice
                AccountProductResponse accountProduct = webClientBuilder.build().get()
                                .uri(clientproductServiceUrl + "/api/v1/client-products/accounts",
                                                uriBuilder -> uriBuilder.path("/{id}").build(accountId))
                                .retrieve()
                                .bodyToMono(AccountProductResponse.class)
                                .doOnSuccess(response -> log.info("Successfully sent request"))
                                .doOnError(error -> log.error("Error sending request", error))
                                .block();

                BigDecimal newBalance = accountProduct.accountBalance().add(amount);
                Long clientId = accountProduct.clientId();

                log.info("Transaction with id {} and amount {} (newbalance {})", accountId, amount, newBalance);
                // change the accountbalance on cpservice
                BalanceRequest deposit = new BalanceRequest(newBalance);

                webClientBuilder.build().put().uri(clientproductServiceUrl + "/api/v1/client-products/accounts",
                                uriBuilder -> uriBuilder.path("/{id}").build(accountId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(deposit)
                                .retrieve()
                                .toBodilessEntity()
                                .doOnSuccess(response -> log.info("Successfully sent account request"))
                                .doOnError(error -> log.error("Error sending account request", error))
                                .block();

                Transaction transaction = new Transaction();
                transaction.setClientProductId(accountId);
                transaction.setClientId(clientId);
                transaction.setAmount(amount);
                transaction.setTransactionType(TransactionType.ACCOUNT_DEPOSIT);
                transaction.setTransactionDate(LocalDateTime.now());

                transactionRepository.save(transaction);
        }

        @Transactional(readOnly = true)
        @Cacheable("transList")
        public TransactionResponse getTransactionsByClientId(Long clientId) {
                Assert.notNull(clientId, "Client ID must not be null");
                Assert.isTrue(clientId > 0, "Client ID must be a positive number");

                log.info("Fetching transactions of client with id {}", clientId);
                
                List<Transaction> transList = transactionRepository.findByClientId(clientId);
                List<TransactionResponse.TransactionItem> transactions = transList
                        .stream()
                        .map(this::mapToTransactionResponse)
                        .toList();

                return new TransactionResponse(transactions);
        }

        private TransactionResponse.TransactionItem mapToTransactionResponse(Transaction transaction) {
                return new TransactionResponse.TransactionItem(transaction.getId(),
                        transaction.getClientProductId(),
                        transaction.getClientId(),
                        transaction.getTransactionType(),
                        transaction.getAmount(),
                        transaction.getTransactionDate());
        }
     
        // private final ClientProductRepository clientProductRepository;
        // private final AccountProductRepository accountProductRepository;

        // @Transactional
        // public void processFeeDeduction(ClientProduct clientProduct, BigDecimal fee)
        // {
        // List<AccountProduct> clientAccounts =
        // accountProductRepository.findByClient(clientProduct.getClient());

        // // List<AccountProduct> clientAccounts = clientProductRepository
        // //
        // .findByClientAndProduct_ProductDefinition_ProductType(clientProduct.getClient(),
        // // ProductType.ACCOUNT)
        // // .stream()
        // // .map(AccountProduct.class::cast)
        // // .collect(Collectors.toList());

        // if (clientAccounts.isEmpty()) {
        // throw new IllegalStateException("No account found for client " +
        // clientProduct.getClient().getId());
        // }

        // // Select account with smallest ID, maybe let client choose which account to
        // use
        // AccountProduct account = clientAccounts.stream()
        // .min(Comparator.comparing(ClientProduct::getId))
        // .orElseThrow(() -> new RuntimeException("No account found."));

        // // Deduct the fee from first account
        // account.setAccountBalance(account.getAccountBalance().subtract(fee));
        // clientProductRepository.save(account);

        // // Save transaction
        // Transaction transaction = new Transaction();
        // transaction.setClientProduct(clientProduct);
        // transaction.setTransactionType(TransactionType.FEE_DEDUCTION);
        // transaction.setAmount(fee.negate());
        // transaction.setTransactionDate(LocalDateTime.now());

        // transactionRepository.save(transaction);
        // clientProductRepository.save(account);

        // // ClientProduct clientProduct =
        // // clientProductServiceClient.getClientProductById(clientProductId);

        // // // Process the deposit
        // // // Update the client product balance (in the respective service, through
        // // Kafka or direct REST call)
        // // // Save the transaction record

        // // Transaction transaction = new Transaction();
        // // transaction.setClientProductId(clientProductId);
        // // transaction.setTransactionType(TransactionType.ACCOUNT_DEPOSIT);
        // // transaction.setAmount(amount);
        // // transaction.setTransactionDate(LocalDateTime.now());

        // // transactionRepository.save(transaction);
        // }

        // @Transactional
        // public void processLoanAddition(ClientProduct clientProduct, BigDecimal
        // loanAmount) {
        // List<AccountProduct> clientAccounts = clientProductRepository
        // .findByClientAndProduct_ProductDefinition_ProductType(clientProduct.getClient(),
        // ProductType.ACCOUNT)
        // .stream()
        // .map(AccountProduct.class::cast)
        // .collect(Collectors.toList());

        // if (clientAccounts.isEmpty()) {
        // throw new IllegalStateException("No account found for client " +
        // clientProduct.getClient().getId());
        // }

        // // Select account with smallest ID
        // AccountProduct account = clientAccounts.stream()
        // .min(Comparator.comparing(ClientProduct::getId))
        // .orElseThrow(() -> new RuntimeException("No account found."));

        // // Deduct the fee from first account
        // account.setAccountBalance(account.getAccountBalance().add(loanAmount));
        // clientProductRepository.save(account);

        // // Save transaction
        // Transaction transaction = new Transaction();
        // transaction.setClientProduct(clientProduct);
        // transaction.setTransactionType(TransactionType.LOAN_ADDITION);
        // transaction.setAmount(loanAmount);
        // transaction.setTransactionDate(LocalDateTime.now());

        // transactionRepository.save(transaction);
        // clientProductRepository.save(account);
        // }

        // @Transactional
        // public void processAccountDeposit(AccountProduct account, BigDecimal amount)
        // {
        // // Deduct the fee from first account
        // account.setAccountBalance(account.getAccountBalance().add(amount));
        // // clientProductRepository.save(account);

        // // Save transaction
        // Transaction transaction = new Transaction();
        // transaction.setClientProduct(account);
        // transaction.setTransactionType(TransactionType.ACCOUNT_DEPOSIT);
        // transaction.setAmount(amount);
        // transaction.setTransactionDate(LocalDateTime.now());

        // transactionRepository.save(transaction);
        // // clientProductRepository.save(account);
        // }
}
