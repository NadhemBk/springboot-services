package de.myshop.order.dtos;


import de.myshop.order.models.PaymentType;
import de.myshop.order.validation.ValidPaymentMethod;
import jakarta.validation.constraints.NotNull;

@ValidPaymentMethod
public record PaymentMethodRequest(
        @NotNull(message = "payment type is required")
        PaymentType type,
        String iban
) {}

