package de.myshop.catalog.services;

import de.myshop.catalog.controllers.dto.ProductResponse;
import de.myshop.catalog.models.ProductOffering;
import de.myshop.catalog.repositories.ProductOfferingRepository;
import de.myshop.catalog.services.exceptions.ProductsNotFoundException;
import de.myshop.catalog.services.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductOfferingRepository repository;
    private final ProductMapper mapper;

    public List<ProductResponse> getProductsByIds(List<String> requestedIds) {
        Set<String> uniqueRequestedIds = new HashSet<>(requestedIds);

        List<ProductOffering> foundEntities = repository.findAllById(uniqueRequestedIds);
        
        Set<String> foundIds = foundEntities.stream()
                .map(ProductOffering::getId)
                .collect(Collectors.toSet());

        if (foundIds.size() < uniqueRequestedIds.size()) {
            List<String> missingIds = uniqueRequestedIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ProductsNotFoundException(missingIds);
        }

        return foundEntities.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
