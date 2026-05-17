package de.myshop.order.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentMethodValidator.class)
@Documented
public @interface ValidPaymentMethod {
    String message() default "IBAN is required for DIRECT_DEBIT";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

