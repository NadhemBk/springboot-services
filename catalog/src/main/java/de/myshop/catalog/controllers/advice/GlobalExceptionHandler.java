package de.myshop.catalog.controllers.advice;

import de.myshop.catalog.services.exceptions.ProductsNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductsNotFoundException.class)
    public ResponseEntity<ProductErrorResponse> handleProductsNotFound(ProductsNotFoundException ex) {
        ProductErrorResponse error = new ProductErrorResponse(
            "Some products in your request do not exist.",
            "INVALID_PRODUCT_IDS",
            ex.getMissingIds(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }
}

record ProductErrorResponse(
    String message,
    String errorCode,
    List<String> missingIds,
    LocalDateTime timestamp
) {}
