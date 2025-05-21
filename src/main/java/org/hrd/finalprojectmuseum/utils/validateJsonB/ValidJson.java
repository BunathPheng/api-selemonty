package org.hrd.finalprojectmuseum.utils.validateJsonB;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = JsonStructureValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJson {
    String message() default "Invalid JSON structure";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String[] requiredFields() default {};
    int maxSize() default Integer.MAX_VALUE;
}