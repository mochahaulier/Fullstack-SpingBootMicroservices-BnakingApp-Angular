package dev.mochahaulier.productservice.repository;

import dev.mochahaulier.productservice.model.ProductDefinition;
import io.micrometer.observation.annotation.Observed;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Observed
public interface ProductDefinitionRepository extends JpaRepository<ProductDefinition, String> {
    @Cacheable("productDefinitions")
    Optional<ProductDefinition> findByProductKey(String productKey);
}