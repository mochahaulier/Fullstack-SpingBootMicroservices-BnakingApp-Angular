package dev.mochahaulier.productservice.exception;

import java.util.List;

public class ProcessingException extends RuntimeException {
    private List<String> errors;

    public ProcessingException(List<String> errors) {
        super("Errors occurred during processing the definitions!");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
