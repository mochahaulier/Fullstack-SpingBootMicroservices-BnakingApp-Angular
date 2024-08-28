package dev.mochahaulier.transactionservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.mochahaulier.transactionservice.model.Transaction;
import io.micrometer.observation.annotation.Observed;

@Repository
@Observed
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByClientId(Long clientId);
}