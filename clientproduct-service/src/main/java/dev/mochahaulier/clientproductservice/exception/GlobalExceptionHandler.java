package dev.mochahaulier.clientproductservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GlobalErrorResponse> handleRuntimeException(RuntimeException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof ClientNotFoundException) {
            return handleClientNotFoundException((ClientNotFoundException) cause);
        } else if (cause instanceof ProductNotFoundException) {
            return handleProductNotFoundException((ProductNotFoundException) cause);
        } else if (cause instanceof ClientServiceUnavailableException) {
            return handleClientServiceUnavailableException((ClientServiceUnavailableException) cause);
        } else if (cause instanceof ProductServiceUnavailableException) {
            return handleProductServiceUnavailableException((ProductServiceUnavailableException) cause);
        }
        logger.error("Unhandled RuntimeException: ", ex);
        return handleGeneralException(ex);
    }

    // @ExceptionHandler(CompletionException.class)
    // public ResponseEntity<GlobalErrorResponse> handleCompletionException(CompletionException ex) {
    //     Throwable cause = ex.getCause();
    //     if (cause instanceof ClientNotFoundException) {
    //         return handleClientNotFoundException((ClientNotFoundException) cause);
    //     } else if (cause instanceof ProductNotFoundException) {
    //         return handleProductNotFoundException((ProductNotFoundException) cause);
    //     } else if (cause instanceof ClientServiceUnavailableException) {
    //         return handleClientServiceUnavailableException((ClientServiceUnavailableException) cause);
    //     } else if (cause instanceof ProductServiceUnavailableException) {
    //         return handleProductServiceUnavailableException((ProductServiceUnavailableException) cause);
    //     }
    //     logger.error("Unhandled CompletionException: ", ex);
    //     return handleGeneralException(ex);
    // }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleClientNotFoundException(ClientNotFoundException ex) {
        logger.warn("ClientNotFoundException: ", ex);
        GlobalErrorResponse response = new GlobalErrorResponse("Client Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        logger.warn("ProductNotFoundException: ", ex);
        GlobalErrorResponse response = new GlobalErrorResponse("Product Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClientServiceUnavailableException.class)
    public ResponseEntity<GlobalErrorResponse> handleClientServiceUnavailableException(ClientServiceUnavailableException ex) {
        logger.warn("ClientServiceUnavailableException: ", ex);
        logger.debug("ClientServiceUnavailableException: ", ex);
        GlobalErrorResponse response = new GlobalErrorResponse("Client Service Unavailable", ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ProductServiceUnavailableException.class)
    public ResponseEntity<GlobalErrorResponse> handleProductServiceUnavailableException(ProductServiceUnavailableException ex) {
        logger.warn("ProductServiceUnavailableException: ", ex);
        logger.debug("ProductServiceUnavailableException: ", ex);
        GlobalErrorResponse response = new GlobalErrorResponse("Product Service Unavailable", ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Unhandled exception: ", ex);
        logger.debug("Unhandled exception: ", ex);
        GlobalErrorResponse response = new GlobalErrorResponse("Internal Server Error", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

// @ControllerAdvice
// public class GlobalExceptionHandler {

//     @ExceptionHandler(CompletionException.class)
//     public ResponseEntity<GlobalErrorResponse> handleCompletionException(CompletionException ex) {
//         Throwable cause = ex.getCause();
//         if (cause instanceof ClientNotFoundException) {
//             return handleClientNotFoundException((ClientNotFoundException) cause);
//         } else if (cause instanceof ProductNotFoundException) {
//             return handleProductNotFoundException((ProductNotFoundException) cause);
//         } else if (cause instanceof ClientServiceUnavailableException) {
//             return handleClientServiceUnavailableException((ClientServiceUnavailableException) cause);
//         } else if (cause instanceof ProductServiceUnavailableException) {
//             return handleProductServiceUnavailableException((ProductServiceUnavailableException) cause);
//         }
//         return handleGeneralException(ex);
//     }

//     @ExceptionHandler(ClientNotFoundException.class)
//     public ResponseEntity<GlobalErrorResponse> handleClientNotFoundException(ClientNotFoundException ex) {
//         GlobalErrorResponse response = new GlobalErrorResponse("Client Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value());
//         return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//     }

//     @ExceptionHandler(ProductNotFoundException.class)
//     public ResponseEntity<GlobalErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
//         GlobalErrorResponse response = new GlobalErrorResponse("Product Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value());
//         return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//     }

//     @ExceptionHandler(ClientServiceUnavailableException.class)
//     public ResponseEntity<GlobalErrorResponse> handleClientServiceUnavailableException(ClientServiceUnavailableException ex) {
//         GlobalErrorResponse response = new GlobalErrorResponse("Client Service Unavailable", ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value());
//         return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
//     }

//     @ExceptionHandler(ProductServiceUnavailableException.class)
//     public ResponseEntity<GlobalErrorResponse> handleProductServiceUnavailableException(ProductServiceUnavailableException ex) {
//         GlobalErrorResponse response = new GlobalErrorResponse("Product Service Unavailable", ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value());
//         return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
//     }

//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<GlobalErrorResponse> handleGeneralException(Exception ex) {
//         GlobalErrorResponse response = new GlobalErrorResponse("Internal Server Error", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
//         return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//     }
// }
