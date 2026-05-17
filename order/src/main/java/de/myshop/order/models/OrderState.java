package de.myshop.order.models;

public enum OrderState {
    DRAFT, PREVIEW, SUBMITTED, CONFIRMED;

    public boolean canTransitionTo(OrderState newState) {
        return switch (this) {
            case DRAFT -> newState == PREVIEW;
            case PREVIEW -> newState == DRAFT || newState == SUBMITTED;
            case SUBMITTED -> newState == CONFIRMED;
            case CONFIRMED -> false;
        };
    }

    public boolean isEditable() {
        return this == DRAFT || this == PREVIEW;
    }
}

