package de.myshop.order.controllers.advice;

import de.myshop.order.services.exceptions.CatalogUnavailableException;
import de.myshop.order.services.exceptions.IdempotencyConflictException;
import de.myshop.order.services.exceptions.IllegalStateTransitionException;
import de.myshop.order.services.exceptions.ProductValidationException;
import de.myshop.order.services.exceptions.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ErrorResponse> handleIdempotency(IdempotencyConflictException ex) {
        return buildError(HttpStatus.CONFLICT, "IDEMPOTENCY_CONFLICT", ex.getMessage());
    }

    @ExceptionHandler({IllegalStateException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return buildError(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(ProductValidationException.class)
    public ResponseEntity<ErrorResponse> handleCatalogError(ProductValidationException ex) {
        return buildError(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_PRODUCTS", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateTransitionException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateTransition(IllegalStateTransitionException ex) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "INVALID_STATE_TRANSITION",
                ex.getMessage()
        );
    }

    @ExceptionHandler(CatalogUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleCatalogUnavailable(CatalogUnavailableException ex) {
        return buildError(
                HttpStatus.SERVICE_UNAVAILABLE,
                "CATALOG_SERVICE_OFFLINE",
                ex.getMessage()
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return buildError(
                HttpStatus.NOT_FOUND,
                "ORDER_NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonErrors(HttpMessageNotReadableException ex) {
        String message = "Invalid input. Check your fields and enums.";

        if (ex.getMessage() != null && ex.getMessage().contains("de.myshop.order.models")) {
            message = "Invalid value for Category or State. Allowed Categories: B2B, B2C. " +
                    "Allowed States: DRAFT, PREVIEW, SUBMITTED, CONFIRMED.";
        }

        return buildError(HttpStatus.BAD_REQUEST, "INVALID_FORMAT", message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "INVALID_INPUT",
                ex.getMessage()
        );
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String code, String msg) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                code,
                msg
        );
        return new ResponseEntity<>(error, status);
    }
}

record ErrorResponse(LocalDateTime timestamp, int status, String errorCode, String message) {}
