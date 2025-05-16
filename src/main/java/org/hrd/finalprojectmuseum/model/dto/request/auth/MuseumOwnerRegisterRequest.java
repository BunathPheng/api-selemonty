package org.hrd.finalprojectmuseum.model.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.enums.Role;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class MuseumOwnerRegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must be at least 3 characters")
    @Size(max = 50, message = "Name cannot be greater than 50 characters")
    private String name;

    @Schema(example = "example@gmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    @Size(max = 255, message = "Email cannot be greater than 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Size(max = 32, message = "Password cannot be greater than 32 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @Pattern(
            regexp = "^$|^(https?:\\/\\/.*\\.(?:png|jpg|jpeg|gif))$",
            message = "Must be a valid image URL (jpg, jpeg, png, gif) or empty"
    )
    private String logoLink;

    @NotNull(message = "Name is required")
    @Digits(integer = 4, fraction = 6, message = "Must be a number with up to 4 integer digits and 6 fractional digits")
    private BigDecimal lat;

    @NotNull(message = "Name is required")
    @Digits(integer = 4, fraction = 6, message = "Must be a number with up to 4 integer digits and 6 fractional digits")
    private Double lng;

    @NotBlank(message = "Description is required")
    @Max(value = 2000, message = "Description can not be greater than 2000")
    private String description;
}
