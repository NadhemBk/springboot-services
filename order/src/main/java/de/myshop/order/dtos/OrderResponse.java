package de.myshop.order.dtos;

import de.myshop.order.models.Category;
import de.myshop.order.models.OrderState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        OrderState state,
        Category category,
        CustomerResponse customer,
        SiteResponse site,
        List<OrderItemResponse> orderItems,
        PaymentMethodResponse paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record CustomerResponse(String id) {}
    public record SiteResponse(String id) {}
    public record PaymentMethodResponse(String type, String iban) {}
}
