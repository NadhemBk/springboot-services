package de.myshop.order.dtos;

import de.myshop.order.models.Category;
import de.myshop.order.validation.ValidPaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
        @NotNull Category category,
        @NotNull String customerId,
        @NotNull String siteId,
        @NotEmpty @Valid List<OrderItemRequest> orderItems,
        @NotNull @Valid PaymentMethodRequest paymentMethod
) {}
