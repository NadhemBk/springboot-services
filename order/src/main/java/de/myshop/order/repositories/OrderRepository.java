package de.myshop.order.repositories;

import de.myshop.order.models.Category;
import de.myshop.order.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdempotencyKey(String idempotencyKey);
    Page<Order> findByCategory(Category category, Pageable pageable);
}
