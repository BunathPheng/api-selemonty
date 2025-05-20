package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import lombok.Data;

@Data
public class MuseumArtifactRequest {
    private String title;
    private String description;
    private String image3DLink;
}
