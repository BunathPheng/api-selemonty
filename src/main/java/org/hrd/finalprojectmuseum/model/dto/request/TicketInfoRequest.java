package org.hrd.finalprojectmuseum.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TicketInfoRequest {

    @NotNull(message = "Local price cannot be null")
    @Positive(message = "Local price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Local price must have at most 8 digits and 2 decimal places")
    @DecimalMax(value = "99999999.99", message = "Local price must be less than or equal to 99999999.99")
    private BigDecimal localPrice;

    @NotNull(message = "Foreign price cannot be null")
    @Positive(message = "Foreign price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Foreign price must have at most 8 digits and 2 decimal places")
    @DecimalMax(value = "99999999.99", message = "Foreign price must be less than or equal to 99999999.99")
    private BigDecimal foreignPrice;

    @NotNull(message = "Total slot cannot be null")
    @Min(value = 0, message = "Total slot must be at least 0")
    private Integer totalSlot;
}
