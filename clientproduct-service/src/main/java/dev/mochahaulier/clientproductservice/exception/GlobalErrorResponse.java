package dev.mochahaulier.clientproductservice.exception;

public record GlobalErrorResponse (
    String error,
    String message,
    int status) {
}
