package de.myshop.catalog.services.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductsNotFoundException extends RuntimeException {
    private final List<String> missingIds;
}
