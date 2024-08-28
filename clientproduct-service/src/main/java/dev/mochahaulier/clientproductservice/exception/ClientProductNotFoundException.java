package dev.mochahaulier.clientproductservice.exception;

public class ClientProductNotFoundException extends RuntimeException {
    public ClientProductNotFoundException(Long id) {
        super("No valid product with id " + id + " found.");
    }
}
