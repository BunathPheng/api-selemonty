package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumZoneResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;
import org.hrd.finalprojectmuseum.repository.ArtifactRepository;
import org.hrd.finalprojectmuseum.repository.ZoneRepository;
import org.hrd.finalprojectmuseum.service.ZoneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final ArtifactRepository artifactRepository;
    LocalDateTime updatedAt = LocalDateTime.now();

    @Override
    public List<MuseumZoneCategory> getAllZonesCategories() {
        List<MuseumZoneCategory> museumZoneCategories = zoneRepository.getAllMuseumZoneCategories();
        if (museumZoneCategories.isEmpty()) {
            throw new AppNotFoundException("Museum Zone Category Not Found");
        }
        return museumZoneCategories;
    }

    @Transactional
    @Override
    public void createMuseumZone(MuseumZoneRequest museumZoneRequest, UUID museumId) {
        boolean zoneCategoryId = zoneRepository.retrieveMuseumZoneCategoryId(museumZoneRequest.getCategoryId());
        if (!zoneCategoryId) {
            throw new AppNotFoundException("Museum zone category Id Not Found");
        }

        UUID museumZoneId = zoneRepository.createMuseumZone(museumZoneRequest, museumId, LocalDateTime.now());
        if(museumZoneId == null) {
            throw new AppNotFoundException("Museum Zone Id Not Found");
        }

        if (museumZoneRequest.getArtifacts() != null && !museumZoneRequest.getArtifacts().isEmpty()) {
            for (MuseumArtifactRequest artifact : museumZoneRequest.getArtifacts()) {
                artifactRepository.createMuseumArtifact(artifact, museumZoneId, updatedAt);
            }
        }
    }

    @Override
    public List<MuseumZoneCategory> getAllZonesCategoriesByMuseumId(UUID museumId) {
        return zoneRepository.retrieveAllZonesCategoriesByMuseumID(museumId);
    }

    @Override
    public MuseumZone getMuseumZoneDetailByZoneId(UUID zoneId) {
        MuseumZone museumZone = zoneRepository.retrieveMuseumZoneDetailByZoneId(zoneId);
        if (museumZone == null) {
            throw new AppNotFoundException("Museum zone ID not found");
        }
        return museumZone;
    };

    @Override
    public void updateMuseumZoneDetailByZoneId(UUID museumZoneId, MuseumZoneUpdateRequest museumZoneUpdateRequest) {
        boolean zoneId = zoneRepository.retrieveMuseumZoneId(museumZoneId);
        if (!zoneId) {
            throw new AppNotFoundException("Museum zone Id Not Found");
        }

        boolean zoneCategoryId = zoneRepository.retrieveMuseumZoneCategoryId(museumZoneUpdateRequest.getCategoryId());
        if (!zoneCategoryId) {
            throw new AppNotFoundException("Museum zone category Id Not Found");
        }
        zoneRepository.updateMuseumZoneDetailByZoneId(museumZoneId, museumZoneUpdateRequest, updatedAt);
    }

    @Override
    public void deleteMuseumZoneByZoneId(UUID museumZoneId) {
        boolean zoneId = zoneRepository.retrieveMuseumZoneId(museumZoneId);
        if (!zoneId) {
            throw new AppNotFoundException("Museum zone Id Not Found");
        }
        zoneRepository.deleteMuseumZoneByZoneId(museumZoneId, true);
        artifactRepository.deleteMuseumArtifactByMuseumId(museumZoneId, true);
    }

    @Override
    public List<MuseumZoneResponse> getAllMuseumZonesByMuseumId(UUID museumId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<MuseumZoneResponse> museumZoneResponses = zoneRepository.retrieveMuseumZoneByMuseumId(museumId, size, offset);
        if (museumZoneResponses == null) {
            throw new AppNotFoundException("Museum Zone Not Found");
        }
        return museumZoneResponses;
    }

    @Override
    public Integer getTotalMuseumZonesByMuseumId(UUID museumId) {
        return zoneRepository.countMuseumZonesByMuseumId(museumId);
    }

    @Override
    public UUID getMuseumIdByUserId(UUID userId) {
        if(zoneRepository.retrieveMuseumIDbyUserID(userId) == null) {
            throw new AppNotFoundException("Museum ID Not Found");
        }
        return zoneRepository.retrieveMuseumIDbyUserID(userId);
    }



}
