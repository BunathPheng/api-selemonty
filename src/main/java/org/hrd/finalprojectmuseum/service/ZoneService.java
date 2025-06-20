package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumZoneResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.ArtifactZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;

import java.util.List;
import java.util.UUID;

public interface ZoneService {
    List<MuseumZoneCategory> getAllZonesCategories();
    void createMuseumZone(MuseumZoneRequest museumZoneRequest, UUID museumId);
    UUID getMuseumIdByUserId(UUID userId);
    List<MuseumZoneCategory> getAllZonesCategoriesByMuseumId(UUID museumId);
    MuseumZone getMuseumZoneDetailByZoneId(UUID museumId);
    void updateMuseumZoneDetailByZoneId(UUID museumZoneId, MuseumZoneUpdateRequest museumZoneUpdateRequest, UUID museumId);
    void deleteMuseumZoneByZoneId(UUID zoneId, UUID museumId);
    List<MuseumZoneResponse> getAllMuseumZonesByMuseumId(UUID museumId, String search, UUID categoryId, Integer page, Integer size);
    Integer getTotalMuseumZonesByMuseumId(UUID museumId, String search, UUID categoryId);
    ArtifactZone getAmountArtifactZone(UUID museumId);
}
