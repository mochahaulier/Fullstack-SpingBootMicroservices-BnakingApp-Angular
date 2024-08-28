package dev.mochahaulier.clientservice.controller;

import dev.mochahaulier.clientservice.dto.ClientRequest;
import dev.mochahaulier.clientservice.dto.ClientResponse;
import dev.mochahaulier.clientservice.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createClient(@RequestBody @Valid ClientRequest clientRequest) {
        clientService.saveClient(clientRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ClientResponse getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientResponse getClientById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateClient(@PathVariable Long id, @RequestBody ClientRequest clientRequest) {
        clientService.updateClient(id, clientRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}