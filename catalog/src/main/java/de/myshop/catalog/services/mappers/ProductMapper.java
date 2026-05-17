package de.myshop.catalog.services.mappers;

import de.myshop.catalog.controllers.dto.ProductResponse;
import de.myshop.catalog.models.ProductOffering;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toResponse(ProductOffering entity) {
        return new ProductResponse(
            entity.getId(),
            entity.getName(),
            entity.getPrice()
        );
    }
}
