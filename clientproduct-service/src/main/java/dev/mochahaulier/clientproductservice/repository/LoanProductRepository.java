package dev.mochahaulier.clientproductservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.mochahaulier.clientproductservice.model.LoanProduct;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    List<LoanProduct> findByClientId(Long clientId);
}
