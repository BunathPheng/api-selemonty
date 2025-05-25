package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MuseumZone1 {
    private UUID zoneId;
    private UUID museumId;
    private String zoneCategoryName;
    private String zoneName;
    private String description;
    private String pictureLink;
    private String videoLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private List<MuseumArtifact> artifacts;
}
