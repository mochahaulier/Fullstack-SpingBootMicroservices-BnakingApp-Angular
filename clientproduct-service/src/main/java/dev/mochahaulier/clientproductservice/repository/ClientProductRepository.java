package dev.mochahaulier.clientproductservice.repository;

// import dev.mochahaulier.clientproductservice.model.Client;
import dev.mochahaulier.clientproductservice.model.ClientProduct;
import io.micrometer.observation.annotation.Observed;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Observed
public interface ClientProductRepository extends JpaRepository<ClientProduct, Long> {
    List<ClientProduct> findByClientId(Long clientId);

    // List<ClientProduct>
    // findByClientAndProduct_ProductDefinition_ProductType(Client client,
    // ProductType productType);
    // List<ClientProduct> findByClientIdAndProductType(Long clientId, ProductType
    // productType);
}
