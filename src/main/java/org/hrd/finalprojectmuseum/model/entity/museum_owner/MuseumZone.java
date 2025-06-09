package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MuseumZone {
    private UUID zoneId;
    private UUID museumId;
    private String zoneCategoryName;
    private String zoneName;
    private String description;
    private String pictureLink;
    private String videoLink;
    private Integer countArtifact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private List<MuseumArtifact> artifacts;
}
