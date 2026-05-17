package de.myshop.order.services.exceptions;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String id) {
        super("Order with ID " + id + " not found");
    }
}