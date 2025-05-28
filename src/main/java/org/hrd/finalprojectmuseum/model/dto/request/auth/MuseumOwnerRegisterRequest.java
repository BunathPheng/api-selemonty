package org.hrd.finalprojectmuseum.model.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hrd.finalprojectmuseum.model.enums.Role;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class MuseumOwnerRegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Schema(example = "example@gmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    @Size(max = 255, message = "Email cannot be greater than 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Logo is required")
    @Pattern(
            regexp = "^https?://.*\\.(png|jpg|jpeg|gif)$",
            message = "Must be a valid image URL (jpg, jpeg, png, gif)"
    )
    private String logoLink;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Digits(integer = 2, fraction = 6, message = "Latitude must have up to 2 integer digits and 6 fractional digits")
    private BigDecimal lat;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Digits(integer = 3, fraction = 6, message = "Longitude must have up to 3 integer digits and 6 fractional digits")
    private BigDecimal lng;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot be greater than 2000 characters")
    private String description;
}
