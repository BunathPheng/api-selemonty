package org.hrd.finalprojectmuseum.utils.valide_age;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinAgeValidator implements ConstraintValidator<MinAge, LocalDate> {

    private int minAge;

    @Override
    public void initialize(MinAge constraintAnnotation) {
        this.minAge = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true; // Let @NotNull handle null validation
        }

        LocalDate today = LocalDate.now();
        LocalDate minBirthDate = today.minusYears(minAge);

        return birthDate.isBefore(minBirthDate) || birthDate.isEqual(minBirthDate);
    }
}