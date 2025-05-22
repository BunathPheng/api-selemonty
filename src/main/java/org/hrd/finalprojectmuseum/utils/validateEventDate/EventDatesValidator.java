package org.hrd.finalprojectmuseum.utils.validateEventDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hrd.finalprojectmuseum.model.dto.request.EventRequest;
import org.hrd.finalprojectmuseum.utils.validateEventDate.ValidateEventDate;
import org.springframework.stereotype.Component;

@Component
public class EventDatesValidator implements ConstraintValidator<ValidateEventDate, EventRequest> {

    @Override
    public boolean isValid(EventRequest request, ConstraintValidatorContext context) {
        // Skip validation if either date is null
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true;
        }

        // Completely disable the default constraint violation
        context.disableDefaultConstraintViolation();

        // Validate that end date is after start date
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            context.buildConstraintViolationWithTemplate("End date must be after start date")
                    .addPropertyNode("endDate")  // This targets the specific field
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}