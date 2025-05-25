package org.hrd.finalprojectmuseum.service.museum;

import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;

import java.util.UUID;

public interface MuseumArtifactService {
    MuseumArtifact createMuseumArtifactByZoneId(MuseumArtifactRequest museumArtifactRequests, UUID museumZoneId);
    void updateMuseumArtifactByArtifactId(UUID artifactId, MuseumArtifactRequest museumArtifactRequest);
    void deleteMuseumArtifactByArtifactId(UUID artifactId);
}
