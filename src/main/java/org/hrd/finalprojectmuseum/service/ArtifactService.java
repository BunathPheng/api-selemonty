package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;

import java.util.UUID;

public interface ArtifactService {
    MuseumArtifact createMuseumArtifactByZoneId(MuseumArtifactRequest museumArtifactRequests, UUID museumZoneId, UUID museumId);
    void updateMuseumArtifactByArtifactId(UUID artifactId, MuseumArtifactRequest museumArtifactRequest, UUID museumId);
    void deleteMuseumArtifactByArtifactId(UUID artifactId, UUID museumId);
    MuseumArtifact getMuseumArtifactByArtifactId(UUID artifactId);

    ListResponse<MuseumArtifact> getAllMuseumArtifactByZoneId(UUID zoneId, String search, Integer page, Integer size);
}
