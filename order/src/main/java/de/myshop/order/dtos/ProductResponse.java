package de.myshop.order.dtos;

import java.math.BigDecimal;

public record ProductResponse(String id, String name, BigDecimal price) {}

