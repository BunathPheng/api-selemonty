package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MuseumLocationRequest {
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Digits(integer = 3, fraction = 8, message = "Latitude must have up to 3 integer digits and 8 fractional digits")
    private BigDecimal lat;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Digits(integer = 4, fraction = 8, message = "Longitude must have up to 4 integer digits and 8 fractional digits")
    private BigDecimal lng;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot be greater than 255 characters")
    private String address;
}
