package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;

import java.util.List;
import java.util.UUID;

@Data
public class MuseumZoneRequest {
    private String name;
    private UUID categoryId;
    private String videoLink;
    private String description;
    private String imageLink;
    private List<MuseumArtifact> artifacts;
}
