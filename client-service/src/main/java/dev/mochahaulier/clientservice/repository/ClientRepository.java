package dev.mochahaulier.clientservice.repository;

import dev.mochahaulier.clientservice.model.Client;
import io.micrometer.observation.annotation.Observed;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Observed
public interface ClientRepository extends JpaRepository<Client, Long> {
}
