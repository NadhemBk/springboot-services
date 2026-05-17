package de.myshop.order.validation;

import de.myshop.order.models.PaymentMethod;
import de.myshop.order.models.PaymentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentMethodValidator implements ConstraintValidator<ValidPaymentMethod, PaymentMethod> {

    @Override
    public boolean isValid(PaymentMethod paymentMethod, ConstraintValidatorContext context) {
        if (paymentMethod == null || paymentMethod.getType() == null) return true;

        if (paymentMethod.getType() == PaymentType.DIRECT_DEBIT) {
            return paymentMethod.getIban() != null && !paymentMethod.getIban().isBlank();
        }

        return true;
    }
}

