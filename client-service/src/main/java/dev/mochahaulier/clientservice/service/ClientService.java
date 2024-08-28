package dev.mochahaulier.clientservice.service;

import dev.mochahaulier.clientservice.dto.ClientRequest;
import dev.mochahaulier.clientservice.dto.ClientResponse;
import dev.mochahaulier.clientservice.exception.ClientNotFoundException;
import dev.mochahaulier.clientservice.model.Client;
import dev.mochahaulier.clientservice.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public void saveClient(ClientRequest clientRequest) {
        Client client = Client.builder()
                .firstName(clientRequest.firstName())
                .lastName(clientRequest.lastName())
                .email(clientRequest.email())
                .phone(clientRequest.phone())
                .build();

        clientRepository.save(client);
        log.info("Client {} is saved", client.getId());
    }

    @Transactional
    public void updateClient(Long id, ClientRequest clientRequest) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        boolean updated = false;
        if (clientRequest.firstName() != null) {
            client.setFirstName(clientRequest.firstName());
            updated = true;
        }
        if (clientRequest.lastName() != null) {
            client.setLastName(clientRequest.lastName());
            updated = true;
        }
        if (clientRequest.email() != null) {
            client.setEmail(clientRequest.email());
            updated = true;
        }
        if (clientRequest.phone() != null) {
            client.setPhone(clientRequest.phone());
            updated = true;
        }

        if (updated) {
            log.info("Updating client with id {}", id);
            client = clientRepository.save(client);
        } else {
            log.info("No fields to update for client with id {}", id);
        }
    }

    @Transactional(readOnly = true)
    public ClientResponse getAllClients() {
        List<Client> clients = clientRepository.findAll();

        List<ClientResponse.ClientItem> clientList =  clients.stream().map(this::mapToClientResponse).toList();

        return new ClientResponse(clientList);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long id) {
        Assert.notNull(id, "Client ID must not be null");
        Assert.isTrue(id > 0, "Client ID must be a positive number");

        ClientResponse.ClientItem client = clientRepository.findById(id)
                .map(this::mapToClientResponse)
                .orElseThrow(() -> {
                    log.error("Client with id {} not found", id);
                    return new ClientNotFoundException(id);
                });

        log.info("Fetching client with id {}", id);
        return new ClientResponse(List.of(client));
    }

    private ClientResponse.ClientItem mapToClientResponse(Client client) {
        return new ClientResponse.ClientItem(client.getId(), client.getFirstName(),
                client.getLastName(), client.getEmail(), client.getPhone());
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}