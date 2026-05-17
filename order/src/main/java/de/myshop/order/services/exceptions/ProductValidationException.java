package de.myshop.order.services.exceptions;

import lombok.Getter;
import java.util.List;

@Getter
public class ProductValidationException extends RuntimeException {
    private final List<String> missingIds;

    public ProductValidationException(String message) {
        super(message);
        this.missingIds = List.of();
    }

    public ProductValidationException(String message, List<String> missingIds) {
        super(message);
        this.missingIds = missingIds;
    }
}
