package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MuseumZoneRequest {
    @NotBlank(message = "name is required")
    private String name;
    @NotNull(message = "Category Id is required")
    private UUID categoryId;
    private String videoLink;
    private String description;
    @NotBlank(message = "Category is required")
    private String pictureLink;
    private List<MuseumArtifactRequest> artifacts;
}
