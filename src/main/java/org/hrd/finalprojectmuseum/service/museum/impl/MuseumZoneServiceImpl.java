package org.hrd.finalprojectmuseum.service.museum.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumZoneResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;
import org.hrd.finalprojectmuseum.repository.museum.MuseumArtifactRepository;
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
    private final MuseumArtifactRepository museumArtifactRepository;
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
                museumArtifactRepository.createMuseumArtifact(artifact, museumZoneId, updatedAt);
            }
        }
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
    };

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
    public void deleteMuseumZoneByZoneId(UUID museumZoneId) {
        boolean zoneId = museumZoneRepository.retrieveMuseumZoneId(museumZoneId);
        if (!zoneId) {
            throw new AppNotFoundException("Museum zone Id Not Found");
        }
        museumZoneRepository.deleteMuseumZoneByZoneId(museumZoneId);
    }

    @Override
    public List<MuseumZoneResponse> getAllMuseumZonesByMuseumId(UUID museumId, Integer page, Integer size) {
        List<MuseumZoneResponse> museumZoneResponses = museumZoneRepository.retrieveMuseumZoneByMuseumId(museumId, page, size);
        if (museumZoneResponses == null) {
            throw new AppNotFoundException("Museum Zone Not Found");
        }
        return museumZoneResponses;
    }

    @Override
    public Integer getTotalMuseumZonesByMuseumId(UUID museumId) {
        return museumZoneRepository.countMuseumZonesByMuseumId(museumId);
    }

    @Override
    public UUID getMuseumIdByUserId(UUID userId) {
        if(museumZoneRepository.retrieveMuseumIDbyUserID(userId) == null) {
            throw new AppNotFoundException("Museum ID Not Found");
        }
        return museumZoneRepository.retrieveMuseumIDbyUserID(userId);
    }



}
