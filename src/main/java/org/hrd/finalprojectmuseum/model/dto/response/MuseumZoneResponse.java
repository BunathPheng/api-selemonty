package org.hrd.finalprojectmuseum.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MuseumZoneResponse {
    private UUID zoneId;
    private UUID museumId;
    private String zoneCategoryName;
    private UUID zoneCategoryId;
    private String zoneName;
    private String description;
    private String pictureLink;
    private Integer countArtifact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
