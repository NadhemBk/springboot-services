package de.myshop.catalog.repositories;

import de.myshop.catalog.models.ProductOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOfferingRepository extends JpaRepository<ProductOffering, String> {
    
}
