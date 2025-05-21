package org.hrd.finalprojectmuseum.service.museum.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;
import org.hrd.finalprojectmuseum.repository.museum.MuseumZoneRepository;
import org.hrd.finalprojectmuseum.service.museum.MuseumZoneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MuseumZoneServiceImpl implements MuseumZoneService {

    private final MuseumZoneRepository museumZoneRepository;

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
        boolean categoryExists = false;
        List<MuseumZoneCategory> categories = getAllZonesCategories();
        for (MuseumZoneCategory category : categories) {
            if (category.getMuseumZoneCategoryId().equals(museumZoneRequest.getCategoryId())) {
                categoryExists = true;
                break;
            }
        }
        if (!categoryExists) {
            throw new AppNotFoundException("Museum Zone Category Not Found");
        }
        UUID museumZoneId = museumZoneRepository.createMuseumZone(museumZoneRequest, museumId, LocalDateTime.now());
        if(museumZoneId == null) {
            throw new AppNotFoundException("Museum Zone Id Not Found");
        }
        LocalDateTime updatedAt = LocalDateTime.now();

        if (museumZoneRequest.getArtifacts() != null && !museumZoneRequest.getArtifacts().isEmpty()) {
            for (MuseumArtifactRequest artifact : museumZoneRequest.getArtifacts()) {
                museumZoneRepository.createMuseumArtifact(artifact, museumZoneId, updatedAt);
            }
        }
    }

    @Override
    public MuseumArtifact createMuseumArtifact(List<MuseumArtifactRequest> museumArtifactRequest, UUID museumZoneId) {
        if(museumZoneId == null) {
            throw new AppNotFoundException("Museum Zone Id Not Found");
        }
        LocalDateTime updatedAt = LocalDateTime.now();
        for (MuseumArtifactRequest artifact : museumArtifactRequest) {
            museumZoneRepository.createMuseumArtifact(artifact, museumZoneId, updatedAt);
        }
        return null;
    }

    @Override
    public List<MuseumZoneCategory> getAllZonesCategoriesByMuseumId(UUID museumId) {
        return museumZoneRepository.retrieveAllZonesCategoriesByMuseumID(museumId);
    }

    @Override
    public MuseumZone getMuseumZoneDetailByZoneId(UUID zoneId) {
        MuseumZone museumZone = museumZoneRepository.retrieveMuseumZoneDetailByZoneId(zoneId);
        if (museumZone == null) {
            throw new AppNotFoundException("Museum zone not found");
        }
        return museumZone;
    }

    @Override
    public UUID getMuseumIdByUserId(UUID userId) {
        if(museumZoneRepository.retrieveMuseumIDbyUserID(userId) == null) {
            throw new AppNotFoundException("Museum ID Not Found");
        }
        return museumZoneRepository.retrieveMuseumIDbyUserID(userId);
    }


}
