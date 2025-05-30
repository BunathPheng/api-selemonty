package org.hrd.finalprojectmuseum.model.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminRequest {
    @NotBlank(message = "Name is required")
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9\\s]*$",
            message = "Name must start with a letter and contain only letters, numbers, and spaces"
    )
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @NotBlank(message = "Profile image link is required")
    @Pattern(
            regexp = "^https?://.*\\.(png|jpg|jpeg|gif|webp)(?:\\?.*)?$",
            message = "Must be a valid image URL ending with png, jpg, jpeg, gif, or webp"
    )
    private String profileImageLink;
}
