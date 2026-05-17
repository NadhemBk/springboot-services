package de.myshop.catalog.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "product_offering")
@Getter
@Setter
public class ProductOffering {

    @Id
    private String id;

    private String name;

    private BigDecimal price;
}
