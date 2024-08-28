package dev.mochahaulier.clientproductservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.mochahaulier.clientproductservice.model.AccountProduct;
// import dev.mochahaulier.clientproductservice.model.Client;

public interface AccountProductRepository extends JpaRepository<AccountProduct, Long> {
    // List<AccountProduct> findByClient(Client client);
    List<AccountProduct> findByClientId(Long clientId);
}
