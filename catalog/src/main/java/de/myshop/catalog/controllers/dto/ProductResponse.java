package de.myshop.catalog.controllers.dto;

import java.math.BigDecimal;

public record ProductResponse(String id, String name, BigDecimal price) {}
