package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MuseumArtifact {
    private UUID id;
    private UUID zoneId;
    private String title;
    private String description;
    private String thirdDModelLink;
    private boolean isDeleted;

}
