package de.myshop.order.models;

import de.myshop.order.validation.ValidPaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

        @NotNull(message = "Payment type is required")
        @Enumerated(EnumType.STRING)
        @Column(name = "payment_type")
        private PaymentType type;

        @Column(name = "payment_iban")
        private String iban;

}

