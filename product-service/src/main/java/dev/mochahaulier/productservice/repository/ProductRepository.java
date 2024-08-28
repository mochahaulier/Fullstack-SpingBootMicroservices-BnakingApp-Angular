package dev.mochahaulier.productservice.repository;

import dev.mochahaulier.productservice.model.Product;
import dev.mochahaulier.productservice.model.ProductDefinition;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductDefinition(ProductDefinition productDefinition);
}
