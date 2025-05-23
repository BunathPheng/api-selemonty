package org.hrd.finalprojectmuseum.service.museum;

import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;

import java.util.List;
import java.util.UUID;

public interface MuseumZoneService {
    List<MuseumZoneCategory> getAllZonesCategories();
    void createMuseumZone(MuseumZoneRequest museumZoneRequest, UUID museumId);
    UUID getMuseumIdByUserId(UUID userId);
    MuseumArtifact createMuseumArtifactByZoneId(MuseumArtifactRequest museumArtifactRequests, UUID museumZoneId);
    List<MuseumZoneCategory> getAllZonesCategoriesByMuseumId(UUID museumId);
    MuseumZone getMuseumZoneDetailByZoneId(UUID museumId);
    void updateMuseumZoneDetailByZoneId(UUID museumZoneId, MuseumZoneUpdateRequest museumZoneUpdateRequest);
    void updateMuseumArtifactByArtifactId(UUID artifactId, MuseumArtifactRequest museumArtifactRequest);
    void deleteMuseumArtifactByArtifactId(UUID artifactId);
    void deleteMuseumZoneByZoneId(UUID zoneId);

}
