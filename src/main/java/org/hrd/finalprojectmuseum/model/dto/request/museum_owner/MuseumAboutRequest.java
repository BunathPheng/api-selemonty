package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class MuseumAboutRequest {
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9\\s]{1,254}$",
            message = "Name must start with a letter and be 2–255 characters"
    )
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Category Id is required")
    private UUID categoryId;

    @NotBlank(message = "Description is required")
    private String description;
}
