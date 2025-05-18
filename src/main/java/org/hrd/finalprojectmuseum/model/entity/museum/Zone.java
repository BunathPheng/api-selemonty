package org.hrd.finalprojectmuseum.model.entity.museum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Zone {
    private UUID zoneId;
    private UUID museumId;
    private UUID zoneCategoryId;
    private String zoneName;
    private String description;
    private String pictureLink;
    private String videoLink;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isDeleted;
}
