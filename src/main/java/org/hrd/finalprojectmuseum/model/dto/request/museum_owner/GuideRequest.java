package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import lombok.Data;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GuideRequest {

    @NotBlank(message = "Guide name is required")
    @Size(min = 2, max = 100, message = "Guide name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Guide name should only contain letters and spaces")
    private String guideName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "Contact number must be 8-15 digits and may start with +")
    private String contactNumber;

    @NotBlank(message = "QR link is required")
    @URL(message = "Please provide a valid URL for the QR link")
    @Size(max = 500, message = "QR link must not exceed 500 characters")
    private String staticQrLink;

    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;
}