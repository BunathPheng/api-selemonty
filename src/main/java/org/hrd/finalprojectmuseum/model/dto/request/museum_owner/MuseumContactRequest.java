package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MuseumContactRequest {
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Must be a valid phone number")
    @NotBlank(message = "Contact number is required")
    private String contactNumber;
}
