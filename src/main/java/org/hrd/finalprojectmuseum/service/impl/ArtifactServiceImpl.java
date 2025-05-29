package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;
import org.hrd.finalprojectmuseum.repository.ArtifactRepository;
import org.hrd.finalprojectmuseum.repository.ZoneRepository;
import org.hrd.finalprojectmuseum.service.ArtifactService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtifactServiceImpl implements ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final ZoneRepository zoneRepository;
    LocalDateTime updatedAt = LocalDateTime.now();


    @Override
    public MuseumArtifact createMuseumArtifactByZoneId(MuseumArtifactRequest museumArtifactRequest, UUID museumZoneId) {
        boolean zoneId = zoneRepository.retrieveMuseumZoneId(museumZoneId);
        if (!zoneId) {
            throw new AppNotFoundException("Museum zone Id Not Found");
        }
        return artifactRepository.createMuseumArtifact(museumArtifactRequest, museumZoneId, updatedAt);
    }

    @Override
    public void updateMuseumArtifactByArtifactId(UUID artifactId, MuseumArtifactRequest museumArtifactRequest) {
        boolean exist = artifactRepository.retrieveMuseumArtifactId(artifactId);
        if (!exist) {
            throw new AppNotFoundException("Museum artifact Id Not Found");
        }
        artifactRepository.updateMuseumArtifactByArtifactId(artifactId, museumArtifactRequest, updatedAt);
    }

    @Override
    public void deleteMuseumArtifactByArtifactId(UUID artifactId) {
        boolean exist = artifactRepository.retrieveMuseumArtifactId(artifactId);
        if (!exist) {
            throw new AppNotFoundException("Museum artifact Id Not Found");
        }
        artifactRepository.deleteMuseumArtifactByArtifactId(artifactId, true);
    }

    @Override
    public MuseumArtifact getMuseumArtifactByArtifactId(UUID artifactId) {
        boolean exist = artifactRepository.retrieveMuseumArtifactId(artifactId);
        if (!exist) {
            throw new AppNotFoundException("Museum artifact Id Not Found");
        }
        return artifactRepository.retrieveMuseumArtifactByArtifactId(artifactId);
    }
}
