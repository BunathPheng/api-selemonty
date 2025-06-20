package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneUpdateRequest;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumZoneResponse;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.*;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.repository.ArtifactRepository;
import org.hrd.finalprojectmuseum.repository.MuseumRepository;
import org.hrd.finalprojectmuseum.repository.ProfileRepository;
import org.hrd.finalprojectmuseum.repository.ZoneRepository;
import org.hrd.finalprojectmuseum.service.AppUserService;
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
    private final MuseumRepository museumRepository;
    private final ProfileRepository profileRepository;
    private final AppUserService appUserService;
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
        MuseumOwner museum = museumRepository.findMuseumOwnerByMuseumId(museumId);
        if (museum == null) {
            throw new AppNotFoundException("Museum with id " + museumId + " not exists");
        }
        if (!museum.getIsApproved()) {
            throw new AppBadRequestException("Booking failed. This museum is not approved by admin");
        }
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
        Integer countArtifact = artifactRepository.countArtifact(museumZone.getZoneId(), "");
        museumZone.setCountArtifact(countArtifact);
        return museumZone;
    };

    @Override
    @Transactional
    public void updateMuseumZoneDetailByZoneId(UUID museumZoneId, MuseumZoneUpdateRequest museumZoneUpdateRequest, UUID museumId) {
        MuseumZone zone = zoneRepository.retrieveMuseumZoneDetailByZoneId(museumZoneId);
        if (zone == null) {
            throw new AppNotFoundException("Museum zone ID not found");
        }

        if (!zone.getMuseumId().equals(museumId)) {
            throw new AppBadRequestException("Zone belongs to another Museum. You can not update this zone");
        }

        boolean zoneCategoryExists = zoneRepository.retrieveMuseumZoneCategoryId(museumZoneUpdateRequest.getCategoryId());
        if (!zoneCategoryExists) {
            throw new AppNotFoundException("Museum zone category Id Not Found");
        }

        zoneRepository.updateMuseumZoneDetailByZoneId(museumZoneId, museumZoneUpdateRequest, LocalDateTime.now());
    }

    @Override
    public void deleteMuseumZoneByZoneId(UUID museumZoneId, UUID museumId) {
        MuseumZone zone = zoneRepository.retrieveMuseumZoneDetailByZoneId(museumZoneId);
        if (zone == null) {
            throw new AppNotFoundException("Museum zone ID not found");
        }
        if (!zone.getMuseumId().equals(museumId)) {
            throw new AppBadRequestException("Zone belongs to another Museum. You can not update this zone");
        }
        zoneRepository.deleteMuseumZoneByZoneId(museumZoneId, true);
        artifactRepository.deleteMuseumArtifactByMuseumId(museumZoneId, true);
    }

    @Override
    public List<MuseumZoneResponse> getAllMuseumZonesByMuseumId(UUID museumId, String search, UUID categoryId, Integer page, Integer size) {
        search = search == null ? "" : search;
        int offset = (page - 1) * size;
        List<MuseumZoneResponse> museumZoneResponses;
        if (categoryId == null){
            museumZoneResponses = zoneRepository.retrieveMuseumZoneByMuseumId(museumId, search, size, offset);
        }else{
            museumZoneResponses = zoneRepository.retrieveMuseumZoneByMuseumIdWithCategory(museumId, search, categoryId, size, offset);
        }
        if (museumZoneResponses == null) {
            throw new AppNotFoundException("Museum Zone Not Found");
        }
        for(MuseumZoneResponse museumZoneResponse : museumZoneResponses){
            Integer countArtifact = artifactRepository.countArtifact(museumZoneResponse.getZoneId(), "");
            museumZoneResponse.setCountArtifact(countArtifact);
        }
        return museumZoneResponses;
    }

    @Override
    public Integer getTotalMuseumZonesByMuseumId(UUID museumId, String search, UUID categoryId) {
        search = search == null ? "" : search;
        if (categoryId == null){
            return zoneRepository.countMuseumZonesByMuseumId(museumId, search);
        }else {
            return zoneRepository.countMuseumZonesByMuseumIdWithCategory(museumId, search, categoryId);
        }
    }

    @Override
    public ArtifactZone getAmountArtifactZone(UUID museumId) {
        UUID userId = appUserService.getUserId();
        AppUserRegister appUserRegister = appUserService.findUserByUserId(userId);

        if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER){
            UUID idMuseumLogin = profileRepository.findMuseumOwnerByUserId(userId).getMuseumId();
            if (!idMuseumLogin.equals(museumId)) {
                throw new AppBadRequestException("Museum " + museumId + " not found, Resource restriction");
            }
        }
        ArtifactZone totalArtifactZone = zoneRepository.findTotalArtifactZone(museumId);
        if (totalArtifactZone == null) {
            throw new AppNotFoundException("Museum zone not found");
        }
        return totalArtifactZone;
    }

    @Override
    public UUID getMuseumIdByUserId(UUID userId) {
        if(zoneRepository.retrieveMuseumIDbyUserID(userId) == null) {
            throw new AppNotFoundException("Museum ID Not Found");
        }
        return zoneRepository.retrieveMuseumIDbyUserID(userId);
    }



}
