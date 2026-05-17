package de.myshop.catalog.controllers;

import de.myshop.catalog.controllers.dto.ProductResponse;
import de.myshop.catalog.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductOfferingController {

    private final ProductService productService;

    @PostMapping("/search")
    public ResponseEntity<List<ProductResponse>> getProductsByIds(@RequestBody List<String> ids) {
        return ResponseEntity.ok(productService.getProductsByIds(ids));
    }
}
