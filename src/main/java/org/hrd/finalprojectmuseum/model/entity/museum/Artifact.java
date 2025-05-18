package org.hrd.finalprojectmuseum.model.entity.museum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Artifact {
    private UUID id;
    private UUID zoneId;
    private String title;
    private String description;
    private String thirdDModelLink;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isDeleted;

}
