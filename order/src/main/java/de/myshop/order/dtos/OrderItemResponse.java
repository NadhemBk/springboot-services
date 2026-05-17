package de.myshop.order.dtos;

public record OrderItemResponse(
        String productOfferingId,
        Integer quantity
) {}
