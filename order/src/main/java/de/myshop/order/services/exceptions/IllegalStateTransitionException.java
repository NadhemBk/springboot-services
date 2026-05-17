package de.myshop.order.services.exceptions;

public class IllegalStateTransitionException extends RuntimeException {
    public IllegalStateTransitionException(String from, String to) {
        super("Invalid state transition from " + from + " to " + to);
    }
}
