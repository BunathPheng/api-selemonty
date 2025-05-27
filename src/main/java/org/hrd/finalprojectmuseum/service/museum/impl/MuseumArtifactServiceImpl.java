package org.hrd.finalprojectmuseum.service.museum.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;
import org.hrd.finalprojectmuseum.repository.museum.MuseumArtifactRepository;
import org.hrd.finalprojectmuseum.repository.museum.MuseumZoneRepository;
import org.hrd.finalprojectmuseum.service.museum.MuseumArtifactService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MuseumArtifactServiceImpl implements MuseumArtifactService {

    private final MuseumArtifactRepository museumArtifactRepository;
    private final MuseumZoneRepository museumZoneRepository;
    LocalDateTime updatedAt = LocalDateTime.now();


    @Override
    public MuseumArtifact createMuseumArtifactByZoneId(MuseumArtifactRequest museumArtifactRequest, UUID museumZoneId) {
        boolean zoneId = museumZoneRepository.retrieveMuseumZoneId(museumZoneId);
        if (!zoneId) {
            throw new AppNotFoundException("Museum zone Id Not Found");
        }
        return museumArtifactRepository.createMuseumArtifact(museumArtifactRequest, museumZoneId, updatedAt);
    }

    @Override
    public void updateMuseumArtifactByArtifactId(UUID artifactId, MuseumArtifactRequest museumArtifactRequest) {
        boolean exist = museumArtifactRepository.retrieveMuseumArtifactId(artifactId);
        if (!exist) {
            throw new AppNotFoundException("Museum artifact Id Not Found");
        }
        museumArtifactRepository.updateMuseumArtifactByArtifactId(artifactId, museumArtifactRequest, updatedAt);
    }

    @Override
    public void deleteMuseumArtifactByArtifactId(UUID artifactId) {
        boolean exist = museumArtifactRepository.retrieveMuseumArtifactId(artifactId);
        if (!exist) {
            throw new AppNotFoundException("Museum artifact Id Not Found");
        }
        museumArtifactRepository.deleteMuseumArtifactByArtifactId(artifactId, true);
    }

    @Override
    public MuseumArtifact getMuseumArtifactByArtifactId(UUID artifactId) {
        boolean exist = museumArtifactRepository.retrieveMuseumArtifactId(artifactId);
        if (!exist) {
            throw new AppNotFoundException("Museum artifact Id Not Found");
        }
        return museumArtifactRepository.retrieveMuseumArtifactByArtifactId(artifactId);
    }
}
