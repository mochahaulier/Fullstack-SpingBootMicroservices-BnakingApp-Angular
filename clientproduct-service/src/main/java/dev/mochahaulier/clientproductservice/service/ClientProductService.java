package dev.mochahaulier.clientproductservice.service;

import dev.mochahaulier.clientproductservice.client.ClientClient;
import dev.mochahaulier.clientproductservice.client.ProductClient;
import dev.mochahaulier.clientproductservice.dto.AccountProductResponse;
import dev.mochahaulier.clientproductservice.dto.ClientProductRequest;
import dev.mochahaulier.clientproductservice.dto.ClientProductResponse;
import dev.mochahaulier.clientproductservice.dto.ClientResponse;
import dev.mochahaulier.clientproductservice.dto.LoanProductResponse;
import dev.mochahaulier.clientproductservice.dto.ProductResponse;
import dev.mochahaulier.clientproductservice.event.ClientProductCreatedEvent;
import dev.mochahaulier.clientproductservice.model.AccountProduct;
import dev.mochahaulier.clientproductservice.model.ClientProduct;
import dev.mochahaulier.clientproductservice.model.LoanProduct;
import dev.mochahaulier.clientproductservice.repository.AccountProductRepository;
import dev.mochahaulier.clientproductservice.repository.ClientProductRepository;
import dev.mochahaulier.clientproductservice.repository.LoanProductRepository;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import dev.mochahaulier.clientproductservice.exception.ClientNotFoundException;
import dev.mochahaulier.clientproductservice.exception.ClientProductNotFoundException;
import dev.mochahaulier.clientproductservice.exception.ClientServiceUnavailableException;
import dev.mochahaulier.clientproductservice.exception.ProductNotFoundException;
import dev.mochahaulier.clientproductservice.exception.ProductServiceUnavailableException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientProductService {

    private final ClientProductRepository clientProductRepository;
    private final AccountProductRepository accountProductRepository;
    private final LoanProductRepository loanProductRepository;
    private final KafkaTemplate<String, ClientProductCreatedEvent> kafkaTemplate;

    private final ClientClient clientClient;
    private final ProductClient productClient;
   
    @Transactional
    public ClientProduct createClientProduct(ClientProductRequest request) {
        // Fetch client and product details concurrently
        CompletableFuture<ClientResponse> clientFuture = CompletableFuture.supplyAsync(() -> clientClient.getClientByIdOrThrow(request.clientId()));
        CompletableFuture<ProductResponse> productFuture = CompletableFuture.supplyAsync(() -> productClient.getProductByIdOrThrow(request.productId()));

        try {
            ClientResponse clientResponse = clientFuture.join();
            ProductResponse productResponse = productFuture.join();
            
            // Assuming the list always contains exactly one item, as it should
            ClientResponse.ClientItem client = clientResponse.clients().get(0);
            ProductResponse.ProductItem product = productResponse.products().get(0);

            ClientProduct clientProduct = createProductBasedOnType(request, product);
            ClientProduct savedClientProduct = clientProductRepository.save(clientProduct);
            publishClientProductEvent(savedClientProduct, request);
    
            return savedClientProduct;
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            log.error("CompletionException caught. Cause: {}", cause.getClass().getName(), cause);
    
            if (cause instanceof ClientNotFoundException || cause instanceof ProductNotFoundException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof ClientServiceUnavailableException || cause instanceof ProductServiceUnavailableException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException("Failed to create client product", cause);
            }
        } catch (Exception e) {
            log.error("General exception caught: {}", e.getClass().getName(), e);
            throw new RuntimeException("Failed to create client product", e);
        }
    }

    private ClientProduct createProductBasedOnType(ClientProductRequest request, ProductResponse.ProductItem product) {
        // Implement logic based on product type (ACCOUNT or LOAN)
        ClientProduct clientProduct;

        switch (product.productType()) {
            case ACCOUNT:
                AccountProduct accountProduct = new AccountProduct();
                accountProduct.setStartDate(request.startDate());
                clientProduct = accountProduct;
                break;
            case LOAN:
                LoanProduct loanProduct = new LoanProduct();
                loanProduct.setStartDate(request.startDate());
                loanProduct.setEndDate(request.endDate());
                loanProduct.setFixedInstallment(request.fixedInstallment());
                loanProduct.setOriginalAmount(request.loanAmount());
                clientProduct = loanProduct;
                break;
            default:
                throw new IllegalArgumentException("Unsupported product type: " + product.productType());
        }

        clientProduct.setClientId(request.clientId());
        clientProduct.setProductId(request.productId());
        clientProduct.setType(product.productType());
        clientProduct.setLastChargeDate(request.startDate());

        return clientProduct;
    }

    private void publishClientProductEvent(ClientProduct clientProduct, ClientProductRequest request) {
        if (clientProduct instanceof AccountProduct accountProduct) {
            log.info("Publishing event for account creation: {}", accountProduct.getId());
            ClientProductCreatedEvent event = new ClientProductCreatedEvent(accountProduct.getId(), request.initialBalance());
            log.info("Start of publishClientProductEvent {} to topic accountCreateTopic", event);
            kafkaTemplate.send("accountCreateTopic", event);
            log.info("End of publishClientProductEvent {} to topic accountCreateTopic", event);                
        }
    }

    private ClientProductResponse.ClientProductItem mapToClientProductResponse(ClientProduct clientProduct) {
        return new ClientProductResponse.ClientProductItem(clientProduct.getId(),
                clientProduct.getClientId(),
                clientProduct.getProductId(),
                clientProduct.getType(),
                clientProduct.getLastChargeDate());
    }

    private AccountProductResponse.AccountProductItem mapToAccountProductResponse(AccountProduct accountProduct) {
        return new AccountProductResponse.AccountProductItem(accountProduct.getId(),
                accountProduct.getClientId(),
                accountProduct.getProductId(),
                accountProduct.getType(),
                accountProduct.getLastChargeDate(),
                accountProduct.getAccountBalance(),
                accountProduct.getStartDate());
    }

    private LoanProductResponse.LoanProductItem mapToLoanProductResponse(LoanProduct loanProduct) {
        return new LoanProductResponse.LoanProductItem(loanProduct.getId(),
                loanProduct.getClientId(),
                loanProduct.getProductId(),
                loanProduct.getType(),
                loanProduct.getLastChargeDate(),
                loanProduct.getOriginalAmount(),
                loanProduct.getFixedInstallment(),
                loanProduct.getStartDate(),
                loanProduct.getEndDate());
    }

    @Transactional(readOnly = true)
    public ClientProductResponse getAllClientProducts() {
        List<ClientProduct> cpList = clientProductRepository.findAll();

        List<ClientProductResponse.ClientProductItem> clientproducts = cpList
                .stream()
                .map(this::mapToClientProductResponse)
                .toList();

        log.info("Fetching all client products");
        return new ClientProductResponse(clientproducts);        
    }

    @Transactional(readOnly = true)
    public ClientProductResponse getClientProductsByClientId(Long clientId) {
        Assert.notNull(clientId, "Client ID must not be null");
        Assert.isTrue(clientId > 0, "Client ID must be a positive number");

        List<ClientProduct> cpList = clientProductRepository.findByClientId(clientId);

        List<ClientProductResponse.ClientProductItem> clientproducts = cpList
                .stream()
                .map(this::mapToClientProductResponse)
                .toList();

        log.info("Fetching product of client with id {}", clientId);
        return new ClientProductResponse(clientproducts);
    }

    @Transactional(readOnly = true)
    public AccountProductResponse getClientAccounts(Long clientId) {
        Assert.notNull(clientId, "Client ID must not be null");
        Assert.isTrue(clientId > 0, "Client ID must be a positive number");

        List<AccountProduct> apList = accountProductRepository.findByClientId(clientId);
        List<AccountProductResponse.AccountProductItem> accountproducts = apList
                .stream()
                .map(this::mapToAccountProductResponse)
                .toList();

        log.info("Fetching accounts of client with id {}", clientId);
        return new AccountProductResponse(accountproducts);
        //return accountProductRepository.findByClientId(clientId);
    }

    @Transactional(readOnly = true)
    public LoanProductResponse getClientLoans(Long clientId) {
        Assert.notNull(clientId, "Client ID must not be null");
        Assert.isTrue(clientId > 0, "Client ID must be a positive number");

        List<LoanProduct> lpList = loanProductRepository.findByClientId(clientId);
        List<LoanProductResponse.LoanProductItem> loanproducts = lpList
            .stream()
            .map(this::mapToLoanProductResponse)
            .toList();

        log.info("Fetching accounts of client with id {}", clientId);
        return new LoanProductResponse(loanproducts);
        // return loanProductRepository.findByClientId(clientId);
    }

    @Transactional(readOnly = true)
    public ClientProductResponse getClientProductById(Long clientProductId) {
        Assert.notNull(clientProductId, "Client ID must not be null");
        Assert.isTrue(clientProductId > 0, "Client ID must be a positive number");

        ClientProductResponse.ClientProductItem clientproduct = clientProductRepository.findById(clientProductId)
                .map(this::mapToClientProductResponse)
                .orElseThrow(() -> {
                    log.error("Client with id {} not found", clientProductId);
                    return new ClientProductNotFoundException(clientProductId);
                });

        log.info("Fetching clientProduct with id {}", clientProductId);
        return new ClientProductResponse(List.of(clientproduct));
    }

    @Transactional(readOnly = true)
    public AccountProductResponse getAccountProductById(Long clientProductId) {
        Assert.notNull(clientProductId, "Client ID must not be null");
        Assert.isTrue(clientProductId > 0, "Client ID must be a positive number");

        AccountProductResponse.AccountProductItem accountproduct = clientProductRepository.findById(clientProductId)
                .filter(AccountProduct.class::isInstance)
                .map(AccountProduct.class::cast)
                .map(this::mapToAccountProductResponse)
                .orElseThrow(() -> {
                    log.error("ClientProduct with id {} not found", clientProductId);
                    return new ClientProductNotFoundException(clientProductId);
                });

        log.info("Fetching clientProduct with id {}", clientProductId);
        return new AccountProductResponse(List.of(accountproduct));
    }

    @Transactional(readOnly = true)
    public LoanProductResponse getLoanProductById(Long clientProductId) {
        Assert.notNull(clientProductId, "Client ID must not be null");
        Assert.isTrue(clientProductId > 0, "Client ID must be a positive number");

       LoanProductResponse.LoanProductItem loanproduct = (clientProductRepository.findById(clientProductId))
                .filter(LoanProduct.class::isInstance)
                .map(LoanProduct.class::cast)
                .map(this::mapToLoanProductResponse)
                .orElseThrow(() -> {
                    log.error("ClientProduct with id {} not found", clientProductId);
                    return new ClientProductNotFoundException(clientProductId);
                });

        log.info("Fetching clientProduct with id {}", clientProductId);
        return new LoanProductResponse(List.of(loanproduct));
    }

    @Transactional
    public void updateLoanProductById(Long clientProductId, ClientProductRequest request) {
        Assert.notNull(clientProductId, "Client ID must not be null");
        Assert.isTrue(clientProductId > 0, "Client ID must be a positive number");

        ClientProduct cp = clientProductRepository.findById(clientProductId)
                .orElseThrow(() -> {
                    log.error("Clientproduct with id {} not found", clientProductId);
                    return new ClientProductNotFoundException(clientProductId);
                });

        // just to check, but shouldn't happen
        if (!(cp instanceof LoanProduct)) {
            throw new ClientProductNotFoundException(clientProductId);
        }

        LoanProduct loanProduct = (LoanProduct) cp;

        if (request.fixedInstallment() != null) {
            loanProduct.setFixedInstallment(request.fixedInstallment());
            clientProductRepository.save(loanProduct);
        }
    }

    @Transactional
    public void updateAccountProductById(Long clientProductId, ClientProductRequest request) {
        Assert.notNull(clientProductId, "Client ID must not be null");
        Assert.isTrue(clientProductId > 0, "Client ID must be a positive number");

        ClientProduct cp = clientProductRepository.findById(clientProductId)
                .orElseThrow(() -> {
                    log.error("Clientproduct with id {} not found", clientProductId);
                    return new ClientProductNotFoundException(clientProductId);
                });

        // just to check, but shouldn't happen
        if (!(cp instanceof AccountProduct)) {
            throw new ClientProductNotFoundException(clientProductId);
        }

        AccountProduct accountProduct = (AccountProduct) cp;

        log.info("Updating clientProduct with id {}", clientProductId);
        if (request.initialBalance() != null) {
            accountProduct.setAccountBalance(request.initialBalance());
            clientProductRepository.save(accountProduct);
        }
    }
}
