package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MuseumArtifactRequest {
    @NotBlank(message = "title is required")
    private String title;
    @NotBlank(message = "description is required")
    private String description;
    @NotBlank(message = "3D model link is required")
    private String thirdDModelLink;
}
