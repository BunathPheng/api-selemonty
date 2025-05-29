package org.hrd.finalprojectmuseum.model.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AdminRequest {
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9\\s]{1,254}$",
            message = "Name must start with a letter and be 2–255 characters"
    )
    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(
            regexp = "^(https?:\\/\\/.*\\.(?:png|jpg|jpeg|gif))$",
            message = "Must be a valid image URL (jpg, jpeg, png, gif)"
    )
    @NotBlank(message = "Profile image link is required")
    private String profileImageLink;
}
