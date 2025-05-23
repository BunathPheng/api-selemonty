package org.hrd.finalprojectmuseum.service.museum.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;
import org.hrd.finalprojectmuseum.repository.museum.MuseumZoneRepository;
import org.hrd.finalprojectmuseum.service.museum.MuseumZoneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MuseumZoneServiceImpl implements MuseumZoneService {

    private final MuseumZoneRepository museumZoneRepository;
    LocalDateTime updatedAt = LocalDateTime.now();

    @Override
    public List<MuseumZoneCategory> getAllZonesCategories() {
        List<MuseumZoneCategory> museumZoneCategories = museumZoneRepository.getAllMuseumZoneCategories();
        if (museumZoneCategories.isEmpty()) {
            throw new AppNotFoundException("Museum Zone Category Not Found");
        }
        return museumZoneCategories;
    }

    @Transactional
    @Override
    public void createMuseumZone(MuseumZoneRequest museumZoneRequest, UUID museumId) {
        boolean zoneCategoryId = museumZoneRepository.retrieveMuseumZoneCategoryId(museumZoneRequest.getCategoryId());
        if (!zoneCategoryId) {
            throw new AppNotFoundException("Museum zone category Id Not Found");
        }

        UUID museumZoneId = museumZoneRepository.createMuseumZone(museumZoneRequest, museumId, LocalDateTime.now());
        if(museumZoneId == null) {
            throw new AppNotFoundException("Museum Zone Id Not Found");
        }

        if (museumZoneRequest.getArtifacts() != null && !museumZoneRequest.getArtifacts().isEmpty()) {
            for (MuseumArtifactRequest artifact : museumZoneRequest.getArtifacts()) {
                museumZoneRepository.createMuseumArtifact(artifact, museumZoneId, updatedAt);
            }
        }
    }

    @Override
    public MuseumArtifact createMuseumArtifactByZoneId(MuseumArtifactRequest museumArtifactRequest, UUID museumZoneId) {
        boolean zoneId = museumZoneRepository.retrieveMuseumZoneId(museumZoneId);
        if (!zoneId) {
            throw new AppNotFoundException("Museum zone Id Not Found");
        }
        return museumZoneRepository.createMuseumArtifact(museumArtifactRequest, museumZoneId, updatedAt);
    }

    @Override
    public List<MuseumZoneCategory> getAllZonesCategoriesByMuseumId(UUID museumId) {
        return museumZoneRepository.retrieveAllZonesCategoriesByMuseumID(museumId);
    }

    @Override
    public MuseumZone getMuseumZoneDetailByZoneId(UUID zoneId) {
        MuseumZone museumZone = museumZoneRepository.retrieveMuseumZoneDetailByZoneId(zoneId);
        if (museumZone == null) {
            throw new AppNotFoundException("Museum zone ID not found");
        }
        return museumZone;
    }

    @Override
    public void updateMuseumZoneDetailByZoneId(UUID museumZoneId, MuseumZoneUpdateRequest museumZoneUpdateRequest) {
        boolean zoneId = museumZoneRepository.retrieveMuseumZoneId(museumZoneId);
        if (!zoneId) {
            throw new AppNotFoundException("Museum zone Id Not Found");
        }

        boolean zoneCategoryId = museumZoneRepository.retrieveMuseumZoneCategoryId(museumZoneUpdateRequest.getCategoryId());
        if (!zoneCategoryId) {
            throw new AppNotFoundException("Museum zone category Id Not Found");
        }

        museumZoneRepository.updateMuseumZoneDetailByZoneId(museumZoneId, museumZoneUpdateRequest, updatedAt);
    }

    @Override
    public void updateMuseumArtifactByArtifactId(UUID artifactId, MuseumArtifactRequest museumArtifactRequest) {
        boolean exist = museumZoneRepository.retrieveMuseumArtifactId(artifactId);
        if (!exist) {
            throw new AppNotFoundException("Museum artifact Id Not Found");
        }

        museumZoneRepository.updateMuseumArtifactByArtifactId(artifactId, museumArtifactRequest, updatedAt);
    }

    @Override
    public UUID getMuseumIdByUserId(UUID userId) {
        if(museumZoneRepository.retrieveMuseumIDbyUserID(userId) == null) {
            throw new AppNotFoundException("Museum ID Not Found");
        }
        return museumZoneRepository.retrieveMuseumIDbyUserID(userId);
    }



}
