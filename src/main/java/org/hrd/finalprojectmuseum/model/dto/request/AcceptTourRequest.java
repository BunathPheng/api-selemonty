package org.hrd.finalprojectmuseum.model.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class AcceptTourRequest {
    @NotNull(message = "Tour price cannot be null")
    @Positive(message = "Tour price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Tour price must have at most 8 digits and 2 decimal places")
    private BigDecimal tourPrice;

    @NotNull(message = "Guide Id is request")
    private List<UUID> guideId;
}
